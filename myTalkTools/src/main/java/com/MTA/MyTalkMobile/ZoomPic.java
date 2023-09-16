/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

// TODO: Auto-generated Javadoc

/**
 * The Class ZoomPic.
 */
@Keep
public class ZoomPic extends Activity {

    /**
     * The Constant INTENT_EXTRA_BOARD_ID.
     */
    public static final String INTENT_EXTRA_BOARD_ID = "boardId";
    /**
     * The Constant INTENT_EXTRA_CONTENT_ID.
     */
    public static final String INTENT_EXTRA_CONTENT_ID = "contentId";
    /**
     * The Constant ONE_SECOND.
     */
    private static final int ONE_SECOND = 1000;
    /**
     * The content.
     */
    private BoardContent content;
    /**
     * The media player.
     */
    private MediaPlayer mediaPlayer;
    /**
     * The do tts.
     */
    private boolean doTTS;
    /**
     * The use external storage.
     */
    private boolean useExternalStorage;
    /**
     * The tts.
     */
    private TextToSpeech tts;
    /**
     * The unzoom interval.
     */
    private int unzoomInterval;
    /**
     * The timer.
     */
    private Timer timer;

    /**
     * Reset timer.
     */
    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (unzoomInterval != 0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    mediaPlayer.stop();
                    tts.stop();
                    finish();
                }
            }, (long) unzoomInterval * ONE_SECOND);
        }
    }

    /**
     * Play sound.
     */
    private void playSound() {
        if (timer != null) {
            timer.cancel();
        }
        if (content.getUrl2().length() > 0) {
            try {
                File fileDir;
                if (useExternalStorage && this.getExternalFilesDir(null) != null) {
                    fileDir = this.getExternalFilesDir(null);
                } else {
                    fileDir = this.getFilesDir();
                }
                if (content.getUrl2().contains("/")) {
                    String str3 = content.getUrl2().replace(" ", "-").replace("/", "-");
                    String fileName;
                    if (fileDir == null) return;
                    fileName = fileDir.getPath() + "/" + str3;
                    FileInputStream fileInputStream = new FileInputStream(fileName);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(fileInputStream.getFD());
                    mediaPlayer.prepareAsync();
                    fileInputStream.close();
                } else {
                    AssetManager assetManager = getApplicationContext().getAssets();
                    AssetFileDescriptor afd = assetManager.openFd(content.getUrl2());
                    FileDescriptor fd = afd.getFileDescriptor();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(fd);
                    mediaPlayer.prepareAsync();
                    afd.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (content.getAlternateTtsText().length() > 0 && doTTS) {
            String toSpeak = content.getAlternateTtsText();
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "theUtId");
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params);
        } else if (content.getText().length() > 0 && doTTS) {
            String toSpeak = content.getText();
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "theUtId");
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public final void onDestroy() {
        mediaPlayer.stop();
        tts.stop();
        tts.shutdown();
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.zoompic);
        Intent localIntent = getIntent();
        try {
            int contentId = localIntent.getIntExtra(INTENT_EXTRA_CONTENT_ID, 1);
            content = new BoardContent(contentId, this);

            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            this.mediaPlayer.setOnCompletionListener(mp -> resetTimer());

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            useExternalStorage = sp.getBoolean(AppPreferences.PREF_KEY_EXTERNAL_STORAGE, true);
            String colorScheme = sp.getString(AppPreferences.PREF_KEY_COLOR_SCHEME, "black,white");
            int foreground = Color.WHITE;
            if (colorScheme.contentEquals("black,white")) {
                foreground = Color.BLACK;
            }
            int background = Color.WHITE;
            if (foreground == Color.WHITE) {
                background = Color.BLACK;
            }
            doTTS = sp.getBoolean(AppPreferences.PREF_KEY_TTS, true);
            unzoomInterval = Integer.parseInt(sp.getString(AppPreferences.PREF_KEY_UNZOOM_INTERVAL, "0"));

            if (content != null) {
                this.setTitle(content.getText());
                ImageView iv = findViewById(R.id.imageView);
                TextView tv = findViewById(R.id.cellTextView);
                tv.setBackgroundColor(background);
                tv.setTextColor(foreground);
                iv.setOnClickListener(v -> playSound());
                ((View) iv.getParent()).setBackgroundColor(background);
                File fileDir;
                if (useExternalStorage && this.getExternalFilesDir(null) != null) {
                    fileDir = this.getExternalFilesDir(null);
                } else {
                    fileDir = this.getFilesDir();
                }
                InputStream inputStream = null;
                tv.setText(content.getText());
                if (content.getUrl() != null && content.getUrl().length() != 0) {
                    if (content.getUrl().contains("/")) {
                        String str3 = content.getUrl().replace(" ", "-").replace("/", "-");
                        File localFile2 = null;
                        if (fileDir != null) localFile2 = new File(fileDir.getPath() + "/" + str3);
                        if (localFile2 != null) {
                            try {
                                inputStream = Files.newInputStream(localFile2.toPath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        AssetManager localAssetManager1 = this.getAssets();
                        try {
                            inputStream = localAssetManager1.open(content.getUrl());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    BitmapFactory.Options imageOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);
                    iv.setImageBitmap(bitmap);
                } catch (Exception ex) {
                    // bitmap too big
                }

                tts = new TextToSpeech(getApplicationContext(), new OnInitListener() {

                    private final UtteranceProgressListener listener = new UtteranceProgressListener() {
                        @Override
                        public void onDone(final String utteranceId) {
                            resetTimer();
                        }

                        @Override
                        public void onError(final String utteranceId) {
                            resetTimer();
                        }

                        @Override
                        public void onStart(final String utteranceId) {
                            // do nothing
                        }
                    };

                    public void onInit(final int status) {
                        playSound();
                        if (doTTS && tts != null) tts.setOnUtteranceProgressListener(listener);
                    }
                });

                resetTimer();
            }
        } catch (Exception ex) {
            finish();
        }
    }
}
