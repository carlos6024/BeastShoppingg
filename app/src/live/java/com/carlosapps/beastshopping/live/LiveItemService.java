package com.carlosapps.beastshopping.live;

import com.carlosapps.beastshopping.entities.Item;
import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.carlosapps.beastshopping.services.ItemService;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class LiveItemService extends BaseLiveService {
    public LiveItemService(BeastShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void AddItem(ItemService.AddItemRequest request){
        ItemService.AddItemResponse response = new ItemService.AddItemResponse();

        if (request.itemName.isEmpty()){
            response.setPropertyErrors("itemName","Item must have a name.");
        }

        if (response.didSuceed()){
            Firebase itemReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + request.shoppingListId).push();
            Firebase listReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.userEmail + "/" + request.shoppingListId);

            Item item = new Item(itemReference.getKey(),request.itemName,request.userEmail,"",false);
            itemReference.child("id").setValue(item.getId());
            itemReference.child("itemName").setValue(item.getItemName());
            itemReference.child("ownerEmail").setValue(item.getOwnerEmail());
            itemReference.child("boughtBy").setValue(item.getBoughtBy());
            itemReference.child("bought").setValue(item.isBought());


            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("dateLastChanged",timeLastChanged);
            listReference.updateChildren(newListData);

        }

        bus.post(response);
    }

    @Subscribe
    public void DeleteItem(ItemService.DeleteItemRequest request){
        Firebase itemReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + request.shoppingListId + "/" + request.itemId);
        Firebase listReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.userEmail + "/" + request.shoppingListId);

        itemReference.removeValue();
        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date", ServerValue.TIMESTAMP);
        Map newListData = new HashMap();
        newListData.put("dateLastChanged",timeLastChanged);
        listReference.updateChildren(newListData);
    }

    @Subscribe
    public void ChangeItemName(ItemService.ChangeItemNameRequest request){
        ItemService.ChangeItemNameResponse response = new ItemService.ChangeItemNameResponse();
        if (request.newItemName.isEmpty()){
            response.setPropertyErrors("itemName","Item must have a name.");
        }

        if (response.didSuceed()){
            Firebase itemReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + request.shoppingListId + "/" + request.itemId);
            Firebase listReference = new Firebase(Utils.FIRE_BASE_SHOPPING_LIST_REFERENCE + request.userEmail + "/" + request.shoppingListId);

            Map newItemData = new HashMap();
            newItemData.put("itemName",request.newItemName);


            itemReference.updateChildren(newItemData);


            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("dateLastChanged",timeLastChanged);
            listReference.updateChildren(newListData);



        }

        bus.post(response);
    }

    @Subscribe
    public void ChangeItemBoughtStatus(ItemService.ChangeBoughtItemStatusRequest request){
        Firebase itemReference = new Firebase(Utils.FIRE_BASE_LIST_ITEMS_REFERENCE + request.shoppingListId + "/" + request.item.getId());
        if (!request.item.isBought()){
            Map newItemData = new HashMap();
            newItemData.put("boughtBy",request.currentUserEmail);
            newItemData.put("bought",true);
            itemReference.updateChildren(newItemData);
        } else if(request.item.getBoughtBy().equals(request.currentUserEmail)){
            Map newItemData = new HashMap();
            newItemData.put("boughtBy","");
            newItemData.put("bought",false);
            itemReference.updateChildren(newItemData);
        }
    }
}
