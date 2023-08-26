/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Json.DeviceDataImage;
import com.MTA.MyTalkMobile.Json.JsonNewDatabaseResultWrapper;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc

/**
 * The Class MergeDataMultiBoardRequest.
 */
class MergeDataMultiBoardRequest extends MyTalkWebService {

    /**
     * The database image.
     */
    private final String databaseImage;

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
     * Instantiates a new merge data multi board request.
     *
     * @param paramUserName   the param user name
     * @param paramUuid       the param uuid
     * @param paramBoardID    the param board id
     * @param deviceDataImage the device data image
     * @param progress2       the progress2
     */
    public MergeDataMultiBoardRequest(final String paramUserName, final String paramUuid,
                                      final String paramBoardID, final DeviceDataImage deviceDataImage,
                                      final AsyncGetNewDatabase progress2) {
        super("MergeDataMultiBoardAndroid");

        this.userName = paramUserName;
        this.uuid = paramUuid;
        this.databaseImage = deviceDataImage.getDatabaseImage();
        this.boardID = paramBoardID;
        this.progress = progress2;
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
                    new JSONObject().put(MyTalkWebService.VAR_DATABASE_IMAGE, databaseImage)
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
