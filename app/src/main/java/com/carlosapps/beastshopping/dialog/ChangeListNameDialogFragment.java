package com.carlosapps.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.User;
import com.carlosapps.beastshopping.entities.UsersSharedWith;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.GetUsersService;
import com.carlosapps.beastshopping.services.ShoppingListService;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeListNameDialogFragment extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_change_list_name_editText)
    EditText newListName;


    public static final String SHOPPING_LIST_EXTRA_INFO = "SHOPPING_LIST_EXTRA_INFO";

    private String mShoppingListId;


    private ValueEventListener mShareWIthListener;
    private UsersSharedWith mSharedWith;
    private Firebase mShareWithReference;




    public static ChangeListNameDialogFragment newInstance(ArrayList<String> shoppingListInfo){
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SHOPPING_LIST_EXTRA_INFO,shoppingListInfo);
        ChangeListNameDialogFragment dialogFragment = new ChangeListNameDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingListId = getArguments().getStringArrayList(SHOPPING_LIST_EXTRA_INFO).get(0);
        mShareWithReference = new Firebase(Utils.FIRE_BASE_SHARED_WITH_REFERENCE + mShoppingListId);
        bus.post(new GetUsersService.GetSharedWithRequest(mShareWithReference));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_list_name,null);
        ButterKnife.bind(this,rootView);
        newListName.setText(getArguments().getStringArrayList(SHOPPING_LIST_EXTRA_INFO).get(1));

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Change Name",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Change Shopping List Name?")
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;



    }

    @Override
    public void onClick(View v) {
        changeAllShoppingListsName(mSharedWith.getSharedWith(),bus,mShoppingListId,userEmail,newListName.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mShareWithReference.removeEventListener(mShareWIthListener);
    }

    @Subscribe
    public void ChangeListName(ShoppingListService.ChangeListNameResponse response){
        if (!response.didSuceed()){
            newListName.setError(response.getPropertyError("listName"));
        }
        dismiss();
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


    public static void changeAllShoppingListsName(HashMap<String,User> usersSharedWith, Bus bus
            , String shoppingListId, String ownerEmail,String newListName){
        if (usersSharedWith !=null && !usersSharedWith.isEmpty()){
            for(User user: usersSharedWith.values()){
                if (usersSharedWith.containsKey(Utils.encodeEmail(user.getEmail()))) {
                    Firebase friendListsReference =
                            new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE +
                                    Utils.encodeEmail(user.getEmail()) + "/" + shoppingListId);

                    bus.post(new ShoppingListService.ChangeListNameRequest(newListName,shoppingListId,Utils.encodeEmail(user.getEmail())));
                    bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(friendListsReference));
                }
            }
        }
        bus.post(new ShoppingListService.ChangeListNameRequest(newListName,shoppingListId,ownerEmail));
        Firebase ownerReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + "/" +
                ownerEmail + "/" +shoppingListId);

        bus.post(new ShoppingListService.UpdateShoppingListTimeStampRequest(ownerReference));

    }

}


