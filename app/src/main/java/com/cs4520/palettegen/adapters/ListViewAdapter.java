package com.cs4520.palettegen.adapters;

import android.content.Context;
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

public class ListViewAdapter extends ArrayAdapter<String> {

    private class ViewHolder {
        private TextView name;
        private View deleteButton;
        private View editButton;
        private SwipeLayout swipeLayout;

        public ViewHolder(View v) {
            swipeLayout = v.findViewById(R.id.swipe_layout);
            deleteButton = v.findViewById(R.id.delete);
            editButton = v.findViewById(R.id.edit_query);
            name = v.findViewById(R.id.name);

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
