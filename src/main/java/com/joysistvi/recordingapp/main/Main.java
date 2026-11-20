package com.joysistvi.recordingapp.main;

import java.sql.*;

public class Main {

    static String url = "jdbc:mysql://localhost:3306/recording_studio_db";
    static String username = "root";
    static String password = "";

    public static void main(String[] args) {

        // 1. TEST ARTISTS CRUD

        viewAllArtists();
        createArtist("Cup of Joe");
        updateArtist(6, "Cup of Joe (OPM)");
        deleteArtist(6);

        // Test ON DELETE RESTRICT on Artists (Artist ID 1 has existing albums)
        deleteArtist(1);


        // 2. TEST ALBUMS CRUD

        viewAllAlbums();
        createAlbum("Coco", 1990, 1);
        updateAlbum(6, "Pebble House", 2021, 1);
        deleteAlbum(6);

        // Test ON DELETE RESTRICT on Albums (Album ID 1 has existing songs)
        deleteAlbum(1);
    }

    // ducking exception
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // CRUD Operation

    // ==========================================
    // ALBUMS CRUD OPERATIONS
    // ==========================================

    // Read Albums
    public static void viewAllAlbums() {
        String query = "SELECT albums.id, albums.name, albums.year, artists.name AS artist_name " +
                "FROM albums " +
                "JOIN artists " +
                "ON albums.artist_id = artists.id"; // Create SQL Statement / Query

        try (Connection connection = connect();
             Statement statement = connection.createStatement(); // create a statement object for sending sql statement
             ResultSet resultSet = statement.executeQuery(query); // execute query then store the result in the result set
        ) {
            System.out.println("\n----- Albums -----");
            System.out.printf("%-4s %-20s %-8s %-20s%n", "ID", "Name", "Year", "Artist"); // print header
            // Extract the data
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int year = resultSet.getInt("year");
                String artistName = resultSet.getString("artist_name");
                System.out.printf("%-4d %-20s %-8d %-20s%n", id, name, year, artistName);
            }
        } catch (SQLException e) {
            System.out.println("Read Albums: " + e.getMessage());
        }



    }

    // DML - Data Manipulation Language (prepareStatement) (executeUpdate) -> Create, Update, Delete
    // DQL - Data Query Language (createStatement) (executeQuery) -> Read / Select

    // Create Album
    public static void createAlbum(String name, int year, int artistId) {
        String query = "INSERT INTO albums (name, year, artist_id) " +
                "VALUES (?, ?, ?)"; // parameterized query (anti-sql injection)

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
        ){
            // setting parameter wild cards
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistId);

            preparedStatement.executeUpdate();

            System.out.println("Album: " + name + " created successfully!");
            viewAllAlbums(); // RAW
        } catch (SQLException e) {
            System.out.println("Update Album: " + e.getMessage());
        }
    }


    //    Activity Solutions Start:

    // Update Album
    public static void updateAlbum(int id, String name, int year, int artistId) {
        String query = "UPDATE albums SET name = ?, year = ?, artist_id = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, artistId);
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nAlbum ID " + id + " updated successfully!");
                viewAllAlbums();
            } else {
                System.out.println("\nNo album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("\nUpdate Album Error: " + e.getMessage());
        }
    }

    // Delete Album
    public static void deleteAlbum(int id) {
        String query = "DELETE FROM albums WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nAlbum ID " + id + " deleted successfully!");
                viewAllAlbums();
            } else {
                System.out.println("\nNo album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("\nDelete Album Error: " + e.getMessage());
        }
    }

    // ==========================================
    // ARTISTS CRUD OPERATIONS
    // ==========================================

    // Read Artists
    public static void viewAllArtists() {
        String query = "SELECT * FROM artists";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("\n----- Artists -----");
            System.out.printf("%-4s %-20s%n", "ID", "Artist Name");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.printf("%-4d %-20s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("Read Artists Error: " + e.getMessage());
        }
    }

    // Create Artist
    public static void createArtist(String name) {
        String query = "INSERT INTO artists (name) VALUES (?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();

            System.out.println("\nArtist: " + name + " created successfully!");
            viewAllArtists();
        } catch (SQLException e) {
            System.out.println("Create Artist Error: " + e.getMessage());
        }
    }

    // Update Artist
    public static void updateArtist(int id, String name) {
        String query = "UPDATE artists SET name = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nArtist ID " + id + " updated successfully!");
                viewAllArtists();
            } else {
                System.out.println("\nNo artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("\nUpdate Artist Error: " + e.getMessage());
        }
    }

    // Delete Artist
    public static void deleteArtist(int id) {
        String query = "DELETE FROM artists WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nArtist ID " + id + " deleted successfully!");
                viewAllArtists();
            } else {
                System.out.println("\nNo artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("\nDelete Artist Error: " + e.getMessage());
        }
    }

    //    Activity Solutions End
}