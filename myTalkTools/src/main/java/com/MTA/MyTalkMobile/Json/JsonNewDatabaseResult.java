/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonNewDatabaseResult. This is a POJO for re-hydrating server responses.
 */
@Keep
public class JsonNewDatabaseResult {

    /**
     * The Exception.
     */
    @Keep
    public String Exception;

    /**
     * The Database image.
     */
    @Keep
    public byte[] DatabaseImage;

    /**
     * The Database path.
     */
    @Keep
    public String DatabasePath;

    /**
     * The Directory list.
     */
    @Keep
    public ArrayList<JsonFileListDirectory> DirectoryList;

    /**
     * Instantiates a new json new database result.
     */
    @Keep
    public JsonNewDatabaseResult() {

    }
}
