package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Artist;
import com.joysistvi.recordingapp.service.ArtistService;
import com.joysistvi.recordingapp.model.User;

import java.util.List;
import java.util.Scanner;

public class ArtistView {
    private final ArtistService artistService;
    private final Scanner scanner;

    public ArtistView(ArtistService artistService) {
        this.artistService = artistService;
        this.scanner = new Scanner(System.in);
    }

    public void handleMenu(User currentUser) {
        if (!currentUser.isAdmin()) {
            handleUserMenu();
            return;
        }

        while (true) {
            System.out.println("\n=== ARTIST MANAGEMENT MENU (ADMIN) ===");
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
                case 1 -> displayAllArtists();
                case 2 -> promptCreateArtist();
                case 3 -> promptUpdateArtist();
                case 4 -> promptSearchArtists();
                case 5 -> promptArchiveArtist();
                case 6 -> promptRestoreArtist();
                case 7 -> promptDeleteArtist();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleUserMenu() {
        while (true) {
            System.out.println("\n=== BROWSE ARTISTS ===");
            System.out.println("1. View All Active Artists");
            System.out.println("2. Search Artists");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllArtists();
                case 2 -> promptSearchArtists();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayAllArtists() {
        List<Artist> artists = artistService.getAllActiveArtists();
        printArtistTable("Active Artists", artists);
    }

    public void promptCreateArtist() {
        System.out.print("\nEnter artist name: ");
        String name = scanner.nextLine();

        if (artistService.createArtist(name)) {
            System.out.println("Artist created successfully!");
            displayAllArtists();
        }
    }

    public void promptUpdateArtist() {
        displayAllArtists();
        int id = promptForInt("\nEnter Artist ID to update: ");
        System.out.print("Enter new artist name: ");
        String name = scanner.nextLine();

        if (artistService.updateArtist(id, name)) {
            System.out.println("Artist ID " + id + " updated successfully!");
            displayAllArtists();
        }
    }

    public void promptSearchArtists() {
        System.out.print("\nEnter artist search keyword: ");
        String keyword = scanner.nextLine();

        List<Artist> results = artistService.searchArtists(keyword);
        if (!results.isEmpty()) {
            printArtistTable("Search Results", results);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("No active artists found matching \"" + keyword.trim() + "\".");
        }
    }

    public void promptArchiveArtist() {
        displayAllArtists();
        int id = promptForInt("\nEnter Artist ID to archive: ");

        if (artistService.archiveArtist(id)) {
            System.out.println("Artist ID " + id + " archived successfully!");
            displayAllArtists();
        }
    }

    public void promptRestoreArtist() {
        List<Artist> archivedArtists = artistService.getAllArchivedArtists();
        if (archivedArtists.isEmpty()) {
            System.out.println("\nNo archived artists available to restore.");
            return;
        }

        printArtistTable("Archived Artists", archivedArtists);
        int id = promptForInt("\nEnter Archived Artist ID to restore: ");

        if (artistService.restoreArtist(id)) {
            System.out.println("Artist ID " + id + " restored successfully!");
            displayAllArtists();
        }
    }

    public void promptDeleteArtist() {
        displayAllArtists();
        int id = promptForInt("\nEnter Artist ID to delete permanently: ");

        if (artistService.deleteArtist(id)) {
            System.out.println("Artist ID " + id + " deleted successfully!");
            displayAllArtists();
        }
    }

    private void printArtistTable(String title, List<Artist> artists) {
        System.out.println("\n----- " + title + " -----");
        if (artists.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-25s%n", "ID", "Artist Name");
        for (Artist artist : artists) {
            System.out.printf("%-4d %-25s%n", artist.getId(), artist.getName());
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