package com.carlosapps.beastshopping.live;

import android.widget.Toast;

import com.carlosapps.beastshopping.entities.Users;
import com.carlosapps.beastshopping.entities.UsersSharedWith;
import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.carlosapps.beastshopping.services.GetUsersService;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

public class LiveUsersService extends BaseLiveService {

    public LiveUsersService(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void getUsersFriends(GetUsersService.GetUserFriendsRequest request){
        final GetUsersService.GetUsersFrendsResponse response = new GetUsersService.GetUsersFrendsResponse();

        response.listener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                response.usersFriends = dataSnapshot.getValue(Users.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(application.getApplicationContext(),firebaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Subscribe
    public void getSharedWith(GetUsersService.GetSharedWithRequest request){
        final GetUsersService.GetSharedWithResponse response = new GetUsersService.GetSharedWithResponse();
        response.listener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                response.usersSharedWith = dataSnapshot.getValue(UsersSharedWith.class);
                bus.post(response);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(application.getApplicationContext(),firebaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}
