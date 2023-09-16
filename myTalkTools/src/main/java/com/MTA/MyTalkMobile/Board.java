/*
 * Copyright MTA Consulting (c) 2014
 */

package com.MTA.MyTalkMobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MTA.MyTalkMobile.Billing.BillingDataSource;
import com.MTA.MyTalkMobile.Json.JsonDocumentFileInfo;
import com.MTA.MyTalkMobile.Json.JsonLexRecord;
import com.MTA.MyTalkMobile.Json.JsonLexRecords;
import com.MTA.MyTalkMobile.Json.JsonUserAccount;
import com.MTA.MyTalkMobile.Search.SearchRequestType;
import com.MTA.MyTalkMobile.Server.AsyncGetNewDatabase;
import com.MTA.MyTalkMobile.Server.AsyncMergeData;
import com.MTA.MyTalkMobile.Server.AsyncOverwriteServerData;
import com.MTA.MyTalkMobile.Server.AsyncOverwriteServerDataByFile;
import com.MTA.MyTalkMobile.Server.CheckTrialPeriod;
import com.MTA.MyTalkMobile.Server.GetSampleNames;
import com.MTA.MyTalkMobile.Server.GetUserRoles;
import com.MTA.MyTalkMobile.Server.GetWordVariants;
import com.MTA.MyTalkMobile.Utilities.DownloadTask;
import com.MTA.MyTalkMobile.Utilities.PromptDialog;
import com.MTA.MyTalkMobile.Utilities.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * This android activity presents and controls a MyTalk board.
 */
