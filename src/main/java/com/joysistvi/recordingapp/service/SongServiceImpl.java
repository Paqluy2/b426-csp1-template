package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.Song;
import com.joysistvi.recordingapp.repository.SongRepository;

import java.util.Collections;
import java.util.List;

public class SongServiceImpl implements SongService {
    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> getAllActiveSongs() {
        return songRepository.findAllActive();
    }

    @Override
    public List<Song> getAllArchivedSongs() {
        return songRepository.findAllArchived();
    }

    @Override
    public boolean createSong(String title, String genre, String length, int albumId) {
        if (title == null || title.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song title is required.");
            return false;
        }
        if (genre == null || genre.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song genre is required.");
            return false;
        }
        if (length == null || length.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song length is required (e.g., 3:45).");
            return false;
        }
        if (albumId <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return false;
        }
        return songRepository.save(new Song(title.trim(), genre.trim(), length.trim(), albumId));
    }

    @Override
    public boolean updateSong(int id, String title, String genre, String length, int albumId) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        if (title == null || title.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song title is required.");
            return false;
        }
        if (genre == null || genre.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song genre is required.");
            return false;
        }
        if (length == null || length.trim().isEmpty()) {
            System.out.println("\nValidation Error: Song length is required (e.g., 3:45).");
            return false;
        }
        if (albumId <= 0) {
            System.out.println("\nValidation Error: Album ID must be greater than 0.");
            return false;
        }

        boolean updated = songRepository.update(new Song(id, title.trim(), genre.trim(), length.trim(), albumId));
        if (!updated) {
            System.out.println("No active song found with ID " + id + ".");
        }
        return updated;
    }

    @Override
    public boolean deleteSong(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        boolean deleted = songRepository.deleteById(id);
        if (!deleted) {
            System.out.println("Could not delete Song ID " + id + " (not found or linked to a playlist).");
        }
        return deleted;
    }

    @Override
    public boolean archiveSong(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        boolean archived = songRepository.archiveById(id);
        if (!archived) {
            System.out.println("No active song found with ID " + id + ".");
        }
        return archived;
    }

    @Override
    public boolean restoreSong(int id) {
        if (id <= 0) {
            System.out.println("\nValidation Error: Song ID must be greater than 0.");
            return false;
        }
        boolean restored = songRepository.restoreById(id);
        if (!restored) {
            System.out.println("No archived song found with ID " + id + ".");
        }
        return restored;
    }

    @Override
    public List<Song> searchSongs(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("\nValidation Error: Search keyword is required.");
            return Collections.emptyList();
        }
        return songRepository.searchByTitle(keyword.trim());
    }
}