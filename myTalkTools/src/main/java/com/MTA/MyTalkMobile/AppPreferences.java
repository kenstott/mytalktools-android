/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;


/**
 * The Class AppPreferences. Holds constants for preference terms, and helps with working out
 * licensing issues relative to available preferences.
 */
public class AppPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    /**
     * The Constant PREF_KEY_TTS used to reference the preference value indicating to use
     * Text-To-Speech.
     */
    public static final String PREF_KEY_TTS = "tts";

    /**
     * The Constant PREF_KEY_PHRASE_IMAGE used to reference the preference value that controls whether
     * or not to put images into the phrase bar.
     */
    public static final String PREF_KEY_PHRASE_IMAGE = "phraseImage";

    /**
     * The Constant PREF_KEY_PHRASE_MODE used to reference the preference value that controls whether
     * or not to display the phrase bar.
     */
    public static final String PREF_KEY_PHRASE_MODE = "phraseMode";

    /**
     * The Constant PREF_KEY_LIMITED_LICENSE used to reference the preference value that indicates a
     * user is running a limited license version.
     */
    public static final String PREF_KEY_LIMITED_LICENSE = "limitedLicense";

    /**
     * The Constant PREF_KEY_FULL_LICENSE used to reference the preference value that indicates a user
     * is running a full license version.
     */
    public static final String PREF_KEY_FULL_LICENSE = "fullLicense";

    /**
     * The Constant PREF_KEY_TRIAL_LICENSE used to reference the preference value that indicates a
     * user is running a trial license version.
     */
    public static final String PREF_KEY_TRIAL_LICENSE = "trialLicense";

    /**
     * The Constant PREF_KEY_EXTERNAL_STORAGE used to reference the preference value that indicates to
     * place media assets on the external storage device.
     */
    public static final String PREF_KEY_EXTERNAL_STORAGE = "externalStorage";
    public static final String PREF_KEY_SCAN_SWITCH = "scanSwitch";
    public static final String PREF_KEY_AUTO_SCAN_INTERVAL = "autoScanInterval";
    public static final String PREF_KEY_AUTO_START_AUTO_SCAN = "autoStartAutoScan";
    public static final String PREF_KEY_AUTO_SCAN_LOOPS = "autoScanLoops";
    public static final String PREF_KEY_SCAN_BY_ROW = "scanByRow";
    public static final String PREF_KEY_AUDITORY_SCANNING = "auditoryScanning";

    /**
     * The Constant PREF_KEY_FIRST_RUN used to reference the preference value that indicates that this
     * is the very first time the application has been run on this device.
     */
    public static final String PREF_KEY_FIRST_RUN = "firstRun";

    /**
     * The Constant PREF_KEY_ZOOM_PICTURES used to reference the preference value that indicates to
     * zoom pictures when a cell is selected.
     */
    public static final String PREF_KEY_ZOOM_PICTURES = "zoomPictures";

    /**
     * The Constant PREF_KEY_MAXIMUM_ROWS used to reference the preference value that defines the
     * maximum number of rows to display on the device.
     */
    public static final String PREF_KEY_MAXIMUM_ROWS = "maximumRows";

    /**
     * The Constant PREF_KEY_COLOR_SCHEME.
     */
    public static final String PREF_KEY_COLOR_SCHEME = "colorScheme";

    /**
     * The Constant PREF_KEY_DEFAULT_FONT_SIZE.
     */
    public static final String PREF_KEY_DEFAULT_FONT_SIZE = "defaultFontSize";

    /**
     * The Constant PREF_KEY_MARGIN_WIDTH.
     */
    public static final String PREF_KEY_MARGIN_WIDTH = "marginWidth";

    /**
     * The Constant PREF_KEY_BACKGROUND_COLOR.
     */
    public static final String PREF_KEY_BACKGROUND_COLOR = "backgroundColor";

    /**
     * The Constant PREF_KEY_USE_MARGIN_FOR_COLOR_CODING.
     */
    public static final String PREF_KEY_USE_MARGIN_FOR_COLOR_CODING = "userMarginForColorCoding";
    public static final String PREF_KEY_HOTSPOTS_VISIBLE = "hotspotsVisible";

    /**
     * The Constant PREF_KEY_REMEMBER_ME.
     */
    public static final String PREF_KEY_REMEMBER_ME = "rememberMe";

    /**
     * The Constant PREF_KEY_SHOW_WELCOME.
     */
    public static final String PREF_KEY_SHOW_WELCOME = "showWelcome";

    /**
     * The Constant PREF_KEY_SHOW_MENU.
     */
    public static final String PREF_KEY_SHOW_MENU = "showMenu";

    /**
     * The Constant PREF_KEY_UNZOOM_INTERVAL.
     */
    public static final String PREF_KEY_UNZOOM_INTERVAL = "unzoomInterval";
    public static final String PREF_KEY_AUTO_WORD_VARIATION = "autoWordVariation";
    public static final String PREF_KEY_AUTO_SPEECH_PARTS = "autoSpeechParts";
    public static final String PREF_KEY_COLOR_KEY = "colorKey";
    public static final String PREF_KEY_MESSAGE_TYPE = "messageType";
    public static final String PREF_KEY_ACTION_PREFERENCE = "actionPreference";

    /**
     * The pref.
     */
    private static Preference pref;

    /**
     * The image pref.
     */
    private static Preference imagePref;

    /**
     * Sets the by license.
     *
     * @param context the new by license
     */
    public static void setByLicense(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean test = !sp.getBoolean(PREF_KEY_LIMITED_LICENSE, true);
        if (pref != null) {
            pref.setEnabled(test);
            imagePref.setEnabled(test);
        }
    }

    @Override
    public final void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                                final String key) {
        if (key.contentEquals("ttsEngine")) {
            SharedPreferences.Editor localEditor1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
            localEditor1.putString("primaryTTS", "");
            localEditor1.putString("secondaryTTS", "");
            localEditor1.apply();

        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("deprecation")
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        super.onCreate(savedInstanceState);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        addPreferencesFromResource(R.xml.preferences);
        setTitle(R.string.mytalkmobile_preferences);
        pref = getPreferenceManager().findPreference(PREF_KEY_PHRASE_MODE);
        imagePref = getPreferenceManager().findPreference(PREF_KEY_PHRASE_IMAGE);
        if (sp.getBoolean(PREF_KEY_LIMITED_LICENSE, true)) {
            pref.setEnabled(false);
            imagePref.setEnabled(false);
        }

        final ListPreference lpEngines = (ListPreference) findPreference("ttsEngine");
        final ListPreference lpPrimary = (ListPreference) findPreference("primaryTTS");
        final ListPreference lpSecondary = (ListPreference) findPreference("secondaryTTS");
        Set<String> engines = sp.getStringSet("Available-TTS-Engines-Labels", new TreeSet<>());
        Set<String> packages = sp.getStringSet("Available-TTS-Engines-Packages", new TreeSet<>());
        CharSequence[] engines_cs = engines.toArray(new CharSequence[engines.size()]);
        CharSequence[] packages_cs = packages.toArray(new CharSequence[packages.size()]);
        lpEngines.setEntries(engines_cs);
        lpEngines.setEntryValues(packages_cs);
        String defaultEngine = lpEngines.getValue();
        Set<String> languages = sp.getStringSet(defaultEngine, new TreeSet<>());
        ArrayList<String> languages_a = new ArrayList<>(languages);
        Collections.sort(languages_a);
        CharSequence[] languages_cs = languages_a.toArray(new CharSequence[languages_a.size()]);
        ArrayList<String> languageDescriptors = ContainerVoiceEngine.getLanguageDescriptors(languages_a);
        String[] languageDescriptions_a = languageDescriptors.toArray(new String[0]);
        lpPrimary.setEntries(languageDescriptions_a);
        lpPrimary.setEntryValues(languages_cs);
        lpSecondary.setEntries(languageDescriptions_a);
        lpSecondary.setEntryValues(languages_cs);


        lpPrimary.setOnPreferenceClickListener(preference -> {
            String defaultEngine1 = lpEngines.getValue();
            Set<String> languages1 = sp.getStringSet(defaultEngine1, new TreeSet<>());
            ArrayList<String> languages_a1 = new ArrayList<>(languages1);
            Collections.sort(languages_a1);
            CharSequence[] languages_cs1 = languages_a1.toArray(new CharSequence[languages_a1.size()]);
            ArrayList<String> languageDescriptors1 = ContainerVoiceEngine.getLanguageDescriptors(languages_a1);
            String[] languageDescriptions_a1 = languageDescriptors1.toArray(new String[0]);
            lpPrimary.setEntries(languageDescriptions_a1);
            lpPrimary.setEntryValues(languages_cs1);
            return false;
        });
        lpSecondary.setOnPreferenceClickListener(preference -> {
            String defaultEngine12 = lpEngines.getValue();
            Set<String> languages12 = sp.getStringSet(defaultEngine12, new TreeSet<>());
            ArrayList<String> languages_a12 = new ArrayList<>(languages12);
            Collections.sort(languages_a12);
            CharSequence[] languages_cs12 = languages_a12.toArray(new CharSequence[languages_a12.size()]);
            ArrayList<String> languageDescriptors12 = ContainerVoiceEngine.getLanguageDescriptors(languages_a12);
            String[] languageDescriptions_a12 = languageDescriptors12.toArray(new String[0]);
            lpSecondary.setEntries(languageDescriptions_a12);
            lpSecondary.setEntryValues(languages_cs12);
            return false;
        });
    }

}
