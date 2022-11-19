package com.olosnet.simpleflat.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {ConfigsModel.class, ProfilesModel.class},
        version = 1
)
public abstract class SimpleFlatDatabase extends RoomDatabase {

    private static SimpleFlatDatabase database;

    public abstract ConfigsDao configsDao();
    public abstract ProfilesDao profilesDao();

    public static SimpleFlatDatabase getInstance(Context context) {
        if(SimpleFlatDatabase.database == null) {
            SimpleFlatDatabase.database = Room.databaseBuilder(
                    context,
                    SimpleFlatDatabase.class,
                    "simple-flat-settings.db"
            ).build();
        }

        return SimpleFlatDatabase.database;
    }
}
