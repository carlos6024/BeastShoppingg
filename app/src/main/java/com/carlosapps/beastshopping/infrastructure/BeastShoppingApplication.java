package com.carlosapps.beastshopping.infrastructure;

import android.app.Application;

import com.carlosapps.beastshopping.live.Module;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;
import com.squareup.otto.Bus;

public class BeastShoppingApplication extends Application {
    private Bus bus;

    public BeastShoppingApplication() {
        bus = new Bus();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Module.Register(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }

    public Bus getBus() {
        return bus;
    }
}
