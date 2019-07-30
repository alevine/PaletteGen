package com.cs4520.palettegen.model;

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

    @Override
    public PaletteColorDisplayItem clone() {
        return new PaletteColorDisplayItem(this.getId(), this.getColorString());
    }
}
