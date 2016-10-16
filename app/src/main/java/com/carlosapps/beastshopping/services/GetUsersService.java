package com.carlosapps.beastshopping.services;

import com.carlosapps.beastshopping.entities.Users;
import com.carlosapps.beastshopping.entities.UsersSharedWith;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class GetUsersService {
    private GetUsersService() {
    }


    public static class GetUserFriendsRequest{
        public Firebase reference;

        public GetUserFriendsRequest(Firebase reference) {
            this.reference = reference;
        }
    }

    public static class GetUsersFrendsResponse{
        public ValueEventListener listener;
        public Users usersFriends;
    }


    public static class GetSharedWithRequest{
        public Firebase reference;

        public GetSharedWithRequest(Firebase reference) {
            this.reference = reference;
        }
    }

    public static class GetSharedWithResponse{
        public ValueEventListener listener;
        public UsersSharedWith usersSharedWith;
    }
}
