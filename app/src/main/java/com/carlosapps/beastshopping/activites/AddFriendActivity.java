package com.carlosapps.beastshopping.activites;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.User;
import com.carlosapps.beastshopping.entities.Users;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.GetUsersService;
import com.carlosapps.beastshopping.views.AddFriendView.AddFriendViewHolder;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

public class AddFriendActivity extends BaseActivity {
    private FirebaseRecyclerAdapter adapter;

    private Firebase friendsReference;

    private ValueEventListener listener;


    private Users currentUserFriends;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_add_friend_list_recyclerView);

        friendsReference = new Firebase(Utils.FIRE_BASE_USER_FRIEND_REFERENCE + userEmail);

        bus.post(new GetUsersService.GetUserFriendsRequest(friendsReference));


        Firebase usersReference = new Firebase(Utils.FIRE_BASE_USER_REFERENCE);

        adapter = new FirebaseRecyclerAdapter<User, AddFriendViewHolder>(
               User.class,
               R.layout.list_user,
               AddFriendViewHolder.class,
               usersReference) {
           @Override
           protected void populateViewHolder(final AddFriendViewHolder addFriendViewHolder, final User user, int i) {
               addFriendViewHolder.populate(user);



               if (currentUserFriends.getUsersFriends()!=null){
                   if (isFriend(currentUserFriends.getUsersFriends(),user)){
                       addFriendViewHolder.userItemView.setImageResource(R.mipmap.ic_check);
                   } else{
                       addFriendViewHolder.userItemView.setImageResource(R.mipmap.ic_plus);
                   }
               }

               addFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (userEmail.equals(Utils.encodeEmail(user.getEmail()))){
                           Toast.makeText(getApplicationContext(),"You can not add yourself",Toast.LENGTH_LONG).show();

                       } else{
                           Firebase friendsReference = new Firebase(Utils.FIRE_BASE_USER_FRIEND_REFERENCE + userEmail +
                                   "/" + "usersFriends/" + Utils.encodeEmail(user.getEmail()));

                           if (isFriend(currentUserFriends.getUsersFriends(),user)){
                               friendsReference.removeValue();
                               addFriendViewHolder.userItemView.setImageResource(R.mipmap.ic_plus);
                           } else{
                               friendsReference.setValue(user);
                               addFriendViewHolder.userItemView.setImageResource(R.mipmap.ic_check);
                           }

                       }
                   }
               });
           }
       };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        friendsReference.removeEventListener(listener);
    }

    @Subscribe
    public void getCurrentUsersFriends(GetUsersService.GetUsersFrendsResponse response){
        listener = response.listener;
        if (response.usersFriends!=null){
            currentUserFriends = response.usersFriends;
        } else{
            currentUserFriends = new Users();
        }
    }


    private boolean isFriend(HashMap<String,User> userFriends,User user){
        return userFriends!=null && userFriends.size()!=0
                && userFriends.containsKey(Utils.encodeEmail(user.getEmail()));
    }



}
