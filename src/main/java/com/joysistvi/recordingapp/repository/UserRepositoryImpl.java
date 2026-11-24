package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final DbConnection dbConnection;

    public UserRepositoryImpl(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<User> findAllActive() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, email, role, is_archived " +
                "FROM users WHERE is_archived = 0 ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllActive Users): " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> findAllArchived() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, email, role, is_archived " +
                "FROM users WHERE is_archived = 1 ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findAllArchived Users): " + e.getMessage());
        }
        return users;
    }

    @Override
    public User findByUsername(String username) {
        String query = "SELECT id, username, password, email, role, is_archived " +
                "FROM users WHERE username = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (findByUsername User): " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean save(User user) {
        String query = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole() != null ? user.getRole() : "USER");
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (save User): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        String query = "UPDATE users SET username = ?, email = ? WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setInt(3, user.getId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (update User): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteById(int id) {
        String query = "DELETE FROM users WHERE id = ?";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (deleteById User): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean archiveById(int id) {
        String query = "UPDATE users SET is_archived = 1 WHERE id = ? AND is_archived = 0";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (archiveById User): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreById(int id) {
        String query = "UPDATE users SET is_archived = 0 WHERE id = ? AND is_archived = 1";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Database Error (restoreById User): " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> searchByUsername(String keyword) {
        List<User> users = new ArrayList<>();
        String query = "SELECT id, username, password, email, role, is_archived " +
                "FROM users WHERE is_archived = 0 AND username LIKE ? ORDER BY id";

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error (searchByUsername User): " + e.getMessage());
        }
        return users;
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("email"),
                resultSet.getString("role"),
                resultSet.getInt("is_archived") == 1
        );
    }
}