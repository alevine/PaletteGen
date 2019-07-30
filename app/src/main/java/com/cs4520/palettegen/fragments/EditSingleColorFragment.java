package com.cs4520.palettegen.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs4520.palettegen.R;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

public class EditSingleColorFragment extends Fragment {
    private PaletteColorDisplayItem colorDisplayItem;

    SeekBar changeHueBar;
    SeekBar changeSaturationBar;
    SeekBar changeValueBar;

    public EditSingleColorFragment() {
        // Required empty constructor
    }

    public static EditSingleColorFragment newInstance(PaletteColorDisplayItem colorDisplayItem) {
        EditSingleColorFragment fragment = new EditSingleColorFragment();
        fragment.colorDisplayItem = colorDisplayItem;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_single_color_fragment, container, false);
        Color c = Color.valueOf(Integer.parseInt(colorDisplayItem.getColorString()));
        int[] hsv = getHSV(c);
        int hue = hsv[0];
        int sat = hsv[1];
        int val = hsv[2];
        changeHueBar = v.findViewById(R.id.changeHueBar);
        changeHueBar.setProgress(hue);
        changeSaturationBar = v.findViewById(R.id.changeSaturationBar);
        changeSaturationBar.setProgress(sat);
        changeValueBar = v.findViewById(R.id.changeValueBar);
        changeValueBar.setProgress(val);

        return v;
    }

    private int[] getHSV(Color c) {
        int[] hsv = new int[3];

        float r = c.red();
        float g = c.green();
        float b = c.blue();

        float min = Math.min(Math.min(r, g), b);
        float max = Math.max(Math.max(r, g), b);

        // set Value
        hsv[2] = Math.round(max * 255f);

        // Are we gray?
        if(max - min == 0) {
            // then we have no hue or saturation.
            hsv[0] = 0;
            hsv[1] = 0;
            return hsv;
        } else {
            // set Saturation
            hsv[1] = Math.round(((max - min) / max) * 255f);
        }

        float hue = 0f;
        if(max == r) {
            hue = (g - b) / (max - min);
        } else if (max == g) {
            hue = 2f + (b - r) / (max - min);
        } else if (max == b) {
            hue = 4f + (r - g) / (max - min);
        }
        hue *= 60;
        // set Hue
        hsv[0] = Math.round(hue > 0 ? hue : hue + 360);
        return hsv;
    }
}
