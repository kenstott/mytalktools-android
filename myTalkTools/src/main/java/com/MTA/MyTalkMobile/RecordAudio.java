/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Utilities.PromptDialog;
import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.File;
import java.io.FileInputStream;

// TODO: Auto-generated Javadoc

/**
 * The Class RecordAudio.
 */
@Keep
class RecordAudio {

    /**
     * The Constant DOT_3GP.
     */
    private static final String DOT_3GP = ".3gp";
    /**
     * The Constant SOUND_RECORDING_TEMP_NAME.
     */
    private static final String SOUND_RECORDING_TEMP_NAME = "soundRecording";
    /**
     * When releasing the media player it doesn't always work on the first try so we will repeat up to
     * this many times.
     */
    private static final int RELEASE_MAXIMUM_TRIES = 100;
    /**
     * Holds the temp filename used when recording audio.
     */
    private static File mediaRecorderOutputFile = null;
    /**
     * The media recorder.
     */
    private static volatile MediaRecorder mediaRecorder = null;
    /**
     * The board.
     */
    private final Board board;
    /**
     * The selected item.
     */
    private final BoardContent selectedItem;
    /**
     * The start.
     */
    private ImageButton start;
    /**
     * The pause.
     */
    private ImageButton pause;
    /**
     * The play.
     */
    private ImageButton play;
    /**
     * The stop.
     */
    private ImageButton stop;
    /**
     * The status.
     */
    private TextView status;

    /**
     * Instantiates a new record audio.
     *
     * @param paramBoard the param board
     */
    public RecordAudio(final Board paramBoard, BoardContent content) {
        this.board = paramBoard;
        selectedItem = content;
    }

