/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Server.IsValidUserRequest;
import com.MTA.MyTalkMobile.Utilities.Network;
import com.MTA.MyTalkMobile.Utilities.Utility;

//import proguard.annotation.Keep;

/**
 * The Class Login - displays a login dialog and stores username and password for use throughout
 * application.
 */
@Keep
class Login extends Dialog implements View.OnClickListener {

    /**
     * The Constant VALIDATED.
     */
    private static final String VALIDATED = "Validated";

    /**
     * The board.
     */
    private final Board board;

    /**
     * The remember me check box.
     */
    private CheckBox rememberMeCheckBox;

    /**
     * The name edit text.
     */
    private EditText nameEditText;

    /**
     * The password edit text.
     */
    private EditText passwordEditText;

    /**
     * The default board edit text.
     */
    private EditText defaultBoardEditText;

    /**
     * the settings
     */
    private SharedPreferences settings;

// --Commented out by Inspection START (1/22/15, 10:31 PM):
//  /**
//   * Instantiates a new login.
//   *
//   * @param context the context
//   * @param index the index
//   */
//  public Login(final Context context, final int index) {
//    super(context, index);
//  }
// --Commented out by Inspection STOP (1/22/15, 10:31 PM)

// --Commented out by Inspection START (1/22/15, 10:31 PM):
//  /**
//   * Instantiates a new login.
//   *
//   * @param context the context
//   * @param paramBoolean the param boolean
//   * @param paramOnCancelListener the param on cancel listener
//   */
//  public Login(final Context context, final boolean paramBoolean,
//      final DialogInterface.OnCancelListener paramOnCancelListener) {
//    super(context, paramBoolean, paramOnCancelListener);
//  }
// --Commented out by Inspection STOP (1/22/15, 10:31 PM)

    /**
     * Instantiates a new login.
     *
     * @param paramBoard the param board
     */
    public Login(final Board paramBoard) {
        super(paramBoard);
        this.board = paramBoard;
        TrialCheck.trialChecked = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public final boolean onKeyDown(final int id, @NonNull final KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
            return true;
        }
        return false;
    }

    /**
     * Str.
     *
     * @param id the id
     * @return the string
     */
    private String str(final int id) {
        return board.getString(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    public final void onClick(final View paramView) {
        int id = paramView.getId();
        if (id == R.id.about) {
            String networkMessage = getContext().getString(R.string.no);
            if (Network.haveNetworkConnection(board)) {
                networkMessage = str(R.string.yes);
            }
            String message = str(R.string.version) + str(R.string.copyright_notice) + networkMessage;
            new AlertDialog.Builder(board).setTitle(R.string.about).setMessage(message).create().show();
        } else if (id == R.id.forgotPassword) {
            if (Network.haveNetworkConnection(board)) {
                Uri uri = Uri.parse(str(R.string.http_www_mytalktools_com_dnn_forgotpassword_aspx));
                board.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                Utility.alert(R.string.requires_an_internet_connection_, board);
            }
        } else if (id == R.id.cancelButton) {
            this.dismiss();
        } else if (id == R.id.createNewAccount) {
            this.dismiss();
            new CreateNewAccount(board).show();
        } else if (id == R.id.okButton) {/* The synced username. */
            String syncedUsername = PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext()).getString(
                    Board.SYNCED_USERNAME, "");
            /* The synced password. */
            String syncedPassword = PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext()).getString(
                    Board.SYNCED_PASSWORD, "");

            if ((!syncedUsername.contentEquals(this.nameEditText.getText().toString()) || !syncedPassword
                    .contentEquals(this.passwordEditText.getText().toString()))
                    || Network.haveNetworkConnection(board)) {
                final Login login = this;
                IsValidUserRequest isv =
                        new IsValidUserRequest(login.nameEditText.getText().toString(), login.passwordEditText
                                .getText().toString());
                isv.executeAsync(result -> {
                    if (result != null && result.contentEquals(VALIDATED)) {
                        Board.setUsername(login.nameEditText.getText().toString());
                        Board.setPassword(login.passwordEditText.getText().toString());
                        Board.setDefaultBoard(login.defaultBoardEditText.getText().toString());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Board.USERNAME, login.nameEditText.getText().toString());
                        editor.putString(Board.PASSWORD, login.passwordEditText.getText().toString());
                        editor.putString(Board.DEFAULT_BOARD, login.defaultBoardEditText.getText().toString());
                        editor.putBoolean(AppPreferences.PREF_KEY_REMEMBER_ME, rememberMeCheckBox.isChecked());
                        editor.apply();
                        editor.putBoolean(AppPreferences.PREF_KEY_SHOW_WELCOME, false);
                        editor.apply();
                        cancel();
                    } else {
                        new AlertDialog.Builder(board).setTitle("Problem").setMessage("Login invalid. " + (result == null ? "NULL" : result)).show();
                        //Toast.makeText(board, "Login invalid.", Toast.LENGTH_SHORT).show();
                    }
                }, board);

            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.login);
        settings = PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
        setTitle(R.string.login_to_MyTalk);
        /* The forgot password. */
        Button forgotPassword = findViewById(R.id.forgotPassword);
        /* The about. */
        Button about = findViewById(R.id.about);
        /* The ok button. */
        Button okButton = findViewById(R.id.okButton);
        /* The cancel button. */
        Button cancelButton = findViewById(R.id.cancelButton);
        /* The create new account button. */
        Button createNewAccountButton = findViewById(R.id.createNewAccount);
        this.rememberMeCheckBox = findViewById(R.id.rememberMe);
        this.nameEditText = findViewById(R.id.nameEditText);
        this.passwordEditText = findViewById(R.id.passwordEditText);
        this.defaultBoardEditText = findViewById(R.id.defaultBoardEditText);

        rememberMeCheckBox.setChecked(settings.getBoolean(AppPreferences.PREF_KEY_REMEMBER_ME, false));
        if (rememberMeCheckBox.isChecked()) {
            nameEditText.setText(Board.getUsername());
            passwordEditText.setText(Board.getPassword());
            defaultBoardEditText.setText(Board.getDefaultBoard());
        } else {
            nameEditText.setText("");
            passwordEditText.setText("");
            defaultBoardEditText.setText("");
        }

        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        createNewAccountButton.setOnClickListener(this);
        about.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }
}
