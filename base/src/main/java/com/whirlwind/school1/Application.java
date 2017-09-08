package com.whirlwind.school1;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.helper.BackendHelper;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this);
        new InitFirebaseTask().execute(getApplicationContext());
    }

    private static class InitFirebaseTask extends AsyncTask<Context, Object, Object> {
        @Override
        protected Object doInBackground(Context... contexts) {
            FirebaseApp.initializeApp(contexts[0]);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            BackendHelper.signIn();
            return null;
        }
    }
}