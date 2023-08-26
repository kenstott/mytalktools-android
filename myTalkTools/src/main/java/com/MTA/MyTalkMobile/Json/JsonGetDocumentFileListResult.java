/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;

/**
 * The Class JsonGetDocumentFileListResult. This is a PJO for re-hydrating server responses.
 */
@Keep
public class JsonGetDocumentFileListResult {

    /**
     * The d.
     */
    @Keep
    public ArrayList<JsonDocumentFileInfo> d;

    /**
     * Instantiates a new json get document file list result.
     */
    @Keep
    public JsonGetDocumentFileListResult() {

    }
}
