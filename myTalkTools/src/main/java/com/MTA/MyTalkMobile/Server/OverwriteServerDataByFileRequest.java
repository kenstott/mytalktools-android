/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Json.JsonNewDatabaseResultWrapper;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc

/**
 * The Class OverwriteServerDataByFileRequest.
 */
class OverwriteServerDataByFileRequest extends MyTalkWebService {

    /**
     * The db file name.
     */
    private final String dbFileName;

    /**
     * The user name.
     */
    private final String userName;

    /**
     * The uuid.
     */
    private final String uuid;

    /**
     * The board id.
     */
    private final String boardID;

    /**
     * The progress.
     */
    private final AsyncGetNewDatabase progress;

    /**
     * Instantiates a new overwrite server data by file request.
     *
     * @param paramDbFileName the param db file name
     * @param paramUserName   the param user name
     * @param paramUuid       the param uuid
     * @param paramBoardID    the param board id
     * @param paramProgress   the param progress
     */
    public OverwriteServerDataByFileRequest(final String paramDbFileName, final String paramUserName,
                                            final String paramUuid, final String paramBoardID, final AsyncGetNewDatabase paramProgress) {
        super("OverwriteServerDataByFile");

        this.userName = paramUserName;
        this.uuid = paramUuid;
        this.boardID = paramBoardID;
        this.dbFileName = paramDbFileName;
        this.progress = paramProgress;
    }

    /**
     * Execute.
     *
     * @param paramBoard the param board
     * @return the gets the new database result
     */
    public final GetNewDatabaseResult execute(final Board paramBoard) {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_DB_FILE_NAME, dbFileName)
                            .put(MyTalkWebService.VAR_USER_NAME, userName).put(MyTalkWebService.VAR_UUID, uuid)
                            .put(MyTalkWebService.VAR_BOARD_ID, boardID).toString();
            if (execute(message)) {
                JsonNewDatabaseResultWrapper j =
                        getGson().fromJson(getJsonResponse(), JsonNewDatabaseResultWrapper.class);
                return new GetNewDatabaseResult(j.d, paramBoard, progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
