/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.view.MenuItem;

import java.util.Stack;
//import proguard.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class UndoRedo.
 */
//@Keep
public class UndoRedo {

    /**
     * The undo states.
     */
    private final Stack<String> undoStates;

    /**
     * The redo states.
     */
    private final Stack<String> redoStates;

    /**
     * The board.
     */
    private final Board board;

    /**
     * The board database name.
     */
    private final String boardDatabaseName;

    /**
     * The redo.
     */
    private MenuItem undo = null, redo = null;

    /**
     * Instantiates a new undo redo.
     *
     * @param paramBoard the param board
     */
    public UndoRedo(final Board paramBoard) {
        this.undoStates = new Stack<>();
        this.redoStates = new Stack<>();
        this.board = paramBoard;
        this.boardDatabaseName = paramBoard.getResources().getString(R.string.boardDatabase);
    }

    /**
     * Sets the undo menu item.
     *
     * @param value the new undo menu item
     */
    public final void setUndoMenuItem(final MenuItem value) {
        this.undo = value;
        updateMenu();
    }

    /**
     * Sets the redo menu item.
     *
     * @param value the new redo menu item
     */
    public final void setRedoMenuItem(final MenuItem value) {
        this.redo = value;
        updateMenu();
    }

    /**
     * Update menu.
     */
    private void updateMenu() {
        if (undo != null && redo != null) {
            undo.setVisible(canUndo());
            redo.setVisible(canRedo());
        }
    }

    /**
     * Save state.
     *
     * @param updateUI the update ui
     */
    private void saveState(final Boolean updateUI) {
        String stateName = this.boardDatabaseName + ".undo." + this.undoStates.size();
        undoStates.push(stateName);
        redoStates.clear();
        board.getDatabase().copyDatabaseToTarget(stateName);
        if (updateUI) {
            updateMenu();
        }
    }

    /**
     * Save state.
     */
    public final void saveState() {
        saveState(true);
    }

    /**
     * Undo.
     */
    public final void undo() {

        // set up redo stack
        if (undoStates.size() > 0) {
            String stateRedoName = this.boardDatabaseName + ".undo." + (this.undoStates.size() + 1);
            board.getDatabase().copyDatabaseToTarget(stateRedoName);
            redoStates.push(stateRedoName);

            // pop from undo stack
            String stateName = undoStates.pop();
            board.getDatabase().copyDatabaseFromSource(stateName);
        }
        updateMenu();
    }

    /**
     * Redo.
     */
    public final void redo() {
        // set up undo stack
        if (redoStates.size() > 0) {
            String stateUndoName = this.boardDatabaseName + ".undo." + (this.undoStates.size() + 1);
            board.getDatabase().copyDatabaseToTarget(stateUndoName);
            undoStates.push(stateUndoName);

            // pop from redo stack
            String stateName = redoStates.pop();
            board.getDatabase().copyDatabaseFromSource(stateName);
        }
        updateMenu();
    }

    /**
     * Can undo.
     *
     * @return the boolean
     */
    private Boolean canUndo() {
        return undoStates.size() > 0;
    }

    /**
     * Can redo.
     *
     * @return the boolean
     */
    private Boolean canRedo() {
        return redoStates.size() > 0;
    }
}
