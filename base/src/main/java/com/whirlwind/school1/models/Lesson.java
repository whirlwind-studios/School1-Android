package com.whirlwind.school1.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Lesson {

    // Metadata

    // Properties
    public String teacher,
            room;

    public String title;
    public int day,
            hour;
}
