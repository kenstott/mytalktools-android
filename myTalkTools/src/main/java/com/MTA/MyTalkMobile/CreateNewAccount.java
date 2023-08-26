/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Json.JsonUserAccount;
import com.MTA.MyTalkMobile.Server.CreateNewAccountRequest;
import com.MTA.MyTalkMobile.Server.GetSampleNames;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The Class CreateNewAccount.
 */
@Keep
class CreateNewAccount extends Dialog implements View.OnClickListener {

    /**
     * The Constant MIN_NAME_LENGTH.
     */
    private static final int MIN_NAME_LENGTH = 7;

    /**
     * The Constant MIN_USERNAME_LENGTH.
     */
    private static final int MIN_USERNAME_LENGTH = 4;

    /**
     * The Constant MAX_USERNAME_LENGTH.
     */
    private static final int MAX_USERNAME_LENGTH = 8;

    /**
     * The Constant MIN_PASSWORD_LENGTH.
     */
    private static final int MIN_PASSWORD_LENGTH = 3;

    /**
     * The Constant UUID.
     */
    private static final String UUID = "android";

    /**
     * The Constant WEB_SERVICE_SUCCESS.
     */
    private static final String WEB_SERVICE_SUCCESS = "Success";

    /**
     * The board.
     */
    private Board board;

    /**
     * The e mail edit text.
     */
    private EditText eMailEditText;

    /**
     * The e mail repeat edit text.
     */
    private EditText eMailRepeatEditText;

    /**
     * The name edit text.
     */
    private EditText nameEditText;

    /**
     * The ok button.
     */
    private Button okButton;
    private Button cancelButton;

    /**
     * The password edit text.
     */
    private EditText passwordEditText;

    /**
     * The password repeat edit text.
     */
    private EditText passwordRepeatEditText;

    /**
     * The username edit text.
     */
    private EditText usernameEditText;

    /**
     * Instantiates a new creates the new account.
     *
     * @param context the context
     * @param layout  the layout
     */
    public CreateNewAccount(final Context context, final int layout) {
        super(context, layout);
    }


    /**
     * Instantiates a new creates the new account.
     *
     * @param context          the context
     * @param b                the b
     * @param onCancelListener the on cancel listener
     */
    public CreateNewAccount(final Context context, final boolean b,
                            final DialogInterface.OnCancelListener onCancelListener) {
        super(context, b, onCancelListener);
    }

    /**
     * Instantiates a new creates the new account.
     *
     * @param paramBoard the param board
     */
    public CreateNewAccount(final Board paramBoard) {
        super(paramBoard);
        this.board = paramBoard;
        this.setCancelable(false);
        CreateNewAccount me = this;
    }

