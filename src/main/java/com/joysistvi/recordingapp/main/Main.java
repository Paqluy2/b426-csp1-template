package com.joysistvi.recordingapp.main;

import com.mysql.cj.jdbc.exceptions.ConnectionFeatureNotAvailableException;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class Main {
    static String url = "jdbc:mysql://localhost:3306/recording_studio_db";
    static String username = "root";
    static String password = "";

    public static void main(String[] args) {
        createAlbum("   ", 1991, 1);                      // Validation Error: blank name
        createAlbum("Coco", 1800, 1);                     // Validation Error: invalid year
        createAlbum("Coco", 1991, 0);                     // Validation Error: invalid artistId
        createAlbum("Coco", 1991, 1);                     // Valid
        updateAlbum(0, "Coco (Remastered)", 1995, 1);     // Validation Error: invalid id
        updateAlbum(7, "Coco (Remastered)", 1995, 1);     // Valid (adjust ID to match your DB)
        searchAlbum("   ");                               // Validation Error: blank keyword
        searchAlbum("Coco");                              // Valid
        archiveAlbum(-1);                                 // Validation Error: invalid id
        archiveAlbum(7);                                  // Valid (requires is_archived column)
        restoreAlbum(0);                                  // Validation Error: invalid id
        restoreAlbum(7);                                  // Valid (requires is_archived column)
        deleteAlbum(0);                                   // Validation Error: invalid id
        deleteAlbum(8);                                   // Valid (adjust ID to match your DB)

        // --- Artists Validation & CRUD Tests ---
        createArtist("   ");                              // Validation Error: blank name
        createArtist("Cup of Joe");                       // Valid
        updateArtist(0, "Cup of Joe (OPM)");              // Validation Error: invalid id
        updateArtist(6, "   ");                           // Validation Error: blank name
        updateArtist(6, "Cup of Joe (OPM)");              // Valid (adjust ID to match your DB)
        searchArtist("");                                 // Validation Error: blank keyword
        searchArtist("Ben");                              // Valid
        archiveArtist(0);                                 // Validation Error: invalid id
        archiveArtist(6);                                 // Valid (requires is_archived column)
        restoreArtist(-2);                                // Validation Error: invalid id
        restoreArtist(6);                                 // Valid (requires is_archived column)
        deleteArtist(0);                                  // Validation Error: invalid id
        deleteArtist(6);                                  // Valid (adjust ID to match your DB)

        // --- Users Validation & Auth Tests ---
        createUser("   ", "secret123", "test@qweqwe.com"); // Validation Error: blank username
        createUser("jdcristobal", "123", "jd@gmail.com");    // Validation Error: password < 6 chars
        createUser("jdcristobal", "secret123", "invalid");    // Validation Error: invalid email
        createUser("jdcristobal", "secret123", "jd@gmail.com"); // Valid
        loginUser("", "secret123");                        // Validation Error: blank username
        loginUser("jdcristobal", "");                         // Validation Error: empty password
        loginUser("jdcristobal", "wrongpass");                // Invalid credentials
        loginUser("jdcristobal", "secret123");                // Valid login
        // try catch with resources
        try (Connection connection = connect()) {
            System.out.println("Connected Successfully: " + connection);

        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }

    }

    // ducking exception
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // CRUD Operation

    // Read Albums
    public static void viewAllAlbums() {
        String query = "SELECT albums.id, albums.name, albums.year, artists.name AS artists_name " +
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
                String artistName = resultSet.getString("artists_name");
                System.out.printf("%-4d %-20s %-8d %-20s%n", id, name, year, artistName);
            }
        } catch (SQLException e) {
            System.out.println("Read Albums: " + e.getMessage());
        }

    }


    public static void createAlbum(String name, int year, int artistId) {
        String query = "INSERT INTO albums (name, year, artist_id) " +
                "VALUES (?, ?, ?)"; // anti-sql

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString( 1, name);
            preparedStatement.setInt( 2, year);
            preparedStatement.setInt( 3, artistId);

            preparedStatement.executeUpdate();

            System.out.println("Album: " + name + " updated successfully!");
            viewAllAlbums();
        } catch (SQLException e) {
            System.out.println("Update Album: " + e.getMessage());
        }
    }

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
                System.out.println("Album ID " + id + " updated successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Update Album: " + e.getMessage());
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
                System.out.println("Album ID " + id + " deleted successfully!");
                viewAllAlbums();
            } else {
                System.out.println("No album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Delete Album: " + e.getMessage());
        }
    }

    // Archive
    public static void archiveAlbum(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }

        String query = "UPDATE albums SET is_archived = 1 WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nAlbum ID " + id + " archived successfully!");
            } else {
                System.out.println("\nNo album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Archive Album: " + e.getMessage());
        }
    }

    // Restore Album
    public static void restoreAlbum(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return;
        }

        String query = "UPDATE albums SET is_archived = 0 WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\nAlbum ID " + id + " restored successfully!");
            } else {
                System.out.println("\nNo album found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Restore Album: " + e.getMessage());
        }
    }

    // Search Album
    public static void searchAlbum(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return;
        }

        String query = "SELECT albums.id, albums.name, albums.year, artists.name AS artists_name " +
                "FROM albums " +
                "JOIN artists ON albums.artist_id = artists.id " +
                "WHERE albums.name LIKE ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("\n----- Search Albums: \"" + keyword.trim() + "\" -----");
                System.out.printf("%-4s %-20s %-8s %-20s%n", "ID", "Name", "Year", "Artist");

                boolean found = false;
                while (resultSet.next()) {
                    found = true;
                    System.out.printf("%-4d %-20s %-8d %-20s%n",
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getInt("year"),
                            resultSet.getString("artists_name"));
                }

                if (!found) {
                    System.out.println("No albums found matching \"" + keyword.trim() + "\".");
                }
            }
        } catch (SQLException e) {
            System.out.println("Search Album: " + e.getMessage());
        }
    }
    // Read Artists
    public static void viewAllArtists() {
        String query = "SELECT id, name FROM artists";

        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("\n----- Artists -----");
            System.out.printf("%-4s %-25s%n", "ID", "Artist Name");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.printf("%-4d %-25s%n", id, name);
            }
        } catch (SQLException e) {
            System.out.println("Read Artists: " + e.getMessage());
        }
    }

    // Create Artist
    public static void createArtist(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Artist name is required.");
            return;
        }

        String query = "INSERT INTO artists (name) VALUES (?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name.trim());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nArtist: " + name.trim() + " created successfully!");
                viewAllArtists();
            }
        } catch (SQLException e) {
            System.out.println("Create Artist: " + e.getMessage());
        }
    }

    // Update Artist
    public static void updateArtist(int id, String name) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Artist name is required.");
            return;
        }

        String query = "UPDATE artists SET name = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name.trim());
            preparedStatement.setInt(2, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nArtist ID " + id + " updated successfully!");
                viewAllArtists();
            } else {
                System.out.println("\nNo artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Update Artist: " + e.getMessage());
        }
    }

    // Delete Artist
    public static void deleteArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

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
            System.out.println("Delete Artist: " + e.getMessage());
        }
    }

    // Archive Artist (Soft Delete)
    public static void archiveArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String query = "UPDATE artists SET is_archived = 1 WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nArtist ID " + id + " archived successfully!");
            } else {
                System.out.println("\nNo artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Archive Artist: " + e.getMessage());
        }
    }

    // Restore Artist
    public static void restoreArtist(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Artist ID must be greater than 0.");
            return;
        }

        String query = "UPDATE artists SET is_archived = 0 WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nArtist ID " + id + " restored successfully!");
            } else {
                System.out.println("\nNo artist found with ID " + id + ".");
            }
        } catch (SQLException e) {
            System.out.println("Restore Artist: " + e.getMessage());
        }
    }

    // Search Artist
    public static void searchArtist(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return;
        }

        String query = "SELECT id, name FROM artists WHERE name LIKE ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + keyword.trim() + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("\n----- Search Artists: \"" + keyword.trim() + "\" -----");
                System.out.printf("%-4s %-25s%n", "ID", "Artist Name");

                boolean found = false;
                while (resultSet.next()) {
                    found = true;
                    System.out.printf("%-4d %-25s%n",
                            resultSet.getInt("id"),
                            resultSet.getString("name"));
                }

                if (!found) {
                    System.out.println("No artists found matching \"" + keyword.trim() + "\".");
                }
            }
        } catch (SQLException e) {
            System.out.println("Search Artist: " + e.getMessage());
        }
    }


    // Create User (Register)
    public static void createUser(String uname, String rawPassword, String email) {
        if (uname == null || uname.trim().isEmpty()) {
            System.out.println("\nValidation Error: Username is required.");
            return;
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@") || !email.contains(".")) {
            System.out.println("\nValidation Error: A valid email address is required.");
            return;
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            System.out.println("\nValidation Error: Password must be at least 6 characters long.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uname.trim());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email.trim());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\nUser: " + uname.trim() + " created successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Create User: " + e.getMessage());
        }
    }

    // Login User
    public static void loginUser(String uname, String candidatePassword) {
        if (uname == null || uname.trim().isEmpty()) {
            System.out.println("\nValidation Error: Username is required.");
            return;
        }
        if (candidatePassword == null || candidatePassword.isEmpty()) {
            System.out.println("\nValidation Error: Password is required.");
            return;
        }

        String query = "SELECT id, username, password, role FROM users WHERE username = ?";

        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, uname.trim());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
                    String role = resultSet.getString("role");

                    if (BCrypt.checkpw(candidatePassword, storedHash)) {
                        System.out.println("\nLogin successful! Welcome, " + uname.trim() + " (" + role + ").");
                    } else {
                        System.out.println("\nLogin failed: Incorrect password.");
                    }
                } else {
                    System.out.println("\nLogin failed: User not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Login User: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Login User: Stored password hash format is invalid (" + e.getMessage() + ").");
        }
    }
}