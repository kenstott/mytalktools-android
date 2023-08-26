/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.CalendarHelper;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class AsyncGetNewDatabase. Used to return a sqlite database representing the boards for a user.
 */
@Keep
public class AsyncGetNewDatabase extends AsyncTask<Board, Bundle, String> {

    /**
     * The Constant MAX_STEPS.
     */
    private static final int MAX_STEPS = 3;
    /**
     * The copy from account.
     */
    private final String copyFromAccount;
    public Runnable runnable = null;
    /**
     * The board.
     */
    private WeakReference<Board> board;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;
    /**
     * The secondary max.
     */
    private int secondaryMax;
    /**
     * The secondary count.
     */
    private int secondaryCount;

    /**
     * Instantiates a new async get new database.
     *
     * @param localBoard the local board
     */
    public AsyncGetNewDatabase(final Board localBoard) {
        super();
        this.setBoard(localBoard);
        this.copyFromAccount = "";
    }

    /**
     * Instantiates a new async get new database.
     *
     * @param localBoard           the local board
     * @param localCopyFromAccount the local copy from account
     */
    public AsyncGetNewDatabase(final Board localBoard, final String localCopyFromAccount) {
        super();
        this.setBoard(localBoard);
        this.copyFromAccount = localCopyFromAccount;
    }

