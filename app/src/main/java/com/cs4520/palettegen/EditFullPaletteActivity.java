package com.cs4520.palettegen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteDbController;
import com.cs4520.palettegen.db.PaletteDbHelper;
import com.cs4520.palettegen.model.EditableColor;

import java.util.Objects;

public class EditFullPaletteActivity extends AppCompatActivity {

    private PaletteDbHelper dbHelper;

    private int paletteId;
    private Palette palette;
    private int[] colorsRGB;
    private EditableColor[] editableColors;

    private EditText paletteNameEditText;
    private View[] paletteColorViews;
    private SeekBar changeSaturationBar;
    private SeekBar changeValueBar;

    private RadioGroup colorFiltersRadioGroup;
    private static int NO_FILTER = R.id.noFilterRadioButton;
    private static int INVERT_FILTER = R.id.invertColorsRadioButton;
    private static int GRAYSCALE_FILTER = R.id.grayscaleColorsRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_full_palette);

        dbHelper = new PaletteDbHelper(EditFullPaletteActivity.this);

        if(getIntent().hasExtra("paletteId")) {
            paletteId = Objects.requireNonNull(getIntent().getExtras()).getInt("paletteId");
        }

        palette = PaletteDbController.getPalette(paletteId, dbHelper);

        paletteNameEditText = findViewById(R.id.paletteNameEditText);
        paletteNameEditText.setText(palette.getPaletteName());

        paletteColorViews = new View[6];
        paletteColorViews[0] = findViewById(R.id.paletteColor1);
        paletteColorViews[1] = findViewById(R.id.paletteColor2);
        paletteColorViews[2] = findViewById(R.id.paletteColor3);
        paletteColorViews[3] = findViewById(R.id.paletteColor4);
        paletteColorViews[4] = findViewById(R.id.paletteColor5);
        paletteColorViews[5] = findViewById(R.id.paletteColor6);

        String[] colors = palette.getColorString().split(",");
        editableColors = new EditableColor[6];
        colorsRGB = new int[6];
        for(int i = 0; i < 6; i++) {
            editableColors[i] = new EditableColor(Integer.parseInt(colors[i]));
            colorsRGB[i] = editableColors[i].getRgb();
        }
        updateColorViews();

        changeSaturationBar = findViewById(R.id.changeAllSaturationBar);
        changeSaturationBar.setProgress(getAverageSaturation());
        changeSaturationBar.setOnSeekBarChangeListener(onSeekBarChangeListener());
        changeValueBar = findViewById(R.id.changeAllValueBar);
        changeValueBar.setProgress(getAverageValue());
        changeValueBar.setOnSeekBarChangeListener(onSeekBarChangeListener());

        colorFiltersRadioGroup = findViewById(R.id.filterRadioGroup);
        colorFiltersRadioGroup.setOnCheckedChangeListener(onFilterGroupCheckedChange());

        Button revertChangesButton = findViewById(R.id.revertChangesButton);
        revertChangesButton.setOnClickListener(onRevertButtonClick());
        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(onSaveButtonClick());
    }

    private void updateColorViews() {
        for(int i = 0; i < 6; i++) {
            paletteColorViews[i].setBackgroundColor(editableColors[i].getRgb());
        }
    }

    private int getAverageSaturation() {
        int total = 0;
        for(EditableColor c : editableColors) {
            total += c.getSaturation();
        }
        return total / 6;
    }

    private int getAverageValue() {
        int total = 0;
        for(EditableColor c : editableColors) {
            total += c.getValue();
        }
        return total / 6;
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                registerChangeColors();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                registerChangeColors();
            }
        };
    }

    private void registerChangeColors() {
        int changeInSat = changeSaturationBar.getProgress() - getAverageSaturation();
        int changeInVal = changeValueBar.getProgress() - getAverageValue();

        for(EditableColor c : editableColors) {
            c.setSaturation(Math.min(Math.max(c.getSaturation() + changeInSat, 0), 255));
            c.setValue(Math.min(Math.max(c.getValue() + changeInVal, 0), 255));
        }

        updateColorViews();
    }

    private RadioGroup.OnCheckedChangeListener onFilterGroupCheckedChange() {
        return (radioGroup, i) -> {
            if(i == NO_FILTER) {
                for(EditableColor c : editableColors) {
                    c.revertFilter();
                }
            } else if (i == INVERT_FILTER) {
                for(EditableColor c : editableColors) {
                    c.applyInvertFilter();
                }
            } else if (i == GRAYSCALE_FILTER) {
                for(EditableColor c : editableColors) {
                    c.applyGrayscaleFilter();
                }
            }

            updateColorViews();
        };
    }

    private View.OnClickListener onRevertButtonClick() {
        return view -> {
            paletteNameEditText.setText(palette.getPaletteName());
            for(int i = 0; i < 6; i++) {
                editableColors[i].setRgb(colorsRGB[i]);
            }
            updateColorViews();
            changeSaturationBar.setProgress(getAverageSaturation());
            changeValueBar.setProgress(getAverageValue());
            colorFiltersRadioGroup.check(NO_FILTER);
        };
    }

    private View.OnClickListener onSaveButtonClick() {
        return view -> {
            StringBuilder colorStringBuilder = new StringBuilder();
            int i = 0;
            for(EditableColor c : editableColors) {
                if(i == 5) {
                    colorStringBuilder.append(c.getRgb());
                } else {
                    colorStringBuilder.append(c.getRgb()).append(",");
                }
                i++;
            }
            palette.setPaletteName(paletteNameEditText.getText().toString());
            palette.setColorString(colorStringBuilder.toString());
            PaletteDbController.updatePalette(paletteId, palette, dbHelper);

            finish();
        };
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
