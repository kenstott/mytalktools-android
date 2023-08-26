/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Keep;
// TODO: Auto-generated Javadoc

/**
 * The Class VideoPlayerController.
 */
@Keep
public class VideoPlayerController extends Activity {

    /**
     * The Constant INTENT_EXTRA_TITLE.
     */
    public static final String INTENT_EXTRA_TITLE = "title";
    /**
     * The Constant INTENT_EXTRA_PATH.
     */
    public static final String INTENT_EXTRA_PATH = "path";
    /**
     * The path.
     */
    private String path;
    /**
     * The uri.
     */
    private Uri uri;
    /**
     * The video view.
     */
    private VideoView videoView;

    /**
     * Start.
     */
    public final void start() {
        this.videoView.start();
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * Sets the path.
     *
     * @param value the new path
     */
    public final void setPath(final String value) {
        this.path = value;
        this.videoView.setVideoPath(value);
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public final Uri getUri() {
        return this.uri;
    }

    /**
     * Sets the uri.
     *
     * @param value the new uri
     */
    public final void setUri(final Uri value) {
        this.uri = value;
        this.videoView.setVideoURI(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.video);
        this.videoView = findViewById(R.id.VideoView);
        this.setTitle(getIntent().getStringExtra("title"));
        this.path = getIntent().getStringExtra("path");
        this.videoView.bringToFront();
        if (this.path.contains("//")) {
            this.videoView.setVideoURI(Uri.parse(this.path));
        } else {
            this.videoView.setVideoPath(this.path);
        }
        this.videoView.setOnCompletionListener(arg0 -> finish());
        this.videoView.start();
    }
}
