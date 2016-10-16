package com.carlosapps.beastshopping.activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.entities.User;
import com.carlosapps.beastshopping.entities.UsersSharedWith;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.GetUsersService;
import com.carlosapps.beastshopping.services.ShoppingListService;
import com.carlosapps.beastshopping.views.ShareListView.ShareListViewHolder;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

public class ShareListActivity extends BaseActivity {

    private String mShoppingListId;

    private Firebase mShareWithReference;
    private Firebase mShoppingListReference;

    private ValueEventListener mShareWIthListener;
    private ValueEventListener mShoppingListListener;

    private UsersSharedWith mSharedWith;
    private ShoppingList mCurrentShoppingList;


    private FirebaseRecyclerAdapter adapter;
    public static String SHOPPING_LIST_EXTRA_INFO = "SHOPPING_LIST_EXTRA_INFO";



    public static Intent newIntent(Context context,String shoppingListId){
        Intent intent = new Intent(context,ShareListActivity.class);
        intent.putExtra(SHOPPING_LIST_EXTRA_INFO,shoppingListId);
        return intent;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        mShoppingListId = getIntent().getStringExtra(SHOPPING_LIST_EXTRA_INFO);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_share_list_recyclerView);

        mShareWithReference = new Firebase(Utils.FIRE_BASE_SHARED_WITH_REFERENCE + mShoppingListId);

        mShoppingListReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + userEmail + "/" + mShoppingListId);

        bus.post(new GetUsersService.GetSharedWithRequest(mShareWithReference));
        bus.post(new ShoppingListService.GetCurrentShoppingListRequest(mShoppingListReference));

        Firebase reference = new Firebase(Utils.FIRE_BASE_USER_FRIEND_REFERENCE + userEmail + "/usersFriends");



        adapter = new FirebaseRecyclerAdapter<User, ShareListViewHolder>(
                User.class,
                R.layout.list_user,
                ShareListViewHolder.class,
                reference) {

            @Override
            protected void populateViewHolder(final ShareListViewHolder shareListViewHolder, final User user, final int i) {
                shareListViewHolder.populate(user);


                if (isSharedWith(mSharedWith.getSharedWith(),user)){
                    shareListViewHolder.userItemView.setImageResource(R.mipmap.ic_check);
                } else{
                    shareListViewHolder.userItemView.setImageResource(R.mipmap.ic_plus);
                }

                shareListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Firebase sharedWithReference = new Firebase(Utils.FIRE_BASE_SHARED_WITH_REFERENCE + mShoppingListId+"/" +"sharedWith/"+
                                 Utils.encodeEmail(user.getEmail()));

                        Firebase friendsShoppingListReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE +
                        Utils.encodeEmail(user.getEmail()) + "/" + mShoppingListId);

                        if (isSharedWith(mSharedWith.getSharedWith(),user)){
                            sharedWithReference.removeValue();
                            friendsShoppingListReference.removeValue();
                            shareListViewHolder.userItemView.setImageResource(R.mipmap.ic_plus);
                            updateAllShoppingListReference(mSharedWith.getSharedWith(),mCurrentShoppingList,bus,true);

                        } else{
                            sharedWithReference.setValue(user);
                            friendsShoppingListReference.setValue(mCurrentShoppingList);
                            shareListViewHolder.userItemView.setImageResource(R.mipmap.ic_check);
                            updateAllShoppingListReference(mSharedWith.getSharedWith(),mCurrentShoppingList,bus,false);
                        }
                    }
                });


            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_friend:
                startActivity(new Intent(this,AddFriendActivity.class));
                return true;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
        mShareWithReference.removeEventListener(mShareWIthListener);
        mShoppingListReference.removeEventListener(mShoppingListListener);
    }

    @Subscribe
    public void getUsersSharedWith(GetUsersService.GetSharedWithResponse response){
        mShareWIthListener = response.listener;

        if (response.usersSharedWith!=null){
            mSharedWith = response.usersSharedWith;
        } else{
            mSharedWith = new UsersSharedWith();
        }
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListResponse response){
        mCurrentShoppingList = response.shoppingList;
        mShoppingListListener = response.valueEventListener;
    }



    public boolean isSharedWith(HashMap<String ,User> usersSharedWIth,User user){
        return usersSharedWIth!=null && usersSharedWIth.size()!=0 &&
                usersSharedWIth.containsKey(Utils.encodeEmail(user.getEmail()));
    }





    public static void updateAllShoppingListReference(HashMap<String,User> usersSharedWith, ShoppingList shoppingList, Bus bus
    ,boolean deletingList){
        if (usersSharedWith !=null && !usersSharedWith.isEmpty()){
            for(User user: usersSharedWith.values()){
                if (usersSharedWith.containsKey(Utils.encodeEmail(user.getEmail())))
                {
                    Firebase friendListsReference =
                            new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE +
                                    Utils.encodeEmail(user.getEmail()) + "/" + shoppingList.getId());

                    if (!deletingList){
                        bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(friendListsReference));
                    }
                }
            }
        }


        Firebase ownerReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + "/" +
                Utils.encodeEmail(shoppingList.getOwnerEmail())  + "/"
                + shoppingList.getId());

        bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(ownerReference));
    }
}
