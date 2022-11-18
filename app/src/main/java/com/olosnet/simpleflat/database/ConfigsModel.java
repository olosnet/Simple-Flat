package com.olosnet.simpleflat.database;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "configs", indices = {@Index(value = {"key"}, unique = true)})
public class ConfigsModel {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String ckey;
    private String value;

    public Long getId() {
        return id;
    }


    public String getKey() {
        return ckey;
    }

    public void setKey(String ckey) {
        this.ckey = ckey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
