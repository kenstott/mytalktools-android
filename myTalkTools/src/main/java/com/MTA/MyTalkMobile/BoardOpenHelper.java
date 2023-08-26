/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Keep;

/**
 * The Class BoardOpenHelper. Makes sure we have a database available and ready for use for a
 * given board.
 */
@Keep
class BoardOpenHelper extends SQLiteOpenHelper {

    /**
     * Instantiates a new board open helper.
     *
     * @param context            the context
     * @param paramString        the param string
     * @param paramCursorFactory the param cursor factory
     * @param index              the index
     */
    public BoardOpenHelper(final Context context, final String paramString,
                           final SQLiteDatabase.CursorFactory paramCursorFactory, final int index) {
        super(context, paramString, paramCursorFactory, index);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite .SQLiteDatabase)
     */
    public void onCreate(final SQLiteDatabase paramSQLiteDatabase) {
    }

    /*
     * (non-Javadoc)
     *
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    public void onUpgrade(final SQLiteDatabase paramSQLiteDatabase, final int index1, final int index2) {
    }
}
