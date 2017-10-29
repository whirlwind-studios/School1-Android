package com.whirlwind.school1.helper;

public class BackendHelper {

    // TODO: Proper user state monitoring
    // TODO: Remove listeners in onDestroy()'s

    public interface Queryable {
        String getKey();

        void setKey(String key);
    }

    public interface ChildInterface {
        String getParent();

        void setParent(String uid);
    }
}