    /**
     * Gets the listener.
     *
     * @return the listener
     */
    private OnClickListener getListener() {

        return (dialog, which) -> {
            releaseMediaRecorder();
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    // recorder.stop();
                    status.setText(R.string._stopped_);
                    saveRecording();

                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    try {
                        if (!mediaRecorderOutputFile.delete()) {
                            Log.d("d", "problem deleting file");
                        }
                        dialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        };
    }

    /**
     * Record audio.
     */
    public final void recordSound() {
        LayoutInflater inflater =
                (LayoutInflater) board.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return;
        View v = inflater.inflate(R.layout.audio_recorder, (ViewGroup) board.getWindow().getDecorView().getRootView(), false);
        start = v.findViewById(R.id.start);
        pause = v.findViewById(R.id.pause);
        play = v.findViewById(R.id.play);
        stop = v.findViewById(R.id.stop);
        status = v.findViewById(R.id.statusText);
        status.setText(R.string.press_mic_to_record);
        TextView toRecord = v.findViewById(R.id.recordText);
        if (selectedItem != null && selectedItem.getText().length() > 0) {
            toRecord.setText(String.format("%s%s", board.getString(R.string.cell_text), selectedItem.getText()));
        }
        start.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        pause.setEnabled(false);

        OnClickListener alertListener = getListener();

        final AlertDialog alert =
                new AlertDialog.Builder(board).setView(v).setTitle(R.string.record_sound)
                        .setPositiveButton(R.string.save, alertListener)
                        .setNegativeButton(R.string.cancel, alertListener).create();

        pause.setOnClickListener(v1 -> {
            if (board.getMediaPlayer(null).isPlaying()) {
                status.setText(R.string._paused_);
                board.getMediaPlayer(null).pause();
            } else {
                board.getMediaPlayer(null).start();
                status.setText(R.string._playing_);
            }
        });

        play.setOnClickListener(v12 -> {
            Board.getSentenceBarQueue().clear();
            try {
                FileInputStream fileInputStream = new FileInputStream(mediaRecorderOutputFile);
                board.getMediaPlayer(null).reset();
                board.getMediaPlayer(null).setDataSource(fileInputStream.getFD());
                board.getMediaPlayer(null).setOnCompletionListener(arg0 -> {
                    status.setText("");
                    stop.setEnabled(false);
                    pause.setEnabled(false);
                    play.setEnabled(true);
                    start.setEnabled(true);
                });
                board.getMediaPlayer(null).prepare();
                fileInputStream.close();
                status.setText(R.string._playing_);
                pause.setEnabled(true);
                start.setEnabled(false);
                play.setEnabled(false);
                stop.setEnabled(true);
                board.getMediaPlayer(null).start();
            } catch (Exception ex) {
                Log.i("warning", ex.toString());
            }
        });

        start.setOnClickListener(v13 -> {
            try {
                status.setText(R.string._recording_);
                stop.setEnabled(true);
                pause.setEnabled(false);
                play.setEnabled(false);
                start.setEnabled(false);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

                for (int t = 0; t < RELEASE_MAXIMUM_TRIES; t++) {
                    try {
                        if (board.getMediaPlayer(null) != null) {
                            while (board.getMediaPlayer(null).isPlaying()) {
                                board.getMediaPlayer(null).stop();
                            }
                        }
                        board.releaseMediaPlayer();
                        releaseMediaRecorder();
                        if (getMediaRecorder() != null) {
                            getMediaRecorder().prepare();
                            getMediaRecorder().start();
                        }
                        break;
                    } catch (Exception e) {
                        if (t == RELEASE_MAXIMUM_TRIES - 1) {
                            throw e;
                        }
                    }
                }

            } catch (Exception e) {
                releaseMediaRecorder();
                alert.cancel();
                e.printStackTrace();
                new AlertDialog.Builder(board).setTitle(R.string.alert).setMessage(R.string.no_mic)
                        .setPositiveButton(R.string.ok, null).create().show();
            }
        });

        stop.setOnClickListener(v14 -> {
            status.setText(R.string._stopped_);
            try {
                if (getMediaRecorder() != null)
                    getMediaRecorder().stop();
            } catch (IllegalStateException ex) {
                Log.i("warning", ex.toString());
            }
            while (board.getMediaPlayer(null).isPlaying()) {
                board.getMediaPlayer(null).stop();
            }
            stop.setEnabled(true);
            play.setEnabled(true);
            pause.setEnabled(false);
            start.setEnabled(true);
        });

        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    /**
     * Gets the media recorder and sets it up to record to a temp filename.
     *
     * @return the media recorder
     */
    private synchronized MediaRecorder getMediaRecorder() {
        if (RecordAudio.mediaRecorder == null) {
            RecordAudio.mediaRecorder = new MediaRecorder();
            try {
                RecordAudio.mediaRecorderOutputFile =
                        File.createTempFile(SOUND_RECORDING_TEMP_NAME, DOT_3GP);
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
            RecordAudio.mediaRecorder.setOnErrorListener((mr, what, extra) -> {

            });
            try {
                RecordAudio.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                RecordAudio.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                RecordAudio.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                RecordAudio.mediaRecorder.setOutputFile(RecordAudio.mediaRecorderOutputFile
                        .getAbsolutePath());
            } catch (Exception exception) {
                Utility.alert(exception.toString(), board);
                return null;
            }
        }
        return RecordAudio.mediaRecorder;
    }

    /**
     * Release media recorder.
     */
    private void releaseMediaRecorder() {
        if (RecordAudio.mediaRecorder != null) {
            RecordAudio.mediaRecorder.release();
            RecordAudio.mediaRecorder = null;
        }
    }

    /**
     * Save recording.
     */
    private void saveRecording() {
        PromptDialog dlg =
                new PromptDialog(board, board.getString(R.string.enter_name),
                        board.getString(R.string.no_punctuation_or_spaces_), "") {

                    @Override
                    public boolean onOkClicked(final String paramInput) {
                        String input = Utility.strip(paramInput);
                        String unique =
                                Utility.makeFilenameUnique(Utility.getMyTalkFilesDir(board).getAbsolutePath()
                                        + "/-" + input + DOT_3GP);
                        String finalName = new File(unique).getName().replace("-", "");
                        Utility.copyFile(mediaRecorderOutputFile.getAbsolutePath(), unique);
                        if (!mediaRecorderOutputFile.delete()) {
                            Log.d("d", "problem deleting file");
                        }
                        Board.getUndoRedo().saveState();
                        selectedItem.setType(1);
                        selectedItem.setUrl2("/" + finalName);
                        selectedItem.persist(board.getApplicationContext());
                        GridView gridView = board.findViewById(R.id.mainGrid);
                        RelativeLayout relativeLayout =
                                board.findViewById(R.id.sentenceBarLayout);
                        gridView.invalidateViews();
                        relativeLayout.invalidate();
                        return false;
                    }
                };
        dlg.show();
    }

}
