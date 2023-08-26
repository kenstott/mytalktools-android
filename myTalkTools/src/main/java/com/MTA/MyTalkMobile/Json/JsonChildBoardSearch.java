/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonChildBoardSearch. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonChildBoardSearch {

    /**
     * The d.
     */
    @Keep
    public ArrayList<ChildBoardSearchResult> d;

    /**
     * Instantiates a new json child board search.
     */
    @Keep
    public JsonChildBoardSearch() {

    }
}
