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
    public Long createConfigEntry(ConfigsModel entry);

    @Update
    public void updateConfigEntry(ConfigsModel entry);

    @Delete
    public void deleteConfigEntry(ConfigsModel entry);

    @Query("SELECT * FROM configs WHERE id = :configID")
    public ConfigsModel getByID(Long configID);

    @Query("SELECT * FROM configs WHERE ckey = :configKey")
    public ConfigsModel getByKey(String configKey);

    @Query("SELECT * FROM configs")
    public List<ConfigsModel> getAll();
}
