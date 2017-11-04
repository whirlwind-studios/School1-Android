package com.whirlwind.school1.helper;

public class BackendHelper {

    // TODO: Proper user state monitoring
    // TODO: Remove listeners in onDestroy()'s

    public interface Queryable {
        String getId();

        void setId(String id);
    }

    public interface ChildInterface {
        String getParent();

        void setParent(String id);
    }
}
