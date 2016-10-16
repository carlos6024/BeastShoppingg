package com.carlosapps.beastshopping.live;

import com.carlosapps.beastshopping.infrastructure.BeastShoppingApplication;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Bus;

public class BaseLiveService {
    protected Bus bus;
    protected BeastShoppingApplication application;
    protected FirebaseAuth auth;

    public BaseLiveService(BeastShoppingApplication application) {
        this.application = application;
        bus = application.getBus();
        bus.register(this);
        auth = FirebaseAuth.getInstance();
    }
}
