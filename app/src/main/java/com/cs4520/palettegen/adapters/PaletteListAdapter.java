package com.cs4520.palettegen.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs4520.palettegen.MainActivity;
import com.cs4520.palettegen.PaletteActivity;
import com.cs4520.palettegen.R;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteDbController;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Comparator;
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
        sortPalettesAlphabetically();
    }

    @NonNull
    @Override
    public PaletteListAdapter.PaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaletteViewHolder(mInflater.inflate(R.layout.item_listview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mPalettes != null) {
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

    public void setPalettes(List<Palette> palettes) {
        this.mPalettes = palettes;
        sortPalettesAlphabetically();
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
        this.recentlyDeletedPosition = position;
        this.mPalettes.remove(position);

        PaletteDbController.deletePalette(this.recentlyDeleted.getId(), ((MainActivity) this.context).getDbHelper());

        showUndoSnackbar();
        notifyDataSetChanged();

        showEmptyTextIfNoPalettes();
    }

    // Shows an "Undo" snackbar that will either go away on its own or undo the deletion
    private void showUndoSnackbar() {
        View view = ((Activity) context).findViewById(R.id.mainLayout);

        Snackbar snackbar = Snackbar.make(view, R.string.undoDelete,
                Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        mPalettes.add(recentlyDeletedPosition, recentlyDeleted);
        sortPalettesAlphabetically();

        PaletteDbController.addPalette(recentlyDeleted, ((MainActivity) context).getDbHelper());
        notifyItemInserted(recentlyDeletedPosition);

        recentlyDeleted = null;
        recentlyDeletedPosition = -1;

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

    private void sortPalettesAlphabetically() {
        mPalettes.sort(Palette::compareTo);
    }

    public void notifyDataSetChangedAndSort() {
        sortPalettesAlphabetically();
        notifyDataSetChanged();
    }
}
