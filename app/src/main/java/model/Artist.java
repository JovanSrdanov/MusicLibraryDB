package model;

import java.util.ArrayList;
import java.util.UUID;

public class Artist {

    private final UUID id;
    private final String name;
    private final ArrayList<Genre> Genres;

    public Artist(UUID id, String name, ArrayList<Genre> Genres) {
        this.id = id;
        this.name = name;
        this.Genres = Genres;
    }

    public ArrayList<Genre> getGenres() {
        return Genres;
    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}
