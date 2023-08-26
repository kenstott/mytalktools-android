/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class UserName. This is a POJO for use in re-hydrating server responses
 */
@Keep
public class JsonUserAccount {

    /**
     * The id.
     */
    @Keep
    public String UserID;

    /**
     * The Title.
     */
    @Keep
    public String Username;

    /**
     * The Media url.
     */
    @Keep
    public String FirstName;

    /**
     * The Source url.
     */
    @Keep
    public String LastName;

    /**
     * The Display url.
     */
    @Keep
    public String Email;

    /**
     * The Width.
     */
    @Keep
    public String DisplayName;

    /**
     * Instantiates a new bing image.
     */
    @Keep
    public JsonUserAccount() {
    }
}
