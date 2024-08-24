package model;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String username;
    private final String password;

    public User(UUID id, String name, String password) {
        this.id = id;
        this.username = name;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
