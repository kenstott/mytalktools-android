/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Billing.BillingDataSource;

public class TrialCheck {

    /**
     * The Constant for FULL_LICENSE.
     */
    public static final String FULL_LICENSE = "full_license";
    /**
     * The Constant for FULL_LICENSE_WEB.
     */
    static final String FULL_LICENSE_PLUS_FAMILY = "full_license_plus_family";
    /**
     * The Constant for FULL_LICENSE_PLUS_URL_PRO.
     */
    static final String FULL_LICENSE_PLUS_PROFESSIONAL = "full_license_plus_professional";
    /**
     * The Constant FULL_LICENSE_TOKEN.
     */
    private static final String FULL_LICENSE_TOKEN = "fullLicenseToken";
    /**
     * The Constant URL.
     */
    private static final String URL = "file:///android_asset/trialCheck.html";
    /**
     * The Constant LIMITED_URL.
     */
    private static final String LIMITED_URL = "file:///android_asset/limitedLicense";
    /**
     * The Constant FULL_URL.
     */
    private static final String FULL_URL = "file:///android_asset/fullLicense";
    /**
     * The Constant FULL_PLUS_URL.
     */
    private static final String FULL_PLUS_URL = "file:///android_asset/fullLicensePlusWebFamily";
    /**
     * The Constant FULL_PLUS_URL_PRO.
     */
    private static final String FULL_PLUS_URL_PRO =
            "file:///android_asset/fullLicensePlusWebProfessional";
    public static boolean trialChecked;
    /**
     * Variable to declare whether or not workspace was activated.
     */
    private static volatile Boolean isWorkSpaceActivated;
    /**
     * The board.
     */
    private final Board board;

    /**
     * The Boolean value if the trial date has passed or now
     */
    private Boolean dateIsAfter;

    private BillingDataSource billingDataSource;

    /**
     * Instantiates a new trial check.
     *
     * @param paramBoard the param board
     */
    public TrialCheck(final Board paramBoard) {
        this.board = paramBoard;
        billingDataSource = BillingDataSource.getInstance(board.getApplication(), Board.currentBoard.knownInappSKUs, new String[]{}, new String[]{});
    }

    /**
     * Solve license issue.
     */
    public final void solveLicenseIssue(Boolean dateIsAfter) {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
        this.dateIsAfter = dateIsAfter;
        SelectLicense sl = new SelectLicense();
        if (sp.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true)) {
            sl.changeToLimitedLicense();
        }
        sl.setOnDismissListener(arg0 -> {
            Log.e("PREFS: ", sp.getAll().toString());
            board.updateMenu();
            if (sp.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)) {
                Toast
                        .makeText(board, R.string.you_are_still_running_limited_license_, Toast.LENGTH_SHORT)
                        .show();
            } else if (sp.getBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true)) {
                Toast.makeText(board, R.string.thanks_, Toast.LENGTH_SHORT).show();
            }
        });
        sl.show();
    }

    public class SelectLicense extends Dialog {

        /**
         * Instantiates a new trial check.
         */
        SelectLicense() {
            super(board);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.app.Dialog#onKeyDown(int, android.view.KeyEvent)
         */
        @Override
        public final boolean onKeyDown(final int id, @NonNull final KeyEvent keyEvent) {
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());

            if (!dateIsAfter && sp.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)) {
                Toast.makeText(board, R.string.you_are_still_running_limited_license_, Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (!dateIsAfter && sp.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true)) {
                Toast.makeText(board, "You are still running a Trial License.", Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (dateIsAfter && sp.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)) {
                Toast.makeText(board, R.string.you_are_still_running_limited_license_, Toast.LENGTH_SHORT).show();
                dismiss();
            } else if (dateIsAfter) {
                Toast.makeText(board, R.string.you_have_to_select_a_license_, Toast.LENGTH_SHORT).show();
            }
            return false;
        }


        /*
         * (non-Javadoc)
         *
         * @see android.app.Dialog#onCreate(android.os.Bundle)
         */
        @Override
        public final void onCreate(final Bundle paramBundle) {
            super.onCreate(paramBundle);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.select_license);
            setWebView();
        }

        /**
         *
         */
        void changeLicense() {
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
            Editor e = sp.edit();
            e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false).apply();
            e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false).apply();
            e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true).apply();
            e.apply();
            AppPreferences.setByLicense(board);
        }

        /**
         *
         */
        private void changeToLimitedLicense() {
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
            Editor e = sp.edit();
            e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true).apply();
            e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, false).apply();
            e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false).apply();
            e.putBoolean(AppPreferences.PREF_KEY_PHRASE_MODE, false).apply();
            AppPreferences.setByLicense(board);
        }

        /**
         * Sets the web view.
         */
        private void setWebView() {
            Button freeButton = findViewById(R.id.free_license);
            Button fullButton = findViewById(R.id.full_license);
            Button fullFamilyButton = findViewById(R.id.full_plus_family_license);
            Button fullProfessionalButton = findViewById(R.id.full_plus_professional_license);

            WebView web = (WebView) findViewById(R.id.trialWebView);

            freeButton.setOnClickListener(v -> {
                changeToLimitedLicense();
                dismiss();
            });
            fullButton.setOnClickListener(v -> {
                billingDataSource.launchBillingFlow(board, TrialCheck.FULL_LICENSE);
                cancel();
            });
            fullFamilyButton.setOnClickListener(v -> {
                billingDataSource.launchBillingFlow(board, TrialCheck.FULL_LICENSE_PLUS_FAMILY);
                cancel();
            });
            fullProfessionalButton.setOnClickListener(v -> {
                billingDataSource.launchBillingFlow(board, TrialCheck.FULL_LICENSE_PLUS_PROFESSIONAL);
                cancel();
            });
        }
    }
}
