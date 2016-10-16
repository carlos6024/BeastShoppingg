package com.carlosapps.beastshopping.entities;

import android.support.annotation.Nullable;

import java.util.HashMap;

public class UsersSharedWith {
    @Nullable
    private HashMap<String,User> sharedWith;

    public UsersSharedWith() {
    }

    @Nullable
    public HashMap<String, User> getSharedWith() {
        return sharedWith;
    }
}
