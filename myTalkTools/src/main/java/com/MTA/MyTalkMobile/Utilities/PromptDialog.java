/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.MTA.MyTalkMobile.R;

// TODO: Auto-generated Javadoc

/**
 * helper for Prompt-Dialog creation.
 */
public abstract class PromptDialog extends AlertDialog.Builder implements OnClickListener {

    /**
     * The input.
     */
    private final EditText input;

    /**
     * Instantiates a new prompt dialog.
     *
     * @param context the context
     * @param title   resource id
     * @param message resource id
     */
    public PromptDialog(final Context context, final int title, final int message) {
        super(context);
        setTitle(title);
        setMessage(message);

        input = new EditText(context);
        setView(input);

        setPositiveButton(R.string.ok, this);
        setNegativeButton(R.string.cancel, this);
    }

    /**
     * Instantiates a new prompt dialog.
     *
     * @param context     the context
     * @param title       the title
     * @param message     the message
     * @param defaultText the default text
     */
    public PromptDialog(final Context context, final String title, final String message,
                        final String defaultText) {
        super(context);
        setTitle(title);
        setMessage(message);

        input = new EditText(context);
        input.setText(defaultText);
        input.setMaxLines(1);
        input.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        input.setSingleLine(true);
        setView(input);

        setPositiveButton(R.string.ok, this);
        setNegativeButton(R.string.cancel, this);
    }

    /**
     * Instantiates a new prompt dialog.
     *
     * @param textProperties the text properties
     * @param title          the title
     * @param message        the message
     * @param text           the text
     */
    public PromptDialog(final Context textProperties, final String title, final String message,
                        final Editable text) {
        super(textProperties);
        setTitle(title);
        setMessage(message);

        input = new EditText(textProperties);
        input.setMaxLines(1);
        input.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        input.setText(text);
        input.setSingleLine(true);
        setView(input);

        setPositiveButton(R.string.ok, this);
        setNegativeButton(R.string.cancel, this);
    }

    /**
     * will be called when "cancel" pressed. closes the dialog. can be overridden.
     *
     * @param dialog the dialog
     */
    private void onCancelClicked(final DialogInterface dialog) {
        dialog.dismiss();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.DialogInterface.OnClickListener#onClick(android.content .DialogInterface,
     * int)
     */
    public final void onClick(final DialogInterface dialog, final int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (onOkClicked(input.getText().toString())) {
                dialog.dismiss();
            }
        } else {
            onCancelClicked(dialog);
        }
    }

    /**
     * called when "ok" pressed.
     *
     * @param paramInput the input
     * @return true, if the dialog should be closed. false, if not.
     */
    protected abstract boolean onOkClicked(String paramInput);
}
