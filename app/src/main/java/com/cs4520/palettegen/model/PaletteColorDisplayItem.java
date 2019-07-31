package com.cs4520.palettegen.model;

import androidx.annotation.NonNull;

public class PaletteColorDisplayItem {
    private final int id;
    private String colorString;
    private boolean displayEditFragment;

    public PaletteColorDisplayItem(int id, String colorString) {
        this.id = id;
        this.colorString = colorString;
        this.displayEditFragment = false;
    }

    public int getId() {
        return id;
    }

    public String getColorString() {
        return colorString;
    }

    public boolean isDisplayEditFragment() {
        return displayEditFragment;
    }

    public void setColorString(String colorString) {
        this.colorString = colorString;
    }

    public void setDisplayEditFragment(boolean displayEditFragment) {
        this.displayEditFragment = displayEditFragment;
    }

    public String getHexString() {
        return this.rgbToHex(this.colorString);
    }

    private String rgbToHex(String s) {
        return "#" + Integer.toHexString(Integer.parseInt(s)).toUpperCase().substring(2);
    }

    @NonNull
    @Override
    public PaletteColorDisplayItem clone() {
        return new PaletteColorDisplayItem(this.getId(), this.getColorString());
    }
}
