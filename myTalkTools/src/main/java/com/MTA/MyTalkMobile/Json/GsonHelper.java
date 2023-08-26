/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class GsonHelper. Fixes some date formatting problems when using Microsoft servers.
 */
@Keep
public class GsonHelper {

    /**
     * Creates the wcf gson.
     *
     * @return the gson
     */
    @Keep
    public static Gson createWcfGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new WcfDateDeserializer());
        return gsonBuilder.create();
    }

    /**
     * The Class WcfDateDeserializer.
     */
    @Keep
    private static class WcfDateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

        /*
         * (non-Javadoc)
         *
         * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement ,
         * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
         */
        @Override
        public final Date deserialize(final JsonElement json, final Type typeOfT,
                                      final JsonDeserializationContext context) {
            String jsonDateToMilliseconds = "/(Date\\((.*?)(\\+.*)?\\))/";
            Pattern pattern = Pattern.compile(jsonDateToMilliseconds);
            Matcher matcher = pattern.matcher(json.getAsJsonPrimitive().getAsString());
            String result = matcher.replaceAll("$2");
            return new Date(Long.parseLong(result));
        }

        /*
         * (non-Javadoc)
         *
         * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type,
         * com.google.gson.JsonSerializationContext)
         */
        @Override
        public final JsonElement serialize(final Date date, final Type arg1,
                                           final JsonSerializationContext arg2) {
            return new JsonPrimitive("/Date(" + date.getTime() + ")/");
        }
    }

}
