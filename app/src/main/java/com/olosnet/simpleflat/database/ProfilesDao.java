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
    void updateProfileEntry(ProfilesModel entry);

    @Delete
    void deleteProfileEntry(ProfilesModel entry);

    @Query("DELETE FROM profiles WHERE id= :profileID")
    void deleteProfileByID(Long profileID);

    @Query("SELECT * FROM profiles WHERE id = :profileID")
    ProfilesModel getByID(Long profileID);

    @Query("SELECT * FROM profiles")
    List<ProfilesModel> getAll();

}
