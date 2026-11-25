package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SongRepositoryImpl implements SongRepository {
    private final DbConnection dbConnection;

    public SongRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Song> findAllActive() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT songs.id, songs.title, songs.genre, songs.length, songs.album_id, " +
                "albums.name AS album_name, songs.is_archived " +
                "FROM songs " +
                "JOIN albums ON songs.album_id = albums.id " +
                "WHERE songs.is_archived = 0 " +
                "ORDER BY songs.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                songs.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActive Songs): " + e.getMessage());
        }
        return songs;
    }

    @Override
    public List<Song> findAllArchived() {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT songs.id, songs.title, songs.genre, songs.length, songs.album_id, " +
                "albums.name AS album_name, songs.is_archived " +
                "FROM songs " +
                "JOIN albums ON songs.album_id = albums.id " +
                "WHERE songs.is_archived = 1 " +
                "ORDER BY songs.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                songs.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchived Songs): " + e.getMessage());
        }
        return songs;
    }

    @Override
    public boolean save(Song song) {
        String query = "INSERT INTO songs (title, genre, length, album_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, song.getTitle());
            preparedStatement.setString(2, song.getGenre());
            preparedStatement.setString(3, song.getLength());
            preparedStatement.setInt(4, song.getAlbumId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (save Song): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Song song) {
        String query = "UPDATE songs SET title = ?, genre = ?, length = ?, album_id = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, song.getTitle());
            preparedStatement.setString(2, song.getGenre());
            preparedStatement.setString(3, song.getLength());
            preparedStatement.setInt(4, song.getAlbumId());
            preparedStatement.setInt(5, song.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (update Song): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String query = "DELETE FROM songs WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteById Song): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String query = "UPDATE songs SET is_archived = 1 WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveById Song): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String query = "UPDATE songs SET is_archived = 0 WHERE id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreById Song): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Song> searchByTitle(String keyword) {
        List<Song> songs = new ArrayList<>();
        String query = "SELECT songs.id, songs.title, songs.genre, songs.length, songs.album_id, " +
                "albums.name AS album_name, songs.is_archived " +
                "FROM songs " +
                "JOIN albums ON songs.album_id = albums.id " +
                "WHERE songs.is_archived = 0 AND songs.title LIKE ? " +
                "ORDER BY songs.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    songs.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByTitle Song): " + e.getMessage());
        }
        return songs;
    }

    private Song mapRow(ResultSet resultSet) throws SQLException {
        return new Song(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("genre"),
                resultSet.getString("length"),
                resultSet.getInt("album_id"),
                resultSet.getString("album_name"),
                resultSet.getInt("is_archived") == 1
        );
    }
}