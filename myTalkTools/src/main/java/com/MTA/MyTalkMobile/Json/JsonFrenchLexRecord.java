/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class JsonDocumentFileInfo. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonFrenchLexRecord {

    @Keep
    public String variant;
    @Keep
    public String _type;
    @Keep
    public String predicate;

    /**
     * Instantiates a new json document file info.
     */
    @Keep
    public JsonFrenchLexRecord() {

    }
}
