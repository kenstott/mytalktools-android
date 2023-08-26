/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Json.DeviceDataImage;
import com.MTA.MyTalkMobile.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AsyncOverwriteServerData.
 */
public class AsyncOverwriteServerData extends AsyncGetNewDatabase {

    /**
     * Instantiates a new async overwrite server data.
     *
     * @param board the board
     */
    public AsyncOverwriteServerData(final Board board) {
        super(board);
        // this.board = board;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.MTA.MyTalkMobile.Server.AsyncGetNewDatabase#doInBackground(com.MTA
     * .MyTalkMobile.Board[])
     */
    @Override
    protected final String doInBackground(final Board... params) {
        setBoard(params[0]);
        try {
            return overwriteServerData(this);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * Overwrite server data.
     *
     * @param progress the progress
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String overwriteServerData(final AsyncOverwriteServerData progress) throws Exception {

        List<File> toDelete;

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getBoard().getApplicationContext());
        Board.setUsername(settings.getString(Board.USERNAME, ""));
        Board.setPassword(settings.getString(Board.PASSWORD, ""));
        Board.setDefaultBoard(settings.getString(Board.DEFAULT_BOARD, ""));

        toDelete = sendFiles(progress);
        GetNewDatabaseResult result;
        progress.updatePrimaryMax(2);
        progress.updateMessage(getBoard().getString(R.string.sending_boards_));

        OverwriteServerDataMultiBoardRequest localGetNewDatabaseRequest =
                new OverwriteServerDataMultiBoardRequest(Board.getUsername(), Board.getPassword(),
                        Board.getDefaultBoard(), new DeviceDataImage(getBoard()), progress);
        progress.incrementPrimaryCount(1);
        progress.updateMessage();
        result = localGetNewDatabaseRequest.execute(this.getBoard());
        progress.incrementPrimaryCount(2);

        if (result != null) {
            if (result.getException() == null) {
                result.write();
                for (File file : toDelete) {
                    if (!file.delete()) {
                        Log.d("d", "Problem deleting file");
                    }
                }
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(getBoard().getApplicationContext()).edit();
                editor.putString(Board.SYNCED_USERNAME, Board.getUsername());
                editor.putString(Board.SYNCED_PASSWORD, Board.getPassword());
                editor.putString(Board.SYNCED_DEFAULT_BOARD, Board.getDefaultBoard());
                editor.apply();
                getBoard().finish();
                Intent localIntent = new Intent(getBoard().getApplicationContext(), Board.class);
                getBoard().startActivity(localIntent);
                return null;
            } else {
                return result.getException();
            }
        }
        return "";
    }
}
