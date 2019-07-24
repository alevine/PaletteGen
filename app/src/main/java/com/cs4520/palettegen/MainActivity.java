package com.cs4520.palettegen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cs4520.palettegen.adapters.ListViewAdapter;
import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> savedPalettes;
    private TextView totalPalettes;
    private SwipeLayout swipeLayout;

    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.savedPalettesListView);

        savedPalettes = new ArrayList<>();

        // TODO get the data from file.
        for (int i = 0; i < 20; i++) {
            savedPalettes.add("Palette " + i);
        }

        setListViewHeader();
        setListViewAdapter();

        ImageView addNewPaletteButton = findViewById(R.id.addNewPaletteButton);
        addNewPaletteButton.setOnClickListener(addNewPaletteListener());
    }

    private void setListViewHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.header_listview, listView, false);
        totalPalettes = header.findViewById(R.id.total);
        swipeLayout = header.findViewById(R.id.swipe_layout);
        setSwipeViewFeatures();
        listView.addHeaderView(header);
    }

    private void setSwipeViewFeatures() {
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_wrapper));

        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                Log.i(TAG, "onStartOpen");
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                Log.i(TAG, "onOpen");

            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                Log.i(TAG, "onStartClose");

            }

            @Override
            public void onClose(SwipeLayout layout) {
                Log.i(TAG, "onClose");

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                Log.i(TAG, "onUpdate - swiping");

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                Log.i(TAG, "onHandRelease");
                if(swipeLayout != null) {
                    swipeLayout.close();
                }
            }
        });
    }

    private void setListViewAdapter() {
        adapter = new ListViewAdapter(this, R.layout.item_listview, savedPalettes);
        listView.setAdapter(adapter);

        totalPalettes.setText(String.format("(%s)", savedPalettes.size()));
    }

    private View.OnClickListener addNewPaletteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moveToImageSelectIntent = new Intent(MainActivity.this, ImageSelectActivity.class);
                startActivity(moveToImageSelectIntent);
            }
        };
    }

    public void updateAdapter() {
        adapter.notifyDataSetChanged(); // update adapter
        totalPalettes.setText(String.format("(%s)", savedPalettes.size()));
    }

}
