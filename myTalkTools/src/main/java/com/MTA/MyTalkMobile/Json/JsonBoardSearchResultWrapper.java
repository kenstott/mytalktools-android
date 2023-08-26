/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonBoardSearchResultWrapper. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonBoardSearchResultWrapper {

    /**
     * The d.
     */
    @Keep
    public ArrayList<BoardSearchResult> d;

    /**
     * Instantiates a new json board search result wrapper.
     */
    @Keep
    public JsonBoardSearchResultWrapper() {

    }
}
