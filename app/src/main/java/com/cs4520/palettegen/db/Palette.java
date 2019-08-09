package com.cs4520.palettegen.db;

import androidx.annotation.NonNull;

public class Palette implements Comparable<Palette> {
    private int id;

    private String paletteName;

    private String colorString;

    public Palette(@NonNull String colorString, @NonNull String paletteName) {
        this.paletteName = paletteName;
        this.colorString = colorString;
    }

    public Palette(@NonNull String colorString, @NonNull String paletteName, int id) {
        this.paletteName = paletteName;
        this.colorString = colorString;
        this.id = id;
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

    @Override
    public int compareTo(Palette palette) {
        return this.paletteName.toLowerCase().compareTo(palette.paletteName.toLowerCase());
    }
}
