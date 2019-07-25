package com.cs4520.palettegen.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "palette_table")
public class Palette {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String paletteName;

    @NonNull
    private String colorString;

    public Palette(@NonNull String colorString, @NonNull String paletteName) {
        this.paletteName = paletteName;
        this.colorString = colorString;
    }

    @NonNull
    public String getPaletteName() {
        return this.paletteName;
    }

    @NonNull
    public String getColorString() {
        return this.colorString;
    }

    public void setPaletteName(@NonNull String paletteName) {
        this.paletteName = paletteName;
    }

    public void setColorString(@NonNull String colorString) {
        this.colorString = colorString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
