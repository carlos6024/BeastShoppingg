package com.carlosapps.beastshopping.activites;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.dialog.AddListDialogFragment;
import com.carlosapps.beastshopping.dialog.DeleteListDialogFragment;
import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.views.ShoppingListViews.ShoppingListViewHolder;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


    @BindView(R.id.activity_main_FAB)
    FloatingActionButton floatingActionButton;


    RecyclerView recyclerView;

    FirebaseRecyclerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView = (RecyclerView) findViewById(R.id.activity_main_listRecyclerView);


        String toolBarName;

        if (userName.contains(" ")){
            toolBarName = userName.substring(0,userName.indexOf(" ")) + "'s Shopping List";
        }else {
            toolBarName = userName + "'s Shopping list";
        }

        getSupportActionBar().setTitle(toolBarName);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Firebase shoppingListReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + userEmail);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        String sortOrder = sharedPreferences.getString(Utils.LIST_ODER_PREFERENCE,Utils.ORDER_BY_KEY);
        Query sortQuery;

        if(sortOrder.equals(Utils.ORDER_BY_KEY)){
            sortQuery = shoppingListReference.orderByKey();
        } else{
            sortQuery = shoppingListReference.orderByChild(sortOrder);
        }






        adapter = new FirebaseRecyclerAdapter<ShoppingList, ShoppingListViewHolder>(ShoppingList.class,
                R.layout.list_shopping_list,
                ShoppingListViewHolder.class,
                sortQuery) {

            @Override
            protected void populateViewHolder(ShoppingListViewHolder shoppingListViewHolder, final ShoppingList shoppingList, int i) {
                shoppingListViewHolder.populate(shoppingList);
                shoppingListViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<String> shoppingListInfo = new ArrayList<>();
                        shoppingListInfo.add(shoppingList.getId());
                        shoppingListInfo.add(shoppingList.getListName());
                        shoppingListInfo.add(shoppingList.getOwnerEmail());
                        startActivity(ListDetailsActivity.newInstance(getApplicationContext(),shoppingListInfo));
                    }
                });

                shoppingListViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (userEmail.equals(Utils.encodeEmail(shoppingList.getOwnerEmail()))){
                            DialogFragment dialogFragment = DeleteListDialogFragment.newInstance(shoppingList.getId(),true);
                            dialogFragment.show(getFragmentManager(),DeleteListDialogFragment.class.getSimpleName());
                            return true;
                        } else{
                            Toast.makeText(getApplicationContext(),"Only the owner can delete a list",Toast.LENGTH_LONG).show();
                            return true;
                        }

                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.action_logout:
               SharedPreferences sharedPreferences2 = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
               SharedPreferences.Editor editor = sharedPreferences2.edit();
               editor.putString(Utils.EMAIL,null).apply();
               editor.putString(Utils.USERNAME,null).apply();
               auth.signOut();
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
               finish();
               return true;

           case R.id.action_sort:
               startActivity(new Intent(getApplicationContext(),SettingActivity.class));
               return true;
       }



        return super.onOptionsItemSelected(item);
    }



    @OnClick(R.id.activity_main_FAB)
    public void setFloatingActionButton(){
        DialogFragment dialogFragment = AddListDialogFragment.newInstance();
        dialogFragment.show(getFragmentManager(),AddListDialogFragment.class.getSimpleName());
    }



}
