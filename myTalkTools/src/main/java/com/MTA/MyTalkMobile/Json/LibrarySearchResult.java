/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class LibrarySearchResult. This is a POJO for re-hydrating server responses.
 */
@Keep
public class LibrarySearchResult {

    /**
     * The Media url.
     */
    @Keep
    public String MediaUrl;

    /**
     * The Thumbnail url.
     */
    @Keep
    public String ThumbnailUrl;

    /**
     * The Tags.
     */
    @Keep
    public String Tags;

    /**
     * The Filename.
     */
    @Keep
    public String Filename;
}
