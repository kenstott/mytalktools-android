/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class NewBoard.
 */

@Keep
class NewBoard extends Dialog {

    /**
     * The Constant MAXIMUM_BOARD_COLUMNS.
     */
    private static final int MAXIMUM_BOARD_COLUMNS = 10;

    /**
     * The Constant MAXIMUM_BOARD_ROWS.
     */
    private static final int MAXIMUM_BOARD_ROWS = 100;
    /**
     * The dialog.
     */
    private final NewBoard dialog;
    /**
     * The columns.
     */
    private int rows, columns;
    /**
     * The rows edit text.
     */
    private Spinner rowsEditText;
    /**
     * The columns edit text.
     */
    private Spinner columnsEditText;

    /**
     * Instantiates a new new board.
     *
     * @param context the context
     */
    public NewBoard(final Context context) {
        super(context);
        dialog = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.newboard);
        rowsEditText = findViewById(R.id.rowsEditText);
        columnsEditText = findViewById(R.id.columnsEditText);
        setTitle(R.string.enter_rows_and_columns);
        Button ok = findViewById(R.id.ok);
        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(v -> {
            rows = -1;
            columns = -1;
            dialog.cancel();
        });
        ok.setOnClickListener(v -> {
            rows =
                    Integer.parseInt(rowsEditText.getItemAtPosition(
                            columnsEditText.getSelectedItemPosition()).toString());
            columns =
                    Integer.parseInt(columnsEditText.getItemAtPosition(
                            columnsEditText.getSelectedItemPosition()).toString());
            if (rows <= 0 || rows > MAXIMUM_BOARD_ROWS) {
                new AlertDialog.Builder(dialog.getContext())
                        .setMessage(R.string.rows_must_be_between_1_and_100)
                        .setPositiveButton(R.string.ok, null).create().show();
                return;
            }
            if (columns <= 0 || columns > MAXIMUM_BOARD_COLUMNS) {
                new AlertDialog.Builder(dialog.getContext())
                        .setMessage(R.string.columns_must_be_between_1_and_100)
                        .setPositiveButton(R.string.ok, null).create().show();
                return;
            }
            dialog.dismiss();
        });
    }

    /**
     * Gets the rows.
     *
     * @return the rows
     */
    public final int getRows() {
        return rows;
    }

    /**
     * Sets the rows.
     *
     * @param value the new rows
     */
    public final void setRows(final int value) {
        this.rows = value;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public final int getColumns() {
        return columns;
    }

    /**
     * Sets the columns.
     *
     * @param value the new columns
     */
    public final void setColumns(final int value) {
        this.columns = value;
    }

}
