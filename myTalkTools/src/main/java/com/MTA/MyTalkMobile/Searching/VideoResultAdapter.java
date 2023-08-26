/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.BoardContent;
import com.MTA.MyTalkMobile.Json.LibrarySearchResult;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Search;

/**
 * The Class VideoResultAdapter. Used to display the results of a search for videos.
 */
@Keep
public class VideoResultAdapter extends ArrayAdapter<LibrarySearchResult> {

    /**
     * The Constant VIDEO_RESULT_HEIGHT.
     */
    private static final int VIDEO_RESULT_HEIGHT = 600;
    /**
     * The search.
     */
    private final Search search;
    /**
     * The content.
     */
    private final BoardContent content;
    /**
     * The username.
     */
    private String username = "";
    /**
     * The library.
     */
    private String library = "";

    /**
     * Instantiates a new video result ContactAdapter.
     *
     * @param context          the context
     * @param layout           the unused
     * @param paramContent     the param content
     * @param paramListResults the param list results
     */
    public VideoResultAdapter(final Search context, final int layout,
                              final BoardContent paramContent, final ListView paramListResults) {
        super(context, layout);
        this.search = context;
        this.content = paramContent;
        paramListResults.setAdapter(this);
        this.setNotifyOnChange(true);
    }

    public Search getSearch() {
        return search;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @NonNull
    @Override
    public final View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        LinearLayout ll;
        if (convertView == null) {
            ll = new LinearLayout(this.search);
        } else {
            ll = (LinearLayout) convertView;
        }
        ll.removeAllViews();
        ll.setOrientation(LinearLayout.HORIZONTAL);
        final LibrarySearchResult videoFile = this.getItem(position);
        if (videoFile != null) {
            final WebView webView = new WebView(this.search);
            String videoPath = videoFile.Filename;
            String libPath = this.username + "/Private%20Library/";
            if (this.library.contentEquals(this.getContext().getString(R.string.public_username))) {
                libPath = "Public/Public%20Library/";
            }
            final String webString =
                    "<style type=\"text/css\"> img { width: auto; height: 80%; }"
                            + " body { background-color:#eaeaea; margin: 10px} </style> "
                            + "<a href=\"https://www.mytalktools.com/dnn/useruploads/" + libPath + videoPath
                            + "\"> <img src=\"" + videoFile.ThumbnailUrl + "\"/></a>" + "<body> <center>" + "<b>"
                            + videoPath + "<br>" + "</b>" + videoFile.Tags + "</center> </body>";

            final LinearLayout.LayoutParams webViewLayoutParams =
                    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, VIDEO_RESULT_HEIGHT);
            webViewLayoutParams.gravity = Gravity.CENTER;
            webView.setLayoutParams(webViewLayoutParams);
            webView.setWebViewClient(new VideoWebView(this, search));
            webView.loadData(webString, "text/html", null);
            ll.addView(webView);
        }
        return ll;
    }


    /**
     * Sets the library.
     *
     * @param value the new library
     */
    public final void setLibrary(final String value) {
        this.library = value;
    }

    /**
     * Sets the username.
     *
     * @param value the new username
     */
    public final void setUsername(final String value) {
        this.username = value;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public BoardContent getContent() {
        return content;
    }


}
