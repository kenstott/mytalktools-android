/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class AsyncOverwriteServerDataByFile.
 */
public class AsyncOverwriteServerDataByFile extends AsyncGetNewDatabase {

    /**
     * The db file name.
     */
    private final String dbFileName;

    /**
     * Instantiates a new async overwrite server data by file.
     *
     * @param board           the board
     * @param localDbFilename the local db filename
     */
    public AsyncOverwriteServerDataByFile(final Board board, final String localDbFilename) {
        super(board);
        this.dbFileName = localDbFilename;
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
            return overwriteServerDataByFile(this, dbFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /**
     * Overwrite server data by file.
     *
     * @param asyncOverwriteServerDataByFile the async overwrite server data by file
     * @param paramDbFilename                the param db filename
     * @return the string
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String overwriteServerDataByFile(
            final AsyncOverwriteServerDataByFile asyncOverwriteServerDataByFile,
            final String paramDbFilename) throws Exception {

        List<File> toDelete;

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getBoard().getApplicationContext());
        Board.setUsername(settings.getString(Board.USERNAME, ""));
        Board.setPassword(settings.getString(Board.PASSWORD, ""));
        Board.setDefaultBoard(settings.getString(Board.DEFAULT_BOARD, ""));

        toDelete = AsyncGetNewDatabase.sendFiles(asyncOverwriteServerDataByFile);
        GetNewDatabaseResult result;
        asyncOverwriteServerDataByFile.updatePrimaryMax(2);
        asyncOverwriteServerDataByFile.updateMessage(getBoard().getString(R.string.sending_boards_));

        OverwriteServerDataByFileRequest l =
                new OverwriteServerDataByFileRequest(getBoard().getRestoreBackupFilename().Name,
                        Board.getUsername(), "android", Board.getDefaultBoard(), asyncOverwriteServerDataByFile);
        l.execute(getBoard());
        asyncOverwriteServerDataByFile.incrementPrimaryCount(1);
        asyncOverwriteServerDataByFile.updateMessage();
        result = l.execute(getBoard());
        asyncOverwriteServerDataByFile.incrementPrimaryCount(2);

        if (result == null) return "";
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
}
