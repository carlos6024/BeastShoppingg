package com.carlosapps.beastshopping.entities;

import java.util.HashMap;

public class User {
    private String email;
    private String name;
    private HashMap<String,Object> timeJoined;
    private boolean hasLoggedInWithPassword;


    public User() {
    }

    public User(String email, String name, HashMap<String, Object> dateJoined, boolean hasLoggedInWithPassword) {
        this.email = email;
        this.name = name;
        this.timeJoined = dateJoined;
        this.hasLoggedInWithPassword = hasLoggedInWithPassword;
    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getTimeJoined() {
        return timeJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }
}
