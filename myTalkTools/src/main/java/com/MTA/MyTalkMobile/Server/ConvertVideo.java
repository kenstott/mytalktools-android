/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Json.JsonInt;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc

/**
 * The Class ConvertVideo.
 */
class ConvertVideo extends MyTalkWebService {

    /**
     * The Constant ONE_MINUTE.
     */
    private static final int ONE_MINUTE = 60000;

    /**
     * The user name.
     */
    private final String userName;

    /**
     * The library name.
     */
    private final String libraryName;

    /**
     * The video name.
     */
    private final String videoName;

    /**
     * The output name.
     */
    private final String outputName;

    /**
     * Instantiates a new convert video.
     *
     * @param paramUserName    the param user name
     * @param paramLibraryName the param library name
     * @param paramVideoName   the param video name
     * @param paramOutputName  the param output name
     */
    public ConvertVideo(final String paramUserName, final String paramLibraryName,
                        final String paramVideoName, final String paramOutputName) {
        super("ConvertVideoAndroid");
        this.userName = paramUserName;
        this.libraryName = paramLibraryName;
        this.videoName = paramVideoName;
        this.outputName = paramOutputName;
    }

    /**
     * Execute.
     */
    public final void execute() {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, userName)
                            .put(MyTalkWebService.VAR_LIBRARY_NAME, libraryName)
                            .put(MyTalkWebService.VAR_VIDEO_NAME, videoName)
                            .put(MyTalkWebService.VAR_OUTPUT_NAME, outputName).toString();
            if (execute(message)) {
                JsonInt j = getGson().fromJson(getJsonResponse(), JsonInt.class);
                if (j.d != 0) {
                    Thread.sleep(ONE_MINUTE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