    /**
     * Send files.
     *
     * @param progress the progress
     * @return the list
     */
    static List<File> sendFiles(final AsyncGetNewDatabase progress) {
        File files = Utility.getMyTalkFilesDir(progress.getBoard());
        ArrayList<File> toDelete = new ArrayList<>();
        ArrayList<File> toSend = new ArrayList<>();
        File[] fileList = files.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.getName().startsWith("-")) {
                    toSend.add(file);
                }
            }
        }
        progress.updatePrimaryMax(toSend.size());
        for (File file : toSend) {
            progress.updateMessage(progress.getBoard().getString(R.string.sending_)
                    + file.getName().replaceFirst("-", ""));
            Object[] result = SendFile.execute(file.getAbsolutePath(), Board.getUsername(), progress);
            progress.incrementPrimaryCount(1);
            if (result.length == 2) {
                toDelete.add(file);
            }
        }
        return toDelete;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(final Board... params) {
        setBoard(params[0]);
        try {
            return getNewDatabase(this, copyFromAccount);
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPreExecute()
     */
    @Override
    protected final void onPreExecute() {
        progressDialog = new ProgressDialog(getBoard());
        progressDialog.setMax(MAX_STEPS);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        progressDialog.setSecondaryProgress(1);
        progressDialog.setMessage(getBoard().getResources().getString(R.string.finding_server));
        progressDialog.setTitle(R.string.syncing);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected final void onPostExecute(final String result) {
        try {
            progressDialog.dismiss();
        } catch (Exception ignore) {
            //
        }
        if (result != null) {
            getBoard().resetLoginToPrevious();
            new AlertDialog.Builder(getBoard()).setTitle(R.string.alert)
                    .setIcon(R.drawable.ic_dialog_alert).setPositiveButton(R.string.ok, null)
                    .setMessage(result).create().show();
        } else {
            Board.setIsLoggedIn(true);
            getBoard().updateMenu();
        }
        if (runnable != null) runnable.run();
    }

    /**
     * Gets the primary max.
     *
     * @return the primary max
     */
    public final int getPrimaryMax() {
        return progressDialog.getMax();
    }

    /**
     * Increment primary count.
     *
     * @param increment the increment
     */
    public final void incrementPrimaryCount(final int increment) {
        getBoard().runOnUiThread(() -> progressDialog.incrementProgressBy(increment));
    }

    /**
     * Update primary max.
     *
     * @param max the max
     */
    public final void updatePrimaryMax(final int max) {
        getBoard().runOnUiThread(() -> {
            progressDialog.setMax(max);
            progressDialog.setIndeterminate(max == 0);
        });
    }

    /**
     * Update secondary max.
     *
     * @param max the max
     */
    public final void updateSecondaryMax(final int max) {
        getBoard().runOnUiThread(() -> {
            secondaryMax = max;
            secondaryCount = 0;
            progressDialog.setIndeterminate(max == 0);
        });
    }

    /**
     * Increment secondary count.
     *
     * @param increment the increment
     */
    public final void incrementSecondaryCount(final int increment) {
        getBoard().runOnUiThread(() -> {
            secondaryCount += increment;
            int count =
                    (int) (((float) secondaryCount / (float) secondaryMax) * progressDialog.getMax());
            progressDialog.setSecondaryProgress(count);
        });
    }

    /**
     * Update message.
     *
     * @param message the message
     */
    public final void updateMessage(final CharSequence message) {
        final CharSequence messageFinal = ((String) message).replace("_reduced", "");
        getBoard().runOnUiThread(() -> progressDialog.setMessage(messageFinal));
    }

    /**
     * Update message.
     */
    final void updateMessage() {
        updateMessage(getBoard().getResources().getString(R.string.retrieving_boards));
    }

    /**
     * Update progress.
     *
     * @param p1      the p1
     * @param p2      the p2
     * @param m1      the m1
     * @param message the message
     */
    public final void updateProgress(final int p1, final int p2, final int m1, final String message) {
        int m2 = 1;
        if (m1 != 0) {
            m2 = m1;
        }
        final int m1f = m2;
        getBoard().runOnUiThread(() -> {
            progressDialog.setMax(m1f);
            progressDialog.setProgress(p1);
            progressDialog.setSecondaryProgress(p2);
            progressDialog.setMessage(message);
            progressDialog.setIndeterminate(m1 == 0);
        });
    }

    /**
     * Gets the new database.
     *
     * @param progress      the progress
     * @param copyFromBoard the copy from board
     * @return the new database
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private String getNewDatabase(final AsyncGetNewDatabase progress, final String copyFromBoard)
            throws Exception {

        progress.updateMessage(getBoard().getResources().getString(R.string.retrieving_boards));
        progress.incrementPrimaryCount(1);

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(board.get().getApplicationContext());
        String username = settings.getString(Board.USERNAME, "");
        String password = settings.getString(Board.PASSWORD, "");
        String defaultBoard = settings.getString(Board.DEFAULT_BOARD, "");

        GetNewDatabaseResult result;
        // if (defaultBoard.length() > 0)
        // {
        if (copyFromBoard.length() > 0) {
            GetNewDatabaseFromOtherAccountMultiBoardRequest localGetNewDatabaseRequest =
                    new GetNewDatabaseFromOtherAccountMultiBoardRequest(username, password, defaultBoard,
                            copyFromBoard, progress);
            result = localGetNewDatabaseRequest.execute(this.getBoard());
        } else {
            GetNewDatabaseRequestMultiBoard localGetNewDatabaseRequest =
                    new GetNewDatabaseRequestMultiBoard(username, password, defaultBoard, progress);
            result = localGetNewDatabaseRequest.execute(this.getBoard());
        }

        if (result != null) {
            if (result.getException() == null) {
                result.write();
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(board.get().getApplicationContext()).edit();
                editor.putString(Board.SYNCED_USERNAME, username);
                editor.putString(Board.SYNCED_PASSWORD, password);
                editor.putString(Board.SYNCED_DEFAULT_BOARD, defaultBoard);
                editor.apply();
                getBoard().finish();
                Intent localIntent = new Intent(board.get().getApplicationContext(), Board.class);
                getBoard().startActivity(localIntent);

                try {
                    CalendarHelper ch = new CalendarHelper(getBoard(), "MyTalk", "MyTalk", "support@mytalk.zendesk.com");
                    ch.UpdateAllEvents();
                    getBoard().updateLocation();
                } catch (Exception ex) {
                    // np
                }

                return null;
            } else {
                return result.getException();
            }
        }
        return "";
    }

    /**
     * Gets the board.
     *
     * @return the board
     */
    final Board getBoard() {
        return board.get();
    }

    /**
     * Sets the board.
     *
     * @param value the new board
     */
    final void setBoard(final Board value) {
        this.board = new WeakReference<>(value);
    }

}
