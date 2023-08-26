package com.oissela.software.multilevelexpindlistview;

import android.content.Context;
//import androidx.annotation.Keep;

//@Keep
public class Utils {
    /**
     * Converting dp units to pixel units
     * <a href="http://developer.android.com/guide/practices/screens_support.html#dips-pels">...</a>
     */
    public static int getPaddingPixels(Context context, int dpValue) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dpValue * scale + 0.5f);
    }
}
