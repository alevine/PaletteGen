package com.cs4520.palettegen.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs4520.palettegen.PaletteActivity;
import com.cs4520.palettegen.R;
import com.cs4520.palettegen.db.Palette;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.List;

/**
 * Attribution: Obtained from a guide to make swipe-able list views on www.devexchanges.info .
 *
 * http://www.devexchanges.info/2015/09/making-swipeable-listview-in-android.html
 *
 * Modified slightly for our purposes.
 */
public class PaletteListAdapter extends BaseSwipeAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private List<Palette> mPalettes; // Cached copy of words

    public PaletteListAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override public int getCount() {
        if (mPalettes != null) {
            return mPalettes.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (mPalettes != null) {
            return mPalettes.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return mPalettes.get(i).getId();
    }

    private View.OnClickListener onEditListener() {
        // TODO implement edit listener
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int paletteId = (int) view.getTag();

                Intent paletteDetailsIntent = new Intent(context, PaletteActivity.class);
                paletteDetailsIntent.putExtra("paletteId", paletteId);
                context.startActivity(paletteDetailsIntent);

                Log.e("ListViewAdapter - edit", "Edit saved palette not yet implemented.");
            }
        };
    }

    private View.OnClickListener onDeleteListener() {
        // TODO implement delete listener
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("ListViewAdapter - delete", "Delete saved palette not yet implemented");
            }
        };
    }

    public void setPalettes(List<Palette> mPalettes) {
        this.mPalettes = mPalettes;
        notifyDataSetChanged();
    }

    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_layout;
    }

    // Here just inflate the view
    @Override public View generateView(int position, ViewGroup parent) {
        return mInflater.inflate(R.layout.item_listview, parent, false);
    }

    // Populate values and set on click here
    @Override public void fillValues(int position, View convertView) {
        SwipeLayout swipeLayout = convertView.findViewById(R.id.swipe_layout);
        View deleteButton = convertView.findViewById(R.id.delete);
        View editButton = convertView.findViewById(R.id.edit_query);
        TextView name = convertView.findViewById(R.id.name);
        View[] colors = new View[6];

        // Assign the correct color views
        colors[0] = convertView.findViewById(R.id.color1);
        colors[1] = convertView.findViewById(R.id.color2);
        colors[2] = convertView.findViewById(R.id.color3);
        colors[3] = convertView.findViewById(R.id.color4);
        colors[4] = convertView.findViewById(R.id.color5);
        colors[5] = convertView.findViewById(R.id.color6);

        // Set SwipeLayout show mode
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        if (mPalettes != null) {
            Palette current = mPalettes.get(position);

            assert current != null;
            name.setText(current.getPaletteName());

            editButton.setOnClickListener(onEditListener());
            deleteButton.setOnClickListener(onDeleteListener());


            // Parse colorstring and set colors
            for (int i = 0; i < 6; i++) {
                int color = Integer.parseInt(current.getColorString().split(",")[i]);
                colors[i].setBackgroundColor(color);
            }

            // Finally set tag of the whole layout for future reference
            editButton.setTag(current.getId());
        } else {
            // Do something here when no palettes?
        }
    }

}
