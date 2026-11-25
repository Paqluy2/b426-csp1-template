package com.joysistvi.recordingapp.model;

import java.sql.Timestamp;

public class Playlist {
    private int id;
    private String name;
    private Timestamp dateCreated;
    private int userId;
    private String username;
    private boolean archived;

    public Playlist() {
    }

    public Playlist(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    public Playlist(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public Playlist(int id, String name, Timestamp dateCreated, int userId, String username, boolean archived) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.userId = userId;
        this.username = username;
        this.archived = archived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}