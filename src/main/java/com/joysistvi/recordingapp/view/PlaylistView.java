package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.Playlist;
import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.service.PlaylistService;
import com.joysistvi.recordingapp.model.Song;
import com.joysistvi.recordingapp.service.SongService;

import java.util.List;
import java.util.Scanner;

public class PlaylistView {
    private final PlaylistService playlistService;
    private final SongService songService;
    private final Scanner scanner;

    public PlaylistView(PlaylistService playlistService, SongService songService) {
        this.playlistService = playlistService;
        this.songService = songService;
        this.scanner = new Scanner(System.in);
    }

    public void handleMenu(User currentUser) {
        while (true) {
            String header = currentUser.isAdmin() ? "PLAYLIST MANAGEMENT (ADMIN)" : "MY PLAYLISTS";
            System.out.println("\n=== " + header + " ===");
            System.out.println("1. View All Active");
            System.out.println("2. Create");
            System.out.println("3. Update");
            System.out.println("4. Search");
            System.out.println("5. Archive (Soft Delete)");
            System.out.println("6. Restore Archived");
            System.out.println("7. Delete (Permanent)");
            System.out.println("8. View Songs in Playlist");
            System.out.println("9. Add Song to Playlist");
            System.out.println("10. Remove Song from Playlist");
            System.out.println("0. Back");

            int choice = promptForInt("Choose an option: ");
            switch (choice) {
                case 1 -> displayAllPlaylists(currentUser);
                case 2 -> promptCreatePlaylist(currentUser);
                case 3 -> promptUpdatePlaylist(currentUser);
                case 4 -> promptSearchPlaylists(currentUser);
                case 5 -> promptArchivePlaylist(currentUser);
                case 6 -> promptRestorePlaylist(currentUser);
                case 7 -> promptDeletePlaylist(currentUser);
                case 8 -> promptViewPlaylistSongs(currentUser);
                case 9 -> promptAddSongToPlaylist(currentUser);
                case 10 -> promptRemoveSongFromPlaylist(currentUser);
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void displayAllPlaylists(User currentUser) {
        List<Playlist> playlists = playlistService.getAllActivePlaylists(currentUser);
        String title = currentUser.isAdmin() ? "Active Playlists" : "My Active Playlists";
        printPlaylistTable(title, playlists);
    }

    public void promptCreatePlaylist(User currentUser) {
        System.out.print("\nEnter playlist name: ");
        String name = scanner.nextLine();
        int userId = currentUser.isAdmin()
                ? promptForInt("Enter Owner User ID: ")
                : currentUser.getId();

        if (playlistService.createPlaylist(name, userId)) {
            System.out.println("Playlist created successfully!");
            displayAllPlaylists(currentUser);
        }
    }

    public void promptUpdatePlaylist(User currentUser) {
        displayAllPlaylists(currentUser);
        int id = promptForInt("\nEnter Playlist ID to update: ");
        System.out.print("Enter new playlist name: ");
        String name = scanner.nextLine();
        int userId = currentUser.isAdmin()
                ? promptForInt("Enter new Owner User ID: ")
                : currentUser.getId();

        if (playlistService.updatePlaylist(id, name, userId, currentUser)) {
            System.out.println("Playlist ID " + id + " updated successfully!");
            displayAllPlaylists(currentUser);
        }
    }

    public void promptSearchPlaylists(User currentUser) {
        System.out.print("\nEnter playlist name keyword: ");
        String keyword = scanner.nextLine();

        List<Playlist> results = playlistService.searchPlaylists(keyword, currentUser);
        if (!results.isEmpty()) {
            printPlaylistTable("Search Results", results);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            System.out.println("No active playlists found matching \"" + keyword.trim() + "\".");
        }
    }

    public void promptArchivePlaylist(User currentUser) {
        displayAllPlaylists(currentUser);
        int id = promptForInt("\nEnter Playlist ID to archive: ");

        if (playlistService.archivePlaylist(id, currentUser)) {
            System.out.println("Playlist ID " + id + " archived successfully!");
            displayAllPlaylists(currentUser);
        }
    }

    public void promptRestorePlaylist(User currentUser) {
        List<Playlist> archivedPlaylists = playlistService.getAllArchivedPlaylists(currentUser);
        if (archivedPlaylists.isEmpty()) {
            System.out.println("\nNo archived playlists available to restore.");
            return;
        }

        printPlaylistTable("Archived Playlists", archivedPlaylists);
        int id = promptForInt("\nEnter Archived Playlist ID to restore: ");

        if (playlistService.restorePlaylist(id, currentUser)) {
            System.out.println("Playlist ID " + id + " restored successfully!");
            displayAllPlaylists(currentUser);
        }
    }

    public void promptDeletePlaylist(User currentUser) {
        displayAllPlaylists(currentUser);
        int id = promptForInt("\nEnter Playlist ID to delete permanently: ");

        if (playlistService.deletePlaylist(id, currentUser)) {
            System.out.println("Playlist ID " + id + " deleted successfully!");
            displayAllPlaylists(currentUser);
        }
    }

    private void printPlaylistTable(String title, List<Playlist> playlists) {
        System.out.println("\n----- " + title + " -----");
        if (playlists.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-4s %-24s %-18s %-20s%n", "ID", "Playlist Name", "Owner", "Date Created");
        for (Playlist playlist : playlists) {
            System.out.printf("%-4d %-24s %-18s %-20s%n",
                    playlist.getId(),
                    playlist.getName(),
                    playlist.getUsername(),
                    playlist.getDateCreated());
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

    public void promptViewPlaylistSongs(User currentUser) {
        displayAllPlaylists(currentUser);
        int playlistId = promptForInt("\nEnter Playlist ID to view songs: ");

        List<Song> songs = playlistService.getSongsInPlaylist(playlistId, currentUser);
        printSongTable("Songs in Playlist #" + playlistId, songs);
    }

    public void promptAddSongToPlaylist(User currentUser) {
        displayAllPlaylists(currentUser);
        int playlistId = promptForInt("\nEnter Playlist ID: ");

        List<Song> activeSongs = songService.getAllActiveSongs();
        printSongTable("Available Songs", activeSongs);
        if (activeSongs.isEmpty()) {
            return;
        }

        int songId = promptForInt("\nEnter Song ID to add: ");
        if (playlistService.addSongToPlaylist(playlistId, songId, currentUser)) {
            System.out.println("Song added to playlist successfully!");
            printSongTable("Songs in Playlist #" + playlistId,
                    playlistService.getSongsInPlaylist(playlistId, currentUser));
        }
    }

    public void promptRemoveSongFromPlaylist(User currentUser) {
        displayAllPlaylists(currentUser);
        int playlistId = promptForInt("\nEnter Playlist ID: ");

        List<Song> songs = playlistService.getSongsInPlaylist(playlistId, currentUser);
        printSongTable("Songs in Playlist #" + playlistId, songs);
        if (songs.isEmpty()) {
            return;
        }

        int songId = promptForInt("\nEnter Song ID to remove: ");
        if (playlistService.removeSongFromPlaylist(playlistId, songId, currentUser)) {
            System.out.println("Song removed from playlist successfully!");
        }
    }

    private void printSongTable(String title, List<Song> songs) {
        System.out.println("\n----- " + title + " -----");
        if (songs.isEmpty()) {
            System.out.println("No songs found.");
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
}