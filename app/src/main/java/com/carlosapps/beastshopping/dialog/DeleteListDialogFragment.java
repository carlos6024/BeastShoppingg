package com.carlosapps.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.entities.User;
import com.carlosapps.beastshopping.entities.UsersSharedWith;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.GetUsersService;
import com.carlosapps.beastshopping.services.ShoppingListService;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class DeleteListDialogFragment extends BaseDialog implements View.OnClickListener{

    public static final String EXTRA_SHOPPING_LIST_ID = "EXTRA_SHOPPING_LIST_ID";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private String mShoppingListId;
    private boolean mIsLongedClicked;


    private ValueEventListener mShareWIthListener;
    private UsersSharedWith mSharedWith;
    private Firebase mShareWithReference;



    public static DeleteListDialogFragment newInstance(String shoppingListId,boolean isLongClicked){
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SHOPPING_LIST_ID,shoppingListId);
        arguments.putBoolean(EXTRA_BOOLEAN,isLongClicked);

        DeleteListDialogFragment dialogFragment = new DeleteListDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingListId = getArguments().getString(EXTRA_SHOPPING_LIST_ID);
        mIsLongedClicked = getArguments().getBoolean(EXTRA_BOOLEAN);
        mShareWithReference = new Firebase(Utils.FIRE_BASE_SHARED_WITH_REFERENCE + mShoppingListId);
        bus.post(new GetUsersService.GetSharedWithRequest(mShareWithReference));

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_list,null))
                .setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Delete Shopping List?")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        if (mIsLongedClicked){
            dismiss();
            deleteAllShoppingLists(mSharedWith.getSharedWith(),bus,mShoppingListId,userEmail);
        } else{
            dismiss();
            getActivity().finish();
            deleteAllShoppingLists(mSharedWith.getSharedWith(),bus,mShoppingListId,userEmail);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mShareWithReference.removeEventListener(mShareWIthListener);
    }

    public static void deleteAllShoppingLists(HashMap<String,User> usersSharedWith, Bus bus
            , String shoppingListId, String ownerEmail){
        if (usersSharedWith !=null && !usersSharedWith.isEmpty()){
            for(User user: usersSharedWith.values()){
                if (usersSharedWith.containsKey(Utils.encodeEmail(user.getEmail())))
                {
                    Firebase friendListsReference =
                            new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE +
                                    Utils.encodeEmail(user.getEmail()) + "/" + shoppingListId);

                    Map newListData = new HashMap();
                    newListData.put("listName","CListIsAboutToGetDeleted");
                    friendListsReference.updateChildren(newListData);
                    friendListsReference.removeValue();

                }
            }
        }
        bus.post(new ShoppingListService.DeleteShoppingListRequest(ownerEmail,shoppingListId));
    }
}
