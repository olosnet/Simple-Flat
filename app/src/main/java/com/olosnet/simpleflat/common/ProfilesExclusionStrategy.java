package com.olosnet.simpleflat.common;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.olosnet.simpleflat.database.ProfilesModel;

public class ProfilesExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == ProfilesModel.class && f.getName().equals("id"));
    }

}
