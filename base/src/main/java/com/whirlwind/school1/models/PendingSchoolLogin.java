package com.whirlwind.school1.models;

public class PendingSchoolLogin {
    public String uid,
            name,
            password;

    public PendingSchoolLogin() {
    }

    public PendingSchoolLogin(String uid, String name, String password) {
        this.uid = uid;
        this.name = name;
        this.password = password;
    }
}
