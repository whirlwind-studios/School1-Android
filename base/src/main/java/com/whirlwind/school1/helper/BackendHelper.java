package com.whirlwind.school1.helper;

import com.google.firebase.database.DatabaseError;
import com.whirlwind.school1.R;
import com.whirlwind.school1.popup.TextPopup;

public class BackendHelper {

    // TODO: Proper user state monitoring

    public static abstract class ValueEventListener implements com.google.firebase.database.ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            new TextPopup(databaseError.getMessage(), databaseError.getDetails()).show();
        }
    }

    public static abstract class ChildEventListener implements com.google.firebase.database.ChildEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            new TextPopup(R.string.error_title, databaseError.getMessage()).show();
        }
    }
}
