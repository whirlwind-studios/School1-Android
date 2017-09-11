package com.whirlwind.school1.helper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.popup.TextPopup;

import java.util.ArrayList;
import java.util.List;

public class BackendHelper {

    private static List<OnTaskCompletedListener<UserInfo>> listeners = new ArrayList<>();

    public static void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                UserInfo userInfo = firebaseAuth.getCurrentUser();
                Log.d("Authstate", "" + String.valueOf(userInfo));
                if (userInfo != null) {
                    for (OnTaskCompletedListener<UserInfo> listener : listeners)
                        listener.onTaskCompleted(userInfo);

                    listeners.clear();
                }
            }
        });
        Log.d("Current user", String.valueOf(auth.getCurrentUser()));
        auth.signInAnonymously();
    }

    public static void getUserLoggedInSchool(final OnTaskCompletedListener<Boolean> listener) {
        runOnce(new OnTaskCompletedListener<UserInfo>() {
            @Override
            public void onTaskCompleted(UserInfo userInfo) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userInfo.getUid())
                        .child("schoolId")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                new TextPopup("Data", String.valueOf(dataSnapshot.getValue())).show();
                                listener.onTaskCompleted(dataSnapshot.getValue() != null);
                            }
                        });
            }
        });
    }

    public static void runOnce(@NonNull OnTaskCompletedListener<UserInfo> listener) {
        UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();
        if (userInfo != null)
            listener.onTaskCompleted(userInfo);
        else
            listeners.add(listener);
    }

    public interface OnTaskCompletedListener<T> {
        void onTaskCompleted(T t);
    }

    public static abstract class ValueEventListener implements com.google.firebase.database.ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            new TextPopup(databaseError.getMessage(), databaseError.getDetails()).show();
        }
    }

    public static abstract class ChildEventListener implements com.google.firebase.database.ChildEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
