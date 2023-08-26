/*

 */
package com.MTA.MyTalkMobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Keep;

import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class FontColorKeyAdapter.
 *
 * @author Kenneth
 */

@Keep
class FontColorKeyAdapter extends BaseAdapter implements SpinnerAdapter {

    /**
     * The font colors.
     */
    private final List<String> fontColors;

    /**
     * The context.
     */
    private final Context context;

    /**
     * Instantiates a new font color key ContactAdapter.
     *
     * @param paramContext    the param context
     * @param paramFontColors the param font colors
     */
    public FontColorKeyAdapter(final Context paramContext, final List<String> paramFontColors) {
        this.fontColors = paramFontColors;
        this.context = paramContext;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    public final int getCount() {
        return fontColors.size();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    public final Object getItem(final int position) {
        return fontColors.get(position);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    public final long getItemId(final int position) {
        return position;
    }

    /**
     * Gets the font color.
     *
     * @param i the i
     * @return the font color
     */
    private Integer getFontColor(final Integer i) {
        switch (i) {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.DKGRAY;
            case 3:
                return Color.LTGRAY;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.rgb(0xFF, 0xB6, 0xC1); // red
            case 7:
                return Color.rgb(0x90, 0xEE, 0x90); // green
            case 8:
                return Color.rgb(0x87, 0xCE, 0xEB); // blue
            case 9:
                return Color.rgb(0xE0, 0xFF, 0xFF); // cyan
            case 10:
                return Color.rgb(0xFF, 0xFF, 0xE0); // yellow
            case 11:
                return Color.MAGENTA;
            case 12:
                return Color.rgb(0xFF, 0xA5, 0x00); // orange
            case 13:
                return Color.rgb(0x80, 0x00, 0x80); // purple
            case 14:
                return Color.rgb(0xA5, 0x2A, 0x2A); // brown
            case 16:
                return Color.rgb(0xFF, 0x00, 0x00); // red
            case 15:
                return Color.TRANSPARENT;
            case 4:
            default:
                return Color.WHITE;
        }
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
                convertView = inflater.inflate(R.layout.fontcolorkeyitem, parent, false);
            if (convertView == null) return null;
        }
        TextView cv = convertView.findViewById(R.id.fontColorKeyItemColor);
        TextView tv = convertView.findViewById(R.id.fontColorKeyItemText);
        try {
            tv.setText(fontColors.get(position));
            cv.setBackgroundColor(getFontColor(position));
        } catch (Exception ignore) {

        }

        return convertView;
    }

}
