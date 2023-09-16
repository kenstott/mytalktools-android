/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Class CaptureWebPage. Assists in displaying a web page and then capturing the web page
 * in a bitmap format.
 */
public class CaptureWebPage extends Activity {

    /**
     * The bitmap.
     */
    private static Bitmap bitmap;
    /**
     * The address.
     */
    private EditText address;
    /**
     * The web.
     */
    private WebView web;
    /**
     * The activity.
     */
    private CaptureWebPage activity;

    /**
     * Gets the bitmap.
     *
     * @return the bitmap
     */
    public static Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Sets the bitmap.
     *
     * @param value the new bitmap
     */
    private static void setBitmap(final Bitmap value) {
        CaptureWebPage.bitmap = value;
    }

    /**
     * Picture2 bitmap.
     *
     * @param webView the web view
     */
    private void picture2Bitmap(final WebView webView) {
        int height = webView.getHeight();
        int width = webView.getWidth();
        try {
            if (height > 0 && width > 0) {
                try {
                    setBitmap(Bitmap.createBitmap(width, height, Config.ARGB_8888));
                    Canvas canvas = new Canvas(getBitmap());
                    webView.draw(canvas);
                } catch (OutOfMemoryError e) {
                    new AlertDialog.Builder(getApplicationContext()).setTitle(R.string.problem)
                            .setMessage(R.string.capture_web_page_out_of_memory).create().show();
                }
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Fix address.
     */
    private void fixAddress() {
        String text = address.getText().toString();
        if (!(text.contains("//") || text.contains("http"))) {
            address.setText(String.format("http://%s", text));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public final boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
            web.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            fixAddress();
            activity.setProgressBarIndeterminateVisibility(true);
            web.loadUrl(address.getText().toString());
            web.requestFocus();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public final void onCreate(final Bundle paramBundle) {
        activity = this;
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.web_image);
        /* The go. */
        Button go = findViewById(R.id.buttonGo);
        /* The back. */
        Button back = findViewById(R.id.buttonBack);
        /* The forward. */
        Button forward = findViewById(R.id.buttonForward);
        address = findViewById(R.id.editTextUrl);
        /* The capture. */
        Button capture = findViewById(R.id.buttonCapture);
        web = findViewById(R.id.webView);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new HelloWebViewClient());
        web.getSettings().setBuiltInZoomControls(true);
        web.setWebChromeClient(new WebChromeClient());

        go.setOnClickListener(v -> {
            fixAddress();
            activity.setProgressBarIndeterminateVisibility(true);
            web.loadUrl(address.getText().toString());
            web.requestFocus();
        });
        back.setOnClickListener(v -> {
            if (web.canGoBack()) {
                web.goBack();
            }
        });
        forward.setOnClickListener(v -> {
            if (web.canGoForward()) {
                web.goForward();
            }
        });
        capture.setOnClickListener(v -> {
            picture2Bitmap(web);
            Intent intent = activity.getIntent();
            activity.setResult(1, intent);
            activity.finish();
        });
    }

    /**
     * The Class HelloWebViewClient.
     */
    private class HelloWebViewClient extends WebViewClient {

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit .WebView,
         * java.lang.String)
         */
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            activity.setProgressBarIndeterminateVisibility(true);
            view.loadUrl(url);
            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
         */
        @Override
        public void onPageFinished(final WebView view, final String url) {
            activity.setProgressBarIndeterminateVisibility(false);
        }
    }
}
