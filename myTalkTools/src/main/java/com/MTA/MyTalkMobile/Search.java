/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.MTA.MyTalkMobile.Json.BoardSearchResult;
import com.MTA.MyTalkMobile.Json.ChildBoardSearchResult;
import com.MTA.MyTalkMobile.Json.LibrarySearchResult;
import com.MTA.MyTalkMobile.Searching.BoardResultAdapter;
import com.MTA.MyTalkMobile.Searching.CellResultAdapter;
import com.MTA.MyTalkMobile.Searching.ImageResultAdapter;
import com.MTA.MyTalkMobile.Searching.SoundResultAdapter;
import com.MTA.MyTalkMobile.Searching.VideoResultAdapter;
import com.MTA.MyTalkMobile.Server.GoogleDrive;
import com.MTA.MyTalkMobile.Server.LibrarySearchRequest;
import com.MTA.MyTalkMobile.Utilities.PromptDialog;
import com.MTA.MyTalkMobile.Utilities.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
//import proguard.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class Search.
 */
//@Keep
public class Search extends Activity implements EasyPermissions.PermissionCallbacks {

    /**
     * The Constant SEARCH_REQUEST.
     */
    public static final String SEARCH_REQUEST = "SearchRequest";
    /**
     * The Constant INTENT_EXTRA_USERNAME.
     */
    public static final String INTENT_EXTRA_USERNAME = "username";
    /**
     * The Constant INTENT_EXTRA_CONTENT_ID.
     */
    public static final String INTENT_EXTRA_CONTENT_ID = "contentId";
    /**
     * The compression ratio used when saving JPEGs.
     */
    private static final int JPEG_COMPRESSION_RATIO = 70;
    /**
     * The Constant REQUEST_SIZE.
     */
    private static final int REQUEST_SIZE = 100;
    /**
     * The Constant MAX_RESULTS.
     */
    private static final int MAX_RESULTS = 100;
    /**
     * The Constant SUBMISSIONS.
     */
    private static final String SUBMISSIONS = "Submissions";
    /**
     * The Constant PUBLIC.
     */
    private static final String PUBLIC = "Public";
    /**
     * The Constant PUBLIC.
     */
    private static final String SYMBOLS = "Symbols";
    /**
     * The Constant SPACE_ESCAPE.
     */
    private static final String SPACE_ESCAPE = "%20";
    /**
     * The Constant DOT_JPG.
     */
    private static final String DOT_JPG = ".jpg";
    /**
     * The max results.
     */
    private static final Integer maxResults = MAX_RESULTS;
    /**
     * The query.
     */
    private static String query = null;
    /**
     * The username.
     */
    private static String username = null;
    /**
     * The library.
     */
    private static String library = null;
    /**
     * The query type.
     */
    private static SearchRequestType queryType = SearchRequestType.UNKNOWN;
    private static int checkedRadioButtonId;
    /**
     * The list results.
     */
    private ListView listResults;
    /**
     * The ContactAdapter.
     */
    private ArrayAdapter<JSONObject> adapter;
    /**
     * The sound ContactAdapter.
     */
    private SoundResultAdapter soundAdapter;
    /**
     * The video ContactAdapter.
     */
    private VideoResultAdapter videoAdapter;
    /**
     * The board ContactAdapter.
     */
    private BoardResultAdapter boardAdapter;
    /**
     * The search.
     */
    private Search search;
    /**
     * The content id.
     */
    private int contentId;
    /**
     * The content.
     */
    private BoardContent content;
    /**
     * The selected image.
     */
    private JSONObject selectedImage;
    private JSONObject selectedSound;
    private JSONObject selectedBoard;
    private GoogleDrive googleDrive;
    private ProgressDialog Dialog;


