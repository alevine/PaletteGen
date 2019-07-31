package com.cs4520.palettegen;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs4520.palettegen.adapters.ColorListAdapter;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbController;
import com.cs4520.palettegen.db.PaletteDbHelper;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

import java.util.ArrayList;
import java.util.List;

public class PaletteActivity extends AppCompatActivity {
    private PaletteDbHelper dbHelper;
    private int paletteId;

    public static int DISPLAY_MODE_HEX = 0;
    public static int DISPLAY_MODE_RGB = 1;
    private int colorStringDisplayMode;

    private ImageView settingsButton;
    private TextView paletteName;
    private ListView colorList;
    private Switch displayModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);
        dbHelper = new PaletteDbHelper(PaletteActivity.this);

        paletteName = findViewById(R.id.paletteViewTitle);

        settingsButton = findViewById(R.id.paletteViewSettingsButton);
        settingsButton.setOnClickListener(settingsButtonListener());

        colorList = findViewById(R.id.colorList);

        displayModeSwitch = findViewById(R.id.viewModeToggle);
        displayModeSwitch.setOnCheckedChangeListener(onViewModeSwitchChanged());

        // Get intent and given path from the camera intent
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        List<PaletteColorDisplayItem> colors = new ArrayList<>();

        /* This whole block uses the db to get the palette from the ID we send */
        if (extras != null) {
            if (extras.containsKey("paletteId")) {
                paletteId = extras.getInt("paletteId");
                Palette palette = PaletteDbController.getPalette(paletteId, dbHelper);
                paletteName.setText(palette.getPaletteName());

                int count = 0;
                for(String s : palette.getColorString().split(",")) {
                    colors.add(new PaletteColorDisplayItem(count, s));
                    count++;
                }
            }
        }

        if (colors.size() == 0) {
            Log.e("Palette Not Found", "Palette Activity couldn't find corresponding palette.");
        } else {
            colorList.setAdapter(new ColorListAdapter(getSupportFragmentManager(),
                    PaletteActivity.this, colors, paletteId, paletteName.getText().toString()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PaletteColorDisplayItem> colors = new ArrayList<>();
        Palette palette = PaletteDbController.getPalette(paletteId, dbHelper);

        paletteName.setText(palette.getPaletteName());

        int count = 0;
        for(String s : palette.getColorString().split(",")) {
            colors.add(new PaletteColorDisplayItem(count, s));
            count++;
        }

        colorList.setAdapter(new ColorListAdapter(getSupportFragmentManager(), PaletteActivity.this, colors, paletteId, palette.getPaletteName()));
    }

    public PaletteDbHelper getDbHelper() {
        return dbHelper;
    }

    private View.OnClickListener settingsButtonListener() {
        return view -> {
            Intent moveToEditPaletteIntent = new Intent(PaletteActivity.this, EditFullPaletteActivity.class);
            moveToEditPaletteIntent.putExtra("paletteId", this.paletteId);
            startActivity(moveToEditPaletteIntent);
        };
    }

    private CompoundButton.OnCheckedChangeListener onViewModeSwitchChanged() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                colorStringDisplayMode = b ? DISPLAY_MODE_RGB : DISPLAY_MODE_HEX;
                ((ColorListAdapter) colorList.getAdapter()).setDisplayMode(colorStringDisplayMode);
                ((ColorListAdapter) colorList.getAdapter()).notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
