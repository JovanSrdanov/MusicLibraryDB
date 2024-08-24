package model;

import java.util.UUID;

public class Genre {
    private final UUID id;
    private final String name;

    public Genre(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
