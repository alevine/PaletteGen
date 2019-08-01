package com.cs4520.palettegen.model;

import android.graphics.Color;

import androidx.annotation.NonNull;

public class PaletteColorDisplayItem {
    private static int hiddenIdCounter = 0;

    private final int hiddenId;
    private final int id;
    private String colorString;
    private boolean displayEditFragment;

    public PaletteColorDisplayItem(int id, String colorString) {
        this.hiddenId = hiddenIdCounter++;
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

    public String getLegibleRgb() {
        Color c = Color.valueOf(Integer.parseInt(this.colorString));
        int r, g, b;
        r = Math.round(c.red() * 255);
        g = Math.round(c.green() * 255);
        b = Math.round(c.blue() * 255);
        return "R:" + r + " G:" + g + " B:" + b;
    }

    @NonNull
    @Override
    public PaletteColorDisplayItem clone() {
        return new PaletteColorDisplayItem(this.getId(), this.getColorString());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  PaletteColorDisplayItem) {
            return this.hiddenId == ((PaletteColorDisplayItem) obj).hiddenId;
        }
        return false;
    }
}
