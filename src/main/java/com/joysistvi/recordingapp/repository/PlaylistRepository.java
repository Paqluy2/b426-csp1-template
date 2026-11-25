package com.joysistvi.recordingapp.repository;

import com.joysistvi.recordingapp.model.Playlist;
import com.joysistvi.recordingapp.model.Song;

import java.util.List;

public interface PlaylistRepository {
    List<Playlist> findAllActive();
    List<Playlist> findAllActiveByUserId(int userId);
    List<Playlist> findAllArchived();
    List<Playlist> findAllArchivedByUserId(int userId);
    boolean save(Playlist playlist);
    boolean update(Playlist playlist);
    boolean updateByUserId(Playlist playlist);
    boolean deleteById(int id);
    boolean deleteByIdAndUserId(int id, int userId);
    boolean archiveById(int id);
    boolean archiveByIdAndUserId(int id, int userId);
    boolean restoreById(int id);
    boolean restoreByIdAndUserId(int id, int userId);
    boolean existsActiveById(int playlistId);
    boolean existsActiveByIdAndUserId(int playlistId, int userId);
    boolean addSongToPlaylist(int playlistId, int songId);
    boolean removeSongFromPlaylist(int playlistId, int songId);
    List<Song> findSongsByPlaylistId(int playlistId);
    List<Playlist> searchByName(String keyword);
    List<Playlist> searchByNameAndUserId(String keyword, int userId);
}