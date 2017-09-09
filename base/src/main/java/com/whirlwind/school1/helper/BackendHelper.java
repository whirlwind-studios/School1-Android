package com.whirlwind.school1.helper;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.whirlwind.school1.models.Group;
import com.whirlwind.school1.models.UserGroup;
import com.whirlwind.school1.popup.TextPopup;

import java.util.ArrayList;
import java.util.List;

public class BackendHelper {

    private static List<UserGroup> userCourses;

    public static void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null)
            FirebaseAuth.getInstance().signInAnonymously();
    }

    public static void getUserLoggedInSchool(final OnTaskCompletedListener<Boolean> listener) {
        runOnBackend(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(firebaseAuth.getCurrentUser().getUid())
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

    public static void getAllCourses(final OnTaskCompletedListener<List<Group>> listener) {
        runOnBackend(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                // populate userCourses for hasCourse()
                // bad coding style, but prefetching by using a helper method is the most reliable method,
                // and you don't need to synchronize a network operation

                // return all available courses
                listener.onTaskCompleted(new ArrayList<Group>());
            }
        });
    }

    public static boolean hasCourse(String uid) {
        return true;
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
