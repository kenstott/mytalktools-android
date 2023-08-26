/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Database;
import com.MTA.MyTalkMobile.R;

/**
 * The Class DeviceDataImage. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class DeviceDataImage {

    /**
     * The database image.
     */
    @Keep
    private String databaseImage;

    /**
     * Instantiates a new device data image.
     */
    @Keep
    public DeviceDataImage() {
    }

    /**
     * Instantiates a new device data image.
     *
     * @param context the context
     */
    @Keep
    public DeviceDataImage(final Board context) {
        String boardDatabase = context.getResources().getString(R.string.boardDatabase);
        if (context.getDatabasePath(boardDatabase).exists()) {
            Database.close();
            databaseImage = Database.base64(context);
        }
    }

    /**
     * Gets the database image.
     *
     * @return the database image
     */
    @Keep
    public final String getDatabaseImage() {
        return this.databaseImage;
    }
}
