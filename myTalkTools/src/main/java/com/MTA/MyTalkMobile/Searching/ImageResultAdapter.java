/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Search;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

/**
 * The Class ImageResultAdapter.
 */
@Keep
public class ImageResultAdapter extends ArrayAdapter<JSONObject> {

    /**
     * Image result padding.
     */
    private static final int IMAGE_RESULT_PADDING = 5;
    /**
     * The image result is about 1/8 of the height of the list.
     */
    private static final int IMAGE_RESULT_HEIGHT_PROPORTION = 8;
    /**
     * the image result is about 1/3 the width of the list.
     */
    private static final int IMAGE_RESULT_WIDTH_PROPORTION = 3;
    /**
     * The Constant HASH.
     */
    private static final String HASH = "#";
    /**
     * The Constant HASH_ESCAPE.
     */
    private static final String HASH_ESCAPE = "%3F";
    /**
     * URL escape sequence for space character.
     */
    private static final CharSequence SPACE_ESCAPE = "%20";
    /**
     * The search.
     */
    private final Search search;
    /**
     * The image cache.
     */
    private final Hashtable<Integer, Drawable> imageCache = new Hashtable<>();

    /**
     * Instantiates a new image result ContactAdapter.
     *
     * @param context the context
     * @param layout  the ContactAdapter item layout
     */
    public ImageResultAdapter(final Search context, final int layout) {
        super(context, layout);
        this.search = context;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrayAdapter#clear()
     */
    @Override
    public final void clear() {
        super.clear();
        imageCache.clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @NonNull
    @Override
    public final View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        LinearLayout ll = (LinearLayout) convertView;
        if (ll == null) {
            ll = new LinearLayout(search);
        }
        ll.removeAllViews();
        ll.setOrientation(LinearLayout.HORIZONTAL);
        JSONObject ir = getItem(position);
        if (ir != null) {
            ImageView iv = new ImageView(search);
            FrameLayout frameLayout = new FrameLayout(search);
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(parent.getWidth() / IMAGE_RESULT_WIDTH_PROPORTION,
                            parent.getHeight() / IMAGE_RESULT_HEIGHT_PROPORTION);
            frameLayout.setLayoutParams(layoutParams);
            frameLayout.setPadding(IMAGE_RESULT_PADDING, IMAGE_RESULT_PADDING, IMAGE_RESULT_PADDING,
                    IMAGE_RESULT_PADDING);
            frameLayout.addView(iv);
            TextView tv = new TextView(search);
            try {
                tv.setText(ir.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(IMAGE_RESULT_PADDING, IMAGE_RESULT_PADDING, IMAGE_RESULT_PADDING,
                    IMAGE_RESULT_PADDING);
            ll.addView(frameLayout);
            ll.addView(tv);
            Runnable runnable = () -> {
                iv.setImageDrawable(imageCache.get(position));
                iv.setAdjustViewBounds(true);
                ListView p = (ListView) parent;
                p.invalidateViews();
            };
            if (!imageCache.containsKey(position)) {
                try {
                    imageOperations(search, ir.getString("thumbnailUrl").replaceAll(HASH, HASH_ESCAPE), (d) -> {
                        imageCache.put(position, d);
                        runnable.run();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // if it fails ... we ignore
                }
            } else {
                runnable.run();
            }
        }
        return ll;
    }

    private void imageOperations(final Context ctx, final String url, GetDrawable result) {
        DownloadTask d = new DownloadTask(url, result);
        d.execute();
    }

    interface GetDrawable {
        void test(Drawable result);
    }

    static class DownloadTask extends AsyncTask<Void, Void, Drawable> {

        final String address;
        final String url;
        final GetDrawable result;
        public Object out;

        DownloadTask(String url, GetDrawable result) {
            this.url = url;
            this.address = url.replace(" ", SPACE_ESCAPE);
            this.result = result;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            InputStream is;
            try {
                is = (InputStream) new URL(url).getContent();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            try {
                return Drawable.createFromStream(is, "src");
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            result.test(drawable);
        }

    }

}
