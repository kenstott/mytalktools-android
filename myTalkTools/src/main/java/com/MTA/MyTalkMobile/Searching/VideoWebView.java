/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Search;
import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.File;
import java.net.URI;

/**
 * The Class VideoWebView.
 */
@Keep
final class VideoWebView extends WebViewClient {

    /**
     * The Constant DOT_MP4.
     */
    private static final String DOT_MP4 = ".mp4";
    /**
     * The Constant DOT_MOV.
     */
    private static final String DOT_MOV = ".mov";
    /**
     * The video result ContactAdapter.
     */
    private final VideoResultAdapter videoResultAdapter;
    /**
     * The context.
     */
    private final Search context;

    /**
     * Instantiates a new video web view.
     *
     * @param pVideoResultAdapter the video result ContactAdapter
     * @param pContext            the context
     */
    VideoWebView(final VideoResultAdapter pVideoResultAdapter, final Search pContext) {
        this.videoResultAdapter = pVideoResultAdapter;
        this.context = pContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView,
     * java.lang.String)
     */
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String paramUrl) {
        final OnClickListener listener = (dialog, which) -> {
            switch (which) {
                case 0:
                    previewVideo(paramUrl);
                    break;
                case 1:
                    context.runOnUiThread(() -> saveCellVideo(paramUrl));
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        };
        final AlertDialog dialog =
                new AlertDialog.Builder(this.videoResultAdapter.getSearch()).setItems(
                                new String[]{this.videoResultAdapter.getSearch().getString(R.string.preview_video),
                                        this.videoResultAdapter.getSearch().getString(R.string.save_to_cell)}, listener)
                        .create();
        dialog.show();
        return true;
    }

    /**
     * Preview video.
     *
     * @param paramUrl the param url
     */
    private void previewVideo(final String paramUrl) {
        final LayoutInflater li =
                (LayoutInflater) this.videoResultAdapter.getSearch().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
        if (li == null) return;
        final View videoPreview = li.inflate(R.layout.video_preview, (ViewGroup) context.getWindow().getDecorView().getRootView(), false);
        final VideoView videoView = videoPreview.findViewById(R.id.videoPlayer);
        videoView.setMediaController(new MediaController(this.videoResultAdapter.getSearch()));
        final ProgressBar progressBar = videoPreview.findViewById(R.id.videoProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        final AlertDialog dialog =
                new AlertDialog.Builder(this.videoResultAdapter.getSearch()).setView(videoPreview)
                        .setTitle(R.string.video_preview).create();
        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            videoView.start();
        });
        videoView.setOnCompletionListener(arg0 -> dialog.cancel());
        videoView.setVideoURI(Uri.parse(paramUrl.replace(DOT_MOV, DOT_MP4)));
        dialog.show();
    }

    /**
     * save video file from server.
     *
     * @param urlPath the url of the video
     */
    private void saveCellVideo(final String urlPath) {
        final URI media = URI.create(urlPath.replace(" ", "%20"));
        final String root = Utility.getMyTalkFilesDir(context).getAbsolutePath();
        final String localFilename =
                root
                        + "/"
                        + media.getPath().replace("/dnn/useruploads/", "").replace("/", "-").replace(" ", "-")
                        .replace(DOT_MOV, DOT_MP4);
        final File file = new File(localFilename);
        final String dbUrl = media.getPath().replace("/dnn/useruploads/", "");
        Intent intent = new Intent();
        intent.putExtra("in", media.toString().replace(DOT_MOV, DOT_MP4));
        intent.putExtra("out", file.getAbsolutePath());
        intent.putExtra("url", dbUrl);
        context.setResult(1, intent);
        context.finish();
    }

}
