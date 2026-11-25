package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Song;
import com.joysistvi.recordingapp.service.SongService;
import com.joysistvi.recordingapp.model.User;

import java.util.List;
import java.util.Scanner;

public class SongView {
    private final SongService songService;
    private final Scanner scanner;

    public SongView(SongService songService) {
        this.songService = songService;
        this.scanner = new Scanner(System.in);
    }

    public void handleMenu(User currentUser) {
        if (!currentUser.isAdmin()) {
            handleUserMenu();
            return;
        }

        while (true) {
            System.out.println("\n=== SONG MANAGEMENT MENU (ADMIN) ===");
            System.out.println("1. View All Active");
            System.out.println("2. Create");
            System.out.println("3. Update");
            System.out.println("4. Search");
            System.out.println("5. Archive (Soft Delete)");
            System.out.println("6. Restore Archived");
            System.out.println("7. Delete (Permanent)");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllSongs();
                case 2 -> promptCreateSong();
                case 3 -> promptUpdateSong();
                case 4 -> promptSearchSongs();
                case 5 -> promptArchiveSong();
                case 6 -> promptRestoreSong();
                case 7 -> promptDeleteSong();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleUserMenu() {
        while (true) {
            System.out.println("\n=== BROWSE SONGS ===");
            System.out.println("1. View All Active Songs");
            System.out.println("2. Search Songs");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllSongs();
                case 2 -> promptSearchSongs();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayAllSongs() {
        List<Song> songs = songService.getAllActiveSongs();
        printSongTable("Active Songs", songs);
    }

    public void promptCreateSong() {
        System.out.print("\nEnter song title: ");
        String title = scanner.nextLine();
        System.out.print("Enter genre (e.g., Folk Pop, Pinoy Rock): ");
        String genre = scanner.nextLine();
        System.out.print("Enter length (e.g., 3:45): ");
        String length = scanner.nextLine();
        int albumId = promptForInt("Enter Album ID: ");

        if (songService.createSong(title, genre, length, albumId)) {
            System.out.println("Song created successfully!");
            displayAllSongs();
        }
    }

    public void promptUpdateSong() {
        displayAllSongs();
        int id = promptForInt("\nEnter Song ID to update: ");
        System.out.print("Enter new song title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter new length (e.g., 3:45): ");
        String length = scanner.nextLine();
        int albumId = promptForInt("Enter new Album ID: ");

        if (songService.updateSong(id, title, genre, length, albumId)) {
            System.out.println("Song ID " + id + " updated successfully!");
            displayAllSongs();
        }
    }

    public void promptSearchSongs() {
        System.out.print("\nEnter song title keyword: ");
        String keyword = scanner.nextLine();

        List<Song> results = songService.searchSongs(keyword);
        if (!results.isEmpty()) {
            printSongTable("Search Results", results);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("No active songs found matching \"" + keyword.trim() + "\".");
        }
    }

    public void promptArchiveSong() {
        displayAllSongs();
        int id = promptForInt("\nEnter Song ID to archive: ");

        if (songService.archiveSong(id)) {
            System.out.println("Song ID " + id + " archived successfully!");
            displayAllSongs();
        }
    }

    public void promptRestoreSong() {
        List<Song> archivedSongs = songService.getAllArchivedSongs();
        if (archivedSongs.isEmpty()) {
            System.out.println("\nNo archived songs available to restore.");
            return;
        }

        printSongTable("Archived Songs", archivedSongs);
        int id = promptForInt("\nEnter Archived Song ID to restore: ");

        if (songService.restoreSong(id)) {
            System.out.println("Song ID " + id + " restored successfully!");
            displayAllSongs();
        }
    }

    public void promptDeleteSong() {
        displayAllSongs();
        int id = promptForInt("\nEnter Song ID to delete permanently: ");

        if (songService.deleteSong(id)) {
            System.out.println("Song ID " + id + " deleted successfully!");
            displayAllSongs();
        }
    }

    private void printSongTable(String title, List<Song> songs) {
        System.out.println("\n----- " + title + " -----");
        if (songs.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-24s %-15s %-8s %-20s%n", "ID", "Title", "Genre", "Length", "Album");
        for (Song song : songs) {
            System.out.printf("%-4d %-24s %-15s %-8s %-20s%n",
                    song.getId(),
                    song.getTitle(),
                    song.getGenre(),
                    song.getLength(),
                    song.getAlbumName());
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