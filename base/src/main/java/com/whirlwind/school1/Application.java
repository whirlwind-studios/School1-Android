package com.whirlwind.school1;

import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}