package com.whirlwind.school1.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Lesson {

    // Metadata
    public String teacher,
            room;

    // Properties
    public String title;
    public int day,
            hour;
    public boolean shared;
}
