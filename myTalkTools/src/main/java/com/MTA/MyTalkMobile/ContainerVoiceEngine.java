package com.MTA.MyTalkMobile;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ContainerVoiceEngine {

    private String label;
    private String packageName;
    private ArrayList<String> languages;
    private Intent intent;

    public ContainerVoiceEngine() {

    }

    public ContainerVoiceEngine(final String label, final String packageName, final ArrayList<String> languages, final Intent intent) {

        this.label = label;
        this.packageName = packageName;
        this.languages = languages;
        this.intent = intent;
    }

    public static Locale getLocale(String language) {
        String[] codes = language.split("-");
        if (codes.length != 2) return null;
        return new Locale(codes[0], codes[1]);
    }

    public static String getLanguageDescriptor(String language) {
        Locale locale = getLocale(language);
        if (locale == null) return "";
        return locale.getDisplayLanguage() + "-" + locale.getCountry();
    }

    public static ArrayList<String> getLanguageDescriptors(ArrayList<String> languages) {
        ArrayList<String> result = new ArrayList<>();
        for (String language : languages) {
            result.add(getLanguageDescriptor(language));
        }
        return result;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(final Intent intent) {
        this.intent = intent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public ArrayList<String> getLanguages() {
        Collections.sort(languages);
        return languages;
    }

    public void setLanguages(final ArrayList<String> languages) {
        this.languages = languages;
    }

    public ArrayList<String> getLanguageDescriptors() {
        ArrayList<String> result = new ArrayList<>();
        for (String language : getLanguages()) {
            result.add(getLanguageDescriptor(language));
        }
        return result;
    }
}