/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonNewDatabaseResultWrapper. This is a POJO for re-hydrating server responses.
 */
@Keep
public class JsonUserAccounts {

    /**
     * The d.
     */
    @Keep
    public ArrayList<JsonUserAccount> d;

    /**
     * Instantiates a new json new database result wrapper.
     */
    @Keep
    public JsonUserAccounts() {

    }
}
