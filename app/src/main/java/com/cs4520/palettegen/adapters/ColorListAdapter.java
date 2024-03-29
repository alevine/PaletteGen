package com.cs4520.palettegen.adapters;

import android.app.Activity;
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

import com.cs4520.palettegen.PaletteActivity;
import com.cs4520.palettegen.R;
import com.cs4520.palettegen.db.Palette;
import com.cs4520.palettegen.db.PaletteDbController;
import com.cs4520.palettegen.fragments.EditSingleColorFragment;
import com.cs4520.palettegen.model.PaletteColorDisplayItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ColorListAdapter extends BaseAdapter {
    private int paletteId;
    private String paletteName;

    private int displayColorStringMode;

    private FragmentManager fm;
    private Context context;
    private LayoutInflater inflater;
    private List<PaletteColorDisplayItem> colors;
    private List<Integer> fragmentFrameIds;
    private List<EditSingleColorFragment> fragments;
    private List<TextView> colorDisplayViews;

    private PaletteColorDisplayItem recentlyChanged;

    public ColorListAdapter(FragmentManager fm, Context c, List<PaletteColorDisplayItem> colors, int paletteId, String paletteName) {
        this.fm = fm;
        this.context = c;
        this.inflater = LayoutInflater.from(c);
        this.colors = colors;
        this.paletteId = paletteId;
        this.paletteName = paletteName;
        this.fragmentFrameIds = new ArrayList<>();
        this.fragments = new ArrayList<>();
        this.colorDisplayViews = new ArrayList<>();

        // default to hex display
        displayColorStringMode = 0;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int i) {
        return colors.get(i);
    }

    public List<PaletteColorDisplayItem> getAllItems() { return colors; }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ColorViewHolder vh;
        PaletteColorDisplayItem item = (PaletteColorDisplayItem) getItem(i);

        if (view == null) {
            view = inflater.inflate(R.layout.item_color_list, viewGroup, false);
            vh = new ColorViewHolder(item, view);
            if (this.colorDisplayViews.size() == i) {
                this.colorDisplayViews.add(vh.colorDisplay);
            }
            view.setTag(vh);
        } else {
            vh = (ColorViewHolder) view.getTag();
        }

        if(!item.equals(vh.item)) {
            vh.item = item;
        }

        if (this.fragmentFrameIds.size() <= i) {
            int id = View.generateViewId();
            vh.editColorFrame.setId(id);
            this.fragmentFrameIds.add(id);
        } else if (vh.editColorFrame.getId() != this.fragmentFrameIds.get(i)) {
            vh.editColorFrame.setId(this.fragmentFrameIds.get(i));
        }

        if (this.fragments.size() == i) {
            this.fragments.add(EditSingleColorFragment.newInstance(this, item));
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(vh.editColorFrame.getId(), this.fragments.get(i), "fragment" + item.getId());
            ft.commit();
        }

        vh.colorDisplay.setOnClickListener(onClickItemListener());

        if(this.displayColorStringMode == PaletteActivity.DISPLAY_MODE_HEX) {
            vh.colorDisplay.setText(item.getHexString());
        } else if(this.displayColorStringMode == PaletteActivity.DISPLAY_MODE_RGB) {
            vh.colorDisplay.setText(item.getLegibleRgb());
        } else {
            vh.colorDisplay.setText(item.getColorString());
        }

        vh.colorDisplay.setBackgroundColor(Integer.parseInt(item.getColorString()));

        FragmentTransaction ft = fm.beginTransaction();

        if (item.isDisplayEditFragment()) {
            fragments.get(i).displayChanges();
            ft.show(fragments.get(i));
        } else {
            ft.hide(fragments.get(i));
        }
        ft.commit();

        return view;
    }

    public void colorChanged(PaletteColorDisplayItem colorDisplayItem) {
        this.recentlyChanged = colors.get(colorDisplayItem.getId());
        this.colors.set(colorDisplayItem.getId(), colorDisplayItem);

        PaletteDbController.updatePalette(
                paletteId,
                new Palette(buildColorStringForPalette(), paletteName, paletteId),
                ((PaletteActivity) (context)).getDbHelper());

        // Hide the editor again because we're all set editing.
        colors.get(colorDisplayItem.getId()).setDisplayEditFragment(false);

        showUndoSnackbar();
        this.notifyDataSetChanged();
    }

    public void colorShifted(PaletteColorDisplayItem colorDisplayItem) {
        TextView colorDisplayView = this.colorDisplayViews.get(colorDisplayItem.getId());
        if (this.displayColorStringMode == PaletteActivity.DISPLAY_MODE_HEX) {
            colorDisplayView.setText(colorDisplayItem.getHexString());
        } else if(this.displayColorStringMode == PaletteActivity.DISPLAY_MODE_RGB) {
            colorDisplayView.setText(colorDisplayItem.getLegibleRgb());
        }
        colorDisplayView.setBackgroundColor(Integer.parseInt(colorDisplayItem.getColorString()));
    }

    public void setDisplayMode(int i) {
        this.displayColorStringMode = i;
    }

    // Hides all color fragments for the screenshot
    public void hideAllFragments() {
        for (PaletteColorDisplayItem item : colors) {
            item.setDisplayEditFragment(false);
        }
    }

    // Shows an "Undo" snackbar that will either go away on its own or undo the deletion
    private void showUndoSnackbar() {
        View view = ((Activity) this.context).findViewById(R.id.paletteActivity);

        Snackbar snackbar = Snackbar.make(view, R.string.undoColorShift,
                Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", v -> undoChange());
        snackbar.show();
    }

    private void undoChange() {
        int recentlyChangedId = this.recentlyChanged.getId();
        this.colors.set(recentlyChangedId, this.recentlyChanged.clone());
        fragments.get(recentlyChangedId).setColorDisplayItem(this.colors.get(recentlyChangedId));
        this.recentlyChanged = null;

        PaletteDbController.updatePalette(
                paletteId,
                new Palette(buildColorStringForPalette(), this.paletteName, paletteId),
                ((PaletteActivity) (context)).getDbHelper());

        this.notifyDataSetChanged();
    }

    private String buildColorStringForPalette() {
        int i = 0;
        StringBuilder colorStringBuilder = new StringBuilder();

        for (PaletteColorDisplayItem c : colors) {
            if (i == 6) {
                break;
            } else if (i == 5) {
                colorStringBuilder.append(c.getColorString());
            } else {
                colorStringBuilder.append(c.getColorString()).append(",");
            }

            i++;
        }
        return colorStringBuilder.toString();
    }

    private View.OnClickListener onClickItemListener() {
        return view -> {
            ViewParent parent = view.getParent();
            if (parent instanceof RelativeLayout) {
                RelativeLayout parentView = (RelativeLayout) parent;
                ColorViewHolder vh = (ColorViewHolder) parentView.getTag();
                vh.item.setDisplayEditFragment(!vh.item.isDisplayEditFragment());
                hideAllOtherEditColorFragments(vh.item.getId());
                this.notifyDataSetChanged();
            }
        };
    }

    private void hideAllOtherEditColorFragments(int id) {
        for (PaletteColorDisplayItem item : colors) {
            if(item.getId() != id) {
                item.setDisplayEditFragment(false);
            }
        }
    }

    public class ColorViewHolder {

        PaletteColorDisplayItem item;
        FrameLayout editColorFrame;
        public View view;
        private TextView colorDisplay;


        ColorViewHolder(PaletteColorDisplayItem item, View v) {
            this.item = item;
            this.view = v;
            this.colorDisplay = v.findViewById(R.id.colorDisplay);
            this.editColorFrame = v.findViewById(R.id.editColorFrame);
        }
    }
}
