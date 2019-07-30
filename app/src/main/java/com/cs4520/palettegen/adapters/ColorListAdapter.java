package com.cs4520.palettegen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cs4520.palettegen.R;
import com.cs4520.palettegen.fragments.EditSingleColorFragment;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;

import java.util.ArrayList;
import java.util.List;

public class ColorListAdapter extends BaseAdapter {
    private FragmentManager fm;
    private LayoutInflater inflater;
    private List<PaletteColorDisplayItem> colors;
    private List<Integer> fragmentFrameIds;
    private List<EditSingleColorFragment> fragments;

    public ColorListAdapter(FragmentManager fm, Context c, List<PaletteColorDisplayItem> colors) {
        this.fm = fm;
        this.inflater = LayoutInflater.from(c);
        this.colors = colors;
        this.fragmentFrameIds = new ArrayList<>();
        this.fragments = new ArrayList<>();
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
        PaletteColorDisplayItem item = (PaletteColorDisplayItem) getItem(i);

        if(view == null) {
            view = inflater.inflate(R.layout.item_color_list, viewGroup, false);
            vh = new ColorViewHolder(item, view);
            view.setTag(vh);
        } else {
            vh = (ColorViewHolder) view.getTag();
        }

        if(this.fragmentFrameIds.size() <= i) {
            int id = View.generateViewId();
            vh.editColorFrame.setId(id);
            this.fragmentFrameIds.add(id);
        } else if (vh.editColorFrame.getId() != this.fragmentFrameIds.get(i)) {
            vh.editColorFrame.setId(this.fragmentFrameIds.get(i));
        }

        if(this.fragments.size() == i) {
            this.fragments.add(EditSingleColorFragment.newInstance(item));
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(vh.editColorFrame.getId(), this.fragments.get(i), "fragment" + item.getId());
            ft.commit();

        }

        if(item != null) {
            vh.colorDisplay.setOnClickListener(onClickItemListener());
            vh.colorDisplay.setText(rgbToHex(item.getColorString()));
            vh.colorDisplay.setBackgroundColor(Integer.parseInt(item.getColorString()));

            FragmentTransaction ft = fm.beginTransaction();
            if(item.isDisplayEditFragment()) {
                ft.show(fragments.get(i));
            } else {
                ft.hide(fragments.get(i));
            }
            ft.commit();
        }

        return view;
    }

    private String rgbToHex(String s) {
        return "#" + Integer.toHexString(Integer.parseInt(s)).toUpperCase();
    }

    private View.OnClickListener onClickItemListener() {
        return view -> {
            ViewParent parent = view.getParent();
            if (parent instanceof RelativeLayout) {
                RelativeLayout parentView = (RelativeLayout) parent;
                ColorViewHolder vh = (ColorViewHolder) parentView.getTag();
                vh.item.setDisplayEditFragment(!vh.item.isDisplayEditFragment());
                this.notifyDataSetChanged();
            }
        };
    }

    private class ColorViewHolder {
        public PaletteColorDisplayItem item;
        public View view;
        public TextView colorDisplay;
        public FrameLayout editColorFrame;

        public ColorViewHolder(PaletteColorDisplayItem item, View v) {
            this.item = item;
            this.view = v;
            this.colorDisplay = v.findViewById(R.id.colorDisplay);
            this.editColorFrame = v.findViewById(R.id.editColorFrame);
        }
    }
}
