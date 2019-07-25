package com.cs4520.palettegen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cs4520.palettegen.adapters.PaletteListAdapter;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteViewModel;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView paletteListView;
    private PaletteListAdapter adapter;

    private PaletteViewModel mPaletteViewModel;

    public final static int NEW_PALETTE_ACTIVITY_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paletteListView = findViewById(R.id.savedPalettesList);

        // Get the Palette ViewModel and add an observer for the live data palettes
        mPaletteViewModel = ViewModelProviders.of(MainActivity.this).get(PaletteViewModel.class);

        mPaletteViewModel.getAllPalettes().observe(MainActivity.this, new Observer<List<Palette>>() {
            @Override
            public void onChanged(@Nullable final List<Palette> palettes) {
                // Update the cached copy of the words in the adapter.
                adapter.setPalettes(palettes);
            }
        });

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
            mPaletteViewModel.insert(palette);
        }
    }

    private void setListViewAdapter() {
        adapter = new PaletteListAdapter(MainActivity.this);
        paletteListView.setAdapter(adapter);
    }

    private View.OnClickListener addNewPaletteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToImageSelectIntent = new Intent(MainActivity.this, ImageSelectActivity.class);
                startActivityForResult(moveToImageSelectIntent, NEW_PALETTE_ACTIVITY_REQUEST);
            }
        };
    }

}
