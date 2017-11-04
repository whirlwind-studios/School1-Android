package com.whirlwind.school1;

import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this);
        FirebaseApp.initializeApp(this);
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}