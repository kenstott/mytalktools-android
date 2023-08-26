/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Json.BoardSearchResult;
import com.MTA.MyTalkMobile.Json.ChildBoardSearchResult;
import com.MTA.MyTalkMobile.Json.JsonBoardSearchResultWrapper;
import com.MTA.MyTalkMobile.Json.JsonChildBoardSearch;
import com.MTA.MyTalkMobile.Json.JsonLibrarySearchResultWrapper;
import com.MTA.MyTalkMobile.Json.LibrarySearchResult;
import com.MTA.MyTalkMobile.Search.SearchRequestType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class LibrarySearchRequest.
 */
public class LibrarySearchRequest extends MyTalkWebService {

    /**
     * The Constant WEB_SERVICE_SUCCESS.
     */
    private static final int WEB_SERVICE_SUCCESS = 200;
    /**
     * The Constant IMAGE_SEARCH.
     */
    private static final String IMAGE_SEARCH = "Search";
    /**
     * The Constant SOUND_SEARCH.
     */
    private static final String SOUND_SEARCH = "SearchSounds";
    /**
     * The Constant VIDEO_SEARCH.
     */
    private static final String VIDEO_SEARCH = "SearchVideos";
    /**
     * The Constant BOARD_SEARCH.
     */
    private static final String BOARD_SEARCH = "SearchBoards";
    /**
     * The Constant CHILD_BOARD_SEARCH.
     */
    private static final String CHILD_BOARD_SEARCH = "GetChildBoard";
    /**
     * The query.
     */
    private final String userName;
    private String query;
    /**
     * The count.
     */
    private Integer offset, count;
    /**
     * The content id.
     */
    private Long contentId;
    /**
     * The library name.
     */
    private String libraryName;

    /**
     * Instantiates a new library search request.
     *
     * @param paramUserName the param user name
     * @param paramQuery    the param query
     * @param paramOffset   the param offset
     * @param paramCount    the param count
     */
    public LibrarySearchRequest(final String paramUserName, final String paramQuery,
                                final Integer paramOffset, final Integer paramCount) {
        this(paramUserName, paramQuery, paramOffset, paramCount, SearchRequestType.LIBRARY_IMAGE);
    }

    /**
     * Instantiates a new library search request.
     *
     * @param paramUserName the param user name
     * @param paramQuery    the param query
     * @param paramOffset   the param offset
     * @param paramCount    the param count
     * @param searchType    the search type
     */
    public LibrarySearchRequest(final String paramUserName, final String paramQuery,
                                final Integer paramOffset, final Integer paramCount, final SearchRequestType searchType) {
        super(returnState(searchType));
        this.userName = paramUserName;
        this.query = paramQuery;
        this.offset = paramOffset;
        this.count = paramCount;
    }

// --Commented out by Inspection START (1/22/15, 10:31 PM):
//  /**
//   * Instantiates a new library search request.
//   *
//   * @param paramUserName the param user name
//   * @param paramContentId the param content id
//   * @param searchType the search type
//   */
//  public LibrarySearchRequest(final String paramUserName, final Long paramContentId,
//      final SearchRequestType searchType) {
//    super(returnState(searchType));
//    this.contentId = paramContentId;
//    this.userName = paramUserName;
//  }
// --Commented out by Inspection STOP (1/22/15, 10:31 PM)

    /**
     * Instantiates a new library search request.
     *
     * @param paramUserName the param user name
     * @param library       the library
     * @param sourceContent the source content
     * @param searchType    the search type
     */
    public LibrarySearchRequest(final String paramUserName, final String library,
                                final ChildBoardSearchResult sourceContent, final SearchRequestType searchType) {
        super(returnState(searchType));
        this.contentId = sourceContent.ContentId;
        this.userName = paramUserName;
        this.libraryName = library;
    }

    /**
     * Return state.
     *
     * @param searchType the search type
     * @return the string
     */
    private static String returnState(final SearchRequestType searchType) {

        switch (searchType) {
            case LIBRARY_SOUND:
                return SOUND_SEARCH;
            case LIBRARY_IMAGE:
                return IMAGE_SEARCH;
            case LIBRARY_VIDEO:
                return VIDEO_SEARCH;
            case LIBRARY_BOARD:
                return BOARD_SEARCH;
            case LIBRARY_CHILD_BOARD:
                return CHILD_BOARD_SEARCH;
        }

        return null;
    }

    /**
     * Execute.
     *
     * @return the array list
     */
    public final ArrayList<LibrarySearchResult> execute() {
        try {
            setP(new HttpPost("https://www.mytalktools.com/dnn/LibrarySearch.asmx/" + this.getName()));
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, userName)
                            .put(MyTalkWebService.VAR_QUERY, query).put(MyTalkWebService.VAR_OFFSET, offset)
                            .put(MyTalkWebService.VAR_COUNT, count).toString();
            if (execute(message)) {
                JsonLibrarySearchResultWrapper j =
                        getGson().fromJson(getJsonResponse(), JsonLibrarySearchResultWrapper.class);
                return j.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Execute board search.
     *
     * @return the array list
     */
    public final ArrayList<BoardSearchResult> executeBoardSearch() {
        try {
            HttpClient hc = new DefaultHttpClient();
            HttpParams httpParameters = hc.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            HttpPost p =
                    new HttpPost("https://www.mytalktools.com/dnn/LibrarySearch.asmx/" + this.getName());
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, userName)
                            .put(MyTalkWebService.VAR_QUERY, query).put(MyTalkWebService.VAR_OFFSET, offset)
                            .put(MyTalkWebService.VAR_COUNT, count).toString();
            StringEntity se = new StringEntity(message, "utf-8");
            se.setContentType("application/json");
            se.setContentEncoding("UTF-8");
            p.setEntity(se);
            p.setHeader("Content-Type", "application/json; charset=utf-8");
            p.setHeader("Accept", "application/json");
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == WEB_SERVICE_SUCCESS) {
                    HttpEntity entity = resp.getEntity();
                    String s = EntityUtils.toString(entity, "UTF-8");
                    JsonBoardSearchResultWrapper j =
                            getGson().fromJson(s, JsonBoardSearchResultWrapper.class);
                    return j.d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Execute child board search.
     *
     * @return the array list
     */
    public final ArrayList<ChildBoardSearchResult> executeChildBoardSearch() {
        try {
            HttpClient hc = new DefaultHttpClient();
            HttpParams httpParameters = hc.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            HttpPost p =
                    new HttpPost("https://www.mytalktools.com/dnn/LibrarySearch.asmx/" + this.getName());
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, userName)
                            .put(MyTalkWebService.VAR_LIBRARY_NAME, this.libraryName)
                            .put(MyTalkWebService.VAR_CONTENT_ID, contentId).toString();
            StringEntity se = new StringEntity(message, "utf-8");
            se.setContentType("application/json");
            se.setContentEncoding("UTF-8");
            p.setEntity(se);
            p.setHeader("Content-Type", "application/json; charset=utf-8");
            p.setHeader("Accept", "application/json");
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == WEB_SERVICE_SUCCESS) {
                    HttpEntity entity = resp.getEntity();
                    String s = EntityUtils.toString(entity, "UTF-8");
                    JsonChildBoardSearch j = getGson().fromJson(s, JsonChildBoardSearch.class);
                    return j.d;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
