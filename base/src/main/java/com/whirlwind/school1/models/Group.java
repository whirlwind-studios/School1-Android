package com.whirlwind.school1.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.whirlwind.school1.helper.BackendHelper;

@IgnoreExtraProperties
public class Group implements BackendHelper.Queryable {

    public static final int TYPE_SCHOOL = 0, TYPE_COURSE = 1, TYPE_PROEJECT = 2, TYPE_MASK = 7; // max 8 different types
    public int type;
    public String parentGroup; // null for schools, uid of parent for every other type
    // Properties
    public String iconLink;
    public String name, description;
    // Metadata
    private String key;

    public Group() {
    }

    public Group(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }
}
