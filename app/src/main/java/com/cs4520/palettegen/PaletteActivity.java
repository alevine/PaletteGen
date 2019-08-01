package com.cs4520.palettegen;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaletteActivity extends AppCompatActivity {
    private PaletteDbHelper dbHelper;
    private int paletteId;
    private final String TAG = "PaletteActivity";
    private PaletteDbHelper helper;
    private SQLiteDatabase db;
    private boolean isFABOpen = false;

    public static int DISPLAY_MODE_HEX = 0;
    public static int DISPLAY_MODE_RGB = 1;
    private int colorStringDisplayMode;

    private TextView paletteName;
    private ListView colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_palette);
        dbHelper = new PaletteDbHelper(PaletteActivity.this);

        paletteName = findViewById(R.id.paletteViewTitle);

        EditText paletteName = findViewById(R.id.paletteViewTitle);

        ImageView settingsButton = findViewById(R.id.paletteViewSettingsButton);
        settingsButton.setOnClickListener(settingsButtonListener());

        colorList = findViewById(R.id.colorList);

        Switch displayModeSwitch = findViewById(R.id.viewModeToggle);
        displayModeSwitch.setOnCheckedChangeListener(onViewModeSwitchChanged());

        // FAB and menu items for exporting the palette
        SpeedDialView speedDialView = findViewById(R.id.speedDial);

        // Add copy item
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_action_copy, R.drawable.ic_copy_fab)
                        .setLabel("Copy CSV")
                        .create()
        );

        // Add download item
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_action_download, R.drawable.ic_download_fab)
                        .setLabel("Download CSV")
                        .create()
        );

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

                helper = new PaletteDbHelper(PaletteActivity.this);
                db = helper.getReadableDatabase();

                String[] projection = {
                    BaseColumns._ID,
                    PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME,
                    PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING
                };

                // Filter results WHERE "ID" = paletteId'
                String paletteSelection = BaseColumns._ID + " = ?";
                String[] paletteSelectionArgs = { Integer.toString(paletteId) };

                Cursor cursor = db.query(
                        PaletteContract.PaletteEntry.TABLE_NAME,   // The table to query
                        projection,                                // The array of columns to return (pass null to get all)
                        paletteSelection,                          // The columns for the WHERE clause
                        paletteSelectionArgs,                      // The value(s) to compare for the WHERE
                        null,
                        null,
                        null
                );

                cursor.moveToNext();
                String colorString = cursor.getString(
                        cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
                String name = cursor.getString(
                        cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));

                paletteName.setText(name);

                int count = 0;

                for(String s : colorString.split(",")) {
                    colors.add(new PaletteColorDisplayItem(count, s));
                    count++;
                }

                speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
                    String formattedColorString = getFormattedColorString(colors);

                    switch (speedDialActionItem.getId()) {
                        case R.id.fab_action_copy:
                            // Copy the CSV colorstring to clipboard
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

                            ClipData clip = ClipData.newPlainText("copiedPalette", formattedColorString);
                            assert clipboard != null;
                            clipboard.setPrimaryClip(clip);

                            // False to close the speed dial (as confirmation?)
                            return false;
                        case R.id.fab_action_download:
                            // Download the CSV colorstring

                            String paletteFileName = "test download";

                            // Create the temp file for saving and write to it
                            try {
                                // Get the directory for the user's public downloads directory.
                                File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                                File download = new File(dir.getAbsolutePath() + paletteFileName);

                                if (download.createNewFile()) {
                                    FileOutputStream out = new FileOutputStream(download);
                                    out.write(formattedColorString.getBytes());
                                    out.close();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return false;
                        default:
                            return false;
                    }
                });

                cursor.close();

                paletteName.setOnEditorActionListener((textView, i, keyEvent) -> {
                    if (i == EditorInfo.IME_ACTION_NEXT) {
                        // Update name of palette
                        updateName(db, paletteName);
                        hideKeyboard();
                    }
                    return true;
                });
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
        for (String s : palette.getColorString().split(",")) {
            colors.add(new PaletteColorDisplayItem(count, s));
            count++;
        }

        colorList.setAdapter(new ColorListAdapter(getSupportFragmentManager(),
                PaletteActivity.this, colors, paletteId, palette.getPaletteName()));
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
        return (compoundButton, b) -> {
            colorStringDisplayMode = b ? DISPLAY_MODE_RGB : DISPLAY_MODE_HEX;
            ((ColorListAdapter) colorList.getAdapter()).setDisplayMode(colorStringDisplayMode);
            ((ColorListAdapter) colorList.getAdapter()).notifyDataSetChanged();
        };
    }

    private void updateName(SQLiteDatabase db, EditText paletteName) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME, paletteName.getText().toString());

        // Define 'where' part of query.
        String selection = PaletteContract.PaletteEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { Integer.toString(this.paletteId) };

        // Update entry for this palette.
        db.update(PaletteContract.PaletteEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    // On destroy, close the helper
    @Override protected void onDestroy() {
        db.close();
        helper.close();
        super.onDestroy();
    }

    /* Gets the formatted string to copy / download from the current list of colors */
    private String getFormattedColorString(List<PaletteColorDisplayItem> colors) {
        StringBuilder result = new StringBuilder();

        if (colorStringDisplayMode == DISPLAY_MODE_HEX) {
            for (PaletteColorDisplayItem p : colors) {
                result.append(p.getHexString()).append(",");
            }

            // cut off last comma
            return result.substring(0, result.length() - 2);
        } else {
            for (PaletteColorDisplayItem p : colors) {
                result.append(p.getLegibleRgb()).append(",");
            }

            // cut off last comma
            return result.substring(0, result.length() - 2);
        }
    }

    /* Hides the soft keyboard if it's open.

       Attr: https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();

        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }

        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
