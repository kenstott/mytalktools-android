/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Json.GsonHelper;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

//import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: Auto-generated Javadoc

/**
 * The Class MyTalkWebService.
 */
public abstract class MyTalkWebService {

    /**
     * The Constant NAMESPACE.
     */
    public static final String NAMESPACE = "https://mytalktools.com/";
    /**
     * The Constant VAR_USERNAME.
     */
    public static final String VAR_USERNAME = "username";
    /**
     * The Constant VAR_USER_NAME.
     */
    static final String VAR_USER_NAME = "userName";
    /**
     * The Constant VAR_PASSWORD.
     */
    static final String VAR_PASSWORD = "password";
    /**
     * The Constant VAR_UUID.
     */
    static final String VAR_UUID = "uuid";
    /**
     * The Constant VAR_LIBRARY_NAME.
     */
    static final String VAR_LIBRARY_NAME = "libraryName";
    /**
     * The Constant VAR_EMAIL.
     */
    static final String VAR_EMAIL = "eMail";
    /**
     * The Constant VAR_FIRST_NAME.
     */
    static final String VAR_FIRST_NAME = "firstName";
    /**
     * The Constant VAR_LAST_NAME.
     */
    static final String VAR_LAST_NAME = "lastName";
    /**
     * The Constant VAR_BOARD_ID.
     */
    static final String VAR_BOARD_ID = "boardID";
    /**
     * The Constant VAR_DATABASE_IMAGE.
     */
    static final String VAR_DATABASE_IMAGE = "databaseImage";
    /**
     * The Constant VAR_DB_FILE_NAME.
     */
    static final String VAR_DB_FILE_NAME = "dbFileName";
    /**
     * The Constant VAR_QUERY.
     */
    static final String VAR_QUERY = "query";
    /**
     * The Constant VAR_OFFSET.
     */
    static final String VAR_OFFSET = "offset";
    /**
     * The Constant VAR_COUNT.
     */
    static final String VAR_COUNT = "count";
    /**
     * The Constant VAR_COPY_FROM_USER_NAME.
     */
    static final String VAR_COPY_FROM_USER_NAME = "copyFromUserName";
    /**
     * The Constant VAR_VIDEO_NAME.
     */
    static final String VAR_VIDEO_NAME = "videoName";
    /**
     * The Constant VAR_OUTPUT_NAME.
     */
    static final String VAR_OUTPUT_NAME = "outputName";
    /**
     * The Constant VAR_SEARCH_PATTERN.
     */
    static final String VAR_SEARCH_PATTERN = "searchPattern";
    /**
     * The Constant VAR_CONTENT_ID.
     */
    static final String VAR_CONTENT_ID = "contentId";
    /**
     * The Constant WEB_SERVICE_SUCCESS.
     */
    private static final int WEB_SERVICE_SUCCESS = 200;
    /**
     * The Constant UTF_82.
     */
    private static final String UTF_82 = "UTF-8";
    /**
     * The Constant UTF_8.
     */
    private static final String UTF_8 = "utf-8";
    /**
     * The Constant APPLICATION_JSON.
     */
    private static final String APPLICATION_JSON = "application/json";
    /**
     * The Constant ACCEPT.
     */
    private static final String ACCEPT = "Accept";
    /**
     * The Constant APPLICATION_JSON_CHARSET_UTF_8.
     */
    private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    /**
     * The Constant CONTENT_TYPE.
     */
    private static final String CONTENT_TYPE = "Content-Type";
    /**
     * The Constant URL.
     */
    private static final String URL = "https://www.mytalktools.com/dnn/sync.asmx";
    /**
     * The name.
     */
    private final String name;

    /**
     * The hc.
     */
    private final HttpClient hc;

    /**
     * The p.
     */
    private HttpPost p = null;

    /**
     * The gson.
     */
    private Gson gson = null;
    //private ObjectMapper mapper = null;

    /**
     * The response.
     */
    private HttpResponse response = null;

    /**
     * Instantiates a new my talk web service.
     *
     * @param action the action
     */
    MyTalkWebService(final String action) {
        name = action;
        hc = new DefaultHttpClient();
        HttpParams httpParameters = hc.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 1000 * 60);
        HttpConnectionParams.setSoTimeout(httpParameters, 1000 * 6000);
        HttpConnectionParams.setTcpNoDelay(httpParameters, true);
        setP(new HttpPost(URL + "/" + name));
        getP().setHeader(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
        getP().setHeader(ACCEPT, APPLICATION_JSON);
        setGson(GsonHelper.createWcfGson());
        //setMapper(new ObjectMapper());
    }

    /**
     * Sets the message.
     *
     * @param message the new message
     */
    private void setMessage(final String message) throws UnsupportedEncodingException {
        StringEntity se = new StringEntity(message, UTF_8);
        se.setContentType(APPLICATION_JSON);
        se.setContentEncoding(UTF_82);
        getP().setEntity(se);
    }

    /**
     * Execute.
     *
     * @param message the message
     * @return true, if successful
     * @throws IOException Signals that an I/O exception has occurred.
     */
    final boolean execute(final String message) throws Exception {
        setMessage(message);
        response = hc.execute(getP());
        return response != null && response.getStatusLine().getStatusCode() == WEB_SERVICE_SUCCESS;
    }

    /**
     * Gets the json response.
     *
     * @return the json response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    final String getJsonResponse() throws Exception {
        if (response != null && response.getStatusLine().getStatusCode() == WEB_SERVICE_SUCCESS) {
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, UTF_82);
        }
        return null;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    final String getName() {
        return name;
    }

    /**
     * Gets the p.
     *
     * @return the p
     */
    private HttpPost getP() {
        return p;
    }

    /**
     * Sets the p.
     *
     * @param value the new p
     */
    final void setP(final HttpPost value) {
        this.p = value;
    }

    /**
     * Gets the gson.
     *
     * @return the gson
     */
    final Gson getGson() {
        return gson;
    }

    /**
     * Sets the gson.
     *
     * @param value the new gson
     */
    private void setGson(final Gson value) {
        this.gson = value;
    }

}
