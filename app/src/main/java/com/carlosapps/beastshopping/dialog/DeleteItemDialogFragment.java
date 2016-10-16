package com.carlosapps.beastshopping.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.carlosapps.beastshopping.R;
import com.carlosapps.beastshopping.services.ItemService;

import java.util.ArrayList;


public class DeleteItemDialogFragment extends BaseDialog implements View.OnClickListener{

    public static final String EXTRA_INFO = "EXTRA_INFO";


    public static DeleteItemDialogFragment newInstance(ArrayList<String> extraInfo) {
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(EXTRA_INFO,extraInfo);
        DeleteItemDialogFragment deleteItemDialogFragment = new DeleteItemDialogFragment();
        deleteItemDialogFragment.setArguments(arguments);
        return deleteItemDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_item,null))
                .setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Delete Item?")
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View view) {
        bus.post(new ItemService.DeleteItemRequest(getArguments().getStringArrayList(EXTRA_INFO).get(0)
                ,getArguments().getStringArrayList(EXTRA_INFO).get(1),
                getArguments().getStringArrayList(EXTRA_INFO).get(2)
        ));
        dismiss();
    }
}


