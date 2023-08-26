/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import androidx.annotation.Keep;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;

//import proguard.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class GetBingImageSearch.
 */
@Keep
class GetBingImageSearch {

    /**
     * The Constant TOP.
     */
    private static final String TOP = "&count=";

    /**
     * The Constant SKIP.
     */
    private static final String SKIP = "&offset=";

    /**
     * The Constant QUERY.
     */
    private static final String QUERY = "q=";

    /**
     * The Constant MARKET_US.
     */
    private static final String MARKET_US = "&mkt=en-US";

    /**
     * The Constant ADULT_STRICT.
     */
    private static final String ADULT_STRICT = "&safeSearch=Strict";

    /**
     * The Constant FILTER_FACES.
     */
    private static final String FILTER_FACES = MARKET_US + ADULT_STRICT
            + "&imageContent=Portrait&size=Large";

    /**
     * The Constant FILTER_DRAWING.
     */
    private static final String FILTER_DRAWING = MARKET_US + ADULT_STRICT
            + "&imageType=Clipart&size=Large";

    /**
     * The Constant FILTER_BW.
     */
    private static final String FILTER_BW = MARKET_US + ADULT_STRICT
            + "&color=Monochrome&size=Large";

    /**
     * The Constant FILTER_PHOTO.
     */
    private static final String FILTER_PHOTO = MARKET_US + ADULT_STRICT
            + "&imageType=Photo&size=Large";

    /**
     * The Constant UTF_8.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * The Constant BING_URL.
     */
    private static final String BING_URL =
            "https://api.cognitive.microsoft.com/bing/v5.0/images/search?";

    /**
     * The Constant BING_CREDENTIALS.
     */
    private static final String BING_CREDENTIALS = "92b176f38a344679b7069dc63944148c";

    /**
     * Execute.
     *
     * @param query  the query
     * @param offset the offset
     * @param count  the count
     * @param type   the type
     * @return the bing image search result wrapper
     */
    public static JSONObject execute(final String query, final long offset,
                                     final long count, final int type) {
        try {
            DefaultHttpClient hc = new DefaultHttpClient();
            HttpParams httpParameters = hc.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            String url = BING_URL;
            String encQuery = URLEncoder.encode(query, UTF_8);
            url = url + QUERY + encQuery;
            if (type == R.id.filterPhoto) {
                url = url + FILTER_PHOTO;
            } else if (type == R.id.filterBW) {
                url = url + FILTER_BW;
            } else if (type == R.id.filterDRawing) {
                url = url + FILTER_DRAWING;
            } else if (type == R.id.filterFaces) {
                url = url + FILTER_FACES;
            } else if (type == 0) {
                url = url + "";
            }
            url = url + SKIP + offset + TOP + count;
            HttpGet p = new HttpGet(url);
            p.addHeader("Ocp-Apim-Subscription-Key", BING_CREDENTIALS);
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = resp.getEntity();
                    String s = EntityUtils.toString(entity, UTF_8);
                    return new JSONObject(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
