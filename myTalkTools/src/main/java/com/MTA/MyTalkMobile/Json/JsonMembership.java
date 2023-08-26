/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class JsonMembership. This is a PJO for re-hydrating server responses.
 */
@Keep
public class JsonMembership {

    /**
     * The Created date.
     */
    @Keep
    private String CreatedDate;

    /**
     * Instantiates a new json membership.
     */
    @Keep
    public JsonMembership() {
    }

    /**
     * Gets the created date.
     *
     * @return the created date
     */
    @Keep
    public final Date getCreatedDate() {
        String jsonDateToMilliseconds = "/(Date\\((.*?)(\\+.*)?\\))/";
        Pattern pattern = Pattern.compile(jsonDateToMilliseconds);
        Matcher matcher = pattern.matcher(CreatedDate);
        String result = matcher.replaceAll("$2");
        return new Date(Long.parseLong(result));
    }
}
