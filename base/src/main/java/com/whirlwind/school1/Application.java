package com.whirlwind.school1;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.helper.BackendHelper;
import com.whirlwind.school1.popup.TextPopup;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this);
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        BackendHelper.runOnce(new BackendHelper.OnTaskCompletedListener<UserInfo>() {
            @Override
            public void onTaskCompleted(UserInfo userInfo) {
                Log.d("OnComplete","blub");
                new TextPopup("Success",userInfo.toString()).show();
            }
        });
        BackendHelper.signIn();
    }
}