package com.cs4520.palettegen.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cs4520.palettegen.MainActivity;
import com.cs4520.palettegen.R;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

/**
 * Attribution: Obtained from a guide to make swipe-able list views on www.devexchanges.info .
 *
 * http://www.devexchanges.info/2015/09/making-swipeable-listview-in-android.html
 *
 * Modified slightly for our purposes.
 */
// Adapter class based on swipe view guide from: http://www.devexchanges.info/2015/09/making-swipeable-listview-in-android.html
public class ListViewAdapter extends ArrayAdapter<String> {

    private class ViewHolder {
        private TextView name;
        private View[] colors;
        private View deleteButton;
        private View editButton;
        private SwipeLayout swipeLayout;

        ViewHolder(View v) {
            swipeLayout = v.findViewById(R.id.swipe_layout);
            deleteButton = v.findViewById(R.id.delete);
            editButton = v.findViewById(R.id.edit_query);
            name = v.findViewById(R.id.name);
            colors = new View[6];
            for (int i = 0; i < 6; i++) {
                colors[i] = v.findViewById(
                        v.getResources().getIdentifier(
                                "color" + (i+1), "View", mainActivity.getPackageName()
                        )
                );
            }

            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        }
    }

    private MainActivity mainActivity;
    private List<String> palettes;

    public ListViewAdapter(MainActivity context, int resource, List<String> palettes) {
        super(context, resource, palettes);
        this.mainActivity = context;
        this.palettes = palettes;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position));

        holder.editButton.setOnClickListener(onEditListener());
        holder.deleteButton.setOnClickListener(onDeleteListener());

        return convertView;
    }

    private View.OnClickListener onEditListener() {
        // TODO implement edit listener
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
