package com.oibsip.todo.model;

public class User {
    private long id;
    private String name;
    private String email;
    private String passwordHash;
    private long createdAt;

    public User(long id, String name, String email, String passwordHash, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public User(String name, String email, String passwordHash) {
        this(-1, name, email, passwordHash, System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
