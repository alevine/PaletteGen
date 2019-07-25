package com.cs4520.palettegen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteViewModel;

public class PaletteActivity extends AppCompatActivity {

    ImageView settingsButton;
    PaletteViewModel paletteViewModel;
    View[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        paletteViewModel = ViewModelProviders.of(PaletteActivity.this).get(PaletteViewModel.class);

        settingsButton = findViewById(R.id.paletteViewSettingsButton);
        settingsButton.setOnClickListener(settingsButtonListener());

        // Get intent and given path from the camera intent
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        colors = new View[6];

        colors[0] = findViewById(R.id.palette1);
        colors[1] = findViewById(R.id.palette2);
        colors[2] = findViewById(R.id.palette3);
        colors[3] = findViewById(R.id.palette4);
        colors[4] = findViewById(R.id.palette5);
        colors[5] = findViewById(R.id.palette6);

        if (extras != null) {
            if (extras.containsKey("paletteId")) {
                int paletteId = extras.getInt("paletteId");

                paletteViewModel.getPalette(paletteId);

                paletteViewModel.getSearchResults().observe(this, new Observer<Palette>() {
                    @Override
                    public void onChanged(@Nullable final Palette palette) {
                        String[] colorList = palette.getColorString().split(",");

                        for (int i = 0; i < 6; i++) {
                            colors[i].setBackgroundColor(Integer.parseInt(colorList[i]));
                        }
                    }
                });
            }
        }

    }

    private View.OnClickListener settingsButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: implement on click method to view/change settings
            }
        };
    }

}
