/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Keep;

import java.util.List;

/**
 * The Class FontSizeKeyAdapter.
 */

@Keep
class FontSizeKeyAdapter extends BaseAdapter implements SpinnerAdapter {

    /**
     * The context.
     */
    private final Context context;

    /**
     * The font size list.
     */
    private final List<String> fontSizeList;

    /**
     * The font size value.
     */
    private final String[] fontSizeValue;

    /**
     * The default font size.
     */
    private final Integer defaultFontSize;

    /**
     * Instantiates a new font size key ContactAdapter.
     *
     * @param paramContext      the param context
     * @param paramFontSizeList the param font size list
     */
    public FontSizeKeyAdapter(final Context paramContext, final List<String> paramFontSizeList) {
        this.context = paramContext;
        this.fontSizeList = paramFontSizeList;
        fontSizeValue = paramContext.getResources().getStringArray(R.array.defaultFontSizeValues);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(paramContext);
        defaultFontSize =
                Integer.parseInt(sp.getString(AppPreferences.PREF_KEY_DEFAULT_FONT_SIZE, "10"));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    public final int getCount() {
        return fontSizeList.size();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    public final Object getItem(final int position) {
        return Integer.parseInt(fontSizeValue[position]);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    public final long getItemId(final int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public final View getView(final int position, final View paramConvertView, final ViewGroup parent) {

        View convertView = paramConvertView;
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null)
                convertView = inflater.inflate(R.layout.fontkeyitem, parent, false);
            if (convertView == null) return null;
        }

        int s = Integer.parseInt(fontSizeValue[position]);
        TextView fcs = convertView.findViewById(R.id.fontKeyItemSize);
        fcs.setText(fontSizeList.get(position));
        Integer fontSize = defaultFontSize;
        if (s != 0) {
            fontSize = s;
        }
        fcs.setTextSize(fontSize);
        return convertView;
    }
}