    /**
     * On click.
     *
     * @param dialog the dialog
     * @param which  the which
     */
    public final void onClick(final DialogInterface dialog, final int which) {
        dialog.cancel();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public final void onClick(final View view) {
        if (view.getId() == R.id.ButtonCancel) {
            cancel();
            return;
        }

        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(board).setCancelable(false).setPositiveButton(R.string.ok,
                        (dialog1, which) -> dialog1.cancel());

        String[] names = this.nameEditText.getText().toString().trim().replace("  ", " ").split(" ");

        if (!this.eMailEditText.getText().toString()
                .contentEquals(this.eMailRepeatEditText.getText().toString())) {
            dialog.setMessage(R.string.confirm_email_problem).create().show();
            return;
        }
        if (!this.passwordEditText.getText().toString()
                .contentEquals(this.passwordRepeatEditText.getText().toString())) {
            dialog.setMessage(R.string.confirm_password_problem).create().show();
            return;
        }
        if ((this.nameEditText.getText().length() < MIN_NAME_LENGTH) || (names.length != 2)) {
            dialog.setMessage(R.string.name_problem).create().show();
            return;
        }
        if (this.usernameEditText.getText().length() < MIN_USERNAME_LENGTH) {
            dialog.setMessage(R.string.username_problem).create().show();
            return;
        }
        if (this.usernameEditText.getText().length() > MAX_USERNAME_LENGTH) {
            dialog.setMessage(R.string.username_too_long).create().show();
            return;
        }
        if (this.passwordEditText.getText().length() < MIN_PASSWORD_LENGTH) {
            dialog.setMessage(R.string.password_too_short).create().show();
            return;
        }
        if (!this.eMailEditText.getText().toString()
                .matches(board.getResources().getString(R.string.email_reg_test))) {
            dialog.setMessage(R.string.invalid_email).create().show();
            return;
        }
        if (!this.usernameEditText.getText().toString().matches("^[a-zA-Z\\d]*$")) {
            dialog.setMessage(R.string.username_characters).create().show();
            return;
        }

        final ProgressDialog showProgress = ProgressDialog.show(getContext(), null, board.getResources().getString(R.string.creating_account), true);

        task x = new task(showProgress, board, this, view, dialog, okButton, cancelButton);

        x.execute(this.usernameEditText.getText().toString(),
                this.passwordEditText.getText().toString(), this.eMailEditText.getText().toString(),
                names[0], names[1]);

    }

    /**
     * Hide keyboard.
     *
     * @param view the view
     */
    private void hideKeyboard(final View view) {
        InputMethodManager imm =
                (InputMethodManager) board.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.newaccount);
        setTitle(R.string.create_new_account);
        this.okButton = findViewById(R.id.ButtonOk);
        /* The cancel button. */
        this.cancelButton = findViewById(R.id.ButtonCancel);
        this.usernameEditText = findViewById(R.id.username);
        this.passwordEditText = findViewById(R.id.password);
        this.passwordRepeatEditText = findViewById(R.id.passwordRepeat);
        this.nameEditText = findViewById(R.id.name);
        this.eMailEditText = findViewById(R.id.email);
        this.eMailRepeatEditText = findViewById(R.id.emailRepeat);
        this.okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        this.eMailRepeatEditText.setOnKeyListener((v, keyCode, event) -> false);

        this.eMailRepeatEditText.setOnEditorActionListener((arg0, arg1, arg2) -> {
            switch (arg1) {
                case EditorInfo.IME_ACTION_NEXT:
                    hideKeyboard(passwordRepeatEditText);
                    okButton.requestFocusFromTouch();
                    return true;
                case EditorInfo.IME_ACTION_GO:
                    onClick(okButton);
                    return true;
                default:
                    break;
            }

            return false;
        });
    }

    static class task extends AsyncTask<String, String, String> {

        final ProgressDialog showProgress;
        final WeakReference<Board> board;
        final CreateNewAccount me;
        final WeakReference<View> view;
        final AlertDialog.Builder dialog;
        final WeakReference<Button> cancelButton;
        final WeakReference<Button> okButton;
        ArrayList<JsonUserAccount> samples;
        private String[] params;
        task(ProgressDialog showProgress, Board board, CreateNewAccount me, View view, AlertDialog.Builder dialog, Button okButton, Button cancelButton) {
            this.showProgress = showProgress;
            this.board = new WeakReference<>(board);
            this.me = me;
            this.view = new WeakReference<>(view);
            this.dialog = dialog;
            this.cancelButton = new WeakReference<>(cancelButton);
            this.okButton = new WeakReference<>(okButton);
        }

        @Override
        protected void onPreExecute() {
            showProgress.show();
        }

        @Override
        protected void onPostExecute(String result) {
            showProgress.dismiss();
            if (result.contentEquals(WEB_SERVICE_SUCCESS)) {
                cancelButton.get().setEnabled(false);
                Board.setUsername(params[0]);
                Board.setPassword(params[1]);
                SharedPreferences.Editor localEditor1 =
                        PreferenceManager.getDefaultSharedPreferences(board.get().getApplicationContext()).edit();
                localEditor1.putString(Board.USERNAME, params[0]);
                localEditor1.putString(Board.PASSWORD, params[1]);
                localEditor1.apply();
                final PopupMenu sampleMenu = new PopupMenu(board.get(), view.get());
                for (JsonUserAccount u : samples) {
                    sampleMenu.getMenu().add(u.DisplayName);
                }
                final String[] selectedSample = {null};
                sampleMenu.setOnMenuItemClickListener(item -> {
                    for (JsonUserAccount u : samples) {
                        if (u.DisplayName.equals(item.getTitle().toString())) {
                            selectedSample[0] = u.Username;
                            board.get().startGetNewDatabase(u.Username);
                            me.dismiss();
                            me.cancel();
                        }
                    }
                    return false;
                });
                sampleMenu.setOnDismissListener(menu -> {
                    if (selectedSample[0] == null) sampleMenu.show();
                });
                sampleMenu.show();
            } else {
                dialog.setMessage(result).create().show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            this.params = params;
            samples = new GetSampleNames().execute();
            String result =
                    new CreateNewAccountRequest(params[0],
                            params[1], params[2],
                            params[3], params[4], UUID).execute();
            if (result == null) result = "Unknown error";
            return result;
        }
    }
}
