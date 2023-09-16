/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;

import androidx.annotation.Keep;

import java.util.Locale;

// TODO: Auto-generated Javadoc

@Keep
class UtterPhrase extends Dialog {

    /**
     * The utter phrase.
     */
    private final UtterPhrase utterPhrase;
    /**
     * The phrase edit text.
     */
    private EditText phraseEditText;

    /**
     * Instantiates a new utter phrase.
     *
     * @param context the context
     */
    public UtterPhrase(final Context context) {
        super(context);
        utterPhrase = this;
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
        setContentView(R.layout.tts);
        setTitle(getContext().getResources().getString(R.string.enter_a_phrase)
                + Locale.getDefault().getDisplayLanguage());
        this.phraseEditText = findViewById(R.id.EditTextPhrase);
        findViewById(R.id.ButtonOk).setOnClickListener(v -> dismiss());

        findViewById(R.id.ButtonCancel).setOnClickListener(v -> {
            utterPhrase.phraseEditText.setText("");
            cancel();
        });
    }
}
