package com.cs4520.palettegen.adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs4520.palettegen.PaletteActivity;
import com.cs4520.palettegen.R;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteContract;
import com.cs4520.palettegen.db.PaletteDbHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for the Recycler View. Follows Androids basic tutorial.
 */
public class PaletteListAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<Palette> mPalettes; // Cached copy of words
    private Palette recentlyDeleted;
    private int recentlyDeletedPosition;

    public PaletteListAdapter(Context context, List<Palette> palettes) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mPalettes = palettes;
    }

    @NonNull
    @Override
    public PaletteListAdapter.PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaletteViewHolder(mInflater.inflate(R.layout.item_listview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mPalettes != null) {
            Log.d("ADAPTER", "mPalettes != null");
            Log.d("ADAPTER", mPalettes.toString());

            Palette current = mPalettes.get(position);

            assert current != null;
            ((PaletteViewHolder) holder).name.setText(current.getPaletteName());

            // Parse colorstring and set colors
            for (int i = 0; i < 6; i++) {
                int color = Integer.parseInt(current.getColorString().split(",")[i]);
                ((PaletteViewHolder) holder).colors[i].setBackgroundColor(color);
            }

            // Finally set tag of the whole layout for future reference
            ((PaletteViewHolder) holder).name.setTag(current.getId());
        } else {
            // Do something here when no palettes?
            Log.d("ADAPTER", "mPalettes == null");
        }
    }

    @Override
    public int getItemCount() {
        if (mPalettes != null) {
            return mPalettes.size();
        } else {
            return 0;
        }
    }

    Context getContext() {
        return this.context;
    }

    public List<Palette> getPalettes() {
        if (this.mPalettes == null) {
            this.mPalettes = new ArrayList<>();
        }

        return this.mPalettes;
    }

    private class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        View[] colors;


        PaletteViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            this.name = itemView.findViewById(R.id.name);
            this.colors = new View[6];

            // Assign the correct color views
            this.colors[0] = itemView.findViewById(R.id.color1);
            this.colors[1] = itemView.findViewById(R.id.color2);
            this.colors[2] = itemView.findViewById(R.id.color3);
            this.colors[3] = itemView.findViewById(R.id.color4);
            this.colors[4] = itemView.findViewById(R.id.color5);
            this.colors[5] = itemView.findViewById(R.id.color6);
        }

        // Add an on click listener to this viewholder, moves to palette activity
        @Override public void onClick(View v) {
            int paletteId = (int) v.findViewById(R.id.name).getTag();

            Intent paletteDetailsIntent = new Intent(context, PaletteActivity.class);
            paletteDetailsIntent.putExtra("paletteId", paletteId);
            context.startActivity(paletteDetailsIntent);
        }
    }

    void deleteItem(int position) {
        this.recentlyDeleted = this.mPalettes.get(position);
        this.mPalettes.remove(position);

        PaletteDbHelper dbHelper = new PaletteDbHelper(this.context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define 'where' part of query.
        String selection = PaletteContract.PaletteEntry._ID + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { Integer.toString(this.recentlyDeleted.getId()) };

        // Issue SQL statement.
        db.delete(PaletteContract.PaletteEntry.TABLE_NAME, selection, selectionArgs);

        showUndoSnackbar(db);
        notifyDataSetChanged();

        showEmptyTextIfNoPalettes();
    }

    // Shows an "Undo" snackbar that will either go away on its own or undo the deletion
    private void showUndoSnackbar(SQLiteDatabase db) {
        View view = ((Activity) context).findViewById(R.id.mainLayout);

        Snackbar snackbar = Snackbar.make(view, R.string.undoDelete,
                Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", v -> undoDelete(db));
        snackbar.show();
    }

    private void undoDelete(SQLiteDatabase db) {
        mPalettes.add(recentlyDeletedPosition, recentlyDeleted);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_COLORSTRING, recentlyDeleted.getColorString());
        values.put(PaletteContract.PaletteEntry.COLUMN_NAME_PALETTE_NAME, recentlyDeleted.getPaletteName());

        // Insert the new row, returning the primary key value of the new row
        recentlyDeleted.setId((int) db.insert(PaletteContract.PaletteEntry.TABLE_NAME, null, values));

        notifyItemInserted(recentlyDeletedPosition);

        db.close();

        showEmptyTextIfNoPalettes();
    }

    private void showEmptyTextIfNoPalettes() {
        RecyclerView recyclerView = ((Activity) context).findViewById(R.id.savedPalettesList);
        TextView emptyText = ((Activity) context).findViewById(R.id.emptyText);

        if (this.mPalettes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        }
    }
}
