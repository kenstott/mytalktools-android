/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Json.JsonDocumentFileInfo;
import com.MTA.MyTalkMobile.Json.JsonGetDocumentFileListResult;

import org.json.JSONObject;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class GetDocumentFileListRequest.
 */
public class GetDocumentFileListRequest extends MyTalkWebService {

    /**
     * The user name.
     */
    private final String userName;

    /**
     * The library name.
     */
    private final String libraryName;

    /**
     * The search pattern.
     */
    private final String searchPattern;

    /**
     * Instantiates a new gets the document file list request.
     *
     * @param paramUserName      the param user name
     * @param paramLibraryName   the param library name
     * @param paramSearchPattern the param search pattern
     */
    public GetDocumentFileListRequest(final String paramUserName, final String paramLibraryName,
                                      final String paramSearchPattern) {
        super("GetDocumentFileList");
        this.userName = paramUserName;
        this.libraryName = paramLibraryName;
        this.searchPattern = paramSearchPattern;
    }

    /**
     * Execute.
     *
     * @return the array list
     */
    public final ArrayList<JsonDocumentFileInfo> execute() {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, this.userName)
                            .put(MyTalkWebService.VAR_LIBRARY_NAME, this.libraryName)
                            .put(MyTalkWebService.VAR_SEARCH_PATTERN, this.searchPattern).toString();
            if (execute(message)) {
                JsonGetDocumentFileListResult j =
                        getGson().fromJson(getJsonResponse(), JsonGetDocumentFileListResult.class);
                return j.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
