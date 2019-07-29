package com.cs4520.palettegen.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cs4520.palettegen.R;
import com.cs4520.palettegen.db.Palette;

import java.util.List;


/**
 * Adapter for the Recycler View. Follows Androids basic tutorial.
 */
public class PaletteListAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<Palette> mPalettes; // Cached copy of words

    public PaletteListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
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

    public void setPalettes(List<Palette> mPalettes) {
        this.mPalettes = mPalettes;
        notifyDataSetChanged();
    }

    private class PaletteViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        View[] colors;


        PaletteViewHolder(@NonNull View itemView) {
            super(itemView);

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
    }

    void deleteItem(int position) {
        Log.d("ADAPTER", "Delete Item Pushed");
        notifyDataSetChanged();
    }
}
