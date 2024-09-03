package com.olosnet.simpleflat.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "profiles")
public class ProfilesModel {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;
    private int r_value;
    private int g_value;
    private int b_value;
    private float brightness_value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getR_value() {
        return r_value;
    }

    public void setR_value(int r_value) {
        this.r_value = r_value;
    }

    public int getG_value() {
        return g_value;
    }

    public void setG_value(int g_value) {
        this.g_value = g_value;
    }

    public int getB_value() {
        return b_value;
    }

    public void setB_value(int b_value) {
        this.b_value = b_value;
    }

    public float getBrightness_value() {
        return brightness_value;
    }

    public void setBrightness_value(float brightness_value) {
        this.brightness_value = brightness_value;
    }
}

