package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtistRepositoryImpl implements ArtistRepository {
    private final DbConnection dbConnection;

    public ArtistRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Artist> findAllActive() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT id, name, is_archived FROM artists WHERE is_archived = 0 ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                artists.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActive Artists): " + e.getMessage());
        }
        return artists;
    }

    @Override
    public List<Artist> findAllArchived() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT id, name, is_archived FROM artists WHERE is_archived = 1 ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                artists.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchived Artists): " + e.getMessage());
        }
        return artists;
    }

    @Override
    public boolean save(Artist artist) {
        String query = "INSERT INTO artists (name) VALUES (?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, artist.getName());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (save Artist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Artist artist) {
        String query = "UPDATE artists SET name = ? WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, artist.getName());
            preparedStatement.setInt(2, artist.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (update Artist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String query = "DELETE FROM artists WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteById Artist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String query = "UPDATE artists SET is_archived = 1 WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveById Artist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String query = "UPDATE artists SET is_archived = 0 WHERE id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreById Artist): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Artist> searchByName(String keyword) {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT id, name, is_archived FROM artists " +
                "WHERE is_archived = 0 AND name LIKE ? ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    artists.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByName Artist): " + e.getMessage());
        }
        return artists;
    }

    private Artist mapRow(ResultSet resultSet) throws SQLException {
        return new Artist(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("is_archived") == 1
        );
    }
}