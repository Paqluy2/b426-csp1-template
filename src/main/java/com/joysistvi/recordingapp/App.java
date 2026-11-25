package com.joysistvi.recordingapp;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.repository.*;
import com.joysistvi.recordingapp.service.*;
import com.joysistvi.recordingapp.view.*;

import java.util.Scanner;

public class App {
    private final SongView songView;
    private final ArtistView artistView;
    private final AlbumView albumView;
    private final UserView userView;
    private final PlaylistView playlistView;
    private final Scanner scanner;
    private User currentUser;

    public App(SongView songView, ArtistView artistView, AlbumView albumView,
               UserView userView, PlaylistView playlistView) {
        this.songView = songView;
        this.artistView = artistView;
        this.albumView = albumView;
        this.userView = userView;
        this.playlistView = playlistView;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        // Bottom-up wiring: DbConnection -> Repository -> Service -> View
        DbConnection dbConnection = new DbConnection();

        SongRepository songRepository = new SongRepositoryImpl(dbConnection);
        SongService songService = new SongServiceImpl(songRepository);
        SongView songView = new SongView(songService);

        ArtistRepository artistRepository = new ArtistRepositoryImpl(dbConnection);
        ArtistService artistService = new ArtistServiceImpl(artistRepository);
        ArtistView artistView = new ArtistView(artistService);

        AlbumRepository albumRepository = new AlbumRepositoryImpl(dbConnection);
        AlbumService albumService = new AlbumServiceImpl(albumRepository);
        AlbumView albumView = new AlbumView(albumService);

        UserRepository userRepository = new UserRepositoryImpl(dbConnection);
        UserService userService = new UserServiceImpl(userRepository);
        UserView userView = new UserView(userService);

        PlaylistRepository playlistRepository = new PlaylistRepositoryImpl(dbConnection);
        PlaylistService playlistService = new PlaylistServiceImpl(playlistRepository);
        PlaylistView playlistView = new PlaylistView(playlistService, songService);

        App app = new App(songView, artistView, albumView, userView, playlistView);
        app.run();
    }

    public void run() {
        while (true) {
            if (!showAuthMenuAndAuthenticate()) {
                System.out.println("Goodbye!");
                return;
            }
            if (currentUser != null) {
                dashboard();
                currentUser = null;
                System.out.println("You have been logged out.");
            }
        }
    }

    private boolean showAuthMenuAndAuthenticate() {
        while (currentUser == null) {
            System.out.println("\n=== RECORDING STUDIO AUTH ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");

            int choice = promptForInt("Choice: ");
            switch (choice) {
                case 1 -> currentUser = userView.promptLoginUser();
                case 2 -> userView.promptCreateUser();
                case 0 -> {
                    return false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        return true;
    }

    private void dashboard() {
        if (currentUser.isAdmin()) {
            adminDashboard();
        } else {
            userDashboard();
        }
    }

    private void adminDashboard() {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD (" + currentUser.getUsername() + ") ===");
            System.out.println("1. Song Management");
            System.out.println("2. Artist Management");
            System.out.println("3. Album Management");
            System.out.println("4. Users Management");
            System.out.println("5. Playlist Management");
            System.out.println("0. Logout");

            int choice = promptForInt("Choice: ");
            switch (choice) {
                case 1 -> songView.handleMenu(currentUser);
                case 2 -> artistView.handleMenu(currentUser);
                case 3 -> albumView.handleMenu(currentUser);
                case 4 -> userView.handleMenu(currentUser);
                case 5 -> playlistView.handleMenu(currentUser);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void userDashboard() {
        while (true) {
            System.out.println("\n=== USER DASHBOARD (" + currentUser.getUsername() + ") ===");
            System.out.println("1. Browse Songs");
            System.out.println("2. Browse Artists");
            System.out.println("3. Browse Albums");
            System.out.println("4. My Playlists");
            System.out.println("0. Logout");

            int choice = promptForInt("Choice: ");
            switch (choice) {
                case 1 -> songView.handleMenu(currentUser);
                case 2 -> artistView.handleMenu(currentUser);
                case 3 -> albumView.handleMenu(currentUser);
                case 4 -> playlistView.handleMenu(currentUser);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private int promptForInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }
}