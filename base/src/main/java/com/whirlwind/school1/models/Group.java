package com.whirlwind.school1.models;

public class Group {

    public static final int TYPE_SCHOOL = 0, TYPE_COURSE = 1, TYPE_PROEJECT = 2, TYPE_MASK = 7; // max 8 different types

    // Metadata
    public int flags;
    public String parentGroup; // null for Schools, uid of parent for every other type

    // Properties
    public String iconLink;
    public String name, description;
}
