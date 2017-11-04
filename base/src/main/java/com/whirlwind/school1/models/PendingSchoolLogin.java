package com.whirlwind.school1.models;

public class PendingSchoolLogin {
    public String id,
            password,
            error;

    public PendingSchoolLogin() {
    }

    public PendingSchoolLogin(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
