package com.cs4520.palettegen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cs4520.palettegen.adapters.PaletteListAdapter;
import com.cs4520.palettegen.adapters.SwipeToDeleteCallback;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteDbController;
import com.cs4520.palettegen.db.PaletteDbHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PaletteDbHelper dbHelper;
    private RecyclerView paletteRecyclerView;
    private PaletteListAdapter adapter;
    private TextView emptyText;

    public final static int NEW_PALETTE_ACTIVITY_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PaletteMain", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new PaletteDbHelper(MainActivity.this);
        emptyText = findViewById(R.id.emptyText);

        paletteRecyclerView = findViewById(R.id.savedPalettesList);

        // Add separator for recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(paletteRecyclerView.getContext(),
                getRequestedOrientation());
        paletteRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set header and custom adapter for the ListView
        setListViewAdapter();

        // Create onClickListener for the floating action button
        ImageView addNewPaletteButton = findViewById(R.id.addNewPaletteButton);
        addNewPaletteButton.setOnClickListener(addNewPaletteListener());

        if (adapter.getPalettes().isEmpty()) {
            paletteRecyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            paletteRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    public PaletteDbHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // We update the adapter's palettes in case some have changed.
        adapter.setPalettes(PaletteDbController.getAllPalettes(dbHelper));
        adapter.notifyDataSetChanged();
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

            PaletteDbController.addPalette(palette, dbHelper);

            adapter.getPalettes().add(palette);
            adapter.notifyDataSetChanged();

            paletteRecyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }

    private void setListViewAdapter() {
        List<Palette> palettes = PaletteDbController.getAllPalettes(dbHelper);

        // Create the adapter with the given palettes list, set the layout manager
        // and adapter of the recycler view
        adapter = new PaletteListAdapter(MainActivity.this, palettes);
        paletteRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        paletteRecyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(paletteRecyclerView);;
    }

    private View.OnClickListener addNewPaletteListener() {
        return view -> {
            Intent moveToImageSelectIntent = new Intent(MainActivity.this, ImageSelectActivity.class);
            startActivityForResult(moveToImageSelectIntent, NEW_PALETTE_ACTIVITY_REQUEST);
        };
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
