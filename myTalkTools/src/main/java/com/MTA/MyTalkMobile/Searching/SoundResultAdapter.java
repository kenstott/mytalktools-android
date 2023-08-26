/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Json.LibrarySearchResult;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Search;
import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.File;
import java.net.URI;

/**
 * The Class SoundResultAdapter.
 */
@Keep
public class SoundResultAdapter extends ArrayAdapter<LibrarySearchResult> {

    /**
     * The Constant SOUND_RESULT_PADDING.
     */
    private static final int SOUND_RESULT_PADDING = 5;
    /**
     * The Constant MIME_TYPE_TEXT_HTML.
     */
    private static final String MIME_TYPE_TEXT_HTML = "text/html";
    /**
     * The Constant SPACE_ESCAPE.
     */
    private static final CharSequence SPACE_ESCAPE = "%20";
    /**
     * The search.
     */
    private final Search search;
    /**
     * The username.
     */
    private String username;
    /**
     * The library.
     */
    private String library;

    /**
     * Instantiates a new sound result ContactAdapter.
     *
     * @param context       the context
     * @param layout        the layout
     * @param paramUsername the param username
     * @param paramLibrary  the param library
     */
    public SoundResultAdapter(final Search context, final int layout,
                              final String paramUsername, final String paramLibrary) {
        super(context, layout);
        this.search = context;
        this.username = paramUsername;
        this.library = paramLibrary;
    }

    /**
     * Sets the username.
     *
     * @param pUsername the new username
     */
    public final void setUsername(final String pUsername) {
        this.username = pUsername;
    }

    /**
     * Sets the library.
     *
     * @param pLibrary the new library
     */
    public final void setLibrary(final String pLibrary) {
        this.library = pLibrary;
    }

    /**
     * Trim file path.
     *
     * @param fileName the file name
     * @return the string
     */
    public final String trimFilePath(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1).substring(
                fileName.lastIndexOf("\\") + 1);
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
        LibrarySearchResult file = getItem(position);
        if (file != null) {
            FrameLayout frameLayout = new FrameLayout(search);
            FrameLayout.LayoutParams layoutParams =
                    new android.widget.FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            frameLayout.setLayoutParams(layoutParams);
            FrameLayout buttonFrameLayout = new FrameLayout(search);
            WebView webView = new WebView(search);
            Button previewButton = new Button(search);
            String soundPath = file.Filename;
            String soundTags = file.Tags;
            String libPath = username + "/Private%20Library/";
            if (library.equals(getContext().getString(R.string.public_username))) {
                libPath = "Public/Public%20Library/";
            }
            final String urlPath = "https://www.mytalktools.com/dnn/useruploads/" + libPath + soundPath;
            previewButton.setText(R.string.save);
            previewButton.setBackgroundResource(R.drawable.buttonstyle);
            previewButton.setGravity(Gravity.CENTER_HORIZONTAL);
            previewButton.setOnClickListener(arg0 -> saveCellSound(urlPath));
        /*
          define WebView to load Audio into - decided to use HTML5 to load Audio into Widgets. - We'll
          see if this works.
         */
            String webString =
                    "<style type=\"text/css\"> audio { width: 100%; } body { background-color:#eaeaea} </style> <body> <audio controls=\"controls\" preload=\"metadata\"> <source src=\"https://www.mytalktools.com/dnn/useruploads/"
                            + libPath
                            + soundPath
                            + "\" type=\"audio/mpeg\" /> </audio> <center>"
                            + "<b>"
                            + soundPath + "</b>" + "<br>" + soundTags + "</center> </body>";
            LinearLayout.LayoutParams webViewLayoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            webViewLayoutParams.gravity = Gravity.CENTER;
            webView.setLayoutParams(webViewLayoutParams);
            webView.loadData(webString, MIME_TYPE_TEXT_HTML, null);
            frameLayout.setPadding(SOUND_RESULT_PADDING, SOUND_RESULT_PADDING, SOUND_RESULT_PADDING,
                    SOUND_RESULT_PADDING);
            frameLayout.addView(webView);
            buttonFrameLayout.addView(previewButton);
            ll.addView(buttonFrameLayout);
            ll.addView(frameLayout);
        }
        return ll;
    }

    /**
     * save audio file from Database.
     *
     * @param urlPath the url path
     */

    private void saveCellSound(final String urlPath) {
        URI media = URI.create(urlPath.replace(" ", SPACE_ESCAPE));
        String root = Utility.getMyTalkFilesDir(search).getAbsolutePath();
        String localFilename =
                root + "/"
                        + media.getPath().replace("/dnn/useruploads/", "").replace("/", "-").replace(" ", "-");
        final String dbUrl = media.getPath().replace("/dnn/useruploads/", "");
        File file = new File(localFilename);
        Intent intent = new Intent();
        intent.putExtra("in", media.toString());
        intent.putExtra("out", file.getAbsolutePath());
        intent.putExtra("url", dbUrl);
        search.setResult(1, intent);
        search.finish();
    }
}
