package com.cs4520.palettegen.model;

import android.graphics.Color;

public class EditableColor {
    private int rgb;
    private int hue; // out of 360
    private int saturation; // out of 255
    private int value; // out of 255

    public EditableColor(int rgb) {
        this.rgb = rgb;
        this.updateHSV();
    }

    public int getRgb() {
        return rgb;
    }

    public int getHue() {
        return hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getValue() {
        return value;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
        updateHSV();
    }

    public void setHue(int hue) {
        this.hue = hue;
        updateRGB();
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
        updateRGB();
    }

    public void setValue(int value) {
        this.value = value;
        updateRGB();
    }

    private void updateHSV() {
        Color c = Color.valueOf(rgb);

        float r = c.red();
        float g = c.green();
        float b = c.blue();

        float min = Math.min(Math.min(r, g), b);
        float max = Math.max(Math.max(r, g), b);

        // set Value
        value = Math.round(max * 255f);

        // Are we gray?
        if(max - min == 0) {
            // then we have no hue or saturation.
            hue = 0;
            saturation = 0;
        } else {
            // set Saturation
            saturation = Math.round(((max - min) / max) * 255f);
        }

        float hueFloat = 0f;
        if(max == r) {
            hueFloat = (g - b) / (max - min);
        } else if (max == g) {
            hueFloat = 2f + (b - r) / (max - min);
        } else if (max == b) {
            hueFloat = 4f + (r - g) / (max - min);
        }
        hueFloat *= 60;
        // set Hue
        hue = Math.round(hueFloat > 0 ? hueFloat : hueFloat + 360);
    }

    private void updateRGB() {
        float satFloat = saturation / 255f;
        float valFloat = value / 255f;

        float chroma = valFloat * satFloat;
        float huePrime = hue / 60f;
        float x = chroma * (1 - Math.abs((huePrime % 2) - 1));

        float r1, g1, b1;
        if(huePrime <= 1) {
            r1 = chroma;
            g1 = x;
            b1 = 0;
        } else if (huePrime <= 2) {
            r1 = x;
            g1 = chroma;
            b1 = 0;
        } else if (huePrime <= 3) {
            r1 = 0;
            g1 = chroma;
            b1 = x;
        } else if (huePrime <= 4) {
            r1 = 0;
            g1 = x;
            b1 = chroma;
        } else if (huePrime <= 5) {
            r1 = x;
            g1 = 0;
            b1 = chroma;
        } else if (huePrime <= 6) {
            r1 = chroma;
            g1 = 0;
            b1 = x;
        } else {
            r1 = 0;
            g1 = 0;
            b1 = 0;
        }

        float m = valFloat - chroma;
        Color c = Color.valueOf(r1 + m, g1 + m, b1 + m);
        rgb = c.toArgb();
    }
}
