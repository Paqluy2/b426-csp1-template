package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Playlist;
import com.joysistvi.recordingapp.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepositoryImpl implements PlaylistRepository {
    private final DbConnection dbConnection;

    public PlaylistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Playlist> findAllActive() {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 0 " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                playlists.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActive Playlists): " + e.getMessage());
        }
        return playlists;
    }

    @Override
    public List<Playlist> findAllArchived() {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 1 " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                playlists.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchived Playlists): " + e.getMessage());
        }
        return playlists;
    }

    @Override
    public boolean save(Playlist playlist) {
        String query = "INSERT INTO playlists (name, user_id) VALUES (?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlist.getName());
            preparedStatement.setInt(2, playlist.getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (save Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Playlist playlist) {
        String query = "UPDATE playlists SET name = ?, user_id = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlist.getName());
            preparedStatement.setInt(2, playlist.getUserId());
            preparedStatement.setInt(3, playlist.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (update Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String query = "DELETE FROM playlists WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteById Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String query = "UPDATE playlists SET is_archived = 1 WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveById Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String query = "UPDATE playlists SET is_archived = 0 WHERE id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreById Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Playlist> searchByName(String keyword) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 0 AND playlists.name LIKE ? " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    playlists.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByName Playlist): " + e.getMessage());
        }
        return playlists;
    }

    private Playlist mapRow(ResultSet resultSet) throws SQLException {
        return new Playlist(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("date_created"),
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getInt("is_archived") == 1
        );
    }

    @Override
    public List<Playlist> findAllActiveByUserId(int userId) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 0 AND playlists.user_id = ? " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    playlists.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActiveByUserId Playlists): " + e.getMessage());
        }
        return playlists;
    }

    @Override
    public List<Playlist> findAllArchivedByUserId(int userId) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 1 AND playlists.user_id = ? " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    playlists.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchivedByUserId Playlists): " + e.getMessage());
        }
        return playlists;
    }

    @Override
    public boolean updateByUserId(Playlist playlist) {
        String query = "UPDATE playlists SET name = ? " +
                "WHERE id = ? AND user_id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, playlist.getName());
            preparedStatement.setInt(2, playlist.getId());
            preparedStatement.setInt(3, playlist.getUserId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (updateByUserId Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteByIdAndUserId(int id, int userId) {
        String query = "DELETE FROM playlists WHERE id = ? AND user_id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteByIdAndUserId Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveByIdAndUserId(int id, int userId) {
        String query = "UPDATE playlists SET is_archived = 1 " +
                "WHERE id = ? AND user_id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveByIdAndUserId Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreByIdAndUserId(int id, int userId) {
        String query = "UPDATE playlists SET is_archived = 0 " +
                "WHERE id = ? AND user_id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreByIdAndUserId Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Playlist> searchByNameAndUserId(String keyword, int userId) {
        List<Playlist> playlists = new ArrayList<>();
        String query = "SELECT playlists.id, playlists.name, playlists.date_created, " +
                "playlists.user_id, users.username, playlists.is_archived " +
                "FROM playlists " +
                "JOIN users ON playlists.user_id = users.id " +
                "WHERE playlists.is_archived = 0 AND playlists.user_id = ? AND playlists.name LIKE ? " +
                "ORDER BY playlists.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    playlists.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByNameAndUserId Playlist): " + e.getMessage());
        }
        return playlists;
    }
    @Override
    public boolean existsActiveById(int playlistId) {
        String query = "SELECT 1 FROM playlists WHERE id = ? AND is_archived = 0 LIMIT 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Database Error (existsActiveById Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsActiveByIdAndUserId(int playlistId, int userId) {
        String query = "SELECT 1 FROM playlists WHERE id = ? AND user_id = ? AND is_archived = 0 LIMIT 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Database Error (existsActiveByIdAndUserId Playlist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean addSongToPlaylist(int playlistId, int songId) {
        String query = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("That song is already in this playlist.");
            } else if (e.getErrorCode() == 1452) {
                System.out.println("Invalid Song ID or Playlist ID.");
            } else {
                System.out.println("Database Error (addSongToPlaylist): " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId) {
        String query = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            preparedStatement.setInt(2, songId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (removeSongFromPlaylist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Song> findSongsByPlaylistId(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT songs.id, songs.title, songs.genre, songs.length, songs.album_id, " +
                "albums.name AS album_name, songs.is_archived " +
                "FROM playlist_songs " +
                "JOIN songs ON playlist_songs.song_id = songs.id " +
                "JOIN albums ON songs.album_id = albums.id " +
                "WHERE playlist_songs.playlist_id = ? AND songs.is_archived = 0 " +
                "ORDER BY songs.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playlistId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    songs.add(new Song(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("genre"),
                            resultSet.getString("length"),
                            resultSet.getInt("album_id"),
                            resultSet.getString("album_name"),
                            resultSet.getInt("is_archived") == 1
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findSongsByPlaylistId): " + e.getMessage());
        }
        return songs;
    }
}