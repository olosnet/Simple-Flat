package com.olosnet.simpleflat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ConfigsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long createConfigEntry(ConfigsModel entry);

    @Update
    void updateConfigEntry(ConfigsModel entry);

    @Delete
    void deleteConfigEntry(ConfigsModel entry);

    @Query("SELECT * FROM configs WHERE id = :configID")
    ConfigsModel getByID(Long configID);

    @Query("SELECT * FROM configs WHERE ckey = :configKey")
    ConfigsModel getByKey(String configKey);

    @Query("SELECT * FROM configs")
    List<ConfigsModel> getAll();
}
