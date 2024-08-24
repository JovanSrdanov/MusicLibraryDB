package model;

import java.util.ArrayList;
import java.util.UUID;

public class Song {

    private final UUID id;
    private final String name;
    private final ArrayList<Artist> artists;
    private final ArrayList<Genre> genres;


    public Song(UUID id, String name, ArrayList<Artist> artist, ArrayList<Genre> genres) {
        this.id = id;
        this.name = name;
        this.artists = artist;
        this.genres = genres;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }
}