    private GoogleDrive getGoogleDrive(GoogleDrive.GoogleDriveResults results, String query, String mimeType) {
        if (googleDrive == null) {
            MyTalkApp app = (MyTalkApp) getApplication();
            Board board = app.getBoardContext();
            googleDrive = new GoogleDrive(board, this, results, query, mimeType);
        }
        return googleDrive;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onSearchRequested()
     */
    @Override
    public final boolean onSearchRequested() {
        Bundle p = new Bundle();
        p.putInt(Search.SEARCH_REQUEST, queryType.ordinal());
        p.putInt(Search.INTENT_EXTRA_CONTENT_ID, contentId);
        p.putString(Search.INTENT_EXTRA_USERNAME, username);
        startSearch(null, false, p, false);
        return true;
    }

    private void googleDriveImageOperations(final String fileId, Utility.GetBitmap result) {

        DownloadTask_googleDriveImageOperations d = new DownloadTask_googleDriveImageOperations(selectedImage, googleDrive, result);
        d.fileId = fileId;
        d.execute();
    }

    private void googleDriveSoundOperations(final String fileId, Utility.GetSound result) {

        DownloadTask_googleDriveSoundOperations d = new DownloadTask_googleDriveSoundOperations(selectedSound, googleDrive, result);
        d.fileId = fileId;
        d.execute();
    }

    private void googleDriveBoardOperations(final String fileId, Utility.GetResult result) {

        DownloadTask_googleDriveBoardOperations d = new DownloadTask_googleDriveBoardOperations(selectedBoard, googleDrive, result);
        d.fileId = fileId;
        d.execute();
    }

    /**
     * Save image from internet.
     *
     * @param filename the filename
     */
    private void saveImageFromInternet(final String filename, Runnable runnable) {
        if (filename == null) {
            runnable.run();
        } else try {
            Dialog = ProgressDialog.show(this, null, "Downloading image...", true);
            if (queryType == SearchRequestType.GOOGLE_DRIVE || queryType == SearchRequestType.GOOGLE_DRIVE_HOTSPOT) {
                googleDriveImageOperations(selectedImage.getString("contentUrl"), (bitmap) -> {
                    if (bitmap != null) {
                        File aFile = new File(filename);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_RATIO, bytes);
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
                        Dialog.dismiss();
                    }
                    runnable.run();
                });
            } else {
                Utility.imageOperations(selectedImage.getString("contentUrl"), (bitmap) -> {
                    if (bitmap != null) {
                        File aFile = new File(filename);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_COMPRESSION_RATIO, bytes);
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
                        try {
                            Dialog.dismiss();
                        } catch (Exception ignore) {

                        }
                    }
                    runnable.run();
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveSoundFromInternet(final String filename, Runnable runnable) {
        if (filename == null) {
            runnable.run();
        } else try {
            Dialog = ProgressDialog.show(this, null, "Downloading board...", true);
            if (queryType == SearchRequestType.GOOGLE_DRIVE_SOUND || queryType == SearchRequestType.GOOGLE_DRIVE_SOUND_HOTSPOT) {
                googleDriveSoundOperations(selectedSound.getString("contentUrl"), (bits) -> {
                    if (bits != null) {
                        File aFile = new File(filename);
                        FileOutputStream fo;
                        try {
                            if (aFile.createNewFile()) {
                                fo = new FileOutputStream(aFile);
                                fo.write(bits);
                                fo.flush();
                                fo.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Dialog.dismiss();
                    } catch (Exception ignore) {

                    }
                    runnable.run();
                });
            }  // should not happen

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveBoardFromInternet(Runnable runnable) {
        try {
            Dialog = ProgressDialog.show(this, null, getString(R.string.downloading_sound_file), true);
            if (queryType == SearchRequestType.GOOGLE_DRIVE_BOARD || queryType == SearchRequestType.GOOGLE_DRIVE_BOARD_HOTSPOT) {
                googleDriveBoardOperations(selectedBoard.getString("contentUrl"), (json) -> {
                    if (json != null) {
                        JSONObject j = (JSONObject) json;

                        try {
                            BoardSearchResult br = new BoardSearchResult(j.getJSONObject("Content"));
                            try {
                                Dialog.dismiss();
                            } catch (Exception ignore) {

                            }
                            DoChildBoardLibraryQueryTask task =
                                    new Search.DoChildBoardLibraryQueryTask(content, br, search, username);
                            task.execute("", "", "");
                        } catch (Exception ignore) {

                        }
                    }
                    runnable.run();
                });
            }  // should not happen

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save cell image.
     */
    private void saveCellImage() {
        if (queryType == SearchRequestType.WEB_IMAGE
                || queryType == SearchRequestType.WEB_IMAGE_HOTSPOT
                || queryType == SearchRequestType.GOOGLE_DRIVE
                || queryType == SearchRequestType.GOOGLE_DRIVE_HOTSPOT) {

            PromptDialog dlg =
                    new PromptDialog(this, getString(R.string.enter_name), "", query.replace(" ", "-")) {

                        @Override
                        public boolean onOkClicked(final String paramInput) {
                            String input = Utility.strip(paramInput);
                            String unique =
                                    Utility.makeFilenameUnique(Utility.getMyTalkFilesDir(search).getAbsolutePath()
                                            + "/-" + input + DOT_JPG);
                            final String finalName = new File(unique).getName().replace("-", "/");
                            saveImageFromInternet(unique, () -> new Handler().postDelayed(() -> {
                                Board.getUndoRedo().saveState();
                                content.setType(1);
                                content.setUrl(finalName);
                                content.persist(getApplicationContext());
                                try {
                                    MyTalkApp app = (MyTalkApp) getApplication();
                                    Board board = (Board) app.getActivityContext();
                                    board.resetGrid(false);
                                } catch (Exception error) {
                                    Log.d("d", error.getLocalizedMessage());
                                }
                            }, 500));
                            finish();
                            return false;
                        }
                    };
            dlg.show();
        } else {
            URI media;
            try {
                media = URI.create(selectedImage.getString("contentUrl").replace(" ", SPACE_ESCAPE));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            String root = Utility.getMyTalkFilesDir(this).getAbsolutePath();
            String replaceUploadPath = media.getPath().replace("/dnn/UserUploads/", "").replace("/dnn/useruploads/", "");
            String localFilename =
                    root
                            + "/"
                            + replaceUploadPath
                            .replace("/", "-").replace(" ", "-");
            File file = new File(localFilename);
            Intent intent = new Intent();
            intent.putExtra("in", media.toString());
            intent.putExtra("out", file.getAbsolutePath());
            intent.putExtra("url", replaceUploadPath);
            search.setResult(1, intent);
            search.finish();
            finish();
        }
    }

    private void saveCellSound() {
        if (queryType == SearchRequestType.GOOGLE_DRIVE_SOUND
                || queryType == SearchRequestType.GOOGLE_DRIVE_SOUND_HOTSPOT) {

            PromptDialog dlg =
                    new PromptDialog(this, getString(R.string.enter_name), "", query.replace(" ", "-")) {

                        @Override
                        public boolean onOkClicked(final String paramInput) {
                            String input = Utility.strip(paramInput);
                            String unique =
                                    Utility.makeFilenameUnique(Utility.getMyTalkFilesDir(search).getAbsolutePath()
                                            + "/-" + input + ".mp3");
                            final String finalName = new File(unique).getName().replace("-", "/");
                            saveSoundFromInternet(unique, () -> new Handler().postDelayed(() -> {
                                Board.getUndoRedo().saveState();
                                content.setType(1);
                                content.setUrl2(finalName);
                                content.persist(getApplicationContext());
                                try {
                                    MyTalkApp app = (MyTalkApp) getApplication();
                                    Board board = (Board) app.getActivityContext();
                                    board.resetGrid(false);
                                } catch (Exception ignore) {

                                }
                            }, 500));
                            finish();
                            return false;
                        }
                    };
            dlg.show();
        }  // should not happen

    }

    private void saveCellBoard() {
        if (queryType == SearchRequestType.GOOGLE_DRIVE_BOARD
                || queryType == SearchRequestType.GOOGLE_DRIVE_BOARD_HOTSPOT) {
            saveBoardFromInternet(() -> Log.d("", ""));

        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle appDataTest = intent.getBundleExtra(SearchManager.APP_DATA);
        query = intent.getStringExtra(SearchManager.QUERY);
        if (appDataTest != null) {
            queryType = SearchRequestType.values()[appDataTest.getInt(SEARCH_REQUEST, 0)];
        }
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        switch (queryType) {
            case FIND_CELL:
                findCell();
                break;
            case LIBRARY_SOUND_HOTSPOT:
            case LIBRARY_SOUND:
                librarySound(intent);
                break;
            case LIBRARY_VIDEO_HOTSPOT:
            case LIBRARY_VIDEO:
                libraryVideo(intent);
                break;
            case LIBRARY_BOARD_HOTSPOT:
            case LIBRARY_BOARD:
                libraryBoard(intent);
                break;
            case GOOGLE_DRIVE:
            case GOOGLE_DRIVE_HOTSPOT:
                GoogleDriveImage(intent);
                break;
            case GOOGLE_DRIVE_SOUND:
            case GOOGLE_DRIVE_SOUND_HOTSPOT:
                GoogleDriveSound(intent);
                break;
            case GOOGLE_DRIVE_BOARD:
            case GOOGLE_DRIVE_BOARD_HOTSPOT:
                GoogleDriveBoard(intent);
                break;
            default:
                webOrLibraryImage(intent);
                break;
        }
    }

    private void GoogleDriveImage(final Intent intent) {
        setTitle(R.string.select_an_image);
        setContentView(R.layout.search);
        setListResults(findViewById(R.id.searchList));
        RadioGroup filterWebGroup = findViewById(R.id.filterWebGroup);
        RadioGroup filterLibraryGroup = findViewById(R.id.filterLibraryGroup);
        filterWebGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoWebQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast t = Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG);
            t.setDuration(Toast.LENGTH_LONG);
            t.show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoLibraryQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        adapter = new ImageResultAdapter(search, R.layout.search);
        getListResults().setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        getListResults().setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            selectedImage = adapter.getItem(arg2);
            saveCellImage();
        });

        // Get the intent, verify the action and get the query
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            filterLibraryGroup.setVisibility(View.GONE);
            filterWebGroup.setVisibility(View.GONE);
        }
        query = intent.getStringExtra(SearchManager.QUERY);

        /* The request size. */
        for (int x = 0; x < maxResults; x += REQUEST_SIZE) {
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            getGoogleDrive(results -> {
                for (com.google.api.services.drive.model.File file : results) {
                    JSONObject o = new JSONObject();
                    try {
                        String description = file.getDescription();
                        if (description != null && description.length() > 0) {
                            o.put("name", file.getName() + "\r\n" + description);
                        } else o.put("name", file.getName());
                        o.put("thumbnailUrl", file.getThumbnailLink());
                        o.put("contentUrl", file.getId());
                        adapter.add(o);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, query, "image").getResultsFromApi();
        }
    }

    private void GoogleDriveSound(final Intent intent) {
        setTitle("Select a Sound File");
        setContentView(R.layout.search);
        setListResults(findViewById(R.id.searchList));
        RadioGroup filterWebGroup = findViewById(R.id.filterWebGroup);
        RadioGroup filterLibraryGroup = findViewById(R.id.filterLibraryGroup);
        filterWebGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoWebQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast t = Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG);
            t.setDuration(Toast.LENGTH_LONG);
            t.show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoLibraryQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        adapter = new ImageResultAdapter(search, R.layout.search);
        getListResults().setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        getListResults().setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            selectedSound = adapter.getItem(arg2);
            saveCellSound();
        });

        // Get the intent, verify the action and get the query
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            filterLibraryGroup.setVisibility(View.GONE);
            filterWebGroup.setVisibility(View.GONE);
        }
        query = intent.getStringExtra(SearchManager.QUERY);

        /* The request size. */
        for (int x = 0; x < maxResults; x += REQUEST_SIZE) {
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            getGoogleDrive(results -> {
                for (com.google.api.services.drive.model.File file : results) {
                    JSONObject o = new JSONObject();
                    try {
                        String description = file.getDescription();
                        if (description != null && description.length() > 0) {
                            o.put("name", file.getName() + "\r\n" + description);
                        } else o.put("name", file.getName());
                        o.put("thumbnailUrl", file.getThumbnailLink());
                        o.put("contentUrl", file.getId());
                        adapter.add(o);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, query, "audio").getResultsFromApi();
        }
    }

    private void GoogleDriveBoard(final Intent intent) {
        setTitle(getString(R.string.select_cell_or_board));
        setContentView(R.layout.search);
        setListResults(findViewById(R.id.searchList));
        RadioGroup filterWebGroup = findViewById(R.id.filterWebGroup);
        RadioGroup filterLibraryGroup = findViewById(R.id.filterLibraryGroup);
        filterWebGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoWebQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast t = Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG);
            t.setDuration(Toast.LENGTH_LONG);
            t.show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoLibraryQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        adapter = new ImageResultAdapter(search, R.layout.search);
        getListResults().setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        getListResults().setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            selectedBoard = adapter.getItem(arg2);
            saveCellBoard();
        });

        // Get the intent, verify the action and get the query
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            filterLibraryGroup.setVisibility(View.GONE);
            filterWebGroup.setVisibility(View.GONE);
        }
        query = intent.getStringExtra(SearchManager.QUERY);

        /* The request size. */
        for (int x = 0; x < maxResults; x += REQUEST_SIZE) {
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            getGoogleDrive(results -> {
                for (com.google.api.services.drive.model.File file : results) {
                    JSONObject o = new JSONObject();
                    try {
                        String description = file.getDescription();
                        if (description != null && description.length() > 0) {
                            o.put("name", file.getName() + "\r\n" + description);
                        } else o.put("name", file.getName());
                        o.put("thumbnailUrl", file.getThumbnailLink());
                        o.put("contentUrl", file.getId());
                        adapter.add(o);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, query, "application/mytalktools").getResultsFromApi();
        }
    }

    /* content saving method for saveChildBoard() */
    /* end content saving functions */

    /**
     * Web or library image.
     *
     * @param intent the Search activity intent
     */
    private void webOrLibraryImage(final Intent intent) {
        setTitle(R.string.select_an_image);
        setContentView(R.layout.search);
        setListResults(findViewById(R.id.searchList));
        RadioGroup filterWebGroup = findViewById(R.id.filterWebGroup);
        RadioGroup filterLibraryGroup = findViewById(R.id.filterLibraryGroup);
        filterWebGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoWebQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            adapter.clear();
            Toast t = Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG);
            t.setDuration(Toast.LENGTH_LONG);
            t.show();
            //for (Integer x = 0; x < maxResults; x += requestSize) {
            new DoLibraryQueryTask(this, adapter).execute(query, "0", maxResults.toString());
            //}
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        adapter = new ImageResultAdapter(search, R.layout.search);
        getListResults().setAdapter(adapter);
        adapter.setNotifyOnChange(true);
        getListResults().setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            selectedImage = adapter.getItem(arg2);
            saveCellImage();
        });

        // Get the intent, verify the action and get the query
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            if (queryType == SearchRequestType.LIBRARY_IMAGE || queryType == SearchRequestType.LIBRARY_IMAGE_HOTSPOT) {
                filterLibraryGroup.setVisibility(View.VISIBLE);
                filterWebGroup.setVisibility(View.GONE);
            }
        }
        query = intent.getStringExtra(SearchManager.QUERY);
        /* The request size. */
        int requestSize = REQUEST_SIZE;
        for (int x = 0; x < maxResults; x += requestSize) {
            if (queryType == SearchRequestType.WEB_IMAGE || queryType == SearchRequestType.WEB_IMAGE_HOTSPOT) {
                Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
                new DoWebQueryTask(this, adapter).execute(query, Integer.toString(x), Integer.toString(requestSize));

            } else {
                Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
                new DoLibraryQueryTask(this, adapter).execute(query, Integer.toString(x), Integer.toString(requestSize));
            }
        }
        // }
    }

    /**
     * Library board.
     *
     * @param intent the search activity intent
     */
    private void libraryBoard(final Intent intent) {
        setTitle(R.string.select_a_board);
        setContentView(R.layout.board_search);
        setListResults(findViewById(R.id.boardSearchList));
        RadioGroup boardFilterLibraryGroup = findViewById(R.id.boardFilterLibraryGroup);
        boardFilterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boardAdapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoBoardLibraryQueryTask(this, boardAdapter).execute(query, "0", maxResults.toString());
        });
        search = this;

        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            if (queryType == SearchRequestType.LIBRARY_BOARD || queryType == SearchRequestType.LIBRARY_BOARD_HOTSPOT) {
                boardFilterLibraryGroup.setVisibility(View.VISIBLE);
            }
            query = intent.getStringExtra(SearchManager.QUERY);
            boardAdapter = new BoardResultAdapter(search, R.layout.board_search, username, content);
            getListResults().setAdapter(boardAdapter);
            boardAdapter.setNotifyOnChange(true);
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoBoardLibraryQueryTask(this, boardAdapter).execute(query, "0", maxResults.toString());
        }
        // }
    }

    /**
     * Library video.
     *
     * @param intent the search activity intent
     */
    private void libraryVideo(final Intent intent) {
        setTitle(R.string.select_a_video);
        setContentView(R.layout.video_search);
        setListResults(findViewById(R.id.videoSearchList));
        RadioGroup filterLibraryGroup = findViewById(R.id.videoFilterLibraryGroup);
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            videoAdapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoVideoLibraryQueryTask(this, videoAdapter).execute(query, "0", maxResults.toString());
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        /* Get the intent, verify the action and get the query */
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            query = intent.getStringExtra(SearchManager.QUERY);
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            if (queryType == SearchRequestType.LIBRARY_VIDEO || queryType == SearchRequestType.LIBRARY_VIDEO_HOTSPOT) {
                filterLibraryGroup.setVisibility(View.VISIBLE);
            }
            query = intent.getStringExtra(SearchManager.QUERY);
            library = PUBLIC;
            videoAdapter =
                    new VideoResultAdapter(search, R.layout.video_search, content, getListResults());
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoVideoLibraryQueryTask(this, videoAdapter).execute(query, "0", maxResults.toString());
        }
        // }
    }

    /**
     * Library sound.
     *
     * @param intent the search activity intent
     */
    private void librarySound(final Intent intent) {

        setTitle(R.string.select_a_sound);
        setContentView(R.layout.sound_search);
        setListResults(findViewById(R.id.soundSearchList));
        RadioGroup filterLibraryGroup = findViewById(R.id.soundFilterLibraryGroup);
        filterLibraryGroup.setOnCheckedChangeListener((group, checkedId) -> {
            soundAdapter.clear();
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoSoundLibraryQueryTask(this, soundAdapter).execute(query, "0", maxResults.toString());
        });
        search = this;
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        /* Get the intent, verify the action and get the query */
        // if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
        Bundle appData = intent.getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            contentId = appData.getInt(Search.INTENT_EXTRA_CONTENT_ID);
            content = new BoardContent(contentId, this);
            queryType = SearchRequestType.values()[appData.getInt(SEARCH_REQUEST, 0)];
            username = appData.getString(Search.INTENT_EXTRA_USERNAME);
            if (queryType == SearchRequestType.LIBRARY_SOUND || queryType == SearchRequestType.LIBRARY_SOUND_HOTSPOT) {
                filterLibraryGroup.setVisibility(View.VISIBLE);
            }
            query = intent.getStringExtra(SearchManager.QUERY);
            library = PUBLIC;
            int checkedRadioButtonId = filterLibraryGroup.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.soundFilterPublic) {
                library = PUBLIC;
            } else if (checkedRadioButtonId == R.id.soundFilterPrivate) {
                library = username;
            }

            soundAdapter =
                    new SoundResultAdapter(search, R.layout.sound_search, username, library
                    );
            getListResults().setAdapter(soundAdapter);
            soundAdapter.setNotifyOnChange(true);
            Toast.makeText(search, R.string.loading_message, Toast.LENGTH_LONG).show();
            new DoSoundLibraryQueryTask(this, soundAdapter).execute(query, "0", maxResults.toString());
        }
        // }
    }

    /**
     * Find cell.
     */
    private void findCell() {
        setTitle(R.string.select_a_cell);
        findCell(query);
        setContentView(R.layout.cell_search);
        setListResults(findViewById(R.id.cellSearchList));
        search = this;
        final ArrayAdapter<BoardContent> cellAdapter = new CellResultAdapter(search, R.layout.cell_search, getListResults(), query);
        getListResults().setAdapter(cellAdapter);
        cellAdapter.setNotifyOnChange(true);
        Toast.makeText(this, R.string.loading_message, Toast.LENGTH_LONG).show();
        cellAdapter.addAll(findCell(query));
    }

    /**
     * Find cell.
     *
     * @param paramQuery the query
     * @return the array list
     */
    private ArrayList<BoardContent> findCell(final String paramQuery) {
        ArrayList<BoardContent> result = BoardContent.findCells(getApplicationContext(), paramQuery);
        Collections.sort(result, (arg0, arg1) -> arg0.getText().compareToIgnoreCase(arg1.getText()));
        return result;
    }

    /**
     * Gets the list results.
     *
     * @return the list results
     */
    private ListView getListResults() {
        return listResults;
    }

    /**
     * Sets the list results.
     *
     * @param paramListResults the new list results
     */
    private void setListResults(final ListView paramListResults) {
        this.listResults = paramListResults;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (queryType == SearchRequestType.GOOGLE_DRIVE || queryType == SearchRequestType.GOOGLE_DRIVE_HOTSPOT)
            googleDrive.getResultsFromApi();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    protected final void onActivityResult(final int requestCode, final int resultCode,
                                          final Intent data) {
        Board.RequestCode request = Board.RequestCode.values()[requestCode];
        switch (request) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    new AlertDialog.Builder(this).setTitle(R.string.alert)
                            .setIcon(R.drawable.ic_dialog_alert).setPositiveButton(R.string.ok, null)
                            .setMessage("This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.").create().show();
                } else {
                    googleDrive.getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        googleDrive.setAccountName(accountName);
                        googleDrive.getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    googleDrive.getResultsFromApi();
                }
                break;
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    googleDrive.connect();
                }
                break;
        }
    }

    @Override
    public final synchronized void onDestroy() {
        if (googleDrive != null) googleDrive.disconnect();
        super.onDestroy();
    }

    /**
     * Delineate the types of available searches.
     */
    public enum SearchRequestType {

        /**
         * unknown search request.
         */
        UNKNOWN,
        /**
         * web image search request.
         */
        WEB_IMAGE,
        /**
         * library image search request.
         */
        LIBRARY_IMAGE,
        /**
         * library sound search request.
         */
        LIBRARY_SOUND,
        /**
         * library video search request.
         */
        LIBRARY_VIDEO,
        /**
         * library board search request.
         */
        LIBRARY_BOARD,
        /**
         * library child board search request (internal).
         */
        LIBRARY_CHILD_BOARD,
        /**
         * find existing cell search request.
         */
        FIND_CELL,

        LIBRARY_SOUND_HOTSPOT,
        WEB_IMAGE_HOTSPOT,
        LIBRARY_IMAGE_HOTSPOT,
        LIBRARY_VIDEO_HOTSPOT,
        GOOGLE_DRIVE,
        GOOGLE_DRIVE_HOTSPOT,
        GOOGLE_DRIVE_BOARD,
        GOOGLE_DRIVE_BOARD_HOTSPOT,
        LIBRARY_BOARD_HOTSPOT,
        GOOGLE_DRIVE_SOUND,
        GOOGLE_DRIVE_SOUND_HOTSPOT
    }

    static class DownloadTask_googleDriveImageOperations extends AsyncTask<Void, Void, Bitmap> {

        private final JSONObject selectedImage;
        private final GoogleDrive googleDrive;
        private final Utility.GetBitmap result;
        String fileId;

        DownloadTask_googleDriveImageOperations(JSONObject selectedImage, GoogleDrive googleDrive, Utility.GetBitmap result) {
            this.selectedImage = selectedImage;
            this.googleDrive = googleDrive;
            this.result = result;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String fileId;
            Bitmap b = null;
            try {
                fileId = selectedImage.getString("contentUrl");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                googleDrive.getService()
                        .files()
                        .get(fileId)
                        .executeMediaAndDownloadTo(bytes);
                byte[] bits = bytes.toByteArray();
                b = BitmapFactory.decodeByteArray(bits, 0, bits.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            result.test(bitmap);
        }
    }

    static class DownloadTask_googleDriveSoundOperations extends AsyncTask<Void, Void, byte[]> {

        private final JSONObject selectedSound;
        private final GoogleDrive googleDrive;
        private final Utility.GetSound result;
        String fileId;

        DownloadTask_googleDriveSoundOperations(JSONObject selectedSound, GoogleDrive googleDrive, Utility.GetSound result) {
            this.selectedSound = selectedSound;
            this.googleDrive = googleDrive;
            this.result = result;
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            String fileId;
            byte[] b = null;
            try {
                fileId = selectedSound.getString("contentUrl");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                googleDrive.getService()
                        .files()
                        .get(fileId)
                        .executeMediaAndDownloadTo(bytes);
                b = bytes.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return b;
        }

        @Override
        protected void onPostExecute(byte[] bits) {
            result.test(bits);
        }
    }

    static class DownloadTask_googleDriveBoardOperations extends AsyncTask<Void, Void, JSONObject> {

        private final JSONObject selectedBoard;
        private final GoogleDrive googleDrive;
        private final Utility.GetResult result;
        String fileId;

        DownloadTask_googleDriveBoardOperations(JSONObject selectedBoard, GoogleDrive googleDrive, Utility.GetResult result) {
            this.selectedBoard = selectedBoard;
            this.googleDrive = googleDrive;
            this.result = result;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String fileId;
            String b;
            JSONObject j;
            JSONArray jj = null;
            try {
                fileId = selectedBoard.getString("contentUrl");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                googleDrive.getService()
                        .files()
                        .get(fileId)
                        .executeMediaAndDownloadTo(bytes);
                b = new String(bytes.toByteArray(), StandardCharsets.UTF_16);
                j = new JSONObject(b);
                HttpClient hc = new DefaultHttpClient();
                HttpParams httpParameters = hc.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);
                HttpConnectionParams.setTcpNoDelay(httpParameters, true);
                HttpPost p =
                        new HttpPost("https://www.mytalktools.com/dnn/sync.asmx/GetLibraryItem");
                String message =
                        new JSONObject().put("libraryItemId", j.get("LibraryItemId")).toString();
                StringEntity se = new StringEntity(message, "utf-8");
                se.setContentType("application/json");
                se.setContentEncoding("UTF-8");
                p.setEntity(se);
                p.setHeader("Content-Type", "application/json; charset=utf-8");
                p.setHeader("Accept", "application/json");
                HttpResponse resp = hc.execute(p);
                if (resp != null) {
                    if (resp.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = resp.getEntity();
                        String s = EntityUtils.toString(entity, "UTF-8");
                        Log.d("", s);
                        jj = new JSONArray(s);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                return jj == null ? null : jj.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject r) {
            result.test(r);
        }
    }

    /**
     * The Class DoChildBoardLibraryQueryTask.
     */
    public static class DoChildBoardLibraryQueryTask extends
            AsyncTask<String, ChildBoardSearchResult, Void> {

        /**
         * The root target content.
         */
        private final BoardContent rootTargetContent;
        /**
         * The activity.
         */
        private final WeakReference<Activity> activity;
        /**
         * The root source content.
         */
        private final BoardSearchResult rootSourceContent;
        /**
         * The progress dialog.
         */
        private final ProgressDialog progressDialog;
        /**
         * The user name.
         */
        private String userName;
        /**
         * The library name.
         */
        private String libraryName;
        /**
         * The downloading file.
         */
        private File downloadingFile = null;

        /**
         * Instantiates a new do child board library query task.
         *
         * @param targetContent the target content
         * @param sourceContent the source content
         * @param paramActivity the activity
         * @param paramUsername the param username
         */
        public DoChildBoardLibraryQueryTask(final BoardContent targetContent,
                                            final BoardSearchResult sourceContent, final Activity paramActivity,
                                            final String paramUsername) {
            super();

            // keep track of the original root variables so we can walk the
            // board trees
            // and make copies
            this.rootTargetContent = targetContent;
            this.rootSourceContent = sourceContent;
            this.activity = new WeakReference<>(paramActivity);
            RadioGroup filterGroup = activity.get().findViewById(R.id.boardFilterLibraryGroup);
            if (filterGroup == null) {
                this.libraryName = paramActivity.getString(R.string.private_library);
                this.userName = paramUsername;
            } else {
                int checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.boardFilterPublic) {
                    this.libraryName = paramActivity.getString(R.string.public_library);
                    this.userName = paramActivity.getString(R.string.public_username);
                } else if (checkedRadioButtonId == R.id.boardFilterPrivate) {
                    this.libraryName = paramActivity.getString(R.string.private_library);
                    this.userName = paramUsername;
                } else if (checkedRadioButtonId == R.id.boardFilterSubmissions) {
                    this.userName = paramActivity.getString(R.string.public_username);
                    this.libraryName = paramActivity.getString(R.string.submissions);
                }
            }

            progressDialog = new ProgressDialog(this.activity.get());
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        /**
         * Download url.
         *
         * @param downloadUrl the download url
         * @return the string
         */
        private String downloadUrl(final String downloadUrl) {
            if (downloadUrl == null || downloadUrl.isEmpty()) {
                return null;
            }
            String urlString = null;
            /* construct file and save with correct path */
            try {
                URI media = URI.create(downloadUrl.replace(" ", SPACE_ESCAPE));
                String root = Utility.getMyTalkFilesDir(this.activity.get()).getAbsolutePath();
                String localFilename =
                        root
                                + "/"
                                + media.getPath().replace("/dnn/UserUploads/", "").replace("/", "-")
                                .replace(" ", "-");
                urlString = media.getPath().replace("/dnn/UserUploads/", "");
                urlString =
                        urlString.replace(activity.get().getString(R.string.quicktime_movie_extension),
                                activity.get().getString(R.string.mpeg_movie_extension));
                downloadingFile = new File(localFilename);
                publishProgress();
                Utility.copyFileFromInternet(media.toString(), downloadingFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }

            /* return String with correct url for setting Url on Board */
            return urlString;
        }

        /**
         * Creates the board.
         *
         * @param targetContent the target content
         * @param sourceContent the source content
         */
        final void createBoard(final BoardContent targetContent,
                               final ChildBoardSearchResult sourceContent) {
            publishProgress(sourceContent);
            targetContent.copyContent(sourceContent);
            targetContent.setUrl(downloadUrl(sourceContent.ImageUrl));
            targetContent.setUrl2(downloadUrl(sourceContent.AudioVideoUrl));

            // if there is no child board id - no need to create a board
            if (sourceContent.ChildBoardId == 0) {
                targetContent.setChildBoardId(0);
                targetContent.setChildBoardLinkId(0);
            }

            targetContent.persist(activity.get().getApplicationContext());

            // if there is no child board id - no need to create a board
            if (sourceContent.ChildBoardId == 0) {
                return;
            }

            // get child board contents
            ArrayList<ChildBoardSearchResult> result;
            result =
                    new LibrarySearchRequest(this.userName, this.libraryName, sourceContent,
                            SearchRequestType.LIBRARY_CHILD_BOARD).executeChildBoardSearch();
            if (result != null) {
                // figure out number of rows and columns
                int rows = result.size() / sourceContent.ChildBoardColumnCount;
                int shortRow = result.size() % sourceContent.ChildBoardColumnCount;
                if (shortRow > 0) {
                    rows++;
                }

                // create the new board
                BoardRow newBoard =
                        new BoardRow(sourceContent.ChildBoardColumnCount, rows, 0, sourceContent.Text,
                                activity.get().getApplicationContext(), BoardRow.sortOrder.NotSorted,
                                BoardRow.sortOrder.NotSorted, BoardRow.sortOrder.NotSorted);

                // get and set the new board Id
                int newId = (int) newBoard.persist(true);
                targetContent.setChildBoardId(newId);
                targetContent.persist(activity.get().getApplicationContext());

                // walk through each item and copy to new board contents
                Object[] newContents = newBoard.getContents().toArray();
                for (int i = 0; i < result.size(); i++) {
                    createBoard((BoardContent) newContents[i], result.get(i));
                }
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected final void onPostExecute(final Void unused) {
            // remove progress dialog
            progressDialog.dismiss();
            activity.get().finish();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected final void onPreExecute() {
            activity.get().runOnUiThread(() -> Board.getUndoRedo().saveState());
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle(activity.get().getString(R.string.adding_boards));
            progressDialog.setMessage("Working...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected final Void doInBackground(final String... params) {

            createBoard(rootTargetContent, rootSourceContent.childBoardSearchResult());
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected final void onProgressUpdate(final ChildBoardSearchResult... items) {
            if (items.length == 0) {
                progressDialog.setMessage(activity.get().getString(R.string.downloading_file_)
                        + downloadingFile.getName());
            }
            for (ChildBoardSearchResult result : items) {
                progressDialog.setMessage(activity.get().getString(R.string.creating_cell_) + result.Text);
            }
            // update UI - somehow - to show progress
        }
    }

    /**
     * The Class DoVideoLibraryQueryTask.
     */
    private static class DoVideoLibraryQueryTask extends AsyncTask<String, LibrarySearchResult, Void> {

        final WeakReference<Search> search;
        final VideoResultAdapter videoAdapter;

        DoVideoLibraryQueryTask(Search search, VideoResultAdapter videoAdapter) {
            this.search = new WeakReference<>(search);
            this.videoAdapter = videoAdapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void unused) {
            search.get().setProgressBarIndeterminateVisibility(false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            RadioGroup filterGroup = search.get().findViewById(R.id.videoFilterLibraryGroup);
            checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
            search.get().setProgressBarIndeterminateVisibility(true);
            if (videoAdapter.getCount() != 0) {
                videoAdapter.clear();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final String... params) {
            String oldLibrary = library;
            int count = Integer.parseInt(params[2]);
            int offset = Integer.parseInt(params[1]);
            library = PUBLIC;
            if (checkedRadioButtonId == R.id.videoFilterPublic) {
                library = PUBLIC;
            } else if (checkedRadioButtonId == R.id.videoFilterPrivate) {
                library = username;
            }
            if (!library.equals(oldLibrary)) {
                offset = 0;
            }
            videoAdapter.setLibrary(library);
            videoAdapter.setUsername(username);
            ArrayList<LibrarySearchResult> result =
                    new LibrarySearchRequest(library, query, offset, count, queryType)
                            .execute();
            if (result != null) {
                for (LibrarySearchResult file : result) {
                    if (file != null) {
                        publishProgress(file);
                    }
                }
            }

            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final LibrarySearchResult... items) {
            for (LibrarySearchResult videoFile : items) {
                videoAdapter.add(videoFile);
            }
        }
    }

    /**
     * The Class DoBoardLibraryQueryTask.
     */
    private static class DoBoardLibraryQueryTask extends AsyncTask<String, BoardSearchResult, Void> {

        final WeakReference<Search> search;
        final BoardResultAdapter boardAdapter;

        DoBoardLibraryQueryTask(Search search, BoardResultAdapter boardAdapter) {
            this.search = new WeakReference<>(search);
            this.boardAdapter = boardAdapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void unused) {
            search.get().setProgressBarIndeterminateVisibility(false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            RadioGroup filterGroup = search.get().findViewById(R.id.boardFilterLibraryGroup);
            checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
            search.get().setProgressBarIndeterminateVisibility(true);
            if (boardAdapter.getCount() != 0) {
                boardAdapter.clear();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final String... params) {
            String oldLibrary = library;
            int count = Integer.parseInt(params[2]);
            int offset = Integer.parseInt(params[1]);
            library = PUBLIC;
            if (checkedRadioButtonId == R.id.boardFilterPublic) {
                library = PUBLIC;
            } else if (checkedRadioButtonId == R.id.boardFilterPrivate) {
                library = username;
            } else if (checkedRadioButtonId == R.id.boardFilterSubmissions) {
                library = SUBMISSIONS;
            }
            if (!library.equals(oldLibrary)) {
                offset = 0;
            }
            boardAdapter.setUsername(library);
            ArrayList<BoardSearchResult> result =
                    new LibrarySearchRequest(library, query, offset, count, queryType)
                            .executeBoardSearch();
            if (result != null) {
                for (BoardSearchResult file : result) {
                    if (file != null) {
                        publishProgress(file);
                    }
                }
            }

            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final BoardSearchResult... items) {
            for (BoardSearchResult boardFile : items) {
                boardAdapter.add(boardFile);
            }
        }
    }

    /**
     * The Class DoSoundLibraryQueryTask.
     */
    private static class DoSoundLibraryQueryTask extends AsyncTask<String, LibrarySearchResult, Void> {

        final WeakReference<Search> search;
        final SoundResultAdapter soundAdapter;

        DoSoundLibraryQueryTask(Search search, SoundResultAdapter soundAdapter) {
            this.search = new WeakReference<>(search);
            this.soundAdapter = soundAdapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void unused) {
            search.get().setProgressBarIndeterminateVisibility(false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            RadioGroup filterGroup = search.get().findViewById(R.id.soundFilterLibraryGroup);
            checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
            search.get().setProgressBarIndeterminateVisibility(true);
            if (soundAdapter.getCount() != 0) {
                soundAdapter.clear();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final String... params) {
            String oldLibrary = library;
            int count = Integer.parseInt(params[2]);
            int offset = Integer.parseInt(params[1]);
            library = PUBLIC;
            if (checkedRadioButtonId == R.id.soundFilterPublic) {
                library = PUBLIC;
            } else if (checkedRadioButtonId == R.id.soundFilterPrivate) {
                library = username;
            }
            if (!library.equals(oldLibrary)) {
                offset = 0;
            }
            soundAdapter.setLibrary(library);
            soundAdapter.setUsername(username);
            ArrayList<LibrarySearchResult> result =
                    new LibrarySearchRequest(library, query, offset, count, queryType)
                            .execute();
            if (result != null) {
                for (LibrarySearchResult file : result) {
                    if (file != null) {
                        publishProgress(file);
                    }
                }
            }

            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final LibrarySearchResult... items) {
            for (LibrarySearchResult file : items) {
                soundAdapter.add(file);
            }
        }
    }

    /**
     * The Class DoLibraryQueryTask.
     */
    private static class DoLibraryQueryTask extends AsyncTask<String, LibrarySearchResult, Void> {

        final WeakReference<Search> search;
        final ArrayAdapter<JSONObject> adapter;

        DoLibraryQueryTask(Search search, ArrayAdapter<JSONObject> adapter) {
            this.search = new WeakReference<>(search);
            this.adapter = adapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void unused) {
            search.get().setProgressBarIndeterminateVisibility(false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            RadioGroup filterGroup = search.get().findViewById(R.id.filterLibraryGroup);
            checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
            search.get().setProgressBarIndeterminateVisibility(true);
            if (adapter.getCount() != 0) {
                adapter.clear();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final String... params) {
            String oldLibrary = library;
            int count = Integer.parseInt(params[2]);
            int offset = Integer.parseInt(params[1]);
            library = PUBLIC;
            if (checkedRadioButtonId == R.id.filterPublic) {
                library = PUBLIC;
            } else if (checkedRadioButtonId == R.id.filterPrivate) {
                library = username;
            } else if (checkedRadioButtonId == R.id.filterSymbolstix) {
                library = SYMBOLS;
            }
            if (!library.equals(oldLibrary)) {
                offset = 0;
            }
            ArrayList<LibrarySearchResult> result =
                    new LibrarySearchRequest(library, query, offset, count).execute();
            if (result != null) {
                for (LibrarySearchResult ir : result) {
                    if (ir != null) {
                        publishProgress(ir);
                    }
                }
            }

            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final LibrarySearchResult... items) {
            for (LibrarySearchResult ir : items) {
                JSONObject x = new JSONObject();
                try {
                    x.put("thumbnailUrl", ir.ThumbnailUrl.replace("http://", "https://"));
                    x.put("name", ir.Filename + "\r\n" + ir.Tags);
                    x.put("contentUrl", ir.MediaUrl.replace("http://", "https://"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.add(x);
            }
        }
    }

    /**
     * The Class DoWebQueryTask.
     */
    private static class DoWebQueryTask extends AsyncTask<String, JSONObject, Void> {

        final WeakReference<Search> search;
        final ArrayAdapter<JSONObject> adapter;

        DoWebQueryTask(Search search, ArrayAdapter<JSONObject> adapter) {
            this.search = new WeakReference<>(search);
            this.adapter = adapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Void unused) {
            boolean atMax = adapter.getCount() != maxResults;
            search.get().setProgressBarIndeterminateVisibility(atMax);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            RadioGroup filterGroup = search.get().findViewById(R.id.filterWebGroup);
            checkedRadioButtonId = filterGroup.getCheckedRadioButtonId();
            search.get().setProgressBarIndeterminateVisibility(true);
            if (adapter.getCount() == 0) {
                adapter.clear();
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final String... params) {
            String localQuery = params[0];
            long offset = Long.parseLong(params[1]);
            long count = Long.parseLong(params[2]);

            JSONObject r =
                    GetBingImageSearch.execute(localQuery, offset, count, checkedRadioButtonId);
            if (r == null) return null;
            try {
                JSONArray array = r.getJSONArray("value");
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    JSONObject ir = array.getJSONObject(i);
                    publishProgress(ir);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final JSONObject... items) {
            for (JSONObject ir : items) {
                adapter.add(ir);
            }
        }
    }

}
