package com.olosnet.simpleflat.database;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {ConfigsModel.class, ProfilesModel.class},
        version = 2
)
public abstract class SimpleFlatDatabase extends RoomDatabase {

    private static SimpleFlatDatabase database;

    public abstract ConfigsDao configsDao();
    public abstract ProfilesDao profilesDao();

    // Migration to "repair" saved profiles
    // from the previous version that had the inverted g and b channel bug
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
            supportSQLiteDatabase.execSQL("ALTER TABLE profiles RENAME COLUMN g_value TO g_value_tmp");
            supportSQLiteDatabase.execSQL("ALTER TABLE profiles RENAME COLUMN b_value TO g_value");
            supportSQLiteDatabase.execSQL("ALTER TABLE profiles RENAME COLUMN g_value_tmp TO b_value");
        }
    };

    public static SimpleFlatDatabase getInstance(Context context) {
        if(SimpleFlatDatabase.database == null) {
            SimpleFlatDatabase.database = Room.databaseBuilder(
                    context,
                    SimpleFlatDatabase.class,
                    "simple-flat-settings.db"
            ).addMigrations(MIGRATION_1_2).build();
        }

        return SimpleFlatDatabase.database;
    }



}
