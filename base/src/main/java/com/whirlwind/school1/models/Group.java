package com.whirlwind.school1.models;

import com.whirlwind.school1.helper.BackendHelper;

public class Group implements BackendHelper.Queryable {

    public static final int TYPE_SCHOOL = 0, TYPE_COURSE = 1, TYPE_PROEJECT = 2, TYPE_MASK = 7; // max 8 different types
    public int type;
    public String parentGroup; // null for schools, uid of parent for every other type
    // Properties
    public String name;
    // Metadata
    protected String id;

    public Group() {
    }

    public Group(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
