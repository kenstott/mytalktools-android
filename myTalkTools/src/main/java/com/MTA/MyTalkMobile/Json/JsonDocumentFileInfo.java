/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.Date;

/**
 * The Class JsonDocumentFileInfo. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonDocumentFileInfo {

    /**
     * The Creation time.
     */
    @Keep
    public Date CreationTime;

    /**
     * The Creation time utc.
     */
    @Keep
    public Date CreationTimeUtc;

    /**
     * The Last access time.
     */
    @Keep
    public Date LastAccessTime;

    /**
     * The Last access time utc.
     */
    @Keep
    public Date LastAccessTimeUtc;

    /**
     * The Last write time.
     */
    @Keep
    public Date LastWriteTime;

    /**
     * The Last write time utc.
     */
    @Keep
    public Date LastWriteTimeUtc;

    /**
     * The Is read only.
     */
    @Keep
    public Boolean IsReadOnly;

    /**
     * The Extension.
     */
    @Keep
    public String Extension;

    /**
     * The Fullname.
     */
    @Keep
    public String Fullname;

    /**
     * The Name.
     */
    @Keep
    public String Name;

    /**
     * The Length.
     */
    @Keep
    public long Length;

    /**
     * The Hash code.
     */
    @Keep
    public int HashCode;

    /**
     * Instantiates a new json document file info.
     */
    @Keep
    public JsonDocumentFileInfo() {

    }
}
