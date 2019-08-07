package com.cs4520.palettegen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs4520.palettegen.R;
import com.cs4520.palettegen.adapters.ColorListAdapter;
import com.cs4520.palettegen.model.EditableColor;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

public class EditSingleColorFragment extends Fragment {
    private ColorListAdapter adapter;
    private PaletteColorDisplayItem colorDisplayItem;

    private SeekBar changeHueBar;
    private SeekBar changeSaturationBar;
    private SeekBar changeValueBar;
    private Button revertChangesButton;

    private EditableColor newColor;

    public EditSingleColorFragment() {
        // Required empty constructor
    }

    public static EditSingleColorFragment newInstance(ColorListAdapter adapter, PaletteColorDisplayItem colorDisplayItem) {
        EditSingleColorFragment fragment = new EditSingleColorFragment();
        fragment.adapter = adapter;
        fragment.colorDisplayItem = colorDisplayItem.clone();
        fragment.newColor = new EditableColor(Integer.parseInt(colorDisplayItem.getColorString()));
        return fragment;
    }

    public void setColorDisplayItem(PaletteColorDisplayItem colorDisplayItem) {
        this.colorDisplayItem = colorDisplayItem.clone();
        this.newColor = new EditableColor(Integer.parseInt(colorDisplayItem.getColorString()));
        
        changeHueBar.setProgress(newColor.getHue());
        changeSaturationBar.setProgress(newColor.getSaturation());
        changeValueBar.setProgress(newColor.getValue());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_single_color_fragment, container, false);

        changeHueBar = v.findViewById(R.id.changeHueBar);
        changeHueBar.setProgress(newColor.getHue());
        changeHueBar.setOnSeekBarChangeListener(barChangeListener());

        changeSaturationBar = v.findViewById(R.id.changeSaturationBar);
        changeSaturationBar.setProgress(newColor.getSaturation());
        changeSaturationBar.setOnSeekBarChangeListener(barChangeListener());

        changeValueBar = v.findViewById(R.id.changeValueBar);
        changeValueBar.setProgress(newColor.getValue());
        changeValueBar.setOnSeekBarChangeListener(barChangeListener());

        revertChangesButton = v.findViewById(R.id.revertChangeButton);
        revertChangesButton.setOnClickListener(onRevertChangesButtonClick());

        Button saveChangesButton = v.findViewById(R.id.saveChangeButton);
        saveChangesButton.setOnClickListener(onSaveChangesButtonClick());

        return v;
    }

    public int getColor() {
        return newColor.getRgb();
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
        newColor.setHue(changeHueBar.getProgress());
        newColor.setSaturation(changeSaturationBar.getProgress());
        newColor.setValue(changeValueBar.getProgress());
        colorDisplayItem.setColorString(String.valueOf(newColor.getRgb()));
        adapter.colorShifted(colorDisplayItem);
    }

    private View.OnClickListener onSaveChangesButtonClick() {
        return view -> {
           colorDisplayItem.setColorString(String.valueOf(newColor.getRgb()));
           colorDisplayItem.setOriginalColorString(colorDisplayItem.getColorString());
           adapter.colorChanged(colorDisplayItem);
        };
    }

    private View.OnClickListener onRevertChangesButtonClick() {
        return view -> {
            newColor.setRgb(Integer.parseInt(colorDisplayItem.getOriginalColorString()));
            colorDisplayItem.setColorString(colorDisplayItem.getOriginalColorString());
            changeHueBar.setProgress(newColor.getHue());
            changeSaturationBar.setProgress(newColor.getSaturation());
            changeValueBar.setProgress(newColor.getValue());
            adapter.notifyDataSetChanged();
        };
    }
}
