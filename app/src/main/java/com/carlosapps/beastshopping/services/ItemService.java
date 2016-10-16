package com.carlosapps.beastshopping.services;

import com.carlosapps.beastshopping.entities.Item;
import com.carlosapps.beastshopping.infrastructure.ServiceResponse;

public class ItemService {
    private ItemService() {
    }

    public static class AddItemRequest{
        public String shoppingListId;
        public String itemName;
        public String userEmail;

        public AddItemRequest(String shoppingListId, String itemName, String userEmail) {
            this.shoppingListId = shoppingListId;
            this.itemName = itemName;
            this.userEmail = userEmail;
        }
    }

    public static class AddItemResponse extends ServiceResponse{
    }


    public static class DeleteItemRequest{
        public String itemId;
        public String shoppingListId;
        public String userEmail;

        public DeleteItemRequest(String itemId, String shoppingListId, String userEmail) {
            this.itemId = itemId;
            this.shoppingListId = shoppingListId;
            this.userEmail = userEmail;
        }
    }

    public static class ChangeItemNameRequest{
        public String itemId;
        public String shoppingListId;
        public String userEmail;
        public String newItemName;

        public ChangeItemNameRequest(String itemId, String shoppingListId, String userEmail, String newItemName) {
            this.itemId = itemId;
            this.shoppingListId = shoppingListId;
            this.userEmail = userEmail;
            this.newItemName = newItemName;
        }
    }

    public static class ChangeItemNameResponse extends ServiceResponse{

    }

    public static class ChangeBoughtItemStatusRequest {
        public Item item;
        public String currentUserEmail;
        public String shoppingListId;

        public ChangeBoughtItemStatusRequest(Item item, String currentUserEmail, String shoppingListId) {
            this.item = item;
            this.currentUserEmail = currentUserEmail;
            this.shoppingListId = shoppingListId;
        }
    }



}
