package com.whirlwind.school1.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PendingSchoolLogin {
    public String id,
            password,
            userId;

    public PendingSchoolLogin() {
    }

    public PendingSchoolLogin(String id, String password) {
        this.id = id;
        this.password = password;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
