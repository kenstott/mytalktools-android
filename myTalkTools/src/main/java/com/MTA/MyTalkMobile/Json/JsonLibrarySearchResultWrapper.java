/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonLibrarySearchResultWrapper. This is a PJO for re-hydrating server responses.
 */
@Keep
public class JsonLibrarySearchResultWrapper {

    /**
     * The d.
     */
    @Keep
    public ArrayList<LibrarySearchResult> d;

    /**
     * Instantiates a new json library search result wrapper.
     */
    @Keep
    public JsonLibrarySearchResultWrapper() {

    }
}
