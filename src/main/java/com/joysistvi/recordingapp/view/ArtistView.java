package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Artist;
import com.joysistvi.recordingapp.service.ArtistService;

import java.util.List;
import java.util.Scanner;

public class ArtistView {
    private final ArtistService artistService;
    private final Scanner scanner;

    public ArtistView(ArtistService artistService) {
        this.artistService = artistService;
        this.scanner = new Scanner(System.in);
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
        Integer id = readInt("\nEnter Artist ID to update: ");
        if (id == null) {
            return;
        }
        System.out.print("Enter new artist name: ");
        String name = scanner.nextLine();

        if (artistService.updateArtist(id, name)) {
            System.out.println("Artist ID " + id + " updated successfully!");
            displayAllArtists();
        } else if (id > 0 && name != null && !name.trim().isEmpty()) {
            System.out.println("No active artist found with ID " + id + ".");
        }
    }

    public void promptDeleteArtist() {
        displayAllArtists();
        Integer id = readInt("\nEnter Artist ID to delete permanently: ");
        if (id == null) {
            return;
        }

        if (artistService.deleteArtist(id)) {
            System.out.println("Artist ID " + id + " deleted successfully!");
            displayAllArtists();
        } else if (id > 0) {
            System.out.println("Could not delete Artist ID " + id + " (not found or referenced by albums).");
        }
    }

    public void promptArchiveArtist() {
        displayAllArtists();
        Integer id = readInt("\nEnter Artist ID to archive: ");
        if (id == null) {
            return;
        }

        if (artistService.archiveArtist(id)) {
            System.out.println("Artist ID " + id + " archived successfully!");
            displayAllArtists();
        } else if (id > 0) {
            System.out.println("No active artist found with ID " + id + ".");
        }
    }

    public void promptRestoreArtist() {
        List<Artist> archivedArtists = artistService.getAllArchivedArtists();
        if (archivedArtists.isEmpty()) {
            System.out.println("\nNo archived artists available to restore.");
            return;
        }

        printArtistTable("Archived Artists", archivedArtists);
        Integer id = readInt("\nEnter Archived Artist ID to restore: ");
        if (id == null) {
            return;
        }

        if (artistService.restoreArtist(id)) {
            System.out.println("Artist ID " + id + " restored successfully!");
            displayAllArtists();
        } else if (id > 0) {
            System.out.println("No archived artist found with ID " + id + ".");
        }
    }

    public void promptSearchArtists() {
        System.out.print("\nEnter artist search keyword: ");
        String keyword = scanner.nextLine();

        List<Artist> results = artistService.searchArtists(keyword);
        if (keyword != null && !keyword.trim().isEmpty()) {
            printArtistTable("Search Results: \"" + keyword.trim() + "\"", results);
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

    private Integer readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: Please enter a valid integer.");
            return null;
        }
    }
}