@Keep
public class Board extends Activity implements EasyPermissions.PermissionCallbacks, OnSharedPreferenceChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    /**
     * The most recent username used to sync with the server.
     */
    public static final String SYNCED_USERNAME = "syncedUsername";
    /**
     * The most recent password used to sync with the server.
     */
    public static final String SYNCED_PASSWORD = "syncedPassword";
    /**
     * The most recent default board name used to sync with the server.
     */
    public static final String SYNCED_DEFAULT_BOARD = "syncedDefaultBoard";
    /**
     * The username for the most recent login.
     */
    public static final String USERNAME = "username";
    /**
     * The password for the most recent login.
     */
    public static final String PASSWORD = "password";
    /**
     * The default board name for the most recent login.
     */
    public static final String DEFAULT_BOARD = "defaultBoard";
    /**
     * The Constant INTENT_EXTRA_BOARD_ID.
     */
    public static final String INTENT_EXTRA_BOARD_ID = "boardId";
    //private static final int REQUEST_CODE_RESOLUTION = 3;
    /**
     * The Constant INTENT_EXTRA_BOARD_NAME.
     */
    public static final String INTENT_EXTRA_BOARD_NAME = "boardName";
    public static final String INTENT_EXTRA_CELL_PIC = "cellPic";
    /**
     * The Constant INTENT_EXTRA_IS_EDITABLE.
     */
    public static final String INTENT_EXTRA_IS_EDITABLE = "isEditable";
    /**
     * The Constant INTENT_EXTRA_SIGN_IN.
     */
    public static final String INTENT_EXTRA_SIGN_IN = "signIn";
    public static final int MOST_USED_BOARD = -20;
    public static final int MOST_RECENTS_BOARD = -30;
    public static final int WORD_VARIANT_BOARD = -50;
    public static final int SCHEDULED_BOARD = -40;
    public static final int LOCATIONS_BOARD = -60;
    public static final int CONTACTS_BOARD = -70;
    public static final Map<String, BillingDataSource.SkuState> licenses = new HashMap<>();
    private static final String INTENT_EXTRA_URI = "uri";
    /**
     * the GO_BACK_COMMAND for "GO BACK" cells.
     */
    private static final int GO_BACK_COMMAND = 19;
    /**
     * The GO_HOME_COMMAND for "GO HOME" cells.
     */
    private static final int GO_HOME_COMMAND = 18;
    private static final String TAG = "drive-quickstart";
    /**
     * The maximum number of seconds for a video recorded by MyTalk.
     */
    private static final int MAXIMUM_VIDEO_SECONDS = 60;
    /**
     * The number of milliseconds to pause after voicing a TTS phrase.
     */
    private static final int PAUSE_BETWEEN_WORDS_TIME = 500;
    /**
     * The number milliseconds that create the long click activity when pressing on the phrase bar
     * play button. Typically this invokes the social sharing features.
     */
    private static final int PHRASE_BAR_LONG_CLICK_TIME = 240;
    /**
     * The pixel height for the phrase bar cells.
     */
    private static final int PHRASE_BAR_CELL_HEIGHT = 200;
    /**
     * The pixel width for the phrase bar cells.
     */
    private static final int PHRASE_BAR_CELL_WIDTH = 200;
    /**
     * The JPEG compression ratio used when saving JPEGs.
     */
    private static final int JPEG_COMPRESSION_RATIO = 70;
    /**
     * When parsing a TTS phrase words must be less than this length.
     */
    private static final int MAX_WORD_LENGTH = 50;
    /**
     * Controls how the volume switch interacts with the volume setting.
     */
    private static final int VOLUME_CONTROL_STREAM = 3;
    /**
     * The background color for phrase bar cell while it is being voiced.
     */
    private static final int COLOR_LIGHT_YELLOW = 0xFFFFFFE0;
    /**
     * The pixels to pad an image in a phrase bar cell.
     */
    private static final int PHRASE_BAR_IMAGE_PADDING = 10;
    /**
     * The pixels to pad the text in a phrase bar cell.
     */
    private static final int PHRASE_BAR_TEXTVIEW_PADDING = 10;
    /**
     * The maximum number of text lines to present in a text-only phrase bar cell.
     */
    private static final int PHRASE_BAR_MAXIMUM_TEXT_LINES = 10;
    /**
     * When redrawing the phrase bar - indicates that you should left align and not highlight any
     * cells.
     */
    private static final int RESET_TO_START = -2;
    /**
     * The Constant MIME_TYPE_PLAIN_TEXT.
     */
    private static final String MIME_TYPE_PLAIN_TEXT = "plain/text";
    /**
     * The Constant VIDEO_FILENAME_PREFIX.
     */
    private static final String VIDEO_FILENAME_PREFIX = "video_";
    /**
     * The Constant SIMPLE_DATE_FORMAT.
     */
    private static final String SIMPLE_DATE_FORMAT = "yyyyMMdd_HHmmss";
    /**
     * The Constant PHOTO_PICKER_IMAGE.
     */
    private static final String PHOTO_PICKER_IMAGE = "image/*";
    /**
     * The Constant CREATE_NEW_BOARD.
     */
    private static final int CREATE_NEW_BOARD = -1;
    /**
     * The Constant DO_NOT_HIGHLIGHT.
     */
    private static final int DO_NOT_HIGHLIGHT = CREATE_NEW_BOARD;
    /**
     * The Constant MAX_SENTENCE_BAR_SIZE.
     */
    private static final int MAX_SENTENCE_BAR_SIZE = 32;
    /**
     * The Constant QUESTION_MARK.
     */
    private static final String QUESTION_MARK = "?";
    /**
     * The Constant DOT_WAV.
     */
    private static final String DOT_WAV = ".wav";
    /**
     * The Constant EDITED.
     */
    private static final String EDITED = "_e";
    /**
     * The Constant JPG.
     */
    private static final String JPG = "jpg";
    /**
     * The Constant PNG.
     */
    private static final String PNG = "png";
    /**
     * The Constant DOT_JPG.
     */
    private static final String DOT_JPG = ".jpg";
    /**
     * Holds a single list of all the cells added to the phrase bar.
     */
    private static final ArrayList<BoardContent> sentenceBarPhrase = new ArrayList<>();
    private static final int PERMISSION_RETURN_CODE = 112;
    //private static final String DOT_PNG = ".png";
    private final static String[] ableNetCodes = {" ", "\n"};
    private final static String[] rjCooperCodes = {"~1", "~3"};
    private static final ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    /**
     * Holds a copy of single list of all the cells added to the phrase bar and then dequeues them
     * from this list in reverse order to support voicing of the phrase bar.
     */
    private static final ArrayList<BoardContent> sentenceBarQueue = new ArrayList<>();
    private static final Stack<Character> characters = new Stack<>();
    //private static final String BUTTON_TEXT = "Call Drive API";
    public static Board currentBoard;
    private static String primaryVoice;
    private static String secondaryVoice;
    private static List<String> commands;
    private static PendingIntent mGeofencePendingIntent;
    private static boolean GoogleApiConnected;
    private static boolean GoogleApiNotAvailable;
    /**
     * The trial check.
     */
    private static TrialCheck trialCheck = null;
    private static BillingDataSource billingDataSource = null;
    /**
     * Indicates that he user is in author mode.
     */
    private static boolean loggedIn;
    /**
     * The selected item id.
     */
    private static int selectedItemId;
    /**
     * Manages undo/redo operations.
     */
    private static UndoRedo undoRedo;
    /**
     * The is editable.
     */
    private static boolean isEditable;
    /**
     * The username of the logged in account.
     */
    private static String username;
    /**
     * The password of the logged in account.
     */
    private static String password;
    /**
     * The default board name of the logged in account.
     */
    private static String defaultBoard;
    /**
     * The media player.
     */
    private static MediaPlayer mediaPlayer = null;
    /**
     * The sentence bar delete ms.
     */
    private static long sentenceBarDeleteMs = 0;
    /**
     * The cell count.
     */
    private static int cellCount;
    /**
     * The TTS engine.
     */
    private static TextToSpeech tts;
    private static TextToSpeech ttsSecondary;
    private static boolean inAdvanceSelection;
    private static GoogleApiClient mGoogleApiClient;
    private static BoardDirectoryAdapter mAdapter;
    public final String[] knownInappSKUs = new String[]{TrialCheck.FULL_LICENSE, TrialCheck.FULL_LICENSE_PLUS_FAMILY, TrialCheck.FULL_LICENSE_PLUS_PROFESSIONAL};
    private ArrayList<ContainerVoiceEngine> containerVEArray;
    private boolean useExternalStorage;
    private boolean isActive;
    /**
     * The sentence bar linear layout.
     */
    private LinearLayout sentenceBarLinearLayout;
    /**
     * The strip.
     */
    private HorizontalScrollView strip;
    /**
     * The linear strip.
     */
    private LinearLayout linearStrip;
    private GridView mainGrid;
    private RecyclerView leftDrawer;
    private DrawerLayout drawerLayout;
    /**
     * The sentence bar layout.
     */
    private RelativeLayout sentenceBarLayout;
    private Timer timer;
    /**
     * dateIsAfter boolean value
     */
    private Boolean dateIsAfter;
    /**
     * The restore backup filename.
     */
    private JsonDocumentFileInfo restoreBackupFilename;
    /**
     * The main view.
     */
    private View mainView;
    /**
     * The board.
     */
    private Board board;
    /**
     * The board id.
     */
    private int boardId;
    private String boardUri;
    /**
     * The board row.
     */
    private BoardRow boardRow;
    /**
     * The board name.
     */
    private String boardName;
    /**
     * Tracks the cell most recently tapped on by user when in author mode.
     */
    private BoardContent selectedItem;
    private BoardContent selectedHotspotItem;
    private Integer selectedScanPosition = null;
    private Integer selectedHotspotScanPosition = null;
    /**
     * If a cell has been 'copied' it is stored here.
     */
    private BoardContent bufferItem;
    /**
     * The main menu.
     */
    private Menu mainMenu;
    /**
     * The video output uri.
     */
    private Uri videoOutputUri;
    /**
     * The database.
     */
    private Database database;
    private SearchRequestType rt;
    private ShareActionProvider shareActionProvider;
    private Menu optionsMenu;
    private String scanSwitch;
    private String[] switchCodes;
    private int autoScanInterval;
    private boolean autoStartAutoScan;
    private int autoScanLoops;
    private int autoScanLoopCounter = 0;
    private boolean scanByRow = false;
    private boolean columnScanMode = false;

    /**
     * Instantiates a new board.
     */
    public Board() {
        Log.d("here", "");
    }

    /**
     * Gets the undo redo.
     *
     * @return the undo redo
     */
    public static UndoRedo getUndoRedo() {
        return undoRedo;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param value the new password
     */
    public static void setPassword(final String value) {
        password = value;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public static String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param value the new username
     */
    public static void setUsername(final String value) {
        username = value;
    }

    /**
     * Gets the default board.
     *
     * @return the default board
     */
    public static String getDefaultBoard() {
        return Board.defaultBoard;
    }

    /**
     * Sets the default board.
     *
     * @param value the new default board
     */
    public static void setDefaultBoard(final String value) {
        Board.defaultBoard = value;
    }

    /**
     * Indicates if the Board is in author mode.
     *
     * @return True if the Board is in author mode.
     */
    public static boolean getIsEditable() {
        return isEditable;
    }

    public static boolean getIsLoggedIn() {
        return loggedIn;
    }

    /**
     * Sets the checks if is logged in.
     *
     * @param value the new checks if is logged in
     */
    public static void setIsLoggedIn(final boolean value) {
        loggedIn = value;
    }

    /**
     * @return list of cell contents for phrase bar
     */
    public static ArrayList<BoardContent> getSentenceBarQueue() {
        return sentenceBarQueue;
    }

    private static String join(ArrayList<?> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(", ");
        }
        return builder.toString();
    }

    private static Bitmap createVideoThumbnail(Context context, Uri uri) {
        MediaMetadataRetriever mediametadataretriever = new MediaMetadataRetriever();
        try {
            mediametadataretriever.setDataSource(context, uri);
            Bitmap bitmap = mediametadataretriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
            if (null != bitmap) {
                int j = 500; //getThumbnailSize(context, i);
                return ThumbnailUtils.extractThumbnail(bitmap, j, j, 2);
            }
            return null;
        } catch (Throwable t) {
            // TODO log
            return null;
        } finally {
            try {
                mediametadataretriever.release();
            } catch (RuntimeException | IOException ignored) {
            }
        }
    }

    private static Uri addVideoToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.MIME_TYPE, "image/mpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    private static List<BoardDirectoryItem> getDummyContents(Board board) {
        ArrayList<BoardDirectoryItem> results = new ArrayList<>();
        BoardRow boardRow = new BoardRow(1, board);
        List<BoardContent> contents = boardRow.getContents();
        for (BoardContent child : contents) {
            results.add(new BoardDirectoryItem(child, board, 0));
        }
        return results;
    }

    public boolean getUseExternalStorage() {
        return useExternalStorage;
    }

    public void setUseExternalStorage(boolean useExternalStorage) {
        this.useExternalStorage = useExternalStorage;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // do nothing
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    public GoogleApiClient getGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    //.addApi(Drive.API)
                    .build();
        }
        return mGoogleApiClient;
    }

    /**
     * Gets the selected restore backup filename.
     *
     * @return the restore backup filename
     */
    public final JsonDocumentFileInfo getRestoreBackupFilename() {
        return this.restoreBackupFilename;
    }

    /**
     * Sets the restore backup filename.
     *
     * @param value the new restore backup filename
     */
    public final void setRestoreBackupFilename(final JsonDocumentFileInfo value) {
        this.restoreBackupFilename = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStart()
     */
    public final void onStart() {
        isActive = true;
        super.onStart();
        if (mainMenu != null) {
            populateMenu(mainMenu);
        }
        mainView.post(() -> {
            Intent intent = getIntent();
            Uri data = intent.getData();
            intent.setData(null);
            handleUri(data);
            this.mainGrid.invalidateViews();
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStop()
     */
    public final void onStop() {
        isActive = false;
        super.onStop();
    }

    /**
     * Play next word.
     */
    private void playNextWord(final Runnable callback) {
        if (Board.getSentenceBarQueue().size() == 0) {
            drawSentenceBarPost(RESET_TO_START);
            if (callback != null) {
                callback.run();
            }
            return;
        }
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        BoardContent bc = Board.getSentenceBarQueue().remove(0);
        drawSentenceBarPost(cellCount++);
        /* log information about cellCount (position) in _drawSentenceBarText */
        boolean doTTS = settings.getBoolean(AppPreferences.PREF_KEY_TTS, true);
        if (bc.getMediaType(board) == BoardContent.MediaType.sound) {
            try {
                bc.playSound(board, getMediaPlayer(callback), null);
            } catch (Exception e) {
                if (bc.getAlternateTtsText() != null && bc.getAlternateTtsText().length() > 0 && doTTS
                        && tts != null) {
                    UtteranceProgressListener listener = new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            playNextWord(callback);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            playNextWord(callback);
                        }
                    };
                    if (tts != null && listener != null) {
                        tts.setOnUtteranceProgressListener(listener);
                        tts.speak(bc.getAlternateTtsText(), 2, null, bc.getAlternateTtsText());
                    }
                } else if (bc.getText() != null && bc.getText().length() > 0 && doTTS && tts != null) {
                    UtteranceProgressListener listener = new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {

                            playNextWord(callback);
                        }

                        @Override
                        public void onError(String utteranceId) {
                            playNextWord(callback);
                        }
                    };
                    if (tts != null) {
                        tts.setOnUtteranceProgressListener(listener);
                        tts.speak(bc.getText(), 2, null, bc.getText());
                    }
                } else {
                    mainView.post(() -> playNextWord(callback));
                    e.printStackTrace();
                }
            }
        } else if (bc.getAlternateTtsText() != null && bc.getAlternateTtsText().length() > 0 && doTTS
                && tts != null) {
            UtteranceProgressListener listener = new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {

                    playNextWord(callback);
                }

                @Override
                public void onError(String utteranceId) {

                    playNextWord(callback);
                }
            };
            if (tts != null) {
                tts.setOnUtteranceProgressListener(listener);
                tts.speak(bc.getAlternateTtsText(), 2, null, bc.getAlternateTtsText());
            }
        } else if (bc.getText() != null && bc.getText().length() > 0 && doTTS && tts != null) {
            UtteranceProgressListener listener = new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {

                    playNextWord(callback);
                }

                @Override
                public void onError(String utteranceId) {

                    playNextWord(callback);
                }
            };
            if (tts != null) {
                tts.setOnUtteranceProgressListener(listener);
                tts.speak(bc.getText(), 2, null, bc.getText());
            }
        } else {
            playNextWord(callback);
        }
    }

    /**
     * Put draw phrase bar operation on to the UI thread queue.
     *
     * @param position The index of the cell to highlight, or RESET_TO_START to left align and not
     *                 highlight.
     */
    private void drawSentenceBarPost(final Integer position) {
        this.runOnUiThread(() -> drawSentenceBar(position));
    }

    /**
     * Draw phrase bar.
     *
     * @param position The index of the cell to highlight, or RESET_TO_START to left align and not
     *                 highlight.
     */
    private void drawSentenceBar(final Integer position) {
        /* fetch settings */
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean phraseImageCheck = settings.getBoolean(AppPreferences.PREF_KEY_PHRASE_IMAGE, false);
        Log.d("phraseImageCheck: ", String.valueOf(phraseImageCheck));
        /* width is for ImageView resizing */
        int width;
        /* height is for ImageView resizing */
        int height;
        String phraseImage;
        String s;
        Bitmap phraseBitmap;
        Bitmap scaledBitmap;
        Iterator<BoardContent> iterator = Board.sentenceBarPhrase.iterator();
        /* retrieve and remove views */
        while (sentenceBarLinearLayout.getChildCount() > 0) {
            View nextChild = sentenceBarLinearLayout.getChildAt(0);
            sentenceBarLinearLayout.removeView(nextChild);
        }
        while (iterator.hasNext()) {
            try {
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(PHRASE_BAR_CELL_WIDTH, PHRASE_BAR_CELL_HEIGHT);
                textView.setLayoutParams(layoutParams);
                BoardContent bc = iterator.next();
                phraseImage = bc.getUrl();
                if (!phraseImageCheck) {
                    s = bc.getText();
                    textView.setMaxLines(PHRASE_BAR_MAXIMUM_TEXT_LINES);
                    textView.setText(s);
                    textView.setGravity(Gravity.CENTER);
                    textView.setPadding(PHRASE_BAR_TEXTVIEW_PADDING, PHRASE_BAR_TEXTVIEW_PADDING,
                            PHRASE_BAR_TEXTVIEW_PADDING, PHRASE_BAR_TEXTVIEW_PADDING);
                    linearLayout.addView(textView);
                    sentenceBarLinearLayout.addView(linearLayout);
                } else {
                    try {
                        ImageView imageView = new ImageView(this);
                        phraseImage = phraseImage.replace("/", "-").replace(" ", "-");
                        phraseImage = Utility.getMyTalkFilesDir(board).getAbsolutePath() + "/" + phraseImage;
                        phraseBitmap = BitmapFactory.decodeFile(phraseImage);
                        scaledBitmap =
                                Bitmap.createScaledBitmap(phraseBitmap, PHRASE_BAR_CELL_WIDTH,
                                        PHRASE_BAR_CELL_HEIGHT, false);
                        width = scaledBitmap.getWidth();
                        height = scaledBitmap.getHeight();
                        //imageView = new ImageView(this);
                        imageView.setImageBitmap(phraseBitmap);
                        imageView.setPadding(PHRASE_BAR_IMAGE_PADDING, PHRASE_BAR_IMAGE_PADDING,
                                PHRASE_BAR_IMAGE_PADDING, PHRASE_BAR_IMAGE_PADDING);
                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(width, height);
                        imageView.setLayoutParams(layoutParams1);
                        imageView.setAdjustViewBounds(true);
                        linearLayout.addView(imageView);
                        s = bc.getText();
                        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                                LayoutParams.MATCH_PARENT));
                        textView.setGravity(Gravity.CENTER);
                        textView.setText(s);
                        textView.setEllipsize(TruncateAt.END);
                        textView.canScrollHorizontally(1);
                        textView.setSingleLine(false);
                        textView.setMaxLines(2);
                        linearLayout.addView(textView);
                        sentenceBarLinearLayout.addView(linearLayout);
                    } catch (Exception e) {
                        s = bc.getText();
                        textView.setMaxLines(PHRASE_BAR_MAXIMUM_TEXT_LINES);
                        textView.setText(s);
                        textView.setGravity(Gravity.CENTER);
                        textView.setPadding(PHRASE_BAR_IMAGE_PADDING, PHRASE_BAR_IMAGE_PADDING,
                                PHRASE_BAR_IMAGE_PADDING, PHRASE_BAR_IMAGE_PADDING);
                        linearLayout.addView(textView);
                        sentenceBarLinearLayout.addView(linearLayout);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (position) {
            case RESET_TO_START:
                /* move cell for visibility */
                mainView.post(() -> strip.smoothScrollTo(0, 0));
                break;
            case DO_NOT_HIGHLIGHT:
                mainView.post(() -> {
                    int x = linearStrip.getChildCount();
                    if (x > 0) {
                        View view = linearStrip.getChildAt(x - 1);
                        int scrollTo = view.getRight();
                        strip.smoothScrollTo(scrollTo, scrollTo);
                    }
                });
                break;
            default:
                mainView.post(() -> {
                    try {
                        Log.d("running.. ", String.valueOf(position));
                        View view = linearStrip.getChildAt(position);
                        Log.d("getChild: ", view.toString());
                        view.setBackgroundColor(COLOR_LIGHT_YELLOW);
                        int scrollTo = view.getLeft();
                        Log.i("scroll to value: ", String.valueOf(scrollTo));
                        strip.smoothScrollTo(scrollTo, scrollTo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
        }
    }

    public boolean canTTS() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean doTTS = settings.getBoolean(AppPreferences.PREF_KEY_TTS, true);
        return doTTS && tts != null;
    }

    /**
     * Release media player.
     */
    public final synchronized void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Gets the media player.
     *
     * @return the media player
     */
    public final synchronized MediaPlayer getMediaPlayer(final Runnable callback) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(arg0 -> {
                try {
                    Thread.sleep(PAUSE_BETWEEN_WORDS_TIME);
                    if (callback != null) playNextWord(callback);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            // do not
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((paramMediaPlayer, index1, index2) -> {
                if (index1 == MediaPlayer.MEDIA_ERROR_SERVER_DIED || index1 == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    return true;
                }
                try {
                    String str = getResources().getString(R.string.error_code) + index1;
                    new AlertDialog.Builder(board).setCancelable(false).setPositiveButton(R.string.ok, (paramDialogInterface, index) -> paramDialogInterface.cancel()).setMessage(str).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.media_error).show();
                } catch (Exception ex) {
                    // hmmm...
                }
                return false;
            });
        }

        return mediaPlayer;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public final synchronized void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Database.close();
        stopTimer();
        super.onDestroy();
    }

    @Override
    public final boolean onKeyUp(final int keyCode, final KeyEvent event) {
        char pressedKey = (char) event.getUnicodeChar();
        if (switchCodes != null && pressedKey != 0) {
            characters.push(pressedKey);
            StringBuilder test1 = new StringBuilder();
            StringBuilder test2 = new StringBuilder();
            for (int x = 0; x < switchCodes[0].length() && x < characters.size(); x++) {
                int p = characters.size() - x - 1;
                if (p >= 0 && p < characters.size())
                    test1.insert(0, characters.get(characters.size() - x - 1).toString());
            }
            for (int x = 0; x < switchCodes[1].length() && x < characters.size(); x++) {
                int p = characters.size() - x - 1;
                if (p >= 0 && p < characters.size())
                    test2.insert(0, characters.get(characters.size() - x - 1).toString());
            }
            if (switchCodes[0].equals(test1.toString())) {
                characters.clear();
                switch1();
            }
            if (switchCodes[1].equals(test2.toString())) {
                characters.clear();
                switch2();
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public final boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && boardName.contentEquals(getResources().getString(
                R.string.home)))
                || keyCode == KeyEvent.KEYCODE_HOME) {
            new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.alert)
                    .setMessage(R.string.want_to_exit)
                    .setPositiveButton(R.string.yes, (dialog, which) -> board.finish()).setNegativeButton(R.string.no, null).show();
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                Intent intent = getIntent();
                setResult(1, intent);
                super.onBackPressed();
                //finish();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
    }

    /**
     * Updates the main activity menu based on current state, i.e. if we are logged in and what
     * license mode we are running.
     *
     * @param menu the main menu
     */
    private void populateMenu(final Menu menu) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (mainMenu == null) {
            getMenuInflater().inflate(R.menu.mainmenu, menu);
            mainMenu = menu;
        }

        boolean test = !username.contentEquals("") && !password.contentEquals("") && loggedIn;

        mainMenu.findItem(R.id.SignIn).setVisible(!test);
        mainMenu.findItem(R.id.authorDone).setVisible(test);
        mainMenu.findItem(R.id.Sync).setVisible(test);
        mainMenu.findItem(R.id.shareBoard).setVisible(test);
        mainMenu.findItem(R.id.Preferences).setVisible(true);
        mainMenu.findItem(R.id.speakActionButton).setVisible(true);
        mainMenu.findItem(R.id.HelpMenu).setVisible(true);
        mainMenu.findItem(R.id.editMode).setVisible(test).setChecked(isEditable);
        if (!test) {
            mainMenu.findItem(R.id.redo).setVisible(false);
            mainMenu.findItem(R.id.undo).setVisible(false);
        } else {
            undoRedo.setRedoMenuItem(mainMenu.findItem(R.id.redo));
            undoRedo.setUndoMenuItem(mainMenu.findItem(R.id.undo));
        }
        if (test && (settings.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false))) {
            mainMenu.findItem(R.id.editMode).setVisible(false);
        }
        if ((loggedIn) && (settings.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)
                || (settings.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true)))) {
            mainMenu.findItem(R.id.buyLicense).setVisible(true);
        }
        if (!loggedIn) {
            mainMenu.findItem(R.id.buyLicense).setVisible(false);
        }
        if ((loggedIn) && (settings.getBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true))) {
            mainMenu.findItem(R.id.buyLicense).setVisible(false);
        }
    }

    /**
     * Updates the main activity menu based on current state, i.e. if we are logged in and what
     * license mode we are running.
     */
    public final void updateMenu() {
        populateMenu(mainMenu);
    }

    /**
     * Save image from camera to a file.
     *
     * @param bitmap   the bitmap returned by the camera
     * @param filename the filename to save to but without an extension
     * @return the filename with the extension
     */
    private String saveImageFromCamera(final Bitmap bitmap, final String filename) {
        File aFile =
                new File(Utility.getMyTalkFilesDir(this).getAbsolutePath() + "/" + filename + DOT_JPG);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Utility.rotateBitmap(bitmap, 0).compress(CompressFormat.JPEG, JPEG_COMPRESSION_RATIO,
                bytes);
        FileOutputStream fo;
        try {
            if (aFile.createNewFile()) {
                fo = new FileOutputStream(aFile);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename + DOT_JPG;
    }

    /**
     * Change the image of the selected cell to the image represented by the bitmap. Requests user to
     * select a name for the image first.
     *
     * @param bitmap the bitmap image to be saved
     */
    private void saveCellImage(final Bitmap bitmap, final BoardContent content) {
        if (bitmap == null || bitmap.getHeight() < 0 || bitmap.getWidth() < 0) {
            Utility.alert(getString(R.string.invalid_image), board);
            return;
        }
        PromptDialog dlg =
                new PromptDialog(this, getString(R.string.enter_name),
                        getString(R.string.no_punctuation_or_spaces_), Utility.strip(selectedItem.getText())) {

                    @Override
                    public boolean onOkClicked(final String input) {
                        if (content != null) {
                            undoRedo.saveState();
                            content.setType(1);
                            content.setUrl(saveImageFromCamera(bitmap, "-" + input).replace("-", "/"));
                            content.persist(getApplicationContext());
                            resetGrid(false);
                            sentenceBarLayout.invalidate();
                        }
                        return false;
                    }
                };
        dlg.show();

    }

    private boolean requestAppPermission(String[] request, int returnCode) {
        ArrayList<String> askFor = new ArrayList<>();
        for (String r : request) {
            if (ActivityCompat.checkSelfPermission(board, r) != PackageManager.PERMISSION_GRANTED)
                askFor.add(r);
        }
        if (askFor.size() > 0) {
            String[] toAskFor = new String[askFor.size()];
            askFor.toArray(toAskFor);
            ActivityCompat.requestPermissions(board, toAskFor, returnCode);
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected final void onActivityResult(final int requestCode, final int resultCode,
                                          final Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                imageEditedResult(resultCode, data, selectedItem);
                return;
            }
        }

        if (requestCode >= 10000 && requestCode <= 10010) {
            try {
                if (data != null) {
                    final Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        Log.d("TAG", containerVEArray.get(requestCode - 10000).getLabel() + " - Bundle Data");
                        final Set<String> keys = bundle.keySet();
                        for (String key : keys) {
                            Log.d("TAG", "Key: " + key + " = " + bundle.get(key));
                        }
                    }
                    if (data.hasExtra("availableVoices")) {
                        containerVEArray.get(requestCode - 10000).setLanguages(data.getStringArrayListExtra("availableVoices"));
                    } else {
                        containerVEArray.get(requestCode - 10000).setLanguages(new ArrayList<>());
                    }
                }
                if (requestCode - 10000 == containerVEArray.size() - 1) {
                    Editor localEditor1 =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    Set<String> engines = new TreeSet<>();
                    Set<String> packages = new TreeSet<>();
                    for (int i = 0; i < containerVEArray.size(); i++) {
                        engines.add(containerVEArray.get(i).getLabel());
                        packages.add(containerVEArray.get(i).getPackageName());
                    }

                    localEditor1.putStringSet("Available-TTS-Engines-Labels", engines);
                    localEditor1.putStringSet("Available-TTS-Engines-Packages", packages);
                    localEditor1.apply();

                    for (int i = 0; i < containerVEArray.size(); i++) {
                        Set<String> voices = new TreeSet<>(containerVEArray.get(i).getLanguages());
                        localEditor1.putStringSet(containerVEArray.get(i).getPackageName(), voices);
                        localEditor1.apply();
                        Log.v("TAG", "cve: " + containerVEArray.get(i).getPackageName() + " - "
                                + containerVEArray.get(i).getLanguages().size() + " - " + containerVEArray.get(i).getLanguages().toString());
                    }
                }

            } catch (final IndexOutOfBoundsException e) {
                Log.e("TAG", "IndexOutOfBoundsException");
                e.printStackTrace();
            } catch (final NullPointerException e) {
                Log.e("TAG", "NullPointerException");
                e.printStackTrace();
            } catch (final Exception e) {
                Log.e("TAG", "Exception");
                e.printStackTrace();
            }
        }

        if (RequestCode.values().length <= requestCode || requestCode < 0) return;
        RequestCode request = RequestCode.values()[requestCode];
        switch (request) {
            case RESULT_PICK_CONTACT:
                if (data != null) contactPicked(data);
                break;
            case CHILD_BOARD_NAVIGATE:
                updateMenu();
                strip = findViewById(R.id.sentenceBarHorizontal);
                linearStrip = findViewById(R.id.sentenceBarLinearLayout);
                break;
            case VIDEO_RECORDER:
                videoRecorderResult(resultCode, selectedItem);
                break;
            case VIDEO_RECORDER_HOTSPOT:
                videoRecorderResult(resultCode, selectedHotspotItem);
                break;
            case PHOTO_EDITED_HOTSPOT:
                if (data != null) imageEditedResult(resultCode, data, selectedHotspotItem);
                break;
            case WEB_PAGE_CAPTURE:
                undoRedo.saveState();
                saveCellImage(CaptureWebPage.getBitmap(), selectedItem);
                break;
            case WEB_PAGE_CAPTURE_HOTSPOT:
                undoRedo.saveState();
                saveCellImage(CaptureWebPage.getBitmap(), selectedHotspotItem);
                break;
            case CAMERA_PIC:
                saveCameraPicResult(data, selectedItem);
                break;
            case CAMERA_PIC_HOTSPOT:
                if (data != null) saveCameraPicResult(data, selectedHotspotItem);
                break;
            case SELECT_PHOTO:
                selectedSavedImageResult(resultCode, data, selectedItem);
                break;
            case SELECT_PHOTO_HOTSPOT:
                if (data != null) selectedSavedImageResult(resultCode, data, selectedHotspotItem);
                break;
            case MY_DATA_CHECK_CODE:
                ttsCheckResult(resultCode);
                getEngines();
                break;
            case SELECTED_LIBRARY_SOUND_HOTSPOT:
            case SELECTED_LIBRARY_VIDEO_HOTSPOT:
                if (resultCode == 1) {
                    mainView.postDelayed(() -> {
                        if (data == null) return;
                        final String out = data.getStringExtra("out");
                        final String in = data.getStringExtra("in");
                        final String url = data.getStringExtra("url");
                        if (out == null || in == null || url == null) return;
                        copyFileFromInternet(in, out, () -> {
                            Board.getUndoRedo().saveState();
                            selectedHotspotItem.setType(1);
                            selectedHotspotItem.setUrl2(url);
                            selectedHotspotItem.persist(board);
                            mainGrid.invalidateViews();
                            sentenceBarLayout.invalidate();
                        });
                    }, 1);
                }
            case SELECTED_LIBRARY_SOUND:
            case SELECTED_LIBRARY_VIDEO:
                if (resultCode == 1) {
                    mainView.postDelayed(() -> {
                        if (data == null) return;
                        final String out = data.getStringExtra("out");
                        if (out == null) return;
                        final String in = data.getStringExtra("in");
                        if (in == null) return;
                        final String url = data.getStringExtra("url");
                        if (url == null) return;
                        copyFileFromInternet(in, out, () -> {
                            Board.getUndoRedo().saveState();
                            selectedItem.setType(1);
                            selectedItem.setUrl2(url);
                            selectedItem.persist(board);
                            mainGrid.invalidateViews();
                            sentenceBarLayout.invalidate();
                        });
                    }, 1);
                }
                break;
            case SELECTED_CELL:
            case SELECTED_LIBRARY_BOARD:
            case SELECTED_LIBRARY_BOARD_HOTSPOT:
            case SELECTED_WEB_IMAGE:
            case SELECTED_WEB_IMAGE_HOTSPOT:
            case SELECTED_GOOGLE_DRIVE:
            case SELECTED_GOOGLE_DRIVE_HOTSPOT:
            case SELECTED_GOOGLE_DRIVE_SOUND:
            case SELECTED_GOOGLE_DRIVE_SOUND_HOTSPOT:
                // done in search
                break;
            case SELECTED_LIBRARY_IMAGE:
                if (resultCode == 1) {
                    mainView.postDelayed(() -> {
                        if (data == null) return;
                        final String out = data.getStringExtra("out");
                        final String in = data.getStringExtra("in");
                        final String url = data.getStringExtra("url");
                        copyFileFromInternet(in, out, () -> {
                            Board.getUndoRedo().saveState();
                            selectedItem.setType(1);
                            selectedItem.setUrl(url);
                            selectedItem.persist(board);
                            mainGrid.invalidateViews();
                            sentenceBarLayout.invalidate();
                        });
                    }, 1);
                }
                break;
            case SELECTED_LIBRARY_IMAGE_HOTSPOT:
                if (resultCode == 1) {
                    mainView.postDelayed(() -> {
                        if (data == null) return;
                        final String out = data.getStringExtra("out");
                        final String in = data.getStringExtra("in");
                        final String url = data.getStringExtra("url");
                        copyFileFromInternet(in, out, () -> {
                            Board.getUndoRedo().saveState();
                            selectedHotspotItem.setType(1);
                            selectedHotspotItem.setUrl(url);
                            selectedHotspotItem.persist(board);
                            mainGrid.invalidateViews();
                            sentenceBarLayout.invalidate();
                        });
                    }, 1);
                }
                break;
            case ADD_LOCATION_RESULT:
                if (resultCode == 1) {
                    try {
                        if (data == null) return;
                        Bundle latLng = data.getExtras();
                        if (latLng == null) return;
                        double latitude = latLng.getDouble("Latitude");
                        double longitude = latLng.getDouble("Longitude");
                        String url = String.format(Locale.getDefault(), "%s/%f,%f", "mtgeo:", latitude, longitude);
                        undoRedo.saveState();
                        selectedItem.setExternalUrl(url);
                        selectedItem.persist(this);
                        updateLocationMonitoring(BoardContent.getLocationContent(board));
                    } catch (Exception ex) {
                        // hmmmm...
                    }
                }
                break;
            default:
                break;
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent == null) {
            Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
            // calling addGeofences() and removeGeofences().
            mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
        }
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void updateLocationMonitoring(ArrayList<BoardContent> contents) {

        try {
            ArrayList<String> geoCodes = new ArrayList<>();
            for (BoardContent content : contents) {
                geoCodes.add(String.format(Locale.getDefault(), "mt-%d", content.getChildBoardId()));
            }
            if (geoCodes.size() > 0)
                LocationServices.GeofencingApi.removeGeofences(getGoogleApiClient(), geoCodes);

            for (BoardContent content : contents) {
                String externalUrl = content.getExternalUrl();
                if (externalUrl.startsWith("mtgeo:/")) {
                    externalUrl = externalUrl.replace("mtgeo:/", "");
                    String[] parts = externalUrl.split(",");
                    double latitude = Float.parseFloat(parts[0]);
                    double longitude = Float.parseFloat(parts[1]);
                    mGeofenceList.add(new Geofence.Builder()
                            // Set the request ID of the geofence. This is a string to identify this
                            // geofence.
                            .setRequestId(String.format(Locale.getDefault(), "mt-%d", content.getChildBoardId()))
                            .setCircularRegion(
                                    latitude,
                                    longitude,
                                    50
                            )
                            .setExpirationDuration(-1)
                            .setLoiteringDelay(1000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER)
                            .build());
                }
            }

            if (mGeofenceList.size() > 0)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.GeofencingApi.addGeofences(
                            getGoogleApiClient(),
                            getGeofencingRequest(),
                            getGeofencePendingIntent()
                    ).setResultCallback(this);
                }
        } catch (Exception ex) {
            //hmmm...
        }
    }

    /**
     * Saves the image returned from a camera as the image of the selected cell.
     *
     * @param data the camera activity returns its data through its Intent.
     */
    private void saveCameraPicResult(final Intent data, BoardContent content) {
        if (data == null) return;
        if (data.getExtras() == null) return;
        try {
            undoRedo.saveState();
            saveCellImage((Bitmap) data.getExtras().get("data"), content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Depending on the availability of TTS capability this will setup the program to use or not use
     * TTS.
     *
     * @param resultCode the result code returned from TTS Check activity
     */
    private synchronized void ttsCheckResult(final int resultCode) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

            if (tts != null && ttsSecondary != null) {
                return;
            }

            SharedPreferences settings1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            // success, create the TTS instance
            tts = new TextToSpeech(getApplicationContext(), status -> {
                if (status != TextToSpeech.SUCCESS) {
                    String message =
                            "Problem initializing text-to-speech engine.";
                    Editor localEditor1 =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    localEditor1.putBoolean(AppPreferences.PREF_KEY_TTS, false);
                    localEditor1.apply();
                    new AlertDialog.Builder(board).setTitle(R.string.alert)
                            .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                }
                Locale locale = Locale.getDefault();
                {
                    String[] codes = primaryVoice.split("-");
                    if (codes.length == 2) {
                        locale = new Locale(codes[0], codes[1]);
                    }
                }
                if (tts == null || locale == null) {
                    return;
                }
                switch (tts.isLanguageAvailable(locale)) {
                    case TextToSpeech.LANG_NOT_SUPPORTED: {
                        String message =
                                getString(R.string.tts_not_available) + locale.getDisplayLanguage();
                        tts.stop();
                        tts.shutdown();
                        tts = null;
                        Editor localEditor1 =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        localEditor1.putBoolean(AppPreferences.PREF_KEY_TTS, false);
                        localEditor1.apply();
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                        break;
                    }
                    case TextToSpeech.LANG_MISSING_DATA: {
                        String message =
                                "Language missing data: " + Locale.getDefault().getDisplayLanguage();
                        tts.stop();
                        tts.shutdown();
                        tts = null;
                        Editor localEditor1 =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        localEditor1.putBoolean(AppPreferences.PREF_KEY_TTS, false);
                        localEditor1.apply();
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                        break;
                    }
                    case TextToSpeech.LANG_AVAILABLE: {
                        locale = new Locale(locale.getLanguage());
                        tts.setLanguage(locale);
                        break;
                    }
                    default:
                        tts.setLanguage(locale);
                        break;
                }
                try {
                    if (tts != null) {
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(final String arg0) {

                                playNextWord(null);
                            }

                            @Override
                            public void onError(final String arg0) {

                                playNextWord(null);
                            }

                            @Override
                            public void onStart(final String utteranceId) {
                                // do nothing
                            }
                        });
                    }
                } catch (Exception error) {
                    try {
                        String message =
                                "Problem with TTS engine: " + error.getLocalizedMessage();
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                    } catch (Exception innerError) {
                        // ignore this error
                    }
                }
            }, settings1.getString("ttsEngine", "com.google.android.tts"));
            ttsSecondary = new TextToSpeech(getApplicationContext(), status -> {
                if (status != TextToSpeech.SUCCESS) {
                    String message =
                            "Problem initializing text-to-speech engine.";
                    Editor localEditor1 =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    localEditor1.putBoolean(AppPreferences.PREF_KEY_TTS, false);
                    localEditor1.apply();
                    try {
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                    } catch (Exception error) {
                        // ignore this error
                    }
                }
                Locale locale = Locale.getDefault();
                {
                    String[] codes = secondaryVoice.split("-");
                    if (codes.length == 2) {
                        locale = new Locale(codes[0], codes[1]);
                    }
                }
                if (ttsSecondary == null || locale == null) {
                    return;
                }
                switch (ttsSecondary.isLanguageAvailable(locale)) {
                    case TextToSpeech.LANG_NOT_SUPPORTED: {
                        String message =
                                getString(R.string.tts_not_available) + locale.getDisplayLanguage();
                        ttsSecondary.stop();
                        ttsSecondary.shutdown();
                        ttsSecondary = null;
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                        break;
                    }
                    case TextToSpeech.LANG_MISSING_DATA: {
                        String message =
                                "Language missing data: " + locale.getDisplayLanguage();
                        ttsSecondary.stop();
                        ttsSecondary.shutdown();
                        ttsSecondary = null;
                        new AlertDialog.Builder(board).setTitle(R.string.alert)
                                .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                        break;
                    }
                    case TextToSpeech.LANG_AVAILABLE: {
                        locale = new Locale(locale.getLanguage());
                        ttsSecondary.setLanguage(locale);
                        break;
                    }
                    default:
                        ttsSecondary.setLanguage(locale);
                        break;
                }
                try {
                    ttsSecondary.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(final String arg0) {

                            playNextWord(null);
                        }

                        @Override
                        public void onError(final String arg0) {

                            playNextWord(null);
                        }

                        @Override
                        public void onStart(final String utteranceId) {
                            // do nothing
                        }
                    });
                } catch (Exception error) {
                    String message =
                            "Problem with TTS engine: " + error.getLocalizedMessage();
                    new AlertDialog.Builder(board).setTitle(R.string.alert)
                            .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                }
            }, settings1.getString("ttsEngine", "com.google.android.tts"));
        } else {
            Intent installTTS = new Intent();
            installTTS.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installTTS);
        }
    }

    /*
      Updates the selected cell based on a photo picked from the photo picker activity.

      @param resultCode the Intent result
     * @param data       the Intent data
     */
    private void selectedSavedImageResult(final int resultCode, final Intent data, BoardContent content) {
        if (resultCode == RESULT_OK) {
            Uri localImage = data.getData();
            if (localImage == null) return;
            try {
                Bitmap bitmap = Utility.decodeUri(localImage, board);
                String filename = Utility.getFilename(new File(localImage.toString()));
                File output =
                        new File(Utility.getMyTalkFilesDir(this).getAbsoluteFile(), "-" + filename + DOT_JPG);
                if (!output.createNewFile()) return;
                OutputStream stream = Files.newOutputStream(output.toPath());
                bitmap.compress(CompressFormat.JPEG, JPEG_COMPRESSION_RATIO, stream);
                stream.flush();
                stream.close();
                undoRedo.saveState();
                content.setType(1);
                content.setUrl("/" + filename + DOT_JPG);
                content.persist(board);
                GridView gridView = board.findViewById(R.id.mainGrid);
                RelativeLayout relativeLayout = board.findViewById(R.id.sentenceBarLayout);
                gridView.invalidateViews();
                relativeLayout.invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
      Updates the selected cell based on an edited imaged returned from the Aviary photo editor
      activity.

      @param resultCode the result code return from the photo edit activity
     * @param data       the Intent data returned from the photo edit activity
     */
    private void imageEditedResult(final int resultCode, final Intent data, BoardContent content) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            Uri mImageUri = result.getUri();
            try {
                File temp = new File(content.getUrl());
                File fileDir = Utility.getMyTalkFilesDir(board);
                boolean isPNG = Utility.getExtension(temp).equalsIgnoreCase(PNG);
                String extension = getImageExtension(isPNG);
                String mNewUrl = Utility.changeExtension(new File(temp.getName()), extension, EDITED);
                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);

                // use the '-' convention to create the physical
                // name
                String newUrl = mNewUrl.replace(" ", "-").replace("/", "-");

                // get new absolute, physical path to store updated
                // file
                File outputFile = new File(fileDir.getAbsolutePath() + "/" + newUrl);
                //File inputFile = new File(mImageUri.toString());
                if (isPNG) {
                    bm.compress(CompressFormat.PNG, 100, Files.newOutputStream(outputFile.toPath()));
                } else {
                    bm.compress(CompressFormat.JPEG, 100, Files.newOutputStream(outputFile.toPath()));
                }
                //FileUtils.copyFile(inputFile, outputFile);

                undoRedo.saveState();
                content.setUrl(mNewUrl);
                content.persist(getApplicationContext());
                resetGrid(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
      Gets correct compression format for writing a bitmap.

      @param isPNG TRUE if looking for PNG compress format
     * @return The correct image compress format
     */
    private CompressFormat getCompressFormat(final boolean isPNG) {
        CompressFormat cf;
        if (isPNG) {
            cf = CompressFormat.PNG;
        } else {
            cf = CompressFormat.JPEG;
        }
        return cf;
    }

    /*
      Gets corrects filename extension for writing an image.

      @param isPNG true if you have a PNG format image
     * @return an appropriate filename extension for an image
     */
    private String getImageExtension(final boolean isPNG) {
        String extension;
        if (isPNG) {
            extension = PNG;
        } else {
            extension = JPG;
        }
        return extension;
    }

    /*
      Updates the selected cell with a video based on the video returned from the video recording
      activity.

      @param resultCode return from video recording
     */

    private void videoRecorderResult(final int resultCode, BoardContent content) {
        switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(this, R.string.video_has_been_saved_, Toast.LENGTH_LONG).show();
                saveVideoRecording(content);
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, R.string.video_recording_cancelled_, Toast.LENGTH_LONG).show();
                break;
            default:
                Toast.makeText(this, R.string.failed_to_record_video_, Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*
      Saves a video recording returned by the video recording activity.
     */
    private void saveVideoRecording(final BoardContent content) {
        PromptDialog dlg =
                new PromptDialog(this, getString(R.string.enter_name),
                        getString(R.string.no_punctuation_or_spaces_), Utility.strip(content.getText())) {

                    @Override
                    public boolean onOkClicked(final String paramInput) {
                        String input = Utility.strip(paramInput);
                        String unique =
                                Utility.makeFilenameUnique(Utility.getMyTalkFilesDir(board).getAbsolutePath()
                                        + "/-" + input + getString(R.string.mpeg_movie_extension));
                        String finalName = new File(unique).getName().replace("-", "/");
                        Utility.copyFile(videoOutputUri.getPath(), unique);
                        undoRedo.saveState();
                        content.setType(1);
                        content.setUrl2(finalName);
                        Uri galleryVideo = addVideoToGallery(videoOutputUri.getPath(), board);
                        Bitmap thumb = createVideoThumbnail(board, galleryVideo);
                        if (thumb != null) {
                            content.setUrl(saveImageFromCamera(thumb, "-" + input).replace("-", "/"));
                        }
                        content.persist(getApplicationContext());
                        resetGrid(false);
                        return false;
                    }
                };
        dlg.show();
    }

    /*
      Creates the TTS sound.
     */
    private void createTextToSpeechSound() {
        PromptDialog dlg = new PromptDialog(this, getString(R.string.enter_phrase), "", selectedItem.getText()) {

            @Override
            public boolean onOkClicked(final String input) {
                //HashMap<String, String> myHashRender = new HashMap<>();
                try {
                    if (input == null) return false;
                    String start = Utility.strip(input);
                    if (start.length() > MAX_WORD_LENGTH) {
                        start = start.substring(0, MAX_WORD_LENGTH);
                    }
                    String unique =
                            Utility.makeFilenameUnique(Utility.getMyTalkFilesDir(board).getAbsolutePath() + "/-"
                                    + start + DOT_WAV);
                    File uFile = new File(unique);
                    tts.synthesizeToFile(input, null, uFile, start);
                    String finalName = new File(unique).getName().replace("-", "/");
                    undoRedo.saveState();
                    selectedItem.setType(1);
                    selectedItem.setUrl2(finalName);
                    selectedItem.persist(getApplicationContext());
                } catch (Exception error) {
                    String message =
                            "Problem with TTS engine: " + error.getLocalizedMessage();
                    new AlertDialog.Builder(board).setTitle(R.string.alert)
                            .setPositiveButton(R.string.ok, null).setMessage(message).create().show();
                }
                return false;
            }
        };
        dlg.show();

    }

    public Integer getSelectedScanPosition() {
        //int total = boardRow.getColumns() * boardRow.getRows();

        return selectedScanPosition;
    }

    public Integer getSelectedHotspotScanPosition() {
        return selectedHotspotScanPosition;
    }

    private SwitchScenario switchScenario() {
        switch (scanSwitch) {
            case "2-buttons":
            case "ablenet":
            case "rjcooper":
                if (autoScanInterval == 0) return SwitchScenario._2_BUTTONS_WITHOUT_AUTO_SCANNING;
                else if (autoStartAutoScan)
                    return SwitchScenario._2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START;
                else return SwitchScenario._2_BUTTONS_WITH_AUTO_SCANNING;
            case "1-button":
                if (autoScanInterval == 0) return SwitchScenario._1_BUTTON_WITHOUT_AUTO_SCANNING;
                else if (autoStartAutoScan)
                    return SwitchScenario._1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START;
                else return SwitchScenario._1_BUTTON_WITH_AUTO_SCANNING;
            default:
                return SwitchScenario._NO_SWITCH;
        }
    }

    private void advanceSelection() {

        if (inAdvanceSelection) return;
        inAdvanceSelection = true;
        if (tts != null && tts.isSpeaking()) {
            inAdvanceSelection = false;
            return;
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            inAdvanceSelection = false;
            return;
        }

        BoardContent currentItem = null;
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean auditoryScanning = settings.getBoolean(AppPreferences.PREF_KEY_AUDITORY_SCANNING, false);
        boolean phraseMode = sentenceBarLayout.getVisibility() == TextView.VISIBLE;

        if (scanByRow && boardRow.getColumns() > 1 && boardRow.getRows() > 1) {

            if (columnScanMode) {

                // if we are in a hotspot - advance or exit
                if (selectedScanPosition != null && boardId != WORD_VARIANT_BOARD) {
                    currentItem = (BoardContent) mainGrid.getAdapter().getItem(selectedScanPosition);
                    if (selectedHotspotScanPosition != null) {
                        if (currentItem == null || currentItem.getHotspotStyle() == 0) {
                            selectedHotspotScanPosition = null;
                        } else {
                            BoardRow boardRow = currentItem.getChildBoard(board);
                            if (boardRow != null) {
                                if (selectedHotspotScanPosition == (boardRow.getColumns() * boardRow.getRows()) - 1) {
                                    selectedHotspotScanPosition = null;
                                } else {
                                    selectedHotspotScanPosition++;
                                    mainGrid.invalidateViews();
                                    inAdvanceSelection = false;
                                    return;
                                }
                            }
                        }
                    }
                }

                // at pre-board - go to beginning
                if (selectedScanPosition == null) {
                    selectedScanPosition = 0;
                    autoScanLoopCounter = 0;
                }
                // at end of column...
                else if (selectedScanPosition % boardRow.getColumns() >= boardRow.getColumns() - 1) {
                    selectedScanPosition = selectedScanPosition - boardRow.getColumns() + 1;
                    autoScanLoopCounter++;
                    if (autoScanLoopCounter == Math.max(autoScanLoops, 1)) {
                        autoScanLoopCounter = 0;
                        mainView.post(() -> columnScanMode = false);
                    }
                }

                // otherwise advance to next cell
                else {
                    selectedScanPosition++;
                }

            } else {

                // at pre-board - go to beginning
                if (selectedScanPosition == null) {
                    selectedScanPosition = 0;
                    autoScanLoopCounter = 0;
                }
                // at play button - go to delete button
                else if (selectedScanPosition == -2) {
                    selectedScanPosition = -1;
                    Button sentenceBarPlay = findViewById(R.id.sentenceBarPlay);
                    sentenceBarPlay.setBackground(ContextCompat.getDrawable(board, R.drawable.blue_play));
                    Button sentenceBarDelete = findViewById(R.id.sentenceBarDelete);
                    sentenceBarDelete.setBackground(ContextCompat.getDrawable(board, R.drawable.red_delete_down));
                }
                // at delete button - go to beginning
                else if (selectedScanPosition == -1) {
                    selectedScanPosition = 0;
                    Button sentenceBarDelete = findViewById(R.id.sentenceBarDelete);
                    sentenceBarDelete.setBackground(ContextCompat.getDrawable(board, R.drawable.red_delete));
                }
                // at end of board...
                else if (selectedScanPosition >= boardRow.getColumns() * (boardRow.getRows() - 1)) {
                    // in phrase mode go to play button
                    if (phraseMode) {
                        selectedScanPosition = -2;
                        Button sentenceBarPlay = findViewById(R.id.sentenceBarPlay);
                        sentenceBarPlay.setBackground(ContextCompat.getDrawable(board, R.drawable.blue_play_down));
                    }
                    // otherwise go to beginning
                    else {
                        selectedScanPosition = 0;
                        autoScanLoopCounter++;
                        if (autoScanLoopCounter == autoScanLoops) {
                            autoScanLoopCounter = 0;
                            mainView.post(this::finish);
                        }
                    }
                }

                // otherwise advance to next cell
                else {
                    selectedScanPosition += boardRow.getColumns();
                }
            }
        } else {

            // if we are in a hotspot - advance or exit
            if (selectedScanPosition != null && boardId != WORD_VARIANT_BOARD) {
                currentItem = (BoardContent) mainGrid.getAdapter().getItem(selectedScanPosition);
                if (selectedHotspotScanPosition != null) {
                    if (currentItem == null || currentItem.getHotspotStyle() == 0) {
                        selectedHotspotScanPosition = null;
                    } else {
                        BoardRow boardRow = currentItem.getChildBoard(board);
                        if (boardRow != null) {
                            if (selectedHotspotScanPosition == (boardRow.getColumns() * boardRow.getRows()) - 1) {
                                selectedHotspotScanPosition = null;
                            } else {
                                selectedHotspotScanPosition++;
                                mainGrid.invalidateViews();
                                inAdvanceSelection = false;
                                return;
                            }
                        }
                    }
                }
            }

            // at pre-board - go to beginning
            if (selectedScanPosition == null) {
                selectedScanPosition = 0;
                autoScanLoopCounter = 0;
            }
            // at play button - go to delete button
            else if (selectedScanPosition == -2) {
                selectedScanPosition = -1;
                Button sentenceBarPlay = findViewById(R.id.sentenceBarPlay);
                sentenceBarPlay.setBackground(ContextCompat.getDrawable(board, R.drawable.blue_play));
                Button sentenceBarDelete = findViewById(R.id.sentenceBarDelete);
                sentenceBarDelete.setBackground(ContextCompat.getDrawable(board, R.drawable.red_delete_down));
            }
            // at delete button - go to beginning
            else if (selectedScanPosition == -1) {
                selectedScanPosition = 0;
                Button sentenceBarDelete = findViewById(R.id.sentenceBarDelete);
                sentenceBarDelete.setBackground(ContextCompat.getDrawable(board, R.drawable.red_delete));
            }
            // at end of board...
            else if (selectedScanPosition == mainGrid.getCount() - 1) {
                // in phrase mode go to play button
                if (phraseMode) {
                    selectedScanPosition = -2;
                    Button sentenceBarPlay = findViewById(R.id.sentenceBarPlay);
                    sentenceBarPlay.setBackground(ContextCompat.getDrawable(board, R.drawable.blue_play_down));
                }
                // otherwise go to beginning
                else {
                    selectedScanPosition = 0;
                    autoScanLoopCounter++;
                    if (autoScanLoopCounter == autoScanLoops) {
                        autoScanLoopCounter = 0;
                        mainView.post(this::finish);
                        inAdvanceSelection = false;
                        return;
                    }
                }
            }
            // can't dereference the cell - just go to beginning
            else if (currentItem == null && boardId != WORD_VARIANT_BOARD) {
                selectedScanPosition = 0;
                autoScanLoopCounter = 0;
            }
            // otherwise advance to next cell
            else {
                selectedScanPosition++;
            }

            // if the cell we are advance to - is a hotspot, highlight first hotspot
            if (board.boardId != WORD_VARIANT_BOARD) {
                currentItem = (BoardContent) mainGrid.getAdapter().getItem(selectedScanPosition);
                if (currentItem != null && currentItem.getHotspotStyle() == 1) {
                    selectedHotspotScanPosition = 0;
                } else {
                    selectedHotspotScanPosition = null;
                }
            }
        }
        if (auditoryScanning && selectedScanPosition != null) {
            switch (selectedScanPosition) {
                case -1: {
                    tts.speak("delete", 2, null, "delete");
                    break;
                }
                case -2: {
                    tts.speak("play", 2, null, "play");
                    break;
                }
                default:
                    currentItem = (BoardContent) mainGrid.getAdapter().getItem(selectedScanPosition);
                    if (currentItem != null) {
                        currentItem.voiceQueue(this, getMediaPlayer(null));
                    }
                    break;
            }
        }
        mainGrid.invalidateViews();
        mainGrid.post(() -> {
            if (selectedScanPosition > 0)
                mainGrid.smoothScrollToPosition(selectedScanPosition);
        });
        inAdvanceSelection = false;
    }

    private void clickItem() {
        if (selectedHotspotScanPosition != null) {
            itemClick(mainGrid.getAdapter(), null, mainGrid, selectedScanPosition, 1);
        } else if (selectedScanPosition != null) {
            itemClick(mainGrid.getAdapter(), null, mainGrid, selectedScanPosition, 0);
        }
    }

    private void startTimer() {

        if (autoScanInterval > 0) {
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(() -> {
                        if (!inAdvanceSelection && isActive) {
                            advanceSelection();
                            sentenceBarLayout.invalidate();
                        }
                    });
                }
            };

            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            timer = new Timer();
            timer.scheduleAtFixedRate(tt, autoScanInterval * 1000L, autoScanInterval * 1000L);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = null;
    }

    private void switch1() {
        switch (switchScenario()) {
            case _2_BUTTONS_WITHOUT_AUTO_SCANNING:
                advanceSelection();
                break;
            case _2_BUTTONS_WITH_AUTO_SCANNING:
                if (selectedScanPosition != null && selectedScanPosition < 0) return;
                if (timer != null && scanByRow) {
                    columnScanMode = !columnScanMode;
                } else {
                    advanceSelection();
                }
                startTimer();
                break;
            case _2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START:
                if (selectedScanPosition != null && selectedScanPosition < 0) return;
                if (timer != null && scanByRow) {
                    columnScanMode = !columnScanMode;
                } else {
                    switch2();
                }
                break;
            case _1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START:
                if (timer != null && scanByRow && !columnScanMode) {
                    columnScanMode = true;
                }
                if (timer != null && scanByRow) {
                    switch2();
                    columnScanMode = false;
                } else {
                    switch2();
                }
                break;
        }
    }

    private void switch2() {
        switch (switchScenario()) {
            case _1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START:
            case _2_BUTTONS_WITHOUT_AUTO_SCANNING:
            case _2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START:
                clickItem();
                break;
            case _2_BUTTONS_WITH_AUTO_SCANNING:
                stopTimer();
                clickItem();
                break;
        }
    }

    boolean isPackageInstalled(String packageName) {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);

            return pi != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle bundle) {
        currentBoard = this;
        board = this;
        super.onCreate(bundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.board);
        mainView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        mainView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                onKeyUp(keyCode, event);
            }
            return false;
        });
        mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                onKeyUp(keyCode, event);
            }
            return false;
        });
        mainGrid.setFocusable(true);
        mainGrid.setFocusableInTouchMode(true);
        mainGrid.requestFocus();
        setUseExternalStorage(settings.getBoolean(AppPreferences.PREF_KEY_EXTERNAL_STORAGE, true));

        boolean doTTS = settings.getBoolean(AppPreferences.PREF_KEY_TTS, true);
        if (doTTS) {
            try {
                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                startActivityForResult(checkIntent, RequestCode.MY_DATA_CHECK_CODE.ordinal());
            } catch (Exception ex) {
                // does not support TTS
                Editor e = settings.edit();
                e.putBoolean(AppPreferences.PREF_KEY_TTS, false);
                e.apply();
            }
        }

        /* test if mainMenu is null, then update accordingly */
        if (mainMenu != null) {
            updateMenu();
        }

        database = new Database(this);
        setVolumeControlStream(VOLUME_CONTROL_STREAM);

        Intent localIntent = getIntent();
        boardId = localIntent.getIntExtra(Board.INTENT_EXTRA_BOARD_ID, 1);
        boardUri = localIntent.getStringExtra(Board.INTENT_EXTRA_URI);
        boardName = localIntent.getStringExtra(Board.INTENT_EXTRA_BOARD_NAME);
        strip = findViewById(R.id.sentenceBarHorizontal);
        linearStrip = findViewById(R.id.sentenceBarLinearLayout);
        if (this.boardName == null) {
            this.boardName = getString(R.string.home);
        }
        setTitle(boardName);

        username = settings.getString(Board.USERNAME, "");
        password = settings.getString(Board.PASSWORD, "");
        defaultBoard = settings.getString(Board.DEFAULT_BOARD, "");

        settings.registerOnSharedPreferenceChangeListener(this);

        if (localIntent.getBooleanExtra(Board.INTENT_EXTRA_SIGN_IN, false)) {
            new CreateNewAccount(board).show();
        }

        if (Board.undoRedo == null) {
            Board.undoRedo = new UndoRedo(this);
        }

        // check if application has been run before
        // when checking for preferences, if the setting isn't present, like
        // "FIRST_RUN" it will default
        // to the value specified, which is true.
        if (settings.getBoolean(AppPreferences.PREF_KEY_FIRST_RUN, true)) {
            settings.edit().putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true).apply();
            settings.edit().putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false).apply();
            settings.edit().putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, false).apply();
            settings.edit().putBoolean(AppPreferences.PREF_KEY_FIRST_RUN, false).apply();
        }

        // log first run value
        Log.d("first run: ",
                String.valueOf(settings.getBoolean(AppPreferences.PREF_KEY_FIRST_RUN, true)));
        // log value of license(s)
        Log.d("trialLicense: ",
                String.valueOf(settings.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true)));
        Log.d("limitedLicense: ",
                String.valueOf(settings.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)));

        if (billingDataSource == null) {
            billingDataSource = new BillingDataSource(getApplication(), knownInappSKUs, new String[0], new String[0]);
            billingDataSource.initialized.observeForever(inited -> {
                if (inited) {

                    Editor e = settings.edit();
                    MutableLiveData<BillingDataSource.SkuState> skuState = billingDataSource.skuStateMap.get(TrialCheck.FULL_LICENSE);
                    if (skuState != null) {
                        skuState.observeForever(fullLicense -> {
                            licenses.put(TrialCheck.FULL_LICENSE, fullLicense);
                            if (fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED || fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED) {
                                e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true);
                                e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                                e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false);
                                e.apply();
                            }
                        });
                    }
                    MutableLiveData<BillingDataSource.SkuState> skuState2 = billingDataSource.skuStateMap.get(TrialCheck.FULL_LICENSE_PLUS_FAMILY);
                    if (skuState2 != null) {
                        skuState2.observeForever(fullLicense -> {
                            licenses.put(TrialCheck.FULL_LICENSE_PLUS_FAMILY, fullLicense);
                            if (fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED || fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED) {
                                e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true);
                                e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                                e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false);
                                e.apply();
                            }
                        });
                    }
                    MutableLiveData<BillingDataSource.SkuState> skuState3 = billingDataSource.skuStateMap.get(TrialCheck.FULL_LICENSE_PLUS_PROFESSIONAL);
                    if (skuState3 != null) {
                        skuState3.observeForever(fullLicense -> {
                            licenses.put(TrialCheck.FULL_LICENSE_PLUS_PROFESSIONAL, fullLicense);
                            if (fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED || fullLicense == BillingDataSource.SkuState.SKU_STATE_PURCHASED_AND_ACKNOWLEDGED) {
                                e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true);
                                e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                                e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false);
                                e.apply();
                            }
                        });
                    }
                }
            });
        }
        // check date for trial period
        mainView.post(() -> {
            try {
                checkLicense();
                AppPreferences.setByLicense(board);
            } catch (Exception ignored) {

            }
        });

        // Use instance field for listener
        /* The listener. */
        OnSharedPreferenceChangeListener listener = (prefs, key) -> {
            Log.d("prefs", key);
            SharedPreferences settings1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            if (key.contentEquals(AppPreferences.PREF_KEY_FULL_LICENSE)
                    || key.contentEquals(AppPreferences.PREF_KEY_LIMITED_LICENSE)
                    || key.contentEquals(AppPreferences.PREF_KEY_TRIAL_LICENSE)) {
                isEditable =
                        loggedIn && !settings1.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                mainGrid.invalidateViews();
                populateMenu(mainMenu);
                if (loggedIn && (settings1.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, true)
                        || (settings1.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true)))) {
                    mainMenu.findItem(R.id.buyLicense).setVisible(true);
                    populateMenu(mainMenu);

                }

                if (!loggedIn) {
                    mainMenu.findItem(R.id.buyLicense).setVisible(true);
                    populateMenu(mainMenu);

                }
            }

            AppPreferences.setByLicense(board);

        };

        settings.registerOnSharedPreferenceChangeListener(listener);

        SearchManager sm = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        if (sm != null) sm.setOnDismissListener(() -> Log.d("", "dismissed search"));

        drawerLayout = findViewById(R.id.drawer_layout);
        leftDrawer = findViewById(R.id.left_drawer);
        leftDrawer.setHasFixedSize(false);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        leftDrawer.setLayoutManager(mLayoutManager);

        if (mAdapter == null) {
            mAdapter = new BoardDirectoryAdapter(this, v -> {
                View vp = (View) v.getParent().getParent();
                int position = leftDrawer.getChildAdapterPosition(vp);
                mAdapter.toggleGroup(position);
            }, v -> {
                int position = leftDrawer.getChildAdapterPosition(v);
                BoardDirectoryItem content = (BoardDirectoryItem) mAdapter.getItemAt(position);
                BoardContent _content = content.getContent();
                if (_content.getChildBoardId() != 0 && !content.isGroup()) {
                    mAdapter.toggleGroup(position);
                } else {
                    Intent localIntent1 = new Intent(Intent.ACTION_VIEW, null, board.getApplicationContext(), Board.class);
                    localIntent1.putExtra(Board.INTENT_EXTRA_BOARD_ID, _content.getBoardId());
                    localIntent1.putExtra(Board.INTENT_EXTRA_BOARD_NAME,
                            "Searched");
                    localIntent1.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, Board.getIsEditable());
                    drawerLayout.closeDrawers();
                    board.startActivity(localIntent1);
                }
            });

            Handler handler = new Handler();
            final Runnable updateTree = () -> {
                try {
                    new zzz(board).execute();
                } catch (Exception ex) {
                    // OK
                }
            };
            handler.postDelayed(updateTree, 10000);
        }
        leftDrawer.setAdapter(mAdapter);
        if (!GoogleApiConnected && !GoogleApiNotAvailable) {
            getGoogleApiClient().connect();
        }
        ArrayList<BoardContent> test = BoardContent.getScheduledContent(this);
        if (test.size() > 0) {
            try {
                CalendarHelper ch = new CalendarHelper(board, "MyTalk", "MyTalk", "support@mytalk.zendesk.com");
                ch.UpdateAllEvents();
            } catch (Exception ignored) {

            }
        }
        updateLocation();
        try {
            resetGrid(false);
        } catch (Exception ex) {
            // np
        }

        secondaryVoice = settings.getString("secondaryTTS", "eng-usa");
        primaryVoice = settings.getString("primaryTTS", "eng-usa");


        // Initialize credentials and service object.
        //getResultsFromApi();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
        if (requestCode == PERMISSION_RETURN_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ArrayList<String> permissionStrings = new ArrayList<>();
                for (String p : permissions) {
                    permissionStrings.add(p.replace("android.permission.", "").replace("_", " "));
                }
                String permissionString = join(permissionStrings);
                Toast.makeText(this, "No permissions granted for: " + permissionString, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.overwriteDevice || requestCode == R.id.shareBoard || requestCode == R.id.overwriteWorkspace || requestCode == R.id.restoreFromBackup || requestCode == R.id.merge || requestCode == R.id.SignIn) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MenuItem item = new MenuItem() {
                    @Override
                    public int getItemId() {
                        return requestCode;
                    }

                    @Override
                    public int getGroupId() {
                        return 0;
                    }

                    @Override
                    public int getOrder() {
                        return 0;
                    }

                    @Override
                    public MenuItem setTitle(CharSequence title) {
                        return null;
                    }

                    @Override
                    public MenuItem setTitle(int title) {
                        return null;
                    }

                    @Override
                    public CharSequence getTitle() {
                        return null;
                    }

                    @Override
                    public MenuItem setTitleCondensed(CharSequence title) {
                        return null;
                    }

                    @Override
                    public CharSequence getTitleCondensed() {
                        return null;
                    }

                    @Override
                    public MenuItem setIcon(Drawable icon) {
                        return null;
                    }

                    @Override
                    public MenuItem setIcon(int iconRes) {
                        return null;
                    }

                    @Override
                    public Drawable getIcon() {
                        return null;
                    }

                    @Override
                    public MenuItem setIntent(Intent intent) {
                        return null;
                    }

                    @Override
                    public Intent getIntent() {
                        return null;
                    }

                    @Override
                    public MenuItem setShortcut(char numericChar, char alphaChar) {
                        return null;
                    }

                    @Override
                    public MenuItem setNumericShortcut(char numericChar) {
                        return null;
                    }

                    @Override
                    public char getNumericShortcut() {
                        return 0;
                    }

                    @Override
                    public MenuItem setAlphabeticShortcut(char alphaChar) {
                        return null;
                    }

                    @Override
                    public char getAlphabeticShortcut() {
                        return 0;
                    }

                    @Override
                    public MenuItem setCheckable(boolean checkable) {
                        return null;
                    }

                    @Override
                    public boolean isCheckable() {
                        return false;
                    }

                    @Override
                    public MenuItem setChecked(boolean checked) {
                        return null;
                    }

                    @Override
                    public boolean isChecked() {
                        return false;
                    }

                    @Override
                    public MenuItem setVisible(boolean visible) {
                        return null;
                    }

                    @Override
                    public boolean isVisible() {
                        return false;
                    }

                    @Override
                    public MenuItem setEnabled(boolean enabled) {
                        return null;
                    }

                    @Override
                    public boolean isEnabled() {
                        return false;
                    }

                    @Override
                    public boolean hasSubMenu() {
                        return false;
                    }

                    @Override
                    public SubMenu getSubMenu() {
                        return null;
                    }

                    @Override
                    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                        return null;
                    }

                    @Override
                    public ContextMenu.ContextMenuInfo getMenuInfo() {
                        return null;
                    }

                    @Override
                    public void setShowAsAction(int actionEnum) {

                    }

                    @Override
                    public MenuItem setShowAsActionFlags(int actionEnum) {
                        return null;
                    }

                    @Override
                    public MenuItem setActionView(View view) {
                        return null;
                    }

                    @Override
                    public MenuItem setActionView(int resId) {
                        return null;
                    }

                    @Override
                    public View getActionView() {
                        return null;
                    }

                    @Override
                    public MenuItem setActionProvider(ActionProvider actionProvider) {
                        return null;
                    }

                    @Override
                    public ActionProvider getActionProvider() {
                        return null;
                    }

                    @Override
                    public boolean expandActionView() {
                        return false;
                    }

                    @Override
                    public boolean collapseActionView() {
                        return false;
                    }

                    @Override
                    public boolean isActionViewExpanded() {
                        return false;
                    }

                    @Override
                    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                        return null;
                    }
                };
                onOptionsItemSelected(item);
            } else {
                ArrayList<String> permissionStrings = new ArrayList<>();
                for (String p : permissions) {
                    permissionStrings.add(p.replace("android.permission.", "").replace("_", " "));
                }
                String permissionString = join(permissionStrings);
                Toast.makeText(this, "No permissions granted for: " + permissionString, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MenuItem item = new MenuItem() {
                    @Override
                    public int getItemId() {
                        return requestCode;
                    }

                    @Override
                    public int getGroupId() {
                        return 0;
                    }

                    @Override
                    public int getOrder() {
                        return 0;
                    }

                    @Override
                    public MenuItem setTitle(CharSequence title) {
                        return null;
                    }

                    @Override
                    public MenuItem setTitle(int title) {
                        return null;
                    }

                    @Override
                    public CharSequence getTitle() {
                        return null;
                    }

                    @Override
                    public MenuItem setTitleCondensed(CharSequence title) {
                        return null;
                    }

                    @Override
                    public CharSequence getTitleCondensed() {
                        return null;
                    }

                    @Override
                    public MenuItem setIcon(Drawable icon) {
                        return null;
                    }

                    @Override
                    public MenuItem setIcon(int iconRes) {
                        return null;
                    }

                    @Override
                    public Drawable getIcon() {
                        return null;
                    }

                    @Override
                    public MenuItem setIntent(Intent intent) {
                        return null;
                    }

                    @Override
                    public Intent getIntent() {
                        return null;
                    }

                    @Override
                    public MenuItem setShortcut(char numericChar, char alphaChar) {
                        return null;
                    }

                    @Override
                    public MenuItem setNumericShortcut(char numericChar) {
                        return null;
                    }

                    @Override
                    public char getNumericShortcut() {
                        return 0;
                    }

                    @Override
                    public MenuItem setAlphabeticShortcut(char alphaChar) {
                        return null;
                    }

                    @Override
                    public char getAlphabeticShortcut() {
                        return 0;
                    }

                    @Override
                    public MenuItem setCheckable(boolean checkable) {
                        return null;
                    }

                    @Override
                    public boolean isCheckable() {
                        return false;
                    }

                    @Override
                    public MenuItem setChecked(boolean checked) {
                        return null;
                    }

                    @Override
                    public boolean isChecked() {
                        return false;
                    }

                    @Override
                    public MenuItem setVisible(boolean visible) {
                        return null;
                    }

                    @Override
                    public boolean isVisible() {
                        return false;
                    }

                    @Override
                    public MenuItem setEnabled(boolean enabled) {
                        return null;
                    }

                    @Override
                    public boolean isEnabled() {
                        return false;
                    }

                    @Override
                    public boolean hasSubMenu() {
                        return false;
                    }

                    @Override
                    public SubMenu getSubMenu() {
                        return null;
                    }

                    @Override
                    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                        return null;
                    }

                    @Override
                    public ContextMenu.ContextMenuInfo getMenuInfo() {
                        return null;
                    }

                    @Override
                    public void setShowAsAction(int actionEnum) {

                    }

                    @Override
                    public MenuItem setShowAsActionFlags(int actionEnum) {
                        return null;
                    }

                    @Override
                    public MenuItem setActionView(View view) {
                        return null;
                    }

                    @Override
                    public MenuItem setActionView(int resId) {
                        return null;
                    }

                    @Override
                    public View getActionView() {
                        return null;
                    }

                    @Override
                    public MenuItem setActionProvider(ActionProvider actionProvider) {
                        return null;
                    }

                    @Override
                    public ActionProvider getActionProvider() {
                        return null;
                    }

                    @Override
                    public boolean expandActionView() {
                        return false;
                    }

                    @Override
                    public boolean collapseActionView() {
                        return false;
                    }

                    @Override
                    public boolean isActionViewExpanded() {
                        return false;
                    }

                    @Override
                    public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                        return null;
                    }
                };
                onContextItemSelected(item);
            } else {
                ArrayList<String> permissionStrings = new ArrayList<>();
                for (String p : permissions) {
                    permissionStrings.add(p.replace("android.permission.", "").replace("_", " "));
                }
                String permissionString = join(permissionStrings);
                Toast.makeText(this, "No permissions granted for: " + permissionString, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateLocation() {
        Handler handler = new Handler();
        final Runnable updateLocation = () -> {
            if (GoogleApiConnected) {
                updateLocationMonitoring(BoardContent.getLocationContent(board));
            } else {
                updateLocation();
            }
        };
        handler.postDelayed(updateLocation, 2000);
    }

    /*
      Check license.
     */
    private void checkLicense() {
        try {
            if (TrialCheck.trialChecked) return;
            TrialCheck.trialChecked = true;
            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            new CheckTrialPeriod().executeAsync(result -> {
                if (result == null) {
                    return;
                }
                Date createdDate = result.Membership.getCreatedDate();
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(createdDate);
                calendarEnd.add(Calendar.DATE, 30);

                Calendar calendarCurrent = Calendar.getInstance();
                Date currentDate = calendarCurrent.getTime();

                Log.d("created date: ", createdDate.toString());
                String firstName = result.FirstName;
                Log.d("current date: ", currentDate.toString());
                Log.d("first name: ", firstName);

                if ((settings.getBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, true) && currentDate.after(calendarEnd.getTime()))) {
                    dateIsAfter = true;
                    trialCheck = new TrialCheck(board);
                    trialCheck.solveLicenseIssue(dateIsAfter);
                } else {
                    dateIsAfter = false;
                }
                isEditable = loggedIn && !settings.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                populateMenu(mainMenu);
                AppPreferences.setByLicense(board);
                GridView gridView = board.findViewById(R.id.mainGrid);
                gridView.invalidateViews();
            }, board);
        } catch (Exception e) {
            dateIsAfter = true;
            e.printStackTrace();
        }
    }

    /*
      Lets user enter a phrase from the keyboard and then immediately voices the phrase or optionally
      inserts the phrase as a cell in the phrase bar.

      @param phraseMode the phrase mode
     */
    private void typePhrase(final boolean phraseMode, final Runnable callback) {
        final UtterPhrase up = new UtterPhrase(board);
        if (phraseMode) {
            up.setOnDismissListener(arg0 -> {
                EditText te = up.findViewById(R.id.EditTextPhrase);
                String either = te.getText().toString();
                if (either.length() > 0) {
                    BoardContent bc = new BoardContent();
                    bc.setText(either);
                    sentenceBarPhrase.add(bc);
                    drawSentenceBarPost(DO_NOT_HIGHLIGHT);
                    up.cancel();
                    if (callback != null) callback.run();
                }
            });
        } else {
            up.setOnDismissListener(arg0 -> {
                EditText te = up.findViewById(R.id.EditTextPhrase);
                String either = te.getText().toString();
                if (either.length() > 0) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, te.getText().toString());
                    if (tts != null) {
                        tts.speak(te.getText().toString(), 2, null, te.getText().toString());
                    }
                    up.cancel();
                    if (callback != null) callback.run();
                }
            });

        }
        up.show();

    }

    public TextToSpeech getVoice(Boolean primary) {
        if (primary || ttsSecondary == null) return tts;
        return ttsSecondary;
    }

    private void processCommands() {
        if (commands != null && commands.size() > 0) {
            switch (commands.get(0)) {
                case "":

                case "show":
                    break;
                case "home":
                    mainView.post(() -> {
                        Intent localIntent = new Intent(getApplicationContext(), Board.class);
                        localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
                        localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(localIntent);
                        finish();
                    });
                    break;

                case "back":
                    mainView.post(this::finish);
                    break;

                case "play":
                    mainView.post(() -> {
                        Board.getSentenceBarQueue().clear();
                        for (BoardContent bc : Board.sentenceBarPhrase) {
                            Board.getSentenceBarQueue().add(bc);
                        }
                        playNextWord(null);
                    });
                    break;

                case "type":
                    mainView.post(() -> {
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(board.getBaseContext());
                        boolean phraseMode = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_PHRASE_MODE, false);
                        typePhrase(phraseMode, null);
                    });
                    break;

                case "clear":
                    mainView.post(() -> {
                        Board.sentenceBarPhrase.clear();
                        drawSentenceBarPost(DO_NOT_HIGHLIGHT);
                        strip.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    });
                    break;

                case "delete":
                    mainView.post(() -> {
                        Board.sentenceBarPhrase.remove(Board.sentenceBarPhrase.size() - 1);
                        drawSentenceBarPost(DO_NOT_HIGHLIGHT);
                        strip.fullScroll(HorizontalScrollView.FOCUS_LEFT);
                    });
                    break;

                case "voice":
                    commands.remove(0);
                    mainView.post(() -> {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, commands.get(0));
                            tts.speak(commands.get(0), 2, null, commands.get(0));
                        } catch (Exception ignored) {

                        }
                    });
                    break;

                case "phraseBarOn":
                    mainView.post(() -> {
                        sentenceBarLayout.setVisibility(TextView.VISIBLE);
                        resetGrid(true);
                    });
                    break;

                case "phraseBarOff":
                    mainView.post(() -> {
                        sentenceBarLayout.setVisibility(TextView.GONE);
                        resetGrid(true);
                    });
                    break;

                case "phraseBarToggle":
                    mainView.post(() -> {
                        if (sentenceBarLayout.getVisibility() == TextView.VISIBLE) {
                            sentenceBarLayout.setVisibility(TextView.GONE);
                        } else {
                            sentenceBarLayout.setVisibility(TextView.VISIBLE);
                        }
                        resetGrid(true);
                    });
                    break;

                case "share":
                case "print":
                    mainView.post(() -> {
                        board.openOptionsMenu();
                        mainView.post(() -> optionsMenu.performIdentifierAction(R.id.shareBoard, 0));
                    });
                    break;

                case "most": {
                    Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.MOST_USED_BOARD);
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.most_used));
                    localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
                    this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                            null);
                }
                break;

                case "recents": {
                    Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.MOST_RECENTS_BOARD);
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.recents));
                    localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
                    this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                            null);
                }
                break;

                case "content": {
                    commands.remove(0);
                    BoardContent content = new BoardContent(board, Integer.parseInt(commands.get(0)));
                    Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, content.getChildBoardId());
                    localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, content.getText());
                    localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
                    this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                            null);
                }
                break;

                default:
                    try {
                        final int ordinal = Integer.parseInt(commands.get(0));
                        mainView.post(() -> itemClick(mainGrid.getAdapter(), null, mainGrid, ordinal, 0));
                    } catch (Exception ignored) {
                    }
                    break;
            }
            commands.remove(0);
            mainView.post(this::processCommands);
        }
    }

    private void handleUri(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            commands = new LinkedList<>(Arrays.asList(uri.getPath().split("/")));
            processCommands();

        }
    }

    /*
      Responds to a click on a board cell.

      @param boardAdapter       the board ContactAdapter
     * @param savedInstanceState the saved instance state
     * @param gridView           the grid view
     * @param index              the index
     * @param unused             the unused
     */
    public final void itemClick(final ListAdapter boardAdapter, final Bundle ignore,
                                final View gridView, final int index, final long unused) {

        Runnable restartTimer = () -> {
            if (switchScenario() == SwitchScenario._2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START || switchScenario() == SwitchScenario._1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START) {
                startTimer();
            }
        };
        if (index == -2) {
            Board.getSentenceBarQueue().clear();
            for (BoardContent bc : Board.sentenceBarPhrase) {
                Board.getSentenceBarQueue().add(bc);
            }
            stopTimer();
            playNextWord(restartTimer);
            return;
        }

        if (index == -1) {
            Board.sentenceBarPhrase.remove(Board.sentenceBarPhrase.size() - 1);
            drawSentenceBarPost(DO_NOT_HIGHLIGHT);
            strip.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            return;
        }

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(board.getBaseContext());
        boolean doTTS = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_TTS, true);
        boolean zoomPic = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_ZOOM_PICTURES, false);
        boolean phraseMode = sentenceBarLayout.getVisibility() == TextView.VISIBLE;
        boolean autoWordVariation = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_AUTO_WORD_VARIATION, false);
        boolean highlightedHotspot = false;
        BoardAdapter ba = (BoardAdapter) boardAdapter;
        BoardContent content = ba.getItem(index);
        content.setTotalUses(content.getTotalUses() + 1);
        content.persist(this);
        if (content.getType() == GO_HOME_COMMAND) {
            Intent localIntent = new Intent(getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(localIntent);
            finish();
            return;
        }
        if (content.getType() == GO_BACK_COMMAND) {
            finish();
            return;
        }

        if (content.getHotspotStyle() == 1) {
            if (unused == 1 && selectedHotspotScanPosition != null) {
                BoardRow boardRow = content.getChildBoard(this);
                BoardContent test = boardRow.getItemByIndex(selectedHotspotScanPosition);
                if (test != null) content = content.mergeHotspot(this, test);
                highlightedHotspot = true;
            } else {
                GridView gv = (GridView) gridView;
                MotionEvent ev = ba.getMotionEvent();
                View v = gv.getChildAt(index);
                ImageView iv = v.findViewById(R.id.imageView);
                if (iv != null) {
                    int[] loc = {0, 0};
                    iv.getLocationOnScreen(loc);
                    float imageX = ev.getX() - loc[0];
                    float imageY = ev.getY() - loc[1];
                    float imageWidth = iv.getWidth();
                    float imageHeight = iv.getHeight();
                    BoardRow boardRow = content.getChildBoard(this);
                    int rows = boardRow.getRows();
                    int columns = boardRow.getColumns();
                    int r = (int) (Math.floor(imageY / imageHeight * rows) + 1);
                    int c = (int) (Math.floor(imageX / imageWidth * columns) + 1);
                    Bitmap bitmap = Utility.drawableToBitmap(iv.getDrawable());
                    Bitmap n = Utility.drawGridOnBitmap(bitmap, rows, columns, r, c);
                    iv.setImageBitmap(n);
                    BoardContent test = content.getHotspotContent(board, imageX, imageY, imageWidth, imageHeight);
                    if (test != null) content = content.mergeHotspot(this, test);
                    highlightedHotspot = true;
                }
            }
        }

        // launch type words feature - cdm
        if (content.getText().contentEquals(QUESTION_MARK) && tts != null && doTTS) {
            typePhrase(phraseMode, restartTimer);
            return;
        }
        if (content.canBeZoomed(board, zoomPic, phraseMode)) {
            Board.getSentenceBarQueue().clear();
            content.showPicture(board);
            return;
        }
        if (content.getMediaType(board) == BoardContent.MediaType.movie) {
            Board.getSentenceBarQueue().clear();
            content.playMovie(this);
            return;
        } else if (content.getDoNotAddToPhraseBar() == 0 && phraseMode
                && content.getAlternateTtsText() != null && content.getAlternateTtsText().length() > 0) {
            if (content.getExternalUrl().isEmpty()) {
                if (Board.sentenceBarPhrase.size() > MAX_SENTENCE_BAR_SIZE) {
                    Board.sentenceBarPhrase.remove(0);
                }
                Board.sentenceBarPhrase.add(content);
                drawSentenceBarPost(DO_NOT_HIGHLIGHT);
            }
        } else if (content.getDoNotAddToPhraseBar() == 0 && phraseMode && content.getText() != null
                && content.getText().length() > 0) {
            if (content.getExternalUrl().isEmpty()) {
                if (Board.sentenceBarPhrase.size() > MAX_SENTENCE_BAR_SIZE) {
                    Board.sentenceBarPhrase.remove(0);
                }
                Board.sentenceBarPhrase.add(content);
                drawSentenceBarPost(DO_NOT_HIGHLIGHT);
            }
        } else if (content.CanBeVoiced(board)) {
            Board.getSentenceBarQueue().clear();
            stopTimer();
            content.voice(board, getMediaPlayer(restartTimer), tts, restartTimer);
        }
        if (!content.getExternalUrl().isEmpty()) {
            {
                Uri uri = Uri.parse(content.getExternalUrl());
                if (content.getExternalUrl().startsWith("mtschedule:/")) {
                    return;
                }
                if (content.getExternalUrl().equals("x")) {
                    return;
                }
                if (content.getExternalUrl().startsWith("mtgeo:/")) {
                    return;
                }
                if (content.getExternalUrl().startsWith("mytalktools:/")) {
                    handleUri(uri);
                    return;
                }
                if (content.getExternalUrl().startsWith("music:/")) {
                    try {
                        Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception ex) {
                        try {
                            String pkgname = "com.sec.android.app.music";
                            PackageManager pkgmanager = getPackageManager();
                            Intent intent = pkgmanager.getLaunchIntentForPackage(pkgname);
                            startActivity(intent);
                        } catch (Exception e2) {
                            Utility.alert(getString(R.string.music_player_not_installed), board);
                        }
                    }
                    return;
                }
                if (content.getExternalUrl().startsWith("tel:")) {
                    if (requestAppPermission(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_RETURN_CODE)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        return;
                    }
                }
                if (content.getExternalUrl().startsWith("contacts:/")) {
                    if (requestAppPermission(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_RETURN_CODE)) {
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, RequestCode.RESULT_PICK_CONTACT.ordinal());
                        return;
                    }
                }
                if (content.getExternalUrl().startsWith("contact:/")) {
                    if (requestAppPermission(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_RETURN_CODE)) {
                        String[] args = content.getExternalUrl().split("/");
                        if (args.length == 2) {
                            String[] selectionArgs = {"%" + args[1] + "%"};
                            (new ContactDialog(this.board, ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?", selectionArgs, null)).show();
                        }
                        return;
                    }
                }
                if (content.getExternalUrl().startsWith("contactdialog:/")) {
                    if (requestAppPermission(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_RETURN_CODE)) {
                        String[] args = content.getExternalUrl().split("/");
                        String[] selectionArgs = new String[args.length - 1];
                        if (args.length > 1) {
                            System.arraycopy(args, 1, selectionArgs, 0, args.length - 1);
                        }
                        (new ContactDialog(this.board, ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts._ID + " = ?", selectionArgs, null)).show();
                        return;
                    }
                }
                if (content.getExternalUrl().startsWith("contactsboard:/")) {
                    if (requestAppPermission(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_RETURN_CODE)) {
                        Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
                        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.CONTACTS_BOARD);
                        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.contacts));
                        localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
                        localIntent.putExtra(Board.INTENT_EXTRA_URI, content.getExternalUrl());
                        this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                                null);
                        return;
                    }
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception ex) {
                        Utility.alert(ex.getMessage(), board);
                    }
                }
            }
        }
        if (content.getChildBoardId() != 0) {
            stopTimer();
            content.navigateToChildBoard(board, isEditable);
        } else if (content.getChildBoardLinkId() != 0) {
            stopTimer();
            content.navigateToLinkedChildBoard(board, isEditable);
        } else if (boardId != Board.WORD_VARIANT_BOARD && autoWordVariation && !(content.getText().equals("") && content.getAlternateTtsText().equals(""))) {
            GetWordVariants gwv = new GetWordVariants(content.getText(), this);
            BoardContent finalContent = content;
            gwv.executeAsync((variants) -> {
                if (variants != null && variants.size() > 0) {
                    try {
                        Board.sentenceBarPhrase.remove(Board.sentenceBarPhrase.size() - 1);
                    } catch (Exception ignored) {
                    }
                    stopTimer();
                    finalContent.navigateToWordVariationBoard(this, isEditable);
                }
            }, this);
        }
        if (highlightedHotspot) {
            mainView.postDelayed(() -> mainGrid.invalidateViews(), 500);
        }
    }

    private void contactPicked(Intent data) {
        if (data == null) return;
        Uri contactUri = data.getData();
        if (contactUri == null) return;
        ContentResolver contentResolver = this.board.getContentResolver();
        String[] projection = new String[]{ContactsContract.Contacts.LOOKUP_KEY};
        Cursor cursor = contentResolver.query(contactUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY) >= 0) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                (new ContactDialog(board, ContactsContract.Contacts.CONTENT_URI, ContactsContract.Contacts.LOOKUP_KEY + " = ?", new String[]{id}, null)).show();
            }
            cursor.close();
        }
    }

    /*
      Reset grid.
     */

    public void resetGrid(boolean phraseBarOverride) {
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        autoScanLoops = Integer.parseInt(sharedPreferences.getString(AppPreferences.PREF_KEY_AUTO_SCAN_LOOPS, "0"));
        scanByRow = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_SCAN_BY_ROW, false);
        sentenceBarLinearLayout = findViewById(R.id.sentenceBarLinearLayout);
        /* The sentence bar text. */
        TextView sentenceBarText = findViewById(R.id.sentenceBarText);
        /* The sentence bar delete button. */
        Button sentenceBarDelete = findViewById(R.id.sentenceBarDelete);
        /* The sentence bar play play button. */
        Button sentenceBarPlay = findViewById(R.id.sentenceBarPlay);
        sentenceBarLayout = findViewById(R.id.sentenceBarLayout);
        if (!phraseBarOverride) {
            if (sharedPreferences.getBoolean(AppPreferences.PREF_KEY_PHRASE_MODE, false)) {
                sentenceBarLayout.setVisibility(TextView.VISIBLE);
            } else {
                Board.sentenceBarPhrase.clear();
                sentenceBarLayout.setVisibility(TextView.GONE);
            }
        }

        boardRow = new BoardRow(boardId, this, boardName, boardUri);
        mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setFocusable(true);
        mainGrid.setFocusableInTouchMode(true);
        mainGrid.requestFocus();
        leftDrawer = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);

        registerForContextMenu(mainGrid);
        drawSentenceBarPost(DO_NOT_HIGHLIGHT);
        mainGrid.setNumColumns(boardRow.getColumns());
        /* Controls the display of the individual cells of a board. */
        BoardAdapter adapter = new BoardAdapter(this, boardRow, this);
        mainGrid.setAdapter(adapter);
        sentenceBarDelete.setOnClickListener(new SentenceBarDeleteClickListener());
        sentenceBarDelete.setOnLongClickListener(v -> {
            Board.sentenceBarPhrase.clear();
            drawSentenceBarPost(DO_NOT_HIGHLIGHT);
            return false;
        });
        sentenceBarPlay.setOnClickListener(new SentenceBarPlayClickListener());
        registerForContextMenu(sentenceBarPlay);

        sentenceBarText.setOnClickListener(v -> {
            Board.getSentenceBarQueue().clear();
            for (BoardContent bc : Board.sentenceBarPhrase) {
                Board.getSentenceBarQueue().add(bc);
            }
            playNextWord(null);
        });

        LinearLayout twoButtonSwitch = findViewById(R.id.twoButtonSwitch);
        LinearLayout oneButtonSwitch = findViewById(R.id.oneButtonSwitch);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button = findViewById(R.id.button);
        scanSwitch = sharedPreferences.getString(AppPreferences.PREF_KEY_SCAN_SWITCH, "none");
        autoScanInterval = Integer.parseInt(sharedPreferences.getString(AppPreferences.PREF_KEY_AUTO_SCAN_INTERVAL, "0"));
        autoStartAutoScan = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_AUTO_START_AUTO_SCAN, false);
        switch (scanSwitch) {
            case "ablenet":
                switchCodes = ableNetCodes;
                twoButtonSwitch.setVisibility(View.GONE);
                oneButtonSwitch.setVisibility(View.GONE);
                break;
            case "rjcooper":
                switchCodes = rjCooperCodes;
                twoButtonSwitch.setVisibility(View.GONE);
                oneButtonSwitch.setVisibility(View.GONE);
                break;
            default:
                switchCodes = null;
                twoButtonSwitch.setVisibility(View.GONE);
                oneButtonSwitch.setVisibility(View.GONE);
                break;
            case "1-button":
                switchCodes = null;
                twoButtonSwitch.setVisibility(View.GONE);
                oneButtonSwitch.setVisibility(View.VISIBLE);
                break;
            case "2-buttons":
                switchCodes = null;
                twoButtonSwitch.setVisibility(View.VISIBLE);
                oneButtonSwitch.setVisibility(View.GONE);
                break;
        }
        button.setOnClickListener(v -> switch1());
        button1.setOnClickListener(v -> switch1());
        button2.setOnClickListener(v -> switch2());

        scanByRow = sharedPreferences.getBoolean(AppPreferences.PREF_KEY_SCAN_BY_ROW, false);
        switch (switchScenario()) {
            case _2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START:
            case _1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START:
                startTimer();
                break;
            case _2_BUTTONS_WITH_AUTO_SCANNING:
            case _1_BUTTON_WITH_AUTO_SCANNING:
            case _NO_SWITCH:
                break;
            case _2_BUTTONS_WITHOUT_AUTO_SCANNING:
            case _1_BUTTON_WITHOUT_AUTO_SCANNING:
                scanByRow = false;
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    public final void onResume() {
        super.onResume();
        resetGrid(false);
        if (selectedItemId != 0) {
            for (BoardContent item : boardRow.getContents()) {
                if (item.getiPhoneId() == selectedItemId) {
                    selectedItem = item;
                }
            }
        }
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public final boolean onPrepareOptionsMenu(final Menu menu) {
        populateMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        /* populateMenu(menu); */
        /* Inflate the menu items for use in the action bar */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MenuItem item = menu.findItem(R.id.shareBoard);
        shareActionProvider = (ShareActionProvider) item.getActionProvider();
        menu.clear();
        optionsMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    private Intent getDefaultShareIntent(int t) {
        final SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(board.getBaseContext());
        String defaultEmail = sp.getString("defaultEmailRecipient", null);
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (defaultEmail != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{defaultEmail});
        }
        switch (t) {
            case 0:
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mytalktools_phrase));
                intent.putExtra(Intent.EXTRA_TEXT, getSharePhraseString());
                break;
            case 1:
                intent.setType("image/jpeg");
                Bitmap bitmap = getBitmapFromView(sentenceBarLinearLayout);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "MyTalkTools", getSharePhraseString());
                Uri screenshotUri = Uri.parse(path);
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                break;
            case 2:
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mytalktools_phrase));
                intent.putExtra(Intent.EXTRA_TEXT, getSharePhraseString());
                Bitmap bitmap2 = getBitmapFromView(sentenceBarLinearLayout);
                String path2 = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap2, "MyTalkTools", getSharePhraseString());
                Uri screenshotUri2 = Uri.parse(path2);
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri2);
                break;
        }
        return intent;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View,
     * android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public final void onCreateContextMenu(final ContextMenu menu, final View v,
                                          final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.sentenceBarPlay) {
            if (getSharePhraseString().isEmpty()) {
                Toast.makeText(this, R.string.unfortunately_your_phrase_bar_is_empty_, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // Inflate menu resource file.
            getMenuInflater().inflate(R.menu.share_action_menu, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.menu_item_share);
            final ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            SharedPreferences sp =
                    PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
            String messageType = sp.getString(AppPreferences.PREF_KEY_MESSAGE_TYPE, "0");
            String actionPreference = sp.getString(AppPreferences.PREF_KEY_ACTION_PREFERENCE, "0");
            //String EXTRA_METADATA = "com.facebook.orca.extra.METADATA";
            if ("0".equals(actionPreference)) {
                switch (messageType) {
                    case "0":
                        new AlertDialog.Builder(this).setPositiveButton(R.string.text_only, (dialog, which) -> {
                            mShareActionProvider.setShareIntent(getDefaultShareIntent(0));
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> menu.performIdentifierAction(R.id.menu_item_share, 0), 5);
                        }).setNegativeButton(R.string.image_only, (dialog, which) -> {
                            mShareActionProvider.setShareIntent(getDefaultShareIntent(1));
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> menu.performIdentifierAction(R.id.menu_item_share, 0), 5);
                        }).setNeutralButton(R.string.text_and_image, (dialog, which) -> {
                            mShareActionProvider.setShareIntent(getDefaultShareIntent(2));
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> menu.performIdentifierAction(R.id.menu_item_share, 0), 5);
                        }).setCancelable(true).create().show();
                        break;
                    case "1":
                    case "2":
                    case "3":
                        mShareActionProvider.setShareIntent(getDefaultShareIntent(Integer.parseInt(messageType) - 1));
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> menu.performIdentifierAction(R.id.menu_item_share, 0), 5);
                        break;

                }
            } else {
                String EXTRA_PROTOCOL_VERSION = "com.facebook.orca.extra.PROTOCOL_VERSION";
                String EXTRA_APP_ID = "com.facebook.orca.extra.APPLICATION_ID";
                int PROTOCOL_VERSION = 20150314;
                String mimeType = "image/*";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setPackage("com.facebook.orca");
                intent.setType(mimeType);
                Bitmap bitmap = getBitmapFromView(sentenceBarLinearLayout);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "MyTalkTools", getSharePhraseString());
                Uri screenshotUri = Uri.parse(path);
                intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                intent.putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION);
                intent.putExtra(EXTRA_APP_ID, getString(R.string.app_id));
                try {
                    this.startActivityForResult(intent, RequestCode.MESSENGER_SENT.ordinal());
                } catch (Exception ex) {
                    new AlertDialog.Builder(board).setMessage(R.string.facebook_not_installed).create().show();
                }
            }

        } else if (isEditable) {
            selectedItemId = selectedItem.getiPhoneId();
            if (selectedHotspotItem == null || selectedItem.getHotspotStyle() == 0 || selectedItem.getChildBoardId() == 0) {
                getMenuInflater().inflate(R.menu.cellmenu, menu);
                menu.setHeaderTitle(R.string.modify_cell);
                menu.setHeaderIcon(R.drawable.ic_menu_edit);
            } else {
                getMenuInflater().inflate(R.menu.hotspot_or_cell, menu);
                menu.setHeaderIcon(R.drawable.ic_menu_edit);
                MenuItem wordVariationBoard = menu.findItem(R.id.wordVariationBoard);
                MenuItem codedWordVariationBoard = menu.findItem(R.id.codedWordVariationBoard);
                MenuItem pasteCell = menu.findItem(R.id.pasteCellHotspot);
                MenuItem deleteSound = menu.findItem(R.id.deleteSoundHotspot);
                MenuItem deleteImage = menu.findItem(R.id.deleteImageHotspot);
                MenuItem editImage = menu.findItem(R.id.editImageHotspot);
                MenuItem rotateImage = menu.findItem(R.id.rotateImageHotspot);
                MenuItem pasteImage = menu.findItem(R.id.pasteImageHotspot);
                MenuItem deleteBoard = menu.findItem(R.id.deleteBoardHotspot);
                MenuItem pasteSound = menu.findItem(R.id.pasteSoundHotspot);
                deleteSound.setEnabled(selectedHotspotItem.getUrl2().length() != 0);
                pasteSound.setEnabled(bufferItem != null && bufferItem.getUrl2().length() != 0);
                deleteBoard.setEnabled(selectedHotspotItem.getChildBoardId() != 0 || selectedHotspotItem.getChildBoardLinkId() != 0);
                deleteImage.setEnabled(selectedHotspotItem.getUrl().length() != 0);
                editImage.setEnabled(selectedHotspotItem.getUrl().length() != 0);
                rotateImage.setEnabled(selectedHotspotItem.getUrl().length() != 0);
                pasteImage.setEnabled(bufferItem != null && bufferItem.getUrl().length() != 0);
                pasteCell.setEnabled(bufferItem != null);
                wordVariationBoard.setEnabled(selectedHotspotItem.getText().length() > 0);
                codedWordVariationBoard.setEnabled(selectedHotspotItem.getText().length() > 0);
            }
            menu.setHeaderTitle(R.string.modify_cell);
            menu.setHeaderIcon(R.drawable.ic_menu_edit);
            MenuItem wordVariationBoard = menu.findItem(R.id.wordVariationBoard);
            MenuItem codedWordVariationBoard = menu.findItem(R.id.codedWordVariationBoard);
            MenuItem pasteCell = menu.findItem(R.id.pasteCell);
            MenuItem compressColumns = menu.findItem(R.id.compressColumns);
            MenuItem deleteLastRow = menu.findItem(R.id.deleteLastRow);
            MenuItem deleteRightColumn = menu.findItem(R.id.deleteRightColumn);
            MenuItem deleteSound = menu.findItem(R.id.deleteSound);
            MenuItem deleteImage = menu.findItem(R.id.deleteImage);
            MenuItem editImage = menu.findItem(R.id.editImage);
            MenuItem rotateImage = menu.findItem(R.id.rotateImage);
            MenuItem pasteImage = menu.findItem(R.id.pasteImage);
            MenuItem deleteBoard = menu.findItem(R.id.deleteBoard);
            MenuItem pasteSound = menu.findItem(R.id.pasteSound);
            MenuItem hotspotBoard = menu.findItem(R.id.hotspotBoard);
            deleteSound.setEnabled(selectedItem.getUrl2().length() != 0);
            pasteSound.setEnabled(bufferItem != null && bufferItem.getUrl2().length() != 0);
            deleteBoard.setEnabled(selectedItem.getChildBoardId() != 0 || selectedItem.getChildBoardLinkId() != 0);
            deleteImage.setEnabled(selectedItem.getUrl().length() != 0);
            hotspotBoard.setEnabled(selectedItem.getUrl().length() != 0);
            editImage.setEnabled(selectedItem.getUrl().length() != 0);
            rotateImage.setEnabled(selectedItem.getUrl().length() != 0);
            pasteImage.setEnabled(bufferItem != null && bufferItem.getUrl().length() != 0);
            deleteLastRow.setEnabled(board.boardRow.getRows() > 1);
            deleteRightColumn.setEnabled(board.boardRow.getColumns() > 1);
            compressColumns.setEnabled(board.boardRow.getColumns() > 1);
            pasteCell.setEnabled(bufferItem != null);
            wordVariationBoard.setEnabled(selectedItem.getText().length() > 0);
            codedWordVariationBoard.setEnabled(selectedItem.getText().length() > 0);

            if (selectedItem.getExternalUrl().length() > 0 && selectedItem.getExternalUrl().startsWith("mtschedule:/")) {
                MenuItem mi = menu.findItem(R.id.addSchedule);
                mi.setTitle(R.string.edit_schedule2);
            } else {
                MenuItem mi = menu.findItem(R.id.addSchedule);
                mi.setTitle(R.string.add_schedule);
            }
            if (selectedItem.getExternalUrl().length() > 0 && selectedItem.getExternalUrl().startsWith("mtgeo:/")) {
                MenuItem mi = menu.findItem(R.id.addLocation);
                mi.setTitle(R.string.edit_location);
            } else {
                MenuItem mi = menu.findItem(R.id.addLocation);
                mi.setTitle(R.string.add_location);
            }
        }
    }

    /*
      Rotate image file.
     */
    private void rotateImageFile(final BoardContent content) {
        new AlertDialog.Builder(this).setTitle(R.string.select_rotation)
                .setIcon(R.drawable.ic_menu_rotate)
                .setItems(R.array.rotationOptions, (dialog, which) -> {
                    float rotation = getResources().getIntArray(R.array.rotationOptionValues)[which];
                    try {
                        File fileDir = Utility.getMyTalkFilesDir(board);
                        InputStream inputStream;
                        String url = content.getUrl();

                        if (url.contains("/")) {
                            String str3 = url.replace(" ", "-").replace("/", "-");
                            File originalImageFile = new File(fileDir.getPath() + "/" + str3);
                            inputStream = Files.newInputStream(originalImageFile.toPath());

                            boolean isPNG = Utility.getExtension(new File(url)).equalsIgnoreCase(PNG);
                            String extension = getImageExtension(isPNG);
                            String newUrl =
                                    Utility.changeExtension(new File(new File(url).getName()), extension, "_r");

                            File outputFile =
                                    new File(fileDir.getAbsolutePath() + "/"
                                            + newUrl.replace(" ", "-").replace("/", "-"));
                            if (!outputFile.createNewFile()) return;
                            FileOutputStream fo = new FileOutputStream(outputFile);

                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
                            inputStream.close();

                            if (bitmap == null) return;
                            Bitmap rotatedBitmap = Utility.rotateBitmap(bitmap, rotation);
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            CompressFormat cf = getCompressFormat(isPNG);
                            rotatedBitmap.compress(cf, JPEG_COMPRESSION_RATIO, bytes);

                            fo.write(bytes.toByteArray());
                            fo.flush();
                            fo.close();

                            undoRedo.saveState();
                            content.setUrl(newUrl);
                            content.persist(getApplicationContext());
                            GridView gridView = board.findViewById(R.id.mainGrid);
                            RelativeLayout relativeLayout =
                                    board.findViewById(R.id.sentenceBarLayout);
                            gridView.invalidateViews();
                            relativeLayout.invalidate();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).create().show();
    }

    /*
      New board.
     */
    private void newBoard(final BoardContent content) {
        final NewBoard dialog = new NewBoard(board);
        dialog.setOnDismissListener(d -> {
            if (dialog.getColumns() != DO_NOT_HIGHLIGHT) {
                undoRedo.saveState();
                BoardRow newBoard =
                        new BoardRow(dialog.getColumns(), dialog.getRows(), content.getUserId(),
                                content.getText(), board, BoardRow.sortOrder.NotSorted,
                                BoardRow.sortOrder.NotSorted, BoardRow.sortOrder.NotSorted);
                content.setType(1);
                int newId = (int) newBoard.persist(true);
                content.setHotspotStyle(0);
                content.setChildBoardId(newId);
                content.persist(board);
                mainGrid.invalidateViews();
                sentenceBarLayout.invalidate();
                Utility.alert(getString(R.string.done_), board);
            }
        });
        dialog.show();
    }

    private void newWordVariantBoard() {
        PromptDialog dlg =
                new PromptDialog(this, getString(R.string.enter_word),
                        null, selectedItem.getText()) {

                    @Override
                    public boolean onOkClicked(final String input) {
                        GetWordVariants gwv = new GetWordVariants(input, board);
                        gwv.executeAsync((d) -> {
                            if (d == null) {
                                Utility.alert("Could not find that word in our dictionary.", board);
                                return;
                            }
                            JsonLexRecords j = new JsonLexRecords(d);
                            List<String> uniqueWords = j.getUniqueWords();
                            undoRedo.saveState();
                            int columns = uniqueWords.size() / 2;
                            columns = Math.min(columns, 4);
                            int rows = uniqueWords.size() / columns;
                            if (uniqueWords.size() % columns > 0) rows++;
                            BoardRow newBoard =
                                    new BoardRow(columns, rows, selectedItem.getUserId(),
                                            selectedItem.getText(), board, BoardRow.sortOrder.BackgroundColor,
                                            BoardRow.sortOrder.Alphabetic, BoardRow.sortOrder.NotSorted);
                            selectedItem.setType(1);
                            int newId = (int) newBoard.persist(true);
                            selectedItem.setChildBoardId(newId);
                            selectedItem.persist(board);
                            newBoard = new BoardRow(newId, board);
                            List<BoardContent> contents = newBoard.getContents();
                            for (BoardContent content : contents) content.setText("");
                            for (int x = 0; x < uniqueWords.size(); x++) {
                                BoardContent content = contents.get(x);
                                content.setText(uniqueWords.get(x));
                                content.persist(board);
                            }
                            for (int x = uniqueWords.size(); x < contents.size(); x++) {
                                BoardContent content = contents.get(x);
                                content.setText("");
                                content.setBackgroundColor(999);
                                content.persist(board);
                            }
                            Utility.alert(getString(R.string.done_), board);
                        }, board);
                        return false;
                    }
                };
        dlg.show();
    }

    private void newCodedWordVariantBoard() {
        PromptDialog dlg =
                new PromptDialog(this, getString(R.string.enter_word),
                        null, selectedItem.getText()) {

                    @Override
                    public boolean onOkClicked(final String input) {
                        SharedPreferences sp =
                                PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
                        String colorKey = sp.getString(AppPreferences.PREF_KEY_COLOR_KEY, "Goosen");
                        int colorKeyCode = colorKey.equals("Goosen") ? 1 : 0;
                        GetWordVariants gwv = new GetWordVariants(input, board);
                        gwv.executeAsync((d) -> {
                            if (d == null) {
                                Utility.alert(getString(R.string.could_not_find_word), board);
                                return;
                            }
                            JsonLexRecords j = new JsonLexRecords(d);
                            List<JsonLexRecord> uniqueWords = j.getUniqueWordsAndTypes(colorKeyCode);
                            undoRedo.saveState();
                            int columns = uniqueWords.size() / 2;
                            columns = Math.min(columns, 4);
                            if (columns == 0) columns = 1;
                            int rows = uniqueWords.size() / columns;
                            if (uniqueWords.size() % columns > 0) rows++;
                            BoardRow newBoard =
                                    new BoardRow(columns, rows, selectedItem.getUserId(),
                                            selectedItem.getText(), board, BoardRow.sortOrder.BackgroundColor,
                                            BoardRow.sortOrder.Alphabetic, BoardRow.sortOrder.NotSorted);
                            selectedItem.setType(1);
                            int newId = (int) newBoard.persist(true);
                            selectedItem.setChildBoardId(newId);
                            selectedItem.persist(board);
                            newBoard = new BoardRow(newId, board);
                            List<BoardContent> contents = newBoard.getContents();
                            for (int x = 0; x < uniqueWords.size(); x++) {
                                BoardContent content = contents.get(x);
                                content.setText(uniqueWords.get(x).Value);
                                content.setBackgroundColor(uniqueWords.get(x).colorCode);
                                content.persist(board);
                            }
                            for (int x = uniqueWords.size(); x < contents.size(); x++) {
                                BoardContent content = contents.get(x);
                                content.setText("");
                                content.setBackgroundColor(999);
                                content.persist(board);
                            }
                            mainGrid.invalidateViews();
                            sentenceBarLayout.invalidate();
                            Utility.alert(getString(R.string.done_), board);
                        }, board);
                        return false;
                    }
                };
        dlg.show();
    }

    /*
      Creates the board with id.

      @param iphoneBoardId  the iphone board id
     * @param persistContent the persist content
     * @param copyType       the copy type
     */
    private void createBoardWithId(final int iphoneBoardId,
                                   final BoardContent persistContent, final int copyType) {

        undoRedo.saveState();

        // delete board
        if (copyType == 2) {
            persistContent.setChildBoardId(0);
            persistContent.setChildBoardLinkId(iphoneBoardId);
            persistContent.persist(this);
            return;
        }

        // get the original board
        BoardRow localBoard = new BoardRow(iphoneBoardId, this);

        // create a copy
        BoardRow newBoard =
                new BoardRow(localBoard.getColumns(), localBoard.getRows(), localBoard.getUserId(),
                        localBoard.getBoardName(), this, localBoard.getSort1(), localBoard.getSort2(),
                        localBoard.getSort3());

        // save the copy
        long newBoardId = newBoard.persist(false);

        if (newBoardId != CREATE_NEW_BOARD && localBoard.getContents().size() > 0) {
            for (BoardContent content : localBoard.getContents()) {
                BoardContent newContent = new BoardContent(content);
                newContent.setBoardId((int) newBoardId);
                newContent.setUserId(newBoard.getUserId());
                newContent.persist(this);
                if (newContent.getChildBoardId() != 0) {
                    createBoardWithId(newContent.getChildBoardId(), newContent, 0);
                }
            }
            if (copyType == 0) {
                BoardContent tmpContent = new BoardContent(this, iphoneBoardId);
                persistContent.setUrl(tmpContent.getUrl());
                persistContent.setUrl2(tmpContent.getUrl2());
                persistContent.setText(tmpContent.getText());
            }
            persistContent.setChildBoardId((int) newBoardId);
            persistContent.setChildBoardLinkId(0);
            persistContent.persist(this);
        }
    }

    /*
      Insert existing board.
     */
    private void insertExistingBoard(final BoardContent content) {
        final List<BoardRow> list = BoardRow.getAllBoardRows(board);
        final CharSequence[] strings = new CharSequence[list.size()];
        new AlertDialog.Builder(board).setTitle(R.string.select_an_option).setItems(R.array.CopyBoard, (dialogOption, whichOption) -> {
            int i = 0;
            for (BoardRow localBoardRow : list) {
                strings[i++] = localBoardRow.getBoardName();
            }
            new AlertDialog.Builder(board).setTitle(whichOption == 0 ? R.string.copy_existing_board : R.string.link_to_existing_board)
                    .setItems(strings, (dialog, which) -> {
                        GridView gridView = board.findViewById(R.id.mainGrid);
                        RelativeLayout relativeLayout =
                                board.findViewById(R.id.sentenceBarLayout);
                        createBoardWithId(list.get(which).getIPhoneBoardId(),
                                content, whichOption + 1);
                        gridView.invalidateViews();
                        relativeLayout.invalidate();
                    }).create().show();
        }).create().show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
    public final boolean onContextItemSelected(final MenuItem item) {

        GridView gridView = board.findViewById(R.id.mainGrid);
        RelativeLayout relativeLayout = board.findViewById(R.id.sentenceBarLayout);
        int itemId = item.getItemId();
        if (itemId == R.id.textToSpeech) {
            createTextToSpeechSound();
        } else if (itemId == R.id.deleteBoardHotspot) {
            deleteBoard(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.deleteBoard) {
            deleteBoard(selectedItem);
            return false;
        } else if (itemId == R.id.newBoard) {
            newBoard(selectedItem);
            return false;
        } else if (itemId == R.id.newBoardHotspot) {
            newBoard(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.wordVariationBoard) {
            newWordVariantBoard();
            return false;
        } else if (itemId == R.id.codedWordVariationBoard) {
            newCodedWordVariantBoard();
            return false;
        } else if (itemId == R.id.hotspotBoard) {
            final HotspotEditor hotspotEditor = new HotspotEditor(selectedItem, board);
            hotspotEditor.show();
            hotspotEditor.setOnDismissListener(dialog -> {
                if (hotspotEditor.getColumns() > 0) {
                    undoRedo.saveState();
                    BoardRow newBoard =
                            new BoardRow(hotspotEditor.getColumns(), hotspotEditor.getRows(), selectedItem.getUserId(),
                                    selectedItem.getText(), board, BoardRow.sortOrder.NotSorted,
                                    BoardRow.sortOrder.NotSorted, BoardRow.sortOrder.NotSorted);
                    selectedItem.setType(1);
                    int newId = (int) newBoard.persist(true);
                    selectedItem.setChildBoardId(newId);
                    selectedItem.setHotspotStyle(1);
                    selectedItem.persist(board);
                    mainGrid.invalidateViews();
                    sentenceBarLayout.invalidate();
                    Utility.alert(getString(R.string.done_), board);
                }
            });
            return false;
        } else if (itemId == R.id.insertExistingBoard) {
            insertExistingBoard(selectedItem);
            return false;
        } else if (itemId == R.id.insertExistingBoardHotspot) {
            insertExistingBoard(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.addRow) {
            undoRedo.saveState();
            boardRow.addRow();
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.addColumn) {
            undoRedo.saveState();
            boardRow.addColumn();
            gridView.setNumColumns(boardRow.getColumns());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.deleteLastRow) {
            undoRedo.saveState();
            boardRow.deleteRow();
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.deleteRightColumn) {
            undoRedo.saveState();
            boardRow.deleteColumn();
            gridView.setNumColumns(boardRow.getColumns());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.compressColumns) {
            undoRedo.saveState();
            boardRow.compress();
            gridView.setNumColumns(boardRow.getColumns());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.stretchColumns) {
            undoRedo.saveState();
            boardRow.stretch();
            gridView.setNumColumns(boardRow.getColumns());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.pasteSound) {
            if (bufferItem != null) {
                undoRedo.saveState();
                selectedItem.setType(1);
                selectedItem.setUrl2(bufferItem.getUrl2());
                selectedItem.persist(getApplicationContext());
                gridView.invalidateViews();
                relativeLayout.invalidate();
            }
            return false;
        } else if (itemId == R.id.pasteSoundHotspot) {
            if (bufferItem != null) {
                undoRedo.saveState();
                selectedHotspotItem.setType(1);
                selectedHotspotItem.setUrl2(bufferItem.getUrl2());
                selectedHotspotItem.persist(getApplicationContext());
                gridView.invalidateViews();
                relativeLayout.invalidate();
            }
            return false;
        } else if (itemId == R.id.deleteSound) {
            undoRedo.saveState();
            selectedItem.setType(1);
            selectedItem.setUrl2("");
            selectedItem.persist(getApplicationContext());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.deleteSoundHotspot) {
            undoRedo.saveState();
            selectedHotspotItem.setType(1);
            selectedHotspotItem.setUrl2("");
            selectedHotspotItem.persist(getApplicationContext());
            gridView.invalidateViews();
            relativeLayout.invalidate();
            return false;
        } else if (itemId == R.id.recordSound) {
            if (requestAppPermission(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.recordSound)) {
                recordSound(selectedItem);
            }
            return false;
        } else if (itemId == R.id.recordSoundHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.recordSoundHotspot)) {
                recordSound(selectedHotspotItem);
            }
            return false;
        } else if (itemId == R.id.soundFromLibraryHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.soundFromLibraryHotspot)) {
                search(SearchRequestType.LIBRARY_SOUND, selectedHotspotItem);
            }
            return false;
        } else if (itemId == R.id.soundFromLibrary) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.soundFromLibrary)) {
                search(SearchRequestType.LIBRARY_SOUND, selectedItem);
            }
            return false;
        } else if (itemId == R.id.rotateImageHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.rotateImageHotspot)) {
                rotateImageFile(selectedHotspotItem);
            }
            return false;
        } else if (itemId == R.id.rotateImage) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.rotateImage)) {
                rotateImageFile(selectedItem);
            }
            return false;
        } else if (itemId == R.id.editImage) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.editImage)) {
                editImage(selectedItem, RequestCode.PHOTO_EDITED);
            }
            return false;
        } else if (itemId == R.id.editImageHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.editImageHotspot)) {
                editImage(selectedHotspotItem, RequestCode.PHOTO_EDITED_HOTSPOT);
            }
            return false;
        } else if (itemId == R.id.pasteImageHotspot) {
            pasteImage(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.pasteImage) {
            pasteImage(selectedItem);
            return false;
        } else if (itemId == R.id.textProperties) {
            updateOtherProperties(selectedItem);
            return false;
        } else if (itemId == R.id.textPropertiesHotspot) {
            updateOtherProperties(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.clearCell) {
            clearCell(selectedItem);
            return false;
//        } else if (itemId == R.id.sendCellToGoogleDrive) {
//            sendCellToGoogleDrive(selectedItem);
//            return false;
        } else if (itemId == R.id.clearCellHotspot) {
            clearCell(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.pasteCell) {
            pasteCell(selectedItem);
            return false;
        } else if (itemId == R.id.pasteCellHotspot) {
            pasteCell(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.copyCellHotspot) {
            bufferItem = selectedHotspotItem;
            return false;
        } else if (itemId == R.id.copyCell) {
            bufferItem = selectedItem;
            return false;
        } else if (itemId == R.id.homeCell) {
            makeHomeCell();
            return false;
        } else if (itemId == R.id.backCell) {
            makeBackCell();
            return false;
        } else if (itemId == R.id.deleteImage) {
            deleteImage(selectedItem);
            return false;
        } else if (itemId == R.id.deleteImageHotspot) {
            deleteImage(selectedHotspotItem);
            return false;
        } else if (itemId == R.id.webPageHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.webPageHotspot)) {
                captureWebPage(RequestCode.WEB_PAGE_CAPTURE_HOTSPOT);
            }
            return false;
        } else if (itemId == R.id.webPage) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.webPage)) {
                captureWebPage(RequestCode.WEB_PAGE_CAPTURE);
            }
            return false;
        } else if (itemId == R.id.fromInternetImage) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.fromInternetImage)) {
                search(SearchRequestType.WEB_IMAGE, selectedItem);
            }
            return false;
        } else if (itemId == R.id.fromInternetImageHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.fromInternetImageHotspot)) {
                search(SearchRequestType.WEB_IMAGE_HOTSPOT, selectedHotspotItem);
            }
            return false;
        } else if (itemId == R.id.imagesFromLibrary) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.imagesFromLibrary)) {
//                    onSearchRequested();
                search(SearchRequestType.LIBRARY_IMAGE, selectedItem);
            }
            return false;
        } else if (itemId == R.id.imagesFromLibraryHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.imagesFromLibraryHotspot)) {
                search(SearchRequestType.LIBRARY_IMAGE_HOTSPOT, selectedItem);
            }
            return false;
        } else if (itemId == R.id.videosFromLibrary) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.videosFromLibrary)) {
                search(SearchRequestType.LIBRARY_VIDEO, selectedItem);
            }
            return false;
        } else if (itemId == R.id.videosFromLibraryHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.videosFromLibraryHotspot)) {
                search(SearchRequestType.LIBRARY_VIDEO_HOTSPOT, selectedItem);
            }
            return false;
        } else if (itemId == R.id.boardsFromGoogleDrive) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.boardsFromLibrary)) {
                search(SearchRequestType.GOOGLE_DRIVE_BOARD, selectedItem);
            }
        } else if (itemId == R.id.boardsFromLibraryHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.boardsFromLibraryHotspot)) {
                search(SearchRequestType.LIBRARY_BOARD_HOTSPOT, selectedItem);
            }
        } else if (itemId == R.id.boardsFromGoogleDriveHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.boardsFromLibrary)) {
                search(SearchRequestType.GOOGLE_DRIVE_BOARD_HOTSPOT, selectedItem);
            }
        } else if (itemId == R.id.savedImages) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.savedImages)) {
                selectSavedImage(RequestCode.SELECT_PHOTO);
            }
            return false;
        } else if (itemId == R.id.savedImagesHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.savedImagesHotspot)) {
                selectSavedImage(RequestCode.SELECT_PHOTO_HOTSPOT);
            }
            return false;
        } else if (itemId == R.id.videoCamera) {
            if (requestAppPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.videoCamera)) {
                recordVideo(RequestCode.VIDEO_RECORDER);
            }
            return false;
        } else if (itemId == R.id.videoCameraHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.videoCameraHotspot)) {
                recordVideo(RequestCode.VIDEO_RECORDER_HOTSPOT);
            }
            return false;
        } else if (itemId == R.id.camera) {
            if (requestAppPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.camera)) {
                getImageFromCamera(RequestCode.CAMERA_PIC);
            }
        } else if (itemId == R.id.cameraHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.cameraHotspot)) {
                getImageFromCamera(RequestCode.CAMERA_PIC_HOTSPOT);
            }
        } else if (itemId == R.id.boardSort) {
            sortBoard();
            return false;
        } else if (itemId == R.id.addSchedule) {
            if (selectedItem.getChildBoardId() == 0) {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.alert)
                        .setMessage("You must have a child board to create a schedule.")
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).show();
                return false;
            }
            if (selectedItem.getExternalUrl().length() > 0 && !selectedItem.getExternalUrl().startsWith("mtschedule:/")) {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.alert)
                        .setMessage("App link is already in use. Delete app link first to add a schedule.")
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).show();
                return false;
            }
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_CALENDAR}, R.id.addSchedule)) {
                final Schedule schedule = new Schedule(this);
                schedule.setCancelable(true);
                schedule.setCanceledOnTouchOutside(true);
                schedule.setOnDismissListener(dialog -> {
                    if (schedule.getCommand().length() > 0)
                        undoRedo.saveState();
                    selectedItem.setExternalUrl(schedule.getCommand());
                    selectedItem.persist(board);
                    CalendarHelper ch = new CalendarHelper(board, "MyTalk", "MyTalk", "support@mytalk.zendesk.com");
                    ch.UpdateAllEvents();
                });
                schedule.show();
                if (selectedItem.getExternalUrl().startsWith("mtschedule:/")) {
                    schedule.updateWidgetsFromCommand(selectedItem.getExternalUrl());
                }
            }
            return false;
        } else if (itemId == R.id.deleteSchedule) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_CALENDAR}, R.id.deleteSchedule)) {
                selectedItem.setExternalUrl("");
                selectedItem.persist(board);
                CalendarHelper ch = new CalendarHelper(board, "MyTalk", "MyTalk", "support@mytalk.zendesk.com");
                ch.UpdateAllEvents();
            }
            return false;
        } else if (itemId == R.id.viewSchedules) {
            Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.SCHEDULED_BOARD);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.recents));
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
            return false;
        } else if (itemId == R.id.addLocation) {
            if (selectedItem.getChildBoardId() == 0) {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.alert)
                        .setMessage("You must have a child board to create a location.")
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).show();
                return false;
            }
            if (selectedItem.getExternalUrl().length() > 0 && !selectedItem.getExternalUrl().startsWith("mtgeo:/")) {
                new AlertDialog.Builder(this).setIcon(R.drawable.ic_dialog_alert).setTitle(R.string.alert)
                        .setMessage("App link is already in use. Delete app link first to add a location.")
                        .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss()).show();
                return false;
            }
            if (requestAppPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, R.id.addLocation)) {
                Intent mapIntent = new Intent(this, MapsActivity.class);
                mapIntent.putExtra("externalUrl", selectedItem.getExternalUrl());
                startActivityForResult(mapIntent, RequestCode.ADD_LOCATION_RESULT.ordinal());
            }
            return false;
        } else if (itemId == R.id.deleteLocation) {
            selectedItem.setExternalUrl("");
            selectedItem.persist(board);
            updateLocation();
            return false;
        } else if (itemId == R.id.viewLocations) {
            return false;
        } else if (itemId == R.id.googleDriveImage) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.googleDriveImage)) {
                search(SearchRequestType.GOOGLE_DRIVE, selectedItem);
            }
        } else if (itemId == R.id.googleDriveSound) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.googleDriveImage)) {
                search(SearchRequestType.GOOGLE_DRIVE_SOUND, selectedItem);
            }
        } else if (itemId == R.id.googleDriveImageHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.googleDriveImageHotspot)) {
                search(SearchRequestType.GOOGLE_DRIVE_HOTSPOT, selectedItem);
            }
        } else if (itemId == R.id.googleDriveSoundHotspot) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.googleDriveImageHotspot)) {
                search(SearchRequestType.GOOGLE_DRIVE_SOUND_HOTSPOT, selectedItem);
            }
        }
        return true;
    }

    /*
      Select saved image.
     */
    private void selectSavedImage(RequestCode code) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType(PHOTO_PICKER_IMAGE);
        startActivityForResult(photoPickerIntent, code.ordinal());
    }

    /*
      Paste image.
     */
    private void pasteImage(BoardContent content) {
        if (bufferItem != null) {
            undoRedo.saveState();
            content.setType(1);
            content.setUrl(bufferItem.getUrl());
            content.persist(getApplicationContext());
            mainGrid.invalidateViews();
            sentenceBarLayout.invalidate();
        }
    }

    /*
      clear cell.
     */
    private void clearCell(BoardContent content) {
        undoRedo.saveState();
        content.clearMedia();
        content.persist(getApplicationContext());
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    /*
      Paste cell.
     */
    private void pasteCell(BoardContent content) {
        if (bufferItem != null) {
            undoRedo.saveState();
            bufferItem.copyMedia(content);
            if (bufferItem.getChildBoardId() != 0) {
                createBoardWithId(bufferItem.getChildBoardId(), content, 0);
            }
            content.persist(getApplicationContext());
            mainGrid.invalidateViews();
            sentenceBarLayout.invalidate();
        }
    }

    /*
      Sort board.
     */
    private void sortBoard() {
        //if (this.selectedItem.getChildBoardId() != 0) {
        BoardSort d = new BoardSort(this, this.boardRow);
        d.setOnDismissListener(arg0 -> mainGrid.invalidateViews());
        d.show();
        //}
    }

    /*
      Record video.
     */
    private void recordVideo(RequestCode code) {
        if (Utility.hasCamera(board)) {
            String timeStamp = new SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.US).format(new Date());
            // setup storage and file
            File mediaFile =
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            .getAbsolutePath(), VIDEO_FILENAME_PREFIX + timeStamp
                            + getString(R.string.mpeg_movie_extension));
            // export path
            videoOutputUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", mediaFile);
            // create new Intent
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            // pass uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoOutputUri);
            // limit time
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, MAXIMUM_VIDEO_SECONDS);
            // set the video image quality to high
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            // start the Video Capture Intent
            startActivityForResult(intent, code.ordinal());

        } else {
            Utility.alert(getString(R.string.no_camera_on_this_device), board);
        }
    }

    /*
      Delete image.
     */
    private void deleteImage(BoardContent content) {
        undoRedo.saveState();
        content.setUrl("");
        content.persist(getApplicationContext());
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    /*
      Make back cell.
     */
    private void makeBackCell() {
        undoRedo.saveState();
        selectedItem.clearMedia();
        selectedItem.setType(GO_BACK_COMMAND);
        selectedItem.setText(getString(R.string.go_back));
        selectedItem.persist(getApplicationContext());
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    /*
      Make home cell.
     */
    private void makeHomeCell() {
        undoRedo.saveState();
        selectedItem.clearMedia();
        selectedItem.setType(GO_HOME_COMMAND);
        selectedItem.setText(getString(R.string.to_home));
        selectedItem.persist(getApplicationContext());
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    /*
      Update other properties.
     */
    private void updateOtherProperties(final BoardContent content) {
        TextProperties textProperties = new TextProperties(this, content);
        textProperties.setOnDismissListener(arg0 -> {
            undoRedo.saveState();
            content.persist(getApplicationContext());
            mainGrid.invalidateViews();
            CalendarHelper ch = new CalendarHelper(board, "MyTalk", "MyTalk", "support@mytalk.zendesk.com");
            ch.UpdateAllEvents();
        });
        textProperties.show();
    }

    /*
      Edits the image.
     */
    private void editImage(BoardContent content, RequestCode ignoredCode) {
        String imageString = null;
        String url = content.getUrl();
        if (url.contains("/")) {
            imageString = url.replace(" ", "-").replace("/", "-");
        }
        Uri imageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(Utility.getMyTalkFilesDir(board) + "/" + imageString));
        CropImage.activity(imageUri)
                .start(board);
    }

    /*
      Record sound.
     */
    private void recordSound(BoardContent content) {
        new RecordAudio(this, content).recordSound();
    }

    /*
      Delete board.
     */
    private void deleteBoard(BoardContent content) {
        undoRedo.saveState();
        content.setType(1);
        content.setChildBoardId(0);
        content.setChildBoardLinkId(0);
        content.setHotspotStyle(0);
        content.persist(getApplicationContext());
        mainGrid.invalidateViews();
        sentenceBarLayout.invalidate();
    }

    /*
      Gets the image from camera and returns via Activity stack.
     */
    private void getImageFromCamera(RequestCode code) {
        try {
            if (Utility.hasCamera(board)) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, code.ordinal());
            } else {
                Utility.alert(getString(R.string.no_camera_on_this_device), board);
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.failed_to_capture_an_image_, Toast.LENGTH_LONG).show();
        }
    }

    /*
      Capture web page as an image and return via Activity stack.
     */
    private void captureWebPage(RequestCode code) {
        Intent intent = new Intent(getApplicationContext(), CaptureWebPage.class);
        startActivityForResult(intent, code.ordinal());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        // Get the intent, verify the action and get the query
        if ("com.MTA.MyTalkMobile.Board".equals(intent.getAction())) {

            int boardId = intent.getIntExtra(Board.INTENT_EXTRA_BOARD_ID, 1);
            boolean isEditable = intent.getBooleanExtra(INTENT_EXTRA_IS_EDITABLE, false);
            String boardName = intent.getStringExtra(Board.INTENT_EXTRA_BOARD_NAME);

            Intent localIntent = new Intent(getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, boardId);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME,
                    boardName);
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);

            startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Bundle p = new Bundle();
            p.putInt(Search.SEARCH_REQUEST, rt.ordinal());
            if (selectedItem != null) {
                p.putInt(Search.INTENT_EXTRA_CONTENT_ID, selectedItem.getiPhoneId());
            }
            if (username != null) {
                p.putString(Search.INTENT_EXTRA_USERNAME, username);
            }

            String query = intent.getStringExtra(SearchManager.QUERY);
            // manually launch the real search activity
            final Intent searchIntent = new Intent(getApplicationContext(), Search.class);
            // add query to the Intent Extras
            searchIntent.putExtra(SearchManager.QUERY, query);
            searchIntent.putExtra(SearchManager.APP_DATA, p);
            switch (rt) {
                case GOOGLE_DRIVE_BOARD:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE_BOARD.ordinal());
                    break;
                case GOOGLE_DRIVE_BOARD_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE_BOARD_HOTSPOT.ordinal());
                    break;
                case GOOGLE_DRIVE:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE.ordinal());
                    break;
                case GOOGLE_DRIVE_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE_HOTSPOT.ordinal());
                    break;
                case GOOGLE_DRIVE_SOUND:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE_SOUND.ordinal());
                    break;
                case GOOGLE_DRIVE_SOUND_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_GOOGLE_DRIVE_SOUND_HOTSPOT.ordinal());
                    break;
                /* web image search request. */
                case WEB_IMAGE:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_WEB_IMAGE.ordinal());
                    break;
                case WEB_IMAGE_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_WEB_IMAGE_HOTSPOT.ordinal());
                    break;
                /* library image search request. */
                case LIBRARY_IMAGE:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_IMAGE.ordinal());
                    break;
                case LIBRARY_IMAGE_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_IMAGE_HOTSPOT.ordinal());
                    break;
                /* library sound search request. */
                case LIBRARY_SOUND:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_SOUND.ordinal());
                    break;
                case LIBRARY_SOUND_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_SOUND_HOTSPOT.ordinal());
                    break;
                /* library video search request. */
                case LIBRARY_VIDEO_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_VIDEO_HOTSPOT.ordinal());
                    break;
                case LIBRARY_VIDEO:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_VIDEO.ordinal());
                    break;
                /* library board search request. */
                case LIBRARY_BOARD:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_BOARD.ordinal());
                    break;
                case LIBRARY_BOARD_HOTSPOT:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_LIBRARY_BOARD_HOTSPOT.ordinal());
                    break;
                /* library childBoard search request (internal). */
                default:
                    break;
                /* find existing cell search request. */
                case FIND_CELL:
                    startActivityForResult(searchIntent, RequestCode.SELECTED_CELL.ordinal());
                    break;
            }
        }
    }

    private void search(final SearchRequestType requestType, BoardContent content) {
        if (selectedItem == null) return;
        Bundle p = new Bundle();
        rt = requestType;
        p.putInt(Search.SEARCH_REQUEST, requestType.ordinal());
        if (content != null) {
            p.putInt(Search.INTENT_EXTRA_CONTENT_ID, content.getiPhoneId());
        }
        if (username != null) {
            p.putString(Search.INTENT_EXTRA_USERNAME, username);
        }
        String text = selectedItem.getText();
        if (text == null) text = "";
        startSearch(text, false, p, false);

    }

    private View getActionBarView() {

        int actionViewResId;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            actionViewResId = getResources().getIdentifier(
//                    "abs__action_bar_container", "id", getPackageName());
//        } else {
        actionViewResId = Resources.getSystem().getIdentifier(
                "action_bar_container", "id", "android");
//        }
        if (actionViewResId > 0) {
            return this.findViewById(actionViewResId);
        }

        return null;
    }

    /*
      Copy from sample.
     */
    private void copyFromSample() {
        View menuItemView = getActionBarView();
        final ArrayList<JsonUserAccount> samples = new GetSampleNames().execute();
        if (samples == null) return;
        final PopupMenu sampleMenu = new PopupMenu(board, menuItemView);
        for (JsonUserAccount u : samples) {
            sampleMenu.getMenu().add(u.DisplayName);
        }
        sampleMenu.getMenu().add("I will type in a name");
        @SuppressWarnings("MismatchedReadAndWriteOfArray") final String[] selectedSample = {null};
        sampleMenu.setOnMenuItemClickListener(item -> {
            for (JsonUserAccount u : samples) {
                if (u.DisplayName.equals(item.getTitle().toString())) {
                    selectedSample[0] = u.Username;
                    board.startGetNewDatabase(u.Username);
                    return false;
                }
            }

            final AlertDialog.Builder alert =
                    new AlertDialog.Builder(board).setTitle(R.string.enter_sample_account_name);
            final EditText input = new EditText(board);
            alert.setView(input);
            alert.setPositiveButton(R.string.ok, (dialog, whichButton) -> {
                final String tempString = input.getText().toString().trim();
                new AlertDialog.Builder(board).setTitle(R.string.are_you_sure)
                        .setMessage(R.string.overwrites_workspace)
                        .setNegativeButton(R.string.no, (dialog1, which) -> dialog1.cancel()).setPositiveButton(R.string.yes, (dialog12, which) -> new AsyncGetNewDatabase(board, tempString).execute(board)).create().show();
            });
            alert.setNegativeButton(R.string.cancel, (dialog, whichButton) -> dialog.cancel());
            alert.show();

            return false;
        });
        sampleMenu.show();
    }

    /*
      Sign in.

      @return the login
     */
    private Login signIn() {
        Login login = new Login(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (login.getWindow() == null) return null;
        WindowManager.LayoutParams olp = login.getWindow().getAttributes();
        if (olp == null) return null;
        lp.copyFrom(olp);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        login.setOnCancelListener(dialog -> {
            mAdapter = null;
            loggedIn = true;
            SharedPreferences settings =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String oldUser = settings.getString(Board.SYNCED_USERNAME, "");
            String oldDefaultBoard = settings.getString(Board.SYNCED_DEFAULT_BOARD, "");
            Runnable complete = () -> {
                username = settings.getString(Board.USERNAME, "");
                GetUserRoles gur = new GetUserRoles(username);
                gur.executeAsync((roles) -> {
                    if (roles == null) {
                        roles = new ArrayList<>();
                    }
                    Log.d("GUR", roles.toString());
                    if (/* roles != null && roles.contains("Android") */ true) {
                        Editor e = settings.edit();
                        Board.licenses.put(TrialCheck.FULL_LICENSE, BillingDataSource.SkuState.SKU_STATE_PURCHASED);
                        e.putBoolean(AppPreferences.PREF_KEY_FULL_LICENSE, true);
                        e.putBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                        e.putBoolean(AppPreferences.PREF_KEY_TRIAL_LICENSE, false);
                        e.apply();
                    } else {
                        checkLicense();
                    }
                    isEditable = loggedIn && !settings.getBoolean(AppPreferences.PREF_KEY_LIMITED_LICENSE, false);
                    populateMenu(mainMenu);
                    AppPreferences.setByLicense(board);
                    GridView gridView = board.findViewById(R.id.mainGrid);
                    gridView.invalidateViews();
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(this::goHome);
                }, board);
            };
            if (!username.contentEquals(oldUser) || !defaultBoard.contentEquals(oldDefaultBoard)) {
                startGetNewDatabase(complete);
            } else {
                complete.run();
            }

        });
        login.show();
        login.getWindow().setAttributes(lp);
        return login;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.copyFromSample) {
            copyFromSample();
            return true;
        } else if (itemId == R.id.SignIn) {
            if (requestAppPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.CALL_PHONE
            }, R.id.SignIn)) {
                    /*
      The login dialog.
     */
                //Login loginDialog =
                signIn();
            }
            return true;
        } else if (itemId == R.id.merge) {
            if (requestAppPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.merge)) {
                merge();
            }
            return true;
        } else if (itemId == R.id.restoreFromBackup) {
            if (requestAppPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.restoreFromBackup)) {
                restoreFromBackup();
            }
            return true;
        } else if (itemId == R.id.overwriteDevice) {
            if (requestAppPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.overwriteDevice)) {
                overwriteDevice();
            }
            return true;
        } else if (itemId == R.id.overwriteWorkspace) {
            if (requestAppPermission(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.overwriteWorkspace)) {
                overwriteWorkspace();
            }
            return true;
        } else if (itemId == R.id.speakActionButton) {
            boolean phraseMode = sentenceBarLayout.getVisibility() == TextView.VISIBLE;
            typePhrase(phraseMode, null);
            return true;
        } else if (itemId == R.id.HelpMenu) {
            return true;
        } else if (itemId == R.id.buyLicense) {
            purchaseLicense();
            return true;
        } else if (itemId == R.id.buyVoices) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.acapelagroup.android.tts")));
            } catch (ActivityNotFoundException ignore) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.acapelagroup.android.tts")));
            }
            return true;
        } else if (itemId == R.id.sendEmail) {
            sendEmail();
            return true;
        } else if (itemId == R.id.visitWebsite) {
            Uri uri = Uri.parse(getString(R.string.http_www_mytalktools_com));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.tellFriend) {
            tellFriend();
            return true;
        } else if (itemId == R.id.Preferences) {
            Intent intent11 = new Intent(getApplicationContext(), AppPreferences.class);
            startActivity(intent11);
            return true;
        } else if (itemId == R.id.homeMenu) {
            goHome();
            return true;
        } else if (itemId == R.id.authorDone) {
            loggedIn = false;
            isEditable = false;
            updateMenu();
            mainGrid.invalidateViews();
            return true;
        } else if (itemId == R.id.editMode) {
            menuItem.setCheckable(true);
            menuItem.setChecked(!menuItem.isChecked());
            isEditable = menuItem.isChecked();
            return true;
        } else if (itemId == R.id.undo) {
            undo();
            return true;
        } else if (itemId == R.id.redo) {
            redo();
            return true;
        } else if (itemId == R.id.findCell) {
            search(SearchRequestType.FIND_CELL, selectedItem);
            return true;
        } else if (itemId == R.id.mostUsed) {
            Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.MOST_USED_BOARD);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.most_used));
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
            return true;
        } else if (itemId == R.id.recents) {
            Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.MOST_RECENTS_BOARD);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.recents));
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
            return true;
        } else if (itemId == R.id.scheduled) {
            Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.SCHEDULED_BOARD);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.scheduled));
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
            return true;
        } else if (itemId == R.id.geoBoards) {
            Intent localIntent = new Intent(this.getApplicationContext(), Board.class);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.LOCATIONS_BOARD);
            localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, getString(R.string.locations));
            localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
            this.startActivityForResult(localIntent, RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                    null);
            return true;
        } else if (itemId == R.id.shareBoard) {
            if (requestAppPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, R.id.shareBoard)) {
                Bitmap bitmap = getBitmapFromView(mainGrid);
                Intent localIntent = new Intent(Intent.ACTION_SEND);
                localIntent.setType("image/jpeg");
                String path;
                path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "MyTalkTools", null);
                Uri screenshotUri = Uri.parse(path);
                localIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                shareActionProvider.setShareIntent(localIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /*
      Redo.
     */
    private void redo() {
        undoRedo.redo();
        database = new Database(this);
        resetGrid(false);
        GridView gridView = board.findViewById(R.id.mainGrid);
        RelativeLayout relativeLayout = board.findViewById(R.id.sentenceBarLayout);
        gridView.invalidateViews();
        relativeLayout.invalidate();
    }

    /*
      Undo.
     */
    private void undo() {
        undoRedo.undo();
        database = new Database(this);
        resetGrid(false);
        GridView gridView = board.findViewById(R.id.mainGrid);
        RelativeLayout relativeLayout = board.findViewById(R.id.sentenceBarLayout);
        gridView.invalidateViews();
        relativeLayout.invalidate();
    }

    /*
      Go home.
     */
    private void goHome() {
        Intent localIntent1 = new Intent(getApplicationContext(), Board.class);
        localIntent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(localIntent1);
        this.finish();
    }

    /*
      Tell friend.
     */
    private void tellFriend() {
        Intent emailIntent1 = new Intent(Intent.ACTION_SEND);
        emailIntent1.putExtra(Intent.EXTRA_SUBJECT, R.string.mytalkmobile_);
        emailIntent1.setType(MIME_TYPE_PLAIN_TEXT);
        emailIntent1.putExtra(Intent.EXTRA_TEXT,
                R.string.hey_check_out_this_cool_new_app_);
        startActivity(emailIntent1);
    }

    /*
      Send email.
     */
    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{getString(R.string.support_mytalk_zendesk_com)});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.support_);
        emailIntent.setType(MIME_TYPE_PLAIN_TEXT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, R.string.hi_i_m_having_trouble_with_);
        startActivity(emailIntent);
    }

    /*
      Purchase license.
     */
    private void purchaseLicense() {
        trialCheck = new TrialCheck(this);
        trialCheck.solveLicenseIssue(dateIsAfter);
    }

    /*
      Overwrite workspace.
     */
    private void overwriteWorkspace() {
        new AlertDialog.Builder(board).setTitle(R.string.confirmation)
                .setMessage(R.string.will_overwrite_workspace)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    startOverwriteServerData();
                    dialog.dismiss();
                }).setNegativeButton(R.string.no, null).create().show();
    }

    /*
      Overwrite device.
     */
    private void overwriteDevice() {
        new AlertDialog.Builder(board).setTitle(R.string.confirmation)
                .setMessage(R.string.sure_overwrite_device_with_workspace)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    startGetNewDatabase();
                    dialog.dismiss();
                }).setNegativeButton(R.string.no, null).create().show();
    }

    /*
      Restore from backup.
     */
    private void restoreFromBackup() {
        RestoreFromBackupDialog dialog = new RestoreFromBackupDialog(this, username, defaultBoard);
        dialog.setOnCancelListener(dialog1 -> {
            if (restoreBackupFilename == null) {
                return;
            }
            final String message =
                    getString(R.string.restore_your_device)
                            + String.format("\n%tc", restoreBackupFilename.CreationTimeUtc);
            new AlertDialog.Builder(board).setTitle(R.string.are_you_sure).setMessage(message)
                    .setNegativeButton(R.string.no, (dialog11, which) -> dialog11.cancel()).setPositiveButton(R.string.yes, (dialog112, which) -> new AsyncOverwriteServerDataByFile(board, restoreBackupFilename.Name)
                            .execute(board)).create().show();
        });
        dialog.show();
    }

    /*
      Merge.
     */
    private void merge() {
        new AlertDialog.Builder(board).setTitle(R.string.confirmation)
                .setMessage(R.string.sure_merge_device_workspace)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    startMergeData();
                    dialog.dismiss();
                }).setNegativeButton(R.string.no, null).create().show();
    }

    /*
      Start get new database.
     */
    private void startGetNewDatabase() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(Board.USERNAME, "");
        password = settings.getString(Board.PASSWORD, "");
        if (username == null || username.length() == 0 || password.length() == 0) {
            new AlertDialog.Builder(board).setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel()).setMessage(R.string.have_an_account).setIcon(R.drawable.ic_dialog_alert)
                    .setTitle(R.string.sorry).create().show();
            return;
        }
        new AsyncGetNewDatabase(this).execute(this);
    }

    /* Getters & Setters */

    private void startGetNewDatabase(Runnable runnable) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(Board.USERNAME, "");
        password = settings.getString(Board.PASSWORD, "");
        if (username == null || username.length() == 0 || password.length() == 0) {
            new AlertDialog.Builder(board).setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel()).setMessage(R.string.have_an_account).setIcon(R.drawable.ic_dialog_alert)
                    .setTitle(R.string.sorry).create().show();
            return;
        }
        AsyncGetNewDatabase adb = new AsyncGetNewDatabase(this);
        adb.runnable = runnable;
        adb.execute(this);
    }

    public final void startGetNewDatabase(String account) {
        new AsyncGetNewDatabase(board, account).execute(board);
    }

    /*
      Start merge data.
     */
    private void startMergeData() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(Board.USERNAME, "");
        password = settings.getString(Board.PASSWORD, "");
        if (username == null || username.length() == 0 || password.length() == 0) {
            new AlertDialog.Builder(board).setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel()).setMessage(R.string.have_an_account).setIcon(R.drawable.ic_dialog_alert)
                    .setTitle(R.string.sorry).create().show();
            return;
        }
        new AsyncMergeData(this).execute(this);
    }

    /*
      Start overwrite server data.
     */
    private void startOverwriteServerData() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = settings.getString(Board.USERNAME, "");
        password = settings.getString(Board.PASSWORD, "");
        if (username == null || username.length() == 0 || password.length() == 0) {
            new AlertDialog.Builder(board).setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel()).setMessage(R.string.have_an_account).setIcon(R.drawable.ic_dialog_alert)
                    .setTitle(R.string.sorry).create().show();
            return;
        }
        new AsyncOverwriteServerData(this).execute(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#
     * onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
     */
    @Override
    public final void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,
                                                final String key) {
        if (key.contentEquals(AppPreferences.PREF_KEY_MAXIMUM_ROWS)
                || key.contentEquals(AppPreferences.PREF_KEY_COLOR_SCHEME)
                || key.contentEquals(AppPreferences.PREF_KEY_DEFAULT_FONT_SIZE)) {
            GridView gridView = board.findViewById(R.id.mainGrid);
            RelativeLayout relativeLayout = board.findViewById(R.id.sentenceBarLayout);
            gridView.invalidateViews();
            relativeLayout.invalidate();
        }
        if (key.contentEquals("primaryTTS")) {
            SharedPreferences settings1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            primaryVoice = settings1.getString("primaryTTS", "eng-usa");
            tts = null;
            ttsSecondary = null;
            ttsCheckResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS);
        }
        if (key.contentEquals("secondaryTTS")) {
            SharedPreferences settings1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            secondaryVoice = settings1.getString("secondaryTTS", "eng-usa");
            tts = null;
            ttsSecondary = null;
            ttsCheckResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS);
        }
        if (key.contentEquals("ttsEngine")) {
            SharedPreferences settings1 =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Editor e = settings1.edit();
            primaryVoice = "";
            secondaryVoice = "";
            e.putString("primaryTTS", "");
            e.putString("secondaryTTS", "");
            e.apply();
            ttsCheckResult(TextToSpeech.Engine.CHECK_VOICE_DATA_PASS);
        }
    }

    public final void setSelectedHotspotItem(final BoardContent value) {
        selectedHotspotItem = value;
    }

    public final BoardContent getSelectedItem() {
        return selectedItem;
    }

    /*
      Sets the selected item.

      @param value the new selected item
     */
    public final void setSelectedItem(final BoardContent value) {
        selectedItem = value;
    }

    /*
      Gets the database used to create the Board.

      @return the database object.
     */
    public final Database getDatabase() {
        return this.database;
    }

    /*
      getSharePhraseString computes the global class String sharePhraseString. from the
      sentenceBarPhrase iterator.

      @return the share phrase string
     */
    private String getSharePhraseString() {
        String s = "";
        for (BoardContent bc : Board.sentenceBarPhrase) {
            if (s.length() > 0) {
                s = s.concat(" ");
            }
            s = s.concat(bc.getText());
        }
        return s;
    }

    /*
      Reset login to previous.
     */
    public final void resetLoginToPrevious() {
        // reset username back to old one
        Editor editor =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editor.putString(Board.USERNAME, sharedPreferences.getString(Board.SYNCED_USERNAME, ""));
        editor.putString(Board.PASSWORD, sharedPreferences.getString(Board.SYNCED_PASSWORD, ""));
        editor.putString(Board.DEFAULT_BOARD,
                sharedPreferences.getString(Board.SYNCED_DEFAULT_BOARD, ""));
        editor.apply();
        Board.setUsername(sharedPreferences.getString(Board.USERNAME, ""));
        Board.setPassword(sharedPreferences.getString(Board.PASSWORD, ""));
        Board.setDefaultBoard(sharedPreferences.getString(Board.DEFAULT_BOARD, ""));
    }

    private void copyFileFromInternet(String in, String out, Runnable postEvent) {
        final DownloadTask downloadTask = new DownloadTask(this, out, postEvent);
        downloadTask.execute(in);
    }

    @Override
    public void onConnected(Bundle bundle) {
        GoogleApiConnected = true;
        GoogleApiNotAvailable = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        GoogleApiConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        GoogleApiNotAvailable = true;
        if (!result.hasResolution()) {
            // show the localized error dialog.
            Dialog connectionFailed = GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0);
            if (connectionFailed != null) {
                connectionFailed.show();
            }
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, RequestCode.REQUEST_CODE_RESOLUTION.ordinal());
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.d("result", status.toString());
    }

    /* End Getters & Setters */

    private void getEngines() {

        final Intent ttsIntent = new Intent();
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        final PackageManager pm = getPackageManager();

        final List<ResolveInfo> list = pm.queryIntentActivities(ttsIntent, PackageManager.GET_META_DATA);

        containerVEArray = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++) {

            final ContainerVoiceEngine cve = new ContainerVoiceEngine();

            cve.setLabel(list.get(i).loadLabel(pm).toString());
            cve.setPackageName(list.get(i).activityInfo.applicationInfo.packageName);

            final Intent getIntent = new Intent();
            getIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

            getIntent.setPackage(cve.getPackageName());
            getIntent.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
            getIntent.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES);

            cve.setIntent(getIntent);

            containerVEArray.add(cve);
        }

        Log.d("TAG", "containerVEArray: " + containerVEArray.size());

        for (int i = 0; i < containerVEArray.size(); i++) {
            startActivityForResult(containerVEArray.get(i).getIntent(), 10000 + i);
        }
    }


    public enum SwitchScenario {
        _2_BUTTONS_WITHOUT_AUTO_SCANNING,
        _2_BUTTONS_WITH_AUTO_SCANNING,
        _2_BUTTONS_WITH_AUTO_SCANNING_AND_AUTO_START,
        _1_BUTTON_WITHOUT_AUTO_SCANNING,
        _1_BUTTON_WITH_AUTO_SCANNING,
        _1_BUTTON_WITH_AUTO_SCANNING_AND_AUTO_START,
        _NO_SWITCH
    }

    /*
      These are the request codes used to identify return values from child activities called from
      Board.
     */
    public enum RequestCode {

        /**
         * An editing image is being returned.
         */
        PHOTO_EDITED,
        /**
         * A user drawing is being returned.
         */
        SCRIBE_EDITED,
        /**
         * A saved image is being returned.
         */
        SELECT_PHOTO,
        /**
         * Information about available TTS resources is being returned.
         */
        MY_DATA_CHECK_CODE,
        /**
         * The camera is returning an image.
         */
        CAMERA_PIC,
        /**
         * The video recorder is returning a movie.
         */
        VIDEO_RECORDER,
        /**
         * A web page image is being returned.
         */
        WEB_PAGE_CAPTURE,
        /**
         * The user is returned from a child board.
         */
        CHILD_BOARD_NAVIGATE,
        /**
         * Return from license purchase activity.
         */
        LICENSE_CHECK,
        /**
         * The selected video.
         */
        SELECTED_LIBRARY_VIDEO,
        /**
         * The selected web image.
         */
        SELECTED_WEB_IMAGE,
        SELECTED_GOOGLE_DRIVE,
        SELECTED_GOOGLE_DRIVE_SOUND,
        SELECTED_GOOGLE_DRIVE_SOUND_HOTSPOT,
        SELECTED_GOOGLE_DRIVE_BOARD,
        SELECTED_GOOGLE_DRIVE_BOARD_HOTSPOT,
        /**
         * The selected library image.
         */
        SELECTED_LIBRARY_IMAGE,
        /**
         * The selected library sound.
         */
        SELECTED_LIBRARY_SOUND,
        /**
         * The selected library board.
         */
        SELECTED_LIBRARY_BOARD,
        /**
         * The selected cell.
         */
        SELECTED_CELL,

        ADD_LOCATION_RESULT,

        SELECTED_LIBRARY_SOUND_HOTSPOT,
        PHOTO_EDITED_HOTSPOT,
        SCRIBE_EDITED_HOTSPOT,
        WEB_PAGE_CAPTURE_HOTSPOT,
        SELECTED_WEB_IMAGE_HOTSPOT,
        SELECTED_LIBRARY_IMAGE_HOTSPOT,
        SELECTED_LIBRARY_VIDEO_HOTSPOT,
        SELECTED_LIBRARY_BOARD_HOTSPOT,
        SELECT_PHOTO_HOTSPOT,
        VIDEO_RECORDER_HOTSPOT,
        CAMERA_PIC_HOTSPOT,
        MESSENGER_SENT,
        REQUEST_CODE_RESOLUTION,
        REQUEST_ACCOUNT_PICKER,
        REQUEST_AUTHORIZATION,
        REQUEST_GOOGLE_PLAY_SERVICES,
        SELECTED_GOOGLE_DRIVE_HOTSPOT,
        REQUEST_PERMISSION_GET_ACCOUNTS,
        RESULT_PICK_CONTACT
    }

    static class zzz extends AsyncTask<Void, Void, List<BoardDirectoryItem>> {

        final WeakReference<Board> board;

        zzz(Board board) {
            this.board = new WeakReference<>(board);
        }

        @Override
        protected List<BoardDirectoryItem> doInBackground(Void... voids) {
            return getDummyContents(board.get());
        }

        @Override
        protected void onPostExecute(List<BoardDirectoryItem> result) {
            for (BoardDirectoryItem bdi : result) {
                mAdapter.add(bdi);
            }
        }
    }

    /*
      The listener interface for receiving sentenceBarPlayClick events. The class that is interested
      in processing a sentenceBarPlayClick event implements this interface, and the object created
      with that class is registered with a component using the component's
      <code>addSentenceBarPlayClickListener</code> method. When the sentenceBarPlayClick event
      occurs, that object's appropriate method is invoked.
     */
    private final class SentenceBarPlayClickListener implements View.OnClickListener {

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        public void onClick(final View v) {
            mediaPlayer = null;
            Board.getSentenceBarQueue().clear();
            for (BoardContent bc : Board.sentenceBarPhrase) {
                Board.getSentenceBarQueue().add(bc);
            }
            cellCount = 0;
            stopTimer();
            playNextWord(Board.this::startTimer);
        }
    }

    /*
      The listener interface for receiving sentenceBarDeleteClick events. The class that is
      interested in processing a sentenceBarDeleteClick event implements this interface, and the
      object created with that class is registered with a component using the component's
      <code>addSentenceBarDeleteClickListener</code> method. When the sentenceBarDeleteClick event
      occurs, that object's appropriate method is invoked.
     */
    private final class SentenceBarDeleteClickListener implements View.OnClickListener {

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        public void onClick(final View v) {
            long sentenceBarDeleteMsNew = System.currentTimeMillis();
            if (Math.abs(Board.sentenceBarDeleteMs - sentenceBarDeleteMsNew) < PHRASE_BAR_LONG_CLICK_TIME) {
                Board.sentenceBarPhrase.clear();
            } else if (Board.sentenceBarPhrase.size() > 0) {
                Board.sentenceBarPhrase.remove(Board.sentenceBarPhrase.size() - 1);
            }
            Board.sentenceBarDeleteMs = sentenceBarDeleteMsNew;
            drawSentenceBarPost(DO_NOT_HIGHLIGHT);
            strip.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        }
    }
}
