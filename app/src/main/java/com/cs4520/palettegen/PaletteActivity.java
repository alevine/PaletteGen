package com.cs4520.palettegen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import java.io.File;

public class PaletteActivity extends AppCompatActivity {

    ImageView settingsButton;
    View[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

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
            if (extras.containsKey("currentPhotoLocation")) {
                String currentPhotoPath = extras.getString("currentPhotoLocation");

                assert currentPhotoPath != null;
                Log.d("path", currentPhotoPath);

                // Use preferred ARGB_8888 decoding for 4 byte pixels
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                // Generate bitmap from the file path
                File f = new File(currentPhotoPath);
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);

                Palette p = createPaletteSync(bitmap);
                getColorsFromPalette(p);
            }
        }

    }

    // Generate palette synchronously and return it
    public Palette createPaletteSync(Bitmap bitmap) {
        return Palette.from(bitmap).generate();
    }

    private View.OnClickListener settingsButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: implement on click method to view/change settings
            }
        };
    }

    // Get the colors from a given palette and assign them to a simple view
    public void getColorsFromPalette(Palette p) {
        int i = 0;

        for (Palette.Swatch s : p.getSwatches()) {
            if (i == 6) {
                break;
            }

            colors[i].setBackgroundColor(s.getRgb());

            i++;
        }
    }

}
