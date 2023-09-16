/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class TextProperties.
 */
@Keep
class TextProperties extends Dialog {

    /**
     * The board.
     */
    private Board board;

    /**
     * The selected item.
     */
    private BoardContent selectedItem;

    /**
     * The m do not add to phrase bar.
     */
    private Switch mDoNotAddToPhraseBar;

    /**
     * The m do not zoom pics.
     */
    private Switch mDoNotZoomPics;

    /**
     * The m always zoom pics.
     */
    private Switch mAlwaysZoomPics;
    private Switch mHideFromUser;
    private Switch mAlternateTTSVoice;

    /**
     * The text box.
     */
    private EditText textBox;

    /**
     * The prompt box.
     */
    private EditText promptBox;

    /**
     * The tts box.
     */
    private EditText ttsBox;

    private EditText appLink;

    /**
     * The m set font size.
     */
    private Spinner mSetFontSize;

    /**
     * The m set font color.
     */
    private Spinner mSetFontColor;

    /**
     * The m set background color.
     */
    private Spinner mSetBackgroundColor;
    private ImageButton mAppLinks;

    /**
     * Instantiates a new text properties.
     *
     * @param paramBoard the param board
     */
    public TextProperties(final Board paramBoard, BoardContent content) {
        super(paramBoard);
        this.board = paramBoard;
        this.selectedItem = content;
    }

    /**
     * Instantiates a new text properties.
     *
     * @param context the context
     */
    public TextProperties(final Context context) {
        super(context);
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

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.textproperties);
        setTitle(R.string.text_prop);
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());

        // get controls
        mAppLinks = findViewById(R.id.appLinkButton);
        textBox = findViewById(R.id.text);
        promptBox = findViewById(R.id.prompt);
        ttsBox = findViewById(R.id.tts);
        appLink = findViewById(R.id.link);
        mDoNotAddToPhraseBar = findViewById(R.id.doNotAddToPhrase);
        mDoNotZoomPics = findViewById(R.id.doNotZoomPics);
        mAlwaysZoomPics = findViewById(R.id.alwaysZoomPics);
        mAlternateTTSVoice = findViewById(R.id.alternateTTSVoice);
        mHideFromUser = findViewById(R.id.hideFromUser);
        mSetFontSize = findViewById(R.id.setFontSize);
        mSetFontColor = findViewById(R.id.setFontColor);
        mSetBackgroundColor = findViewById(R.id.setBackgroundColor);
        /* The done button. */
        Button doneButton = findViewById(R.id.doneButton);

        // set content
        textBox.setText(this.selectedItem.getText());
        promptBox.setText(this.selectedItem.getTtsSpeechPrompt());
        ttsBox.setText(this.selectedItem.getAlternateTtsText());
        appLink.setText(this.selectedItem.getExternalUrl());
        mDoNotAddToPhraseBar.setChecked(this.selectedItem.getDoNotAddToPhraseBar() == 1);
        mDoNotZoomPics.setChecked(this.selectedItem.getDoNotZoomPics() == 1);
        mAlwaysZoomPics.setChecked(this.selectedItem.getZoom() == 1);
        mHideFromUser.setChecked(this.selectedItem.getHidden());
        mAlternateTTSVoice.setChecked(this.selectedItem.getAlternateTTSVoice());

        // background color
        String colorKey = sp.getString("colorKey", "Goosen");
        String[] colorArray;
        if (colorKey.equals("Fitzgerald")) {
            colorArray = board.getResources().getStringArray(R.array.fitzgeraldsCodes);
        } else {
            colorArray = board.getResources().getStringArray(R.array.goosensCodes);
        }
        ColorKeyAdapter mSetBackgroundColorAdapter =
                new ColorKeyAdapter(getContext(), Arrays.asList(colorArray));
        mSetBackgroundColor.setAdapter(mSetBackgroundColorAdapter);
        mSetBackgroundColor.setSelection(selectedItem.getBackgroundColor());

        // set font color spinner
        String[] fontColorArray;
        // get displayed labels
        fontColorArray = board.getResources().getStringArray(R.array.fontColor);
        // create ContactAdapter to style spinner items
        FontColorKeyAdapter mSetFontColorAdapter =
                new FontColorKeyAdapter(getContext(), Arrays.asList(fontColorArray));
        // push and pull of content is handled in Adapter class
        mSetFontColor.setAdapter(mSetFontColorAdapter);
        mSetFontColor.setSelection(selectedItem.getForegroundColor());

        // set font size spinner
        // get displayed labels
        String[] fontSizeArray = board.getResources().getStringArray(R.array.defaultFontSize);
        // create ContactAdapter to style spinner items
        FontSizeKeyAdapter mSetFontSizeAdapter =
                new FontSizeKeyAdapter(getContext(), Arrays.asList(fontSizeArray));
        // get label keys
        String[] fontSizeValuesArray =
                board.getResources().getStringArray(R.array.defaultFontSizeValues);
        // set spinner to cell value
        List<String> fontSizeValues = Arrays.asList(fontSizeValuesArray);
        mSetFontSize.setAdapter(mSetFontSizeAdapter);
        // find index value and set spinner
        mSetFontSize.setSelection(fontSizeValues.indexOf(Integer.toString(selectedItem.getFontSize())));

        mAppLinks.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(getContext(), mAppLinks);
            final String[] commands = board.getResources().getStringArray(R.array.AppLinkNames);
            final String[] commandText = board.getResources().getStringArray(R.array.AppLinkCommands);
            for (String c : commands) {
                menu.getMenu().add(c);
            }
            menu.setOnMenuItemClickListener(item -> {
                int i = 0;
                for (String c : commands) {
                    if (c.equals(item.getTitle().toString())) {
                        break;
                    }
                    i++;
                }
                String text = commandText[i];
                appLink.setText(text);
                return false;
            });
            menu.show();
        });
        // save content
        doneButton.setOnClickListener(arg0 -> {
            // transfer values back
            selectedItem.setText(textBox.getText().toString());
            selectedItem.setExternalUrl(appLink.getText().toString());
            selectedItem.setAlternateTtsText(ttsBox.getText().toString());
            selectedItem.setTtsSpeechPrompt(promptBox.getText().toString());
            selectedItem.setDoNotAddToPhraseBar(mDoNotAddToPhraseBar.isChecked());
            selectedItem.setDoNotZoomPics(mDoNotZoomPics.isChecked());
            selectedItem.setZoom(mAlwaysZoomPics.isChecked());
            selectedItem.setHidden(mHideFromUser.isChecked());
            selectedItem.setAlternateTTSVoice(mAlternateTTSVoice.isChecked());
            selectedItem.setBackgroundColor(mSetBackgroundColor.getSelectedItemPosition());
            selectedItem.setFontSize((Integer) mSetFontSize.getSelectedItem());
            selectedItem.setForegroundColor(mSetFontColor.getSelectedItemPosition());
            dismiss();

        });
    }
}
