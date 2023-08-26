/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.BoardContent;
import com.MTA.MyTalkMobile.Json.BoardSearchResult;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Search;
import com.MTA.MyTalkMobile.Search.DoChildBoardLibraryQueryTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Class BoardResultAdapter.
 */
@SuppressLint("SetJavaScriptEnabled")
@Keep
public class BoardResultAdapter extends ArrayAdapter<BoardSearchResult> {

    /**
     * The Constant BITMAP_SCALED_SIZE.
     */
    private static final int BITMAP_SCALED_SIZE = 500;
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
    private String username;

    /**
     * Instantiates a new board result ContactAdapter.
     *
     * @param paramContext  the context
     * @param layout        the unused
     * @param paramUsername the param username
     * @param paramContent  the param content
     */
    public BoardResultAdapter(final Search paramContext, final int layout,
                              final String paramUsername, final BoardContent paramContent) {
        super(paramContext, layout);
        this.search = paramContext;
        this.username = paramUsername;
        this.content = paramContent;
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
     * Trim file path.
     *
     * @param fileName the file name
     * @return the string
     */
    public final String trimFilePath(final String fileName) {
        return fileName.substring(fileName.lastIndexOf("/") + 1).substring(
                fileName.lastIndexOf("\\") + 1);
    }

    /**
     * Drawable from url.
     *
     * @param url the url
     * @return the drawable
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Drawable drawableFromUrl(final String url) throws Exception {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        Bitmap b = Bitmap.createScaledBitmap(x, BITMAP_SCALED_SIZE, BITMAP_SCALED_SIZE, false);
        return new BitmapDrawable(search.getResources(), b);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @NonNull
    @Override
    public final View getView(final int position, final View paramConvertView, @NonNull final ViewGroup parent) {
        View convertView = paramConvertView;
        final BoardSearchResult boardFile = getItem(position);

        /* top level LinearLayout */
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) search.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.board_search_item, parent, false);
        }

        if (boardFile != null && convertView != null) {
            ImageView imageView = convertView.findViewById(R.id.boardSearchCellImage);
            TextView cellText = convertView.findViewById(R.id.boardSearchCellText);
            TextView boardTags = convertView.findViewById(R.id.boardSearchBoardTags);
            WebView webView = convertView.findViewById(R.id.boardSearchBoard);

            if (boardFile.ImageUrl != null) {
                try {
                    imageView.setImageDrawable(drawableFromUrl(boardFile.ImageUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            cellText.setText(boardFile.Text);
            boardTags.setText(boardFile.Tags);

            convertView.setOnClickListener(arg0 -> {
                /* set new sound file */
                saveChildBoard(boardFile);
            });

            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
            settings.setLoadWithOverviewMode(true);
            settings.setLoadsImagesAutomatically(true);
            settings.setUseWideViewPort(true);
            settings.setDatabaseEnabled(true);
            settings.setDomStorageEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            try {
                webView
                        .loadUrl("https://www.mytalktools.com/dnn/prod/preview.html?username="
                                + username
                                + "&rows=3&device=browser&orientation=portrait&defaultFontSize=15&headerFontSize=20&unzoomInterval=0&tts=false&zoom=true&phrasebar=false&phrasebarPics=false&boardId="
                                + boardFile.ChildBoardId + "&includeChildBoards=true&homeName=" + boardFile.Text);
            } catch (Exception ignore) {
            }
        }
        return convertView;
    }

    /**
     * Save child board.
     *
     * @param result the result
     */
    private void saveChildBoard(final BoardSearchResult result) {
        DoChildBoardLibraryQueryTask task =
                new Search.DoChildBoardLibraryQueryTask(content, result, search, username);
        task.execute("", "", "");
    }
}
