package com.cs4520.palettegen;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbHelper;

public class PaletteActivity extends AppCompatActivity {

    ImageView settingsButton;
    TextView paletteName;
    TextView[] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        paletteName = findViewById(R.id.paletteViewTitle);

        settingsButton = findViewById(R.id.paletteViewSettingsButton);
        settingsButton.setOnClickListener(settingsButtonListener());

        // Get intent and given path from the camera intent
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        colors = new TextView[6];

        colors[0] = findViewById(R.id.palette1);
        colors[1] = findViewById(R.id.palette2);
        colors[2] = findViewById(R.id.palette3);
        colors[3] = findViewById(R.id.palette4);
        colors[4] = findViewById(R.id.palette5);
        colors[5] = findViewById(R.id.palette6);

        if (extras != null) {
            if (extras.containsKey("paletteId")) {
                int paletteId = extras.getInt("paletteId");

                PaletteDbHelper dbHelper = new PaletteDbHelper(PaletteActivity.this);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] projection = {
                    BaseColumns._ID,
                    PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME,
                    PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING
                };

                // Filter results WHERE "ID" = paletteId'
                String selection = BaseColumns._ID + " = ?";
                String[] selectionArgs = { Integer.toString(paletteId) };

                Cursor cursor = db.query(
                        PaletteContract.PaletteEntry.TABLE_NAME,   // The table to query
                        projection,                                // The array of columns to return (pass null to get all)
                        selection,                                 // The columns for the WHERE clause
                        selectionArgs,
                        null,
                        null,
                        null
                );

                cursor.moveToNext();
                String colorString = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
                String name = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));

                paletteName.setText(name);

                String[] colorList = colorString.split(",");

                for (int i = 0; i < 6; i++) {
                    colors[i].setBackgroundColor(Integer.parseInt(colorList[i]));
                    colors[i].setText("#" + Integer.toHexString(Integer.parseInt(colorList[i])).toUpperCase());
                }

                cursor.close();
            }
        }

    }

    private View.OnClickListener settingsButtonListener() {
        return view -> {
            // TODO: implement on click method to view/change settings
        };
    }

}
