package com.cs4520.palettegen.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs4520.palettegen.R;
import com.cs4520.palettegen.adapters.ColorListAdapter;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

public class EditSingleColorFragment extends Fragment {
    private ColorListAdapter adapter;
    private PaletteColorDisplayItem colorDisplayItem;

    private SeekBar changeHueBar;
    private SeekBar changeSaturationBar;
    private SeekBar changeValueBar;
    private View editedColorView;
    private Button saveChangesButton;

    private int newColor;

    public EditSingleColorFragment() {
        // Required empty constructor
    }

    public static EditSingleColorFragment newInstance(ColorListAdapter adapter, PaletteColorDisplayItem colorDisplayItem) {
        EditSingleColorFragment fragment = new EditSingleColorFragment();
        fragment.adapter = adapter;
        fragment.colorDisplayItem = colorDisplayItem.clone();
        fragment.newColor = Integer.parseInt(colorDisplayItem.getColorString());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_single_color_fragment, container, false);
        int[] hsv = getHSV(newColor);
        int hue = hsv[0];
        int sat = hsv[1];
        int val = hsv[2];

        changeHueBar = v.findViewById(R.id.changeHueBar);
        changeHueBar.setProgress(hue);
        changeHueBar.setOnSeekBarChangeListener(barChangeListener());

        changeSaturationBar = v.findViewById(R.id.changeSaturationBar);
        changeSaturationBar.setProgress(sat);
        changeSaturationBar.setOnSeekBarChangeListener(barChangeListener());

        changeValueBar = v.findViewById(R.id.changeValueBar);
        changeValueBar.setProgress(val);
        changeValueBar.setOnSeekBarChangeListener(barChangeListener());

        editedColorView = v.findViewById(R.id.editedColorView);
        editedColorView.setBackgroundColor(newColor);

        saveChangesButton = v.findViewById(R.id.saveChangeButton);
        saveChangesButton.setOnClickListener(onSaveChangesButtonClick());

        return v;
    }

    private int[] getHSV(int color) {
        Color c = Color.valueOf(color);

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

    private SeekBar.OnSeekBarChangeListener barChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateNewColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateNewColor();
            }
        };
    }

    private void updateNewColor() {
        newColor = getRGBfromHSV(changeHueBar.getProgress(), changeSaturationBar.getProgress(), changeValueBar.getProgress());
        editedColorView.setBackgroundColor(newColor);
    }

    private int getRGBfromHSV(int hue, int sat, int val) {
        float satFloat = sat / 255f;
        float valFloat = val / 255f;

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
        return c.toArgb();
    }

    private View.OnClickListener onSaveChangesButtonClick() {
        return view -> {
           colorDisplayItem.setColorString(String.valueOf(newColor));
           adapter.colorChanged(colorDisplayItem);
        };
    }
}
