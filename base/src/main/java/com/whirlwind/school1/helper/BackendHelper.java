package com.whirlwind.school1.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BackendHelper {

    // TODO: Proper user state monitoring
    // TODO: Remove listeners in onDestroy()'s

    public static CollectionReference getItemsReference(String groupId) {
        CollectionReference items;
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(groupId))
            items = FirebaseFirestore.getInstance()
                    .collection("users");
        else
            items = FirebaseFirestore.getInstance()
                    .collection("groups");
        return items.document(groupId).collection("items");
    }

    public interface Queryable {
        String getId();

        void setId(String id);
    }

    public interface ChildInterface {
        String getParent();

        void setParent(String id);
    }
}
