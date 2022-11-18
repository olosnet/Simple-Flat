package com.olosnet.simpleflat.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProfilesDao {

    @Insert
    public Long createProfileEntry(ProfilesModel entry);

    @Update
    public void updateProfileEntry(ProfilesModel entry);

    @Delete
    public void deleteProfileEntry(ProfilesModel entry);

    @Query("SELECT * FROM profiles WHERE id = :profileID")
    public ProfilesModel getByID(Long profileID);

    @Query("SELECT * FROM profiles")
    public List<ProfilesModel> getAll();

}
