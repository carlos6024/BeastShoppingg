package com.carlosapps.beastshopping.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.carlosapps.beastshopping.infrastructure.Utils;
import com.squareup.otto.Bus;

public class BaseDialog extends DialogFragment {
    protected BeastShoppingApplication application;
    protected Bus bus;
    protected String userEmail,userName;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BeastShoppingApplication) getActivity().getApplication();
        bus  = application.getBus();
        bus.register(this);
        userEmail = getActivity().getSharedPreferences(Utils.MY_PREFERENCE,Context.MODE_PRIVATE).getString(Utils.EMAIL,"");
        userName= getActivity().getSharedPreferences(Utils.MY_PREFERENCE,Context.MODE_PRIVATE).getString(Utils.USERNAME,"");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
