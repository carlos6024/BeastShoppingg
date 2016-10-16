package com.carlosapps.beastshopping.services;

import com.carlosapps.beastshopping.entities.ShoppingList;
import com.carlosapps.beastshopping.infrastructure.ServiceResponse;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

public class ShoppingListService {

    private ShoppingListService() {
    }

    public static class AddShoppingListRequest {
        public String shoppingListName;
        public String ownerName;
        public String ownerEmail;

        public AddShoppingListRequest(String shoppingListName, String ownerName, String ownerEmail) {
            this.shoppingListName = shoppingListName;
            this.ownerName = ownerName;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class AddShoppingListResponse extends ServiceResponse{
    }

    public static class DeleteShoppingListRequest{
        public String ownerEmail;
        public String shoppingListId;

        public DeleteShoppingListRequest(String ownerEmail, String shoppingListId) {
            this.ownerEmail = ownerEmail;
            this.shoppingListId = shoppingListId;
        }
    }

    public static class ChangeListNameRequest{
        public String newShoppingListName;
        public String shoppingListId;
        public String shoppingListOwnerEmail;

        public ChangeListNameRequest(String newShoppingListName, String shoppingListId, String shoppingListOwnerEmail) {
            this.newShoppingListName = newShoppingListName;
            this.shoppingListId = shoppingListId;
            this.shoppingListOwnerEmail = shoppingListOwnerEmail;
        }
    }

    public static class ChangeListNameResponse extends ServiceResponse{

    }

    public static class GetCurrentShoppingListRequest{
        public Firebase reference;

        public GetCurrentShoppingListRequest(Firebase reference) {
            this.reference = reference;
        }
    }

    public static class GetCurrentShoppingListResponse{
        public ShoppingList shoppingList;
        public ValueEventListener valueEventListener;
    }


    public static class UpdateShoppingListTimeStampRequest {
        public Firebase FirebaseReference;

        public UpdateShoppingListTimeStampRequest(Firebase firebaseReference) {
            FirebaseReference = firebaseReference;
        }
    }


}
