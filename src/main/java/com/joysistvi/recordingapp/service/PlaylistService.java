package com.joysistvi.recordingapp.service;

import com.joysistvi.recordingapp.model.Playlist;
import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.model.Song;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getAllActivePlaylists(User currentUser);
    List<Playlist> getAllArchivedPlaylists(User currentUser);
    boolean createPlaylist(String name, int userId);
    boolean updatePlaylist(int id, String name, int userId, User currentUser);
    boolean deletePlaylist(int id, User currentUser);
    boolean archivePlaylist(int id, User currentUser);
    boolean restorePlaylist(int id, User currentUser);
    boolean addSongToPlaylist(int playlistId, int songId, User currentUser);
    boolean removeSongFromPlaylist(int playlistId, int songId, User currentUser);
    List<Song> getSongsInPlaylist(int playlistId, User currentUser);
    List<Playlist> searchPlaylists(String keyword, User currentUser);
}