/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonFileListDirectory. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonFileListDirectory {

    /**
     * The Name.
     */
    @Keep
    public String Name;

    /**
     * The File list.
     */
    @Keep
    public ArrayList<JsonDocumentFileInfo> FileList;

    /**
     * Instantiates a new json file list directory.
     */
    @Keep
    public JsonFileListDirectory() {

    }
}
