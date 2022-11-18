package com.olosnet.simpleflat.database;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "configs", indices = {@Index(value = {"ckey"}, unique = true)})
public class ConfigsModel {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String ckey;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCkey() {
        return ckey;
    }

    public void setCkey(String ckey) {
        this.ckey = ckey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
