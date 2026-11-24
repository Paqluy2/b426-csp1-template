package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Album;
import com.joysistvi.recordingapp.service.AlbumService;

import java.util.List;
import java.util.Scanner;

public class AlbumView {
    private final AlbumService albumService;
    private final Scanner scanner;

    public AlbumView(AlbumService albumService) {
        this.albumService = albumService;
        this.scanner = new Scanner(System.in);
    }

    public void displayAllAlbums() {
        List<Album> albums = albumService.getAllActiveAlbums();
        printAlbumTable("Active Albums", albums);
    }

    public void promptCreateAlbum() {
        System.out.print("\nEnter album name: ");
        String name = scanner.nextLine();
        Integer year = readInt("Enter release year (1900-2100): ");
        if (year == null) {
            return;
        }
        Integer artistId = readInt("Enter Artist ID: ");
        if (artistId == null) {
            return;
        }

        if (albumService.createAlbum(name, year, artistId)) {
            System.out.println("Album created successfully!");
            displayAllAlbums();
        }
    }

    public void promptUpdateAlbum() {
        displayAllAlbums();
        Integer id = readInt("\nEnter Album ID to update: ");
        if (id == null) {
            return;
        }
        System.out.print("Enter new album name: ");
        String name = scanner.nextLine();
        Integer year = readInt("Enter new release year (1900-2100): ");
        if (year == null) {
            return;
        }
        Integer artistId = readInt("Enter new Artist ID: ");
        if (artistId == null) {
            return;
        }

        if (albumService.updateAlbum(id, name, year, artistId)) {
            System.out.println("Album ID " + id + " updated successfully!");
            displayAllAlbums();
        } else if (id > 0 && name != null && !name.trim().isEmpty()
                && year >= 1900 && year <= 2100 && artistId > 0) {
            System.out.println("No active album found with ID " + id + ".");
        }
    }

    public void promptDeleteAlbum() {
        displayAllAlbums();
        Integer id = readInt("\nEnter Album ID to delete permanently: ");
        if (id == null) {
            return;
        }

        if (albumService.deleteAlbum(id)) {
            System.out.println("Album ID " + id + " deleted successfully!");
            displayAllAlbums();
        } else if (id > 0) {
            System.out.println("Could not delete Album ID " + id + " (not found or referenced by songs).");
        }
    }

    public void promptArchiveAlbum() {
        displayAllAlbums();
        Integer id = readInt("\nEnter Album ID to archive: ");
        if (id == null) {
            return;
        }

        if (albumService.archiveAlbum(id)) {
            System.out.println("Album ID " + id + " archived successfully!");
            displayAllAlbums();
        } else if (id > 0) {
            System.out.println("No active album found with ID " + id + ".");
        }
    }

    public void promptRestoreAlbum() {
        List<Album> archivedAlbums = albumService.getAllArchivedAlbums();
        if (archivedAlbums.isEmpty()) {
            System.out.println("\nNo archived albums available to restore.");
            return;
        }

        printAlbumTable("Archived Albums", archivedAlbums);
        Integer id = readInt("\nEnter Archived Album ID to restore: ");
        if (id == null) {
            return;
        }

        if (albumService.restoreAlbum(id)) {
            System.out.println("Album ID " + id + " restored successfully!");
            displayAllAlbums();
        } else if (id > 0) {
            System.out.println("No archived album found with ID " + id + ".");
        }
    }

    public void promptSearchAlbums() {
        System.out.print("\nEnter album search keyword: ");
        String keyword = scanner.nextLine();

        List<Album> results = albumService.searchAlbums(keyword);
        if (keyword != null && !keyword.trim().isEmpty()) {
            printAlbumTable("Search Results: \"" + keyword.trim() + "\"", results);
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