/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Keep;

/**
 * The Class BoardSort. Creates a dialog for managing the sorting options for a given board.
 */
@Keep
class BoardSort extends Dialog {

    /**
     * The board row.
     */
    private final BoardRow boardRow;

    /**
     * Instantiates a new board sort.
     *
     * @param context       the context
     * @param paramBoardRow the param board row
     */
    public BoardSort(final Board context, final BoardRow paramBoardRow) {
        super(context);
        this.boardRow = paramBoardRow;
    }

    /**
     * Instantiates a new board sort.
     *
     * @param context       the context
     * @param theme         the theme
     * @param paramBoardRow the param board row
     */
    public BoardSort(final Board context, final int theme, final BoardRow paramBoardRow) {
        super(context, theme);
        this.boardRow = paramBoardRow;
    }

    /**
     * Instantiates a new board sort.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     * @param paramBoardRow  the param board row
     */
    public BoardSort(final Board context, final boolean cancelable,
                     final OnCancelListener cancelListener, final BoardRow paramBoardRow) {
        super(context, cancelable, cancelListener);
        this.boardRow = paramBoardRow;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        setTitle(R.string.update_board_sorting);
        setContentView(R.layout.board_sort);
        Button done = findViewById(R.id.boardSortDone);
        final Spinner level1 = findViewById(R.id.boardSortLevel1);
        final Spinner level2 = findViewById(R.id.boardSortLevel2);
        final Spinner level3 = findViewById(R.id.boardSortLevel3);

        level1.setSelection(boardRow.getSort1().ordinal());
        level2.setSelection(boardRow.getSort2().ordinal());
        level3.setSelection(boardRow.getSort3().ordinal());

        done.setOnClickListener(v -> {
            boardRow.setSort1(BoardRow.sortOrder.values()[level1.getSelectedItemPosition()]);
            boardRow.setSort2(BoardRow.sortOrder.values()[level2.getSelectedItemPosition()]);
            boardRow.setSort3(BoardRow.sortOrder.values()[level3.getSelectedItemPosition()]);
            Board.getUndoRedo().saveState();
            boardRow.persist(false);
            dismiss();
        });
    }
}
