package com.carlosapps.beastshopping.activites;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.dialog.AddItemDialogFragment;
import com.carlosapps.beastshopping.dialog.ChangeItemNameDialogFragment;
import com.carlosapps.beastshopping.dialog.ChangeListNameDialogFragment;
import com.carlosapps.beastshopping.dialog.DeleteItemDialogFragment;
import com.carlosapps.beastshopping.dialog.DeleteListDialogFragment;
import com.carlosapps.beastshopping.entities.Item;
import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.ItemService;
import com.carlosapps.beastshopping.services.ShoppingListService;
import com.carlosapps.beastshopping.views.ItemListViews.ListItemViewHolder;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ListDetailsActivity extends BaseActivity {

    @BindView(R.id.activity_list_details_FAB)
    FloatingActionButton floatingActionButton;

    private FirebaseRecyclerAdapter adapter;

    public static final String SHOPPING_LIST_DETAILS = "SHOPPING_LIST_DETAILS";

    private String mShoppingId;
    private String mShoppingName;
    private String mShoppingOwner;


    private Firebase mShoppingListReference;

    private ValueEventListener mShoppingListListener;

    private ShoppingList mCurrentShoppingList;


    public static Intent newInstance(Context context, ArrayList<String> shoppingListInfo){
        Intent intent = new Intent(context,ListDetailsActivity.class);
        intent.putStringArrayListExtra(SHOPPING_LIST_DETAILS,shoppingListInfo);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        ButterKnife.bind(this);
        RecyclerView recyclerView =(RecyclerView) findViewById(R.id.activity_list_details_RecyclerView);
        mShoppingId = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(0);
        mShoppingName = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(1);
        mShoppingOwner = getIntent().getStringArrayListExtra(SHOPPING_LIST_DETAILS).get(2);

        mShoppingListReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + userEmail + "/" + mShoppingId);
        bus.post(new ShoppingListService.GetCurrentShoppingListRequest(mShoppingListReference));

        Firebase itemsReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + mShoppingId);

        adapter = new FirebaseRecyclerAdapter<Item,ListItemViewHolder>(Item.class,
                R.layout.list_shopping_item,
                ListItemViewHolder.class,
                itemsReference) {


            @Override
            protected void populateViewHolder(ListItemViewHolder listItemViewHolder, final Item item, int i) {
                listItemViewHolder.populate(item,getApplicationContext(),userEmail);

                listItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> extraInfo = new ArrayList<>();
                        extraInfo.add(item.getId());
                        extraInfo.add(mShoppingId);
                        extraInfo.add(userEmail);

                        DialogFragment dialogFragment = DeleteItemDialogFragment.newInstance(extraInfo);
                        dialogFragment.show(getFragmentManager(), DeleteItemDialogFragment.class.getSimpleName());
                    }
                });


                listItemViewHolder.itemName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (Utils.encodeEmail(mShoppingOwner).equals(userEmail)| Utils.encodeEmail(item.getOwnerEmail()).equals(userEmail)){
                            ArrayList<String> extraInfo = new ArrayList<>();
                            extraInfo.add(item.getId());
                            extraInfo.add(mShoppingId);
                            extraInfo.add(userEmail);
                            extraInfo.add(item.getItemName());
                            DialogFragment dialogFragment = ChangeItemNameDialogFragment.newInstance(extraInfo);
                            dialogFragment.show(getFragmentManager(),ChangeItemNameDialogFragment.class.getSimpleName());
                            return true;
                        } else{
                            Toast.makeText(getApplicationContext(),"Only the owner of the list or item can rename it",Toast.LENGTH_LONG)
                                    .show();
                            return true;
                        }
                    }
                });

                listItemViewHolder.itemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!item.isBought()){
                            bus.post(new ItemService.ChangeBoughtItemStatusRequest(item,userEmail,mShoppingId));
                        } else if(Utils.encodeEmail(item.getBoughtBy()).equals(userEmail)){
                            bus.post(new ItemService.ChangeBoughtItemStatusRequest(item,userEmail,mShoppingId));
                        } else {
                            Toast.makeText(getApplicationContext(),"Only the person who bought can un buy this item",Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                });

            }
        };

        getSupportActionBar().setTitle(mShoppingName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Utils.encodeEmail(mShoppingOwner).equals(userEmail)){
            getMenuInflater().inflate(R.menu.menu_list_details,menu);
            return true;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_change_list_name:
                ArrayList<String> shoppingListInfo = new ArrayList<>();
                shoppingListInfo.add(mShoppingId);
                shoppingListInfo.add(mShoppingName);
                DialogFragment dialogFragment = ChangeListNameDialogFragment.newInstance(shoppingListInfo);
                dialogFragment.show(getFragmentManager(),ChangeListNameDialogFragment.class.getSimpleName());
                return true;

            case R.id.action_delete_list:
                DialogFragment dialogFragment1 = DeleteListDialogFragment.newInstance(mShoppingId,false);
                dialogFragment1.show(getFragmentManager(),DeleteListDialogFragment.class.getSimpleName());
                return true;

            case R.id.action_share_list:
                startActivity(ShareListActivity.newIntent(getApplicationContext(),mShoppingId));
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mShoppingListReference.removeEventListener(mShoppingListListener);
        adapter.cleanup();
        Log.i(ListDetailsActivity.class.getSimpleName(),"On destroy was called");
    }




    @OnClick(R.id.activity_list_details_FAB)
    public void setFloatingActionButton(){
        DialogFragment dialogFragment = AddItemDialogFragment.newInstance(mShoppingId);
        dialogFragment.show(getFragmentManager(),AddItemDialogFragment.class.getSimpleName());
    }


    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListResponse response){
        mShoppingListListener = response.valueEventListener;
        mCurrentShoppingList = response.shoppingList;
        mShoppingName = mCurrentShoppingList.getListName();

        if (mCurrentShoppingList.getListName().equals("CListIsAboutToGetDeleted")){
            finish();
        } else{
            getSupportActionBar().setTitle(mCurrentShoppingList.getListName());

        }
    }
}
