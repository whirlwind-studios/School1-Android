package com.whirlwind.school1.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Error {
    public int code;

    @ServerTimestamp
    public Date timestamp;

    public String message;
}