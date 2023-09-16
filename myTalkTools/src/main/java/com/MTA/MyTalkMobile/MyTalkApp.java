/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ViewConfiguration;

import androidx.annotation.Keep;

//import android.support.multidex.MultiDexApplication;

//import com.testflightapp.lib.TestFlight;

// TODO: Auto-generated Javadoc

@Keep
public class MyTalkApp extends Application implements ContextProvider {

    /**
     * The Constant TEST_FLIGHT_ID.
     */
    //private static final String TEST_FLIGHT_ID = "0ce6fec5-16ee-4c11-8649-719e9256a5ce";

    private Activity currentActivity;
    private Board currentBoardActivity;


    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public final void onCreate() {
        /* force overflow menu on ALL Android devices */
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            java.lang.reflect.Field menuKeyField =
                    ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onCreate();

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        // Initialize TestFlight with your app token.
        //TestFlight.takeOff(this, TEST_FLIGHT_ID);
        // ...
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity instanceof Board) {
                    MyTalkApp.this.currentBoardActivity = (Board) activity;
                }
                MyTalkApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity instanceof Board) {
                    MyTalkApp.this.currentBoardActivity = (Board) activity;
                }
                MyTalkApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity instanceof Board) {
                    MyTalkApp.this.currentBoardActivity = (Board) activity;
                }
                MyTalkApp.this.currentActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                MyTalkApp.this.currentActivity = null;
            }

            @Override
            public void onActivityStopped(Activity activity) {
                // don't clear current activity because activity may get stopped after
                // the new activity is resumed
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // don't clear current activity because activity may get destroyed after
                // the new activity is resumed
            }
        });
    }

    @Override
    public Activity getActivityContext() {
        return currentActivity;
    }

    public Board getBoardContext() {
        return currentBoardActivity;
    }

}
