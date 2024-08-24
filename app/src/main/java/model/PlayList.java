package model;

import java.util.ArrayList;
import java.util.UUID;

public class PlayList {
    private final UUID id;
    private final User user;
    private final String name;
    private final ArrayList<Song> songs;

    public PlayList(UUID id, String name, User user, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.songs = songs;
    }

    public User getUser() {
        return user;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

}
