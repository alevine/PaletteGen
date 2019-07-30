package com.cs4520.palettegen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cs4520.palettegen.R;

import java.util.List;

public class ColorListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> colors;

    public ColorListAdapter(Context c, List<String> colors) {
        this.inflater = LayoutInflater.from(c);
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int i) {
        return colors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ColorViewHolder vh;
        if(view == null) {
            view = inflater.inflate(R.layout.item_color_list, viewGroup, false);
            vh = new ColorViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ColorViewHolder) view.getTag();
        }

        String item = (String) getItem(i);
        if(item != null) {
            vh.colorDisplay.setText(rgbToHex(item));
            vh.colorDisplay.setBackgroundColor(Integer.parseInt(item));
        }

        return view;
    }

    private String rgbToHex(String s) {
        return "#" + Integer.toHexString(Integer.parseInt(s)).toUpperCase();
    }

    private class ColorViewHolder {
        public View view;
        public TextView colorDisplay;
        public FrameLayout editColorFrame;

        public ColorViewHolder(View v) {
            this.view = v;
            this.colorDisplay = v.findViewById(R.id.colorDisplay);
            this.editColorFrame = v.findViewById(R.id.editColorFrame);
        }
    }
}
