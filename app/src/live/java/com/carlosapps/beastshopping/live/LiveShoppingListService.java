package com.carlosapps.beastshopping.live;

import android.widget.Toast;

import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.ShoppingListService;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LiveShoppingListService extends BaseLiveService {
    public LiveShoppingListService(BeastShoppingApplication application) {
        super(application);
    }


    @Subscribe
    public void AddShoppingList(ShoppingListService.AddShoppingListRequest request){
        ShoppingListService.AddShoppingListResponse response = new ShoppingListService.AddShoppingListResponse();

        if (request.shoppingListName.isEmpty()){
            response.setPropertyErrors("listName","Shopping List must have a name");
        }

        if (response.didSuceed()){
            Firebase reference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.ownerEmail).push();
            HashMap<String,Object> timestampedCreated = new HashMap<>();
            timestampedCreated.put("timestamp", ServerValue.TIMESTAMP);
            ShoppingList shoppingList = new ShoppingList(reference.getKey(),request.shoppingListName,
                    Utils.decodeEmail(request.ownerEmail),request.ownerName,timestampedCreated);
            reference.child("id").setValue(shoppingList.getId());
            reference.child("listName").setValue(shoppingList.getListName());
            reference.child("ownerEmail").setValue(shoppingList.getOwnerEmail());
            reference.child("ownerName").setValue(shoppingList.getOwnerName());
            reference.child("dateCreated").setValue(shoppingList.getDateCreated());
            reference.child("dateLastChanged").setValue(shoppingList.getDateLastChanged());
            Toast.makeText(application.getApplicationContext(),"List has been created!",Toast.LENGTH_LONG).show();
        }

        bus.post(response);
    }

    @Subscribe
    public void DeleteShoppingList(ShoppingListService.DeleteShoppingListRequest request){
        Firebase reference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.ownerEmail + "/" + request.shoppingListId);
        Firebase itemReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + request.shoppingListId);
        Firebase sharedWithReference = new Firebase(Utils.FIRE_BASE_SHARED_WITH_REFERENCE + request.shoppingListId);
        reference.removeValue();
        itemReference.removeValue();
        sharedWithReference.removeValue();
    }


    @Subscribe
    public void ChangeListName(ShoppingListService.ChangeListNameRequest request){
        ShoppingListService.ChangeListNameResponse response = new ShoppingListService.ChangeListNameResponse();
        if (request.newShoppingListName.isEmpty()){
            response.setPropertyErrors("listName","Shopping list must a name");
        }

        if (response.didSuceed()){
            Firebase reference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.shoppingListOwnerEmail+ "/" + request.shoppingListId);
            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date",ServerValue.TIMESTAMP);


            Map newListData = new HashMap();
            newListData.put("listName",request.newShoppingListName);
            newListData.put("dateLastChanged",timeLastChanged);
            reference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void getCurrentShoppingList(ShoppingListService.GetCurrentShoppingListRequest request){
        final ShoppingListService.GetCurrentShoppingListResponse response = new ShoppingListService.GetCurrentShoppingListResponse();
        response.valueEventListener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                response.shoppingList = dataSnapshot.getValue(ShoppingList.class);
                if (response.shoppingList!=null){
                    bus.post(response);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(application.getApplicationContext(),firebaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Subscribe
    public void UpdateShoppingListTimeStamp(ShoppingListService.UpdateShoppingListTimeStampRequest request){
        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date",ServerValue.TIMESTAMP);
        Map newListData = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        request.FirebaseReference.updateChildren(newListData);
    }

}
