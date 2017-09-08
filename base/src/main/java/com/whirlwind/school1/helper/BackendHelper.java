package com.whirlwind.school1.helper;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.popup.TextPopup;

public class BackendHelper {

    private static final String introCompleted = "introCompleted";

    public static void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null)
            FirebaseAuth.getInstance().signInAnonymously();
    }

    public static boolean getIntroCompleted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(introCompleted, false);
    }

    public static void setIntroCompleted(Context context, boolean completed) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(introCompleted, completed).apply();
    }

    public static void getUserLoggedInSchool(final OnTaskCompletedListener<Boolean> listener) {
        runOnBackend(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(firebaseAuth.getCurrentUser().getUid())
                        .child("schoolUid")
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

    public static void runOnBackend(@NonNull final FirebaseAuth.AuthStateListener authStateListener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
            authStateListener.onAuthStateChanged(auth);
        else
            auth.addAuthStateListener(authStateListener);
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
}
