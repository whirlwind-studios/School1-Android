package com.whirlwind.school1.helper;

import com.google.firebase.database.DatabaseError;
import com.whirlwind.school1.R;
import com.whirlwind.school1.popup.TextPopup;

public class BackendHelper {

    // TODO: Proper user state monitoring
    // TODO: Remove listeners in onDestroy()'s

    public interface Queryable {
        String getKey();

        void setKey(String key);
    }

    public interface ChildInterface {
        String getParent();

        void setParent(String uid);
    }

    public static abstract class ValueEventListener implements com.google.firebase.database.ValueEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            new TextPopup(R.string.error_title, databaseError.getMessage()).show();
        }
    }

    public static abstract class ChildEventListener implements com.google.firebase.database.ChildEventListener {
        @Override
        public void onCancelled(DatabaseError databaseError) {
            new TextPopup(R.string.error_title, databaseError.getMessage()).show();
        }
    }
}
