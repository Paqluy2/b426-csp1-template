package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Album;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlbumRepositoryImpl implements AlbumRepository {
    private final DbConnection dbConnection;

    public AlbumRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Album> findAllActive() {
        List<Album> albums = new ArrayList<>();
        String query = "SELECT albums.id, albums.name, albums.year, albums.artist_id, " +
                "artists.name AS artist_name, albums.is_archived " +
                "FROM albums " +
                "JOIN artists ON albums.artist_id = artists.id " +
                "WHERE albums.is_archived = 0 " +
                "ORDER BY albums.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                albums.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActive Albums): " + e.getMessage());
        }
        return albums;
    }

    @Override
    public List<Album> findAllArchived() {
        List<Album> albums = new ArrayList<>();
        String query = "SELECT albums.id, albums.name, albums.year, albums.artist_id, " +
                "artists.name AS artist_name, albums.is_archived " +
                "FROM albums " +
                "JOIN artists ON albums.artist_id = artists.id " +
                "WHERE albums.is_archived = 1 " +
                "ORDER BY albums.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                albums.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchived Albums): " + e.getMessage());
        }
        return albums;
    }

    @Override
    public boolean save(Album album) {
        String query = "INSERT INTO albums (name, year, artist_id) VALUES (?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, album.getName());
            preparedStatement.setInt(2, album.getYear());
            preparedStatement.setInt(3, album.getArtistId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (save Album): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Album album) {
        String query = "UPDATE albums SET name = ?, year = ?, artist_id = ? " +
                "WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, album.getName());
            preparedStatement.setInt(2, album.getYear());
            preparedStatement.setInt(3, album.getArtistId());
            preparedStatement.setInt(4, album.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (update Album): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String query = "DELETE FROM albums WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteById Album): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String query = "UPDATE albums SET is_archived = 1 WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveById Album): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String query = "UPDATE albums SET is_archived = 0 WHERE id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreById Album): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Album> searchByName(String keyword) {
        List<Album> albums = new ArrayList<>();
        String query = "SELECT albums.id, albums.name, albums.year, albums.artist_id, " +
                "artists.name AS artist_name, albums.is_archived " +
                "FROM albums " +
                "JOIN artists ON albums.artist_id = artists.id " +
                "WHERE albums.is_archived = 0 AND albums.name LIKE ? " +
                "ORDER BY albums.id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    albums.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByName Album): " + e.getMessage());
        }
        return albums;
    }

    private Album mapRow(ResultSet resultSet) throws SQLException {
        return new Album(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("year"),
                resultSet.getInt("artist_id"),
                resultSet.getString("artist_name"),
                resultSet.getInt("is_archived") == 1
        );
    }
}