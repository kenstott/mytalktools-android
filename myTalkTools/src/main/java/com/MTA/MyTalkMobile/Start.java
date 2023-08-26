/* Line 81 Stop(); replaced with Interrupt(); */
package com.MTA.MyTalkMobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Keep;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

// TODO: Auto-generated Javadoc

/**
 * The Class Start.
 */
@Keep
public class Start extends Activity {

    /**
     * The Constant SPLASH_TIME.
     */
    private static final int SPLASH_TIME = 100;
    /**
     * The splash time.
     */
    private final int splashTime = 2000;
    /**
     * The active.
     */
    private boolean active = true;
    /**
     * The show menu.
     */
    private boolean showWelcome, showMenu;

    /**
     * The shared preferences.
     */
    private SharedPreferences sharedPreferences;

    /**
     * The user name.
     */
    private String userName;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStart()
     */
    public final void onStart() {
        /*
         * StrictMode OFF - API LEVEL 9 FEATURE author: CDM Do not remove this. It is essential for
         * Network functions. Originally implemented in MyTalkApp.java
         */
        super.onStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStop()
     */
    public final void onStop() {
        super.onStop();
    }

    /**
     * Show app.
     */
    private void showApp() {
        Intent welcome = new Intent(getApplicationContext(), Welcome.class);
        if (showWelcome) {
            finish();
            welcome.putExtra(Welcome.INTENT_EXTRA_URL, "010-welcome.html");
            startActivity(welcome);
        } else if (showMenu) {
            finish();
            welcome.putExtra(Welcome.INTENT_EXTRA_URL, "020-menu.html");
            startActivity(welcome);
        } else {
            finish();
            Intent newIntent = new Intent(getApplicationContext(), Board.class);
            Intent intent = getIntent();
            Uri openUri = intent.getData();
            if (openUri != null) {
                if (intent.getAction() != null && intent.getAction().equals("android.provider.calendar.action.HANDLE_CUSTOM_EVENT")) {
                    String myUriString = intent.getStringExtra(CalendarContract.EXTRA_CUSTOM_APP_URI);
                    Uri myUri = Uri.parse(myUriString);
                    newIntent.setData(myUri);
                } else newIntent.setData(openUri);
            }
            startActivity(newIntent);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle bundle) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.d("pref", sp.getAll().toString());
        showWelcome = sp.getBoolean(AppPreferences.PREF_KEY_SHOW_WELCOME, true);
        showMenu = sp.getBoolean(AppPreferences.PREF_KEY_SHOW_MENU, true);
        super.onCreate(bundle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.splash);
        Drawable svgDrawable = svg.createPictureDrawable();
        ImageView imageView = findViewById(R.id.splashImage);
        imageView.setImageDrawable(svgDrawable);
        new Thread() {
            @Override
            public void run() {
                int i = 0;
                try {
                    while (true) {
                        if (active && i > splashTime) {
                            return;
                        }
                        sleep(SPLASH_TIME);
                        if (!active) {
                            continue;
                        }
                        i += SPLASH_TIME;
                    }
                } catch (InterruptedException localInterruptedException) {
                    showApp();
                    // stop();
                    // cdm 4.12.13
                    interrupt();
                } finally {
                    showApp();
                    // stop();
                    // cdm 4.12.13
                    interrupt();
                }
            }
        }.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public final boolean onTouchEvent(final MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == 0) {
            this.active = false;
        }
        return true;
    }
}
