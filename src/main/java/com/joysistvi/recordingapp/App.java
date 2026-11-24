package com.joysistvi.recordingapp;

import com.joysistvi.recordingapp.config.DbConnection;
import com.joysistvi.recordingapp.repository.AlbumRepository;
import com.joysistvi.recordingapp.repository.AlbumRepositoryImpl;
import com.joysistvi.recordingapp.repository.ArtistRepository;
import com.joysistvi.recordingapp.repository.ArtistRepositoryImpl;
import com.joysistvi.recordingapp.repository.UserRepository;
import com.joysistvi.recordingapp.repository.UserRepositoryImpl;
import com.joysistvi.recordingapp.service.AlbumService;
import com.joysistvi.recordingapp.service.AlbumServiceImpl;
import com.joysistvi.recordingapp.service.ArtistService;
import com.joysistvi.recordingapp.service.ArtistServiceImpl;
import com.joysistvi.recordingapp.service.UserService;
import com.joysistvi.recordingapp.service.UserServiceImpl;
import com.joysistvi.recordingapp.view.AlbumView;
import com.joysistvi.recordingapp.view.ArtistView;
import com.joysistvi.recordingapp.view.UserView;

public class App {
    public static void main(String[] args) {
        DbConnection dbConnection = new DbConnection();

        // Artist wiring
        ArtistRepository artistRepository = new ArtistRepositoryImpl(dbConnection);
        ArtistService artistService = new ArtistServiceImpl(artistRepository);
        ArtistView artistView = new ArtistView(artistService);

        // Album wiring
        AlbumRepository albumRepository = new AlbumRepositoryImpl(dbConnection);
        AlbumService albumService = new AlbumServiceImpl(albumRepository);
        AlbumView albumView = new AlbumView(albumService);

        // User wiring
        UserRepository userRepository = new UserRepositoryImpl(dbConnection);
        UserService userService = new UserServiceImpl(userRepository);
        UserView userView = new UserView(userService);

        // --- Artist Operations ---
        artistView.displayAllArtists();
        artistView.promptCreateArtist();
        artistView.promptUpdateArtist();
        artistView.promptSearchArtists();
        artistView.promptArchiveArtist();
        artistView.promptRestoreArtist();
        artistView.promptDeleteArtist();

        // --- Album Operations ---
        albumView.displayAllAlbums();
        albumView.promptCreateAlbum();
        albumView.promptUpdateAlbum();
        albumView.promptSearchAlbums();
        albumView.promptArchiveAlbum();
        albumView.promptRestoreAlbum();
        albumView.promptDeleteAlbum();

        // --- User Operations ---
        userView.displayAllUsers();
        userView.promptCreateUser();
        userView.promptLoginUser();
        userView.promptUpdateUser();
        userView.promptSearchUsers();
        userView.promptArchiveUser();
        userView.promptRestoreUser();
        userView.promptDeleteUser();
    }
}