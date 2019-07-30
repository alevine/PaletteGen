package com.cs4520.palettegen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs4520.palettegen.adapters.PaletteListAdapter;
import com.cs4520.palettegen.adapters.SwipeToDeleteCallback;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView paletteRecyclerView;
    private PaletteListAdapter adapter;
    PaletteDbHelper dbHelper;

    public final static int NEW_PALETTE_ACTIVITY_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paletteRecyclerView = findViewById(R.id.savedPalettesList);
        dbHelper = new PaletteDbHelper(MainActivity.this);

        // Set header and custom adapter for the ListView
        setListViewAdapter();

        // Create onClickListener for the floating action button
        ImageView addNewPaletteButton = findViewById(R.id.addNewPaletteButton);
        addNewPaletteButton.setOnClickListener(addNewPaletteListener());
    }

    // On activity result of the generate Palette activity, insert it into the database
    // and update the resulting UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // We have requested a new palette and it is completed, add it to the database
        if (requestCode == NEW_PALETTE_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            String colorString = data.getStringExtra("colorString");
            String paletteName = data.getStringExtra("paletteName");

            assert paletteName != null;
            assert colorString != null;
            Palette palette = new Palette(colorString, paletteName);

            // Insert new Palette into database, get all Palettes and update adapter
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING, colorString);
            values.put(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME, paletteName);

            // Insert the new row, returning the primary key value of the new row
            palette.setId((int) db.insert(PaletteContract.PaletteEntry.TABLE_NAME, null, values));

            adapter.getPalettes().add(palette);
            adapter.notifyDataSetChanged();
        }
    }

    private void setListViewAdapter() {
        List<Palette> palettes = new ArrayList<>();

        // Database setup stuff
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + PaletteContract.PaletteEntry.TABLE_NAME, null);

        // Boilerplate code to get all Palettes from the database.
        // Needed to show the database list on create of the main activity
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME));
                String colorstring = cursor.getString(cursor.getColumnIndex(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING));
                int id = cursor.getInt(cursor.getColumnIndex(PaletteContract.PaletteEntry._ID));

                Palette selected = new Palette(colorstring, name, id);
                palettes.add(selected);

                cursor.moveToNext();
            }
        }

        // Create the adapter with the given palettes list, set the layout manager
        // and adapter of the recycler view
        adapter = new PaletteListAdapter(MainActivity.this, palettes);
        paletteRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        paletteRecyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(paletteRecyclerView);

        cursor.close();
    }

    private View.OnClickListener addNewPaletteListener() {
        return view -> {
            Intent moveToImageSelectIntent = new Intent(MainActivity.this, ImageSelectActivity.class);
            startActivityForResult(moveToImageSelectIntent, NEW_PALETTE_ACTIVITY_REQUEST);
        };
    }

    // On destroy, close the helper
    @Override protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }


}
