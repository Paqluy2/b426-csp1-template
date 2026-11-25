package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.Playlist;
import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.repository.PlaylistRepository;
import com.joysistvi.recordingapp.model.Song;

import java.util.Collections;
import java.util.List;

public class PlaylistServiceImpl implements PlaylistService {
    private final PlaylistRepository playlistRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Playlist> getAllActivePlaylists(User currentUser) {
        if (currentUser.isAdmin()) {
            return playlistRepository.findAllActive();
        }
        return playlistRepository.findAllActiveByUserId(currentUser.getId());
    }

    @Override
    public List<Playlist> getAllArchivedPlaylists(User currentUser) {
        if (currentUser.isAdmin()) {
            return playlistRepository.findAllArchived();
        }
        return playlistRepository.findAllArchivedByUserId(currentUser.getId());
    }

    @Override
    public boolean createPlaylist(String name, int userId) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Playlist name is required.");
            return false;
        }
        if (userId <= 0) {
            System.out.println("\nValidation Error: User ID must be greater than 0.");
            return false;
        }
        return playlistRepository.save(new Playlist(name.trim(), userId));
    }

    @Override
    public boolean updatePlaylist(int id, String name, int userId, User currentUser) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            System.out.println("\nValidation Error: Playlist name is required.");
            return false;
        }
        if (userId <= 0) {
            System.out.println("\nValidation Error: User ID must be greater than 0.");
            return false;
        }

        boolean updated = currentUser.isAdmin()
                ? playlistRepository.update(new Playlist(id, name.trim(), userId))
                : playlistRepository.updateByUserId(new Playlist(id, name.trim(), currentUser.getId()));

        if (!updated) {
            System.out.println("No active playlist found with ID " + id + " (or you do not own it).");
        }
        return updated;
    }

    @Override
    public boolean deletePlaylist(int id, User currentUser) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        boolean deleted = currentUser.isAdmin()
                ? playlistRepository.deleteById(id)
                : playlistRepository.deleteByIdAndUserId(id, currentUser.getId());

        if (!deleted) {
            System.out.println("Could not delete Playlist ID " + id + " (not found, not owned by you, or still contains songs).");
        }
        return deleted;
    }

    @Override
    public boolean archivePlaylist(int id, User currentUser) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        boolean archived = currentUser.isAdmin()
                ? playlistRepository.archiveById(id)
                : playlistRepository.archiveByIdAndUserId(id, currentUser.getId());

        if (!archived) {
            System.out.println("No active playlist found with ID " + id + " (or you do not own it).");
        }
        return archived;
    }

    @Override
    public boolean restorePlaylist(int id, User currentUser) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        boolean restored = currentUser.isAdmin()
                ? playlistRepository.restoreById(id)
                : playlistRepository.restoreByIdAndUserId(id, currentUser.getId());

        if (!restored) {
            System.out.println("No archived playlist found with ID " + id + " (or you do not own it).");
        }
        return restored;
    }

    @Override
    public List<Playlist> searchPlaylists(String keyword, User currentUser) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return Collections.emptyList();
        }
        if (currentUser.isAdmin()) {
            return playlistRepository.searchByName(keyword.trim());
        }
        return playlistRepository.searchByNameAndUserId(keyword.trim(), currentUser.getId());
    }
    @Override
    public boolean addSongToPlaylist(int playlistId, int songId, User currentUser) {
        if (playlistId <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        if (songId <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        if (!canAccessPlaylist(playlistId, currentUser)) {
            System.out.println("\nAccess Denied: Playlist not found or you do not own it.");
            return false;
        }
        return playlistRepository.addSongToPlaylist(playlistId, songId);
    }

    @Override
    public boolean removeSongFromPlaylist(int playlistId, int songId, User currentUser) {
        if (playlistId <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return false;
        }
        if (songId <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        if (!canAccessPlaylist(playlistId, currentUser)) {
            System.out.println("\nAccess Denied: Playlist not found or you do not own it.");
            return false;
        }
        boolean removed = playlistRepository.removeSongFromPlaylist(playlistId, songId);
        if (!removed) {
            System.out.println("Song ID " + songId + " was not found in Playlist ID " + playlistId + ".");
        }
        return removed;
    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId, User currentUser) {
        if (playlistId <= 0) {
            System.out.println("\nValidation Error: Playlist ID must be greater than 0.");
            return Collections.emptyList();
        }
        if (!canAccessPlaylist(playlistId, currentUser)) {
            System.out.println("\nAccess Denied: Playlist not found or you do not own it.");
            return Collections.emptyList();
        }
        return playlistRepository.findSongsByPlaylistId(playlistId);
    }

    private boolean canAccessPlaylist(int playlistId, User currentUser) {
        if (currentUser.isAdmin()) {
            return playlistRepository.existsActiveById(playlistId);
        }
        return playlistRepository.existsActiveByIdAndUserId(playlistId, currentUser.getId());
    }
}