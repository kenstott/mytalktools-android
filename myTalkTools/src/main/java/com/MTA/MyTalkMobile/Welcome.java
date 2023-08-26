/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class Welcome.
 */
@SuppressLint("SetJavaScriptEnabled")
@Keep
public class Welcome extends Activity {

    /**
     * The Constant INTENT_EXTRA_URL.
     */
    public static final String INTENT_EXTRA_URL = "url";
    /**
     * The Constant FILE_ANDROID_ASSET.
     */
    private static final String FILE_ANDROID_ASSET = "file:///android_asset/";
    /**
     * The Constant MENU_CHECK_BOX_NAME.
     */
    private static final String MENU_CHECK_BOX_NAME = "MenuCheckBox";
    /**
     * The Constant ACTION_PROTOCOL.
     */
    private static final String ACTION_PROTOCOL = "action://";
    /**
     * The Constant ANDROID_RESOURCE_PROTOCOL.
     */
    private static final String ANDROID_RESOURCE_PROTOCOL = "android.resource://";
    /**
     * The Constant VIDEO_PROTOCOL.
     */
    private static final String VIDEO_PROTOCOL = "video://";
    /**
     * The show menu.
     */
    private boolean showMenu;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.welcome);
        WebView web = findViewById(R.id.welcomeWebView);
        web.getSettings().setJavaScriptEnabled(true);
        web.addJavascriptInterface(new MenuCheckBox(), MENU_CHECK_BOX_NAME);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String paramUrl) {
                String url = paramUrl;
                try {
                    if (url.startsWith(VIDEO_PROTOCOL)) {
                        if (url.endsWith("1Minute.mp4")) {
                            url = ANDROID_RESOURCE_PROTOCOL + getPackageName() + "/" + R.raw.oneminute;
                        } else if (url.endsWith("workspace.mp4")) {
                            url = ANDROID_RESOURCE_PROTOCOL + getPackageName() + "/" + R.raw.workspace;
                        } else if (url.endsWith("UsingMobile.mp4")) {
                            url = ANDROID_RESOURCE_PROTOCOL + getPackageName() + "/" + R.raw.usingmobile;
                        }
                        Intent localIntent = new Intent(getApplicationContext(), VideoPlayerController.class);
                        localIntent.putExtra(VideoPlayerController.INTENT_EXTRA_PATH, url);
                        localIntent.putExtra(VideoPlayerController.INTENT_EXTRA_TITLE, "");
                        startActivity(localIntent);
                    } else if (url.startsWith(ACTION_PROTOCOL)) {
                        Intent localIntent = new Intent(getApplicationContext(), Board.class);
                        if (url.endsWith("createAccount")) {
                            localIntent.putExtra(Board.INTENT_EXTRA_SIGN_IN, true);
                        }
                        if (url.endsWith("goBack")) {
                            view.goBack();
                            return true;
                        }
                        startActivity(localIntent);
                    } else {
                        view.loadUrl(url);
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        Intent localIntent = getIntent();
        String url = localIntent.getStringExtra(INTENT_EXTRA_URL);
        web.loadUrl(FILE_ANDROID_ASSET + url);
    }

    /**
     * The Class MenuCheckBox.
     */
    private class MenuCheckBox {

        /**
         * Check.
         */
        @JavascriptInterface
        public void check() {
            showMenu = !showMenu;
            SharedPreferences.Editor editor =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.putBoolean(AppPreferences.PREF_KEY_SHOW_MENU, !showMenu);
            editor.apply();
        }
    }
}
