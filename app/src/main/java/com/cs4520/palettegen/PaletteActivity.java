package com.cs4520.palettegen;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs4520.palettegen.adapters.ColorListAdapter;
import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbHelper;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

import java.util.ArrayList;
import java.util.List;

public class PaletteActivity extends AppCompatActivity {

    // ID of currently editing palette
    private int paletteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);

        TextView paletteName = findViewById(R.id.paletteViewTitle);

        ImageView settingsButton = findViewById(R.id.paletteViewSettingsButton);
        settingsButton.setOnClickListener(settingsButtonListener());

        ListView colorList = findViewById(R.id.colorList);

        // Get intent and given path from the camera intent
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        List<PaletteColorDisplayItem> colors = new ArrayList<>();

        /* This whole block uses the db to get the palette from the ID we send */
        if (extras != null) {
            if (extras.containsKey("paletteId")) {
                paletteId = extras.getInt("paletteId");

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
                        selectionArgs,                             // The value(s) to compare for the WHERE
                        null,
                        null,
                        null
                );

                cursor.moveToNext();
                String colorString = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
                String name = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));

                paletteName.setText(name);

                int count = 0;
                for(String s : colorString.split(",")) {
                    colors.add(new PaletteColorDisplayItem(count, s));
                    count++;
                }

                cursor.close();
            }
        }

        if (colors.size() == 0) {
            Log.e("Palette Not Found", "Palette Activity couldn't find corresponding palette.");
        } else {
            colorList.setAdapter(new ColorListAdapter(getSupportFragmentManager(),
                    PaletteActivity.this, colors, paletteId, paletteName.getText().toString()));
        }
    }

    private View.OnClickListener settingsButtonListener() {
        return view -> {
            // TODO: implement on click method to view/change settings
        };
    }
}
