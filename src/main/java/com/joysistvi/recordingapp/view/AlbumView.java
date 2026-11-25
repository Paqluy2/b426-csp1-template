package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Album;
import com.joysistvi.recordingapp.service.AlbumService;
import com.joysistvi.recordingapp.model.User;

import java.util.List;
import java.util.Scanner;

public class AlbumView {
    private final AlbumService albumService;
    private final Scanner scanner;

    public AlbumView(AlbumService albumService) {
        this.albumService = albumService;
        this.scanner = new Scanner(System.in);
    }

    public void handleMenu(User currentUser) {
        if (!currentUser.isAdmin()) {
            handleUserMenu();
            return;
        }

        while (true) {
            System.out.println("\n=== ALBUM MANAGEMENT MENU (ADMIN) ===");
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
                case 1 -> displayAllAlbums();
                case 2 -> promptCreateAlbum();
                case 3 -> promptUpdateAlbum();
                case 4 -> promptSearchAlbums();
                case 5 -> promptArchiveAlbum();
                case 6 -> promptRestoreAlbum();
                case 7 -> promptDeleteAlbum();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleUserMenu() {
        while (true) {
            System.out.println("\n=== BROWSE ALBUMS ===");
            System.out.println("1. View All Active Albums");
            System.out.println("2. Search Albums");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllAlbums();
                case 2 -> promptSearchAlbums();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayAllAlbums() {
        List<Album> albums = albumService.getAllActiveAlbums();
        printAlbumTable("Active Albums", albums);
    }

    public void promptCreateAlbum() {
        System.out.print("\nEnter album name: ");
        String name = scanner.nextLine();
        int year = promptForInt("Enter release year (1900-2100): ");
        int artistId = promptForInt("Enter Artist ID: ");

        if (albumService.createAlbum(name, year, artistId)) {
            System.out.println("Album created successfully!");
            displayAllAlbums();
        }
    }

    public void promptUpdateAlbum() {
        displayAllAlbums();
        int id = promptForInt("\nEnter Album ID to update: ");
        System.out.print("Enter new album name: ");
        String name = scanner.nextLine();
        int year = promptForInt("Enter new release year (1900-2100): ");
        int artistId = promptForInt("Enter new Artist ID: ");

        if (albumService.updateAlbum(id, name, year, artistId)) {
            System.out.println("Album ID " + id + " updated successfully!");
            displayAllAlbums();
        }
    }

    public void promptSearchAlbums() {
        System.out.print("\nEnter album search keyword: ");
        String keyword = scanner.nextLine();

        List<Album> results = albumService.searchAlbums(keyword);
        if (!results.isEmpty()) {
            printAlbumTable("Search Results", results);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("No active albums found matching \"" + keyword.trim() + "\".");
        }
    }

    public void promptArchiveAlbum() {
        displayAllAlbums();
        int id = promptForInt("\nEnter Album ID to archive: ");

        if (albumService.archiveAlbum(id)) {
            System.out.println("Album ID " + id + " archived successfully!");
            displayAllAlbums();
        }
    }

    public void promptRestoreAlbum() {
        List<Album> archivedAlbums = albumService.getAllArchivedAlbums();
        if (archivedAlbums.isEmpty()) {
            System.out.println("\nNo archived albums available to restore.");
            return;
        }

        printAlbumTable("Archived Albums", archivedAlbums);
        int id = promptForInt("\nEnter Archived Album ID to restore: ");

        if (albumService.restoreAlbum(id)) {
            System.out.println("Album ID " + id + " restored successfully!");
            displayAllAlbums();
        }
    }

    public void promptDeleteAlbum() {
        displayAllAlbums();
        int id = promptForInt("\nEnter Album ID to delete permanently: ");

        if (albumService.deleteAlbum(id)) {
            System.out.println("Album ID " + id + " deleted successfully!");
            displayAllAlbums();
        }
    }

    private void printAlbumTable(String title, List<Album> albums) {
        System.out.println("\n----- " + title + " -----");
        if (albums.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-22s %-6s %-22s%n", "ID", "Album Name", "Year", "Artist");
        for (Album album : albums) {
            System.out.printf("%-4d %-22s %-6d %-22s%n",
                    album.getId(),
                    album.getName(),
                    album.getYear(),
                    album.getArtistName());
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