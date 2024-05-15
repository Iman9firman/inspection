package com.garasi.kita.inspection.RTP.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String name;
    private String detail;
    private String role;
    private boolean enabled;

    // Constructors
    public User() {
    }

    public User(String username, String password, String name, String detail, String role, boolean enabled) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.detail = detail;
        this.role = role;
        this.enabled = enabled;
    }

    // Getters and Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
