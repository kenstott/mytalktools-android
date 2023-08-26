/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Database;
import com.MTA.MyTalkMobile.Json.JsonDocumentFileInfo;
import com.MTA.MyTalkMobile.Json.JsonFileListDirectory;
import com.MTA.MyTalkMobile.Json.JsonNewDatabaseResult;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Utilities.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;

// TODO: Auto-generated Javadoc

/**
 * The Class GetNewDatabaseResult.
 */
public class GetNewDatabaseResult {

    /**
     * The Constant ONE_SECOND.
     */
    private static final int ONE_SECOND = 1000;

    /**
     * The board.
     */
    private final Board board;
    /**
     * The database path.
     */
    private final String databasePath;
    /**
     * The exception.
     */
    private final String exception;
    /**
     * The progress.
     */
    private final AsyncGetNewDatabase progress;
    /**
     * The buffer size.
     */
    private final int bufferSize = 64 * 1024;
    /**
     * The array of byte.
     */
    private final byte[] arrayOfByte = new byte[bufferSize];
    /**
     * The copy to pictures.
     */
    private final boolean copyToPictures;
    /**
     * The picture dir.
     */
    private final File pictureDir;
    /**
     * The database byte image.
     */
    private byte[] databaseByteImage;
    /**
     * The json file list dirs.
     */
    private ArrayList<JsonFileListDirectory> jsonFileListDirs;

    /**
     * Instantiates a new gets the new database result.
     *
     * @param result     the result
     * @param paramBoard the param board
     * @param progress2  the progress2
     */
    public GetNewDatabaseResult(final JsonNewDatabaseResult result, final Board paramBoard,
                                final AsyncGetNewDatabase progress2) {
        this.board = paramBoard;
        this.progress = progress2;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(paramBoard);
        copyToPictures = sp.getBoolean("downloadMedia", false);
        this.databasePath = result.DatabasePath;
        pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (result.Exception == null || result.Exception.equals("")) {
            this.exception = null;
            progress2.updateMessage("Received boards...");
            progress2.incrementPrimaryCount(1);
            this.databaseByteImage = result.DatabaseImage;
            this.jsonFileListDirs = result.DirectoryList;
        } else {
            this.exception = result.Exception;
        }
    }

    /**
     * Update content file.
     *
     * @param dirName          the dir name
     * @param documentFileInfo the document file info
     */
    private void updateContentFile(final String dirName, final JsonDocumentFileInfo documentFileInfo) {
        if (documentFileInfo.Name.endsWith(".caf")) {
            return;
        }
        if (documentFileInfo.Name.endsWith(".aiff")) {
            return;
        }
        if (documentFileInfo.Name.endsWith(".wav")) {
            return;
        }
        if (documentFileInfo.Name.endsWith(board.getString(R.string.quicktime_movie_extension))) {
            progress.updateMessage("Converting: " + documentFileInfo.Name);
            progress.updateSecondaryMax(0);
            new ConvertVideo(PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext())
                    .getString(Board.USERNAME, ""), dirName, documentFileInfo.Name.replace("/", "-").replace(
                    " ", "-"), documentFileInfo.Name
                    .replace("/", "-")
                    .replace(" ", "-")
                    .replace(board.getString(R.string.quicktime_movie_extension),
                            board.getString(R.string.mpeg_movie_extension))).execute();
            documentFileInfo.Name =
                    (documentFileInfo.Name.replace(board.getString(R.string.quicktime_movie_extension),
                            board.getString(R.string.mpeg_movie_extension)));
        }
        File aFile =
                new File(Utility.getMyTalkFilesDir(board).getAbsolutePath() + "/"
                        + (dirName + documentFileInfo.Name).replace("/", "-").replace(" ", "-"));
        String tttString;
        try {
            URI xxx = new URI("https", "www.mytalktools.com", "/dnn/UserUploads/" + dirName + documentFileInfo.Name, null);
            String y = xxx.toASCIIString();
            tttString = (new URL(y)).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        try {
            if (aFile.isFile()) {
                progress.updateMessage("Comparing: " + documentFileInfo.Name);
                Date creationTime = documentFileInfo.CreationTime;
                Date lastModified = new Date(aFile.lastModified());
                if (creationTime != null && creationTime.getTime() - lastModified.getTime() < ONE_SECOND) {
                    if (aFile.length() != 0) {
                        return;
                    }
                }
            }
            progress.updateMessage(board.getString(R.string.downloading_) + documentFileInfo.Name);
            HttpClient hc = new DefaultHttpClient();
            HttpParams httpParameters = hc.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 1000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            HttpGet hg = new HttpGet(tttString);
            HttpResponse hr = hc.execute(hg);
            HttpEntity entity = hr.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            FileOutputStream outputStream = new FileOutputStream(aFile);
            progress.updateSecondaryMax((int) documentFileInfo.Length);
            while (true) {
                int i = bufferedInputStream.read(arrayOfByte, 0, bufferSize);
                if (i == -1) {
                    outputStream.flush();
                    outputStream.close();
                    bufferedInputStream.close();
                    inputStream.close();
                    if (!aFile.setLastModified(documentFileInfo.CreationTime.getTime())) {
                        Log.d("d", "Problem changing creation time on file");
                    }
                    if (copyToPictures) {
                        Utility.copyFile(aFile.getAbsolutePath(),
                                pictureDir.getAbsolutePath() + "/" + aFile.getName());
                    }
                    return;
                }
                progress.incrementSecondaryCount(bufferSize);
                outputStream.write(arrayOfByte, 0, i);
            }
        } catch (Exception localIOException2) {
            localIOException2.printStackTrace();
        }
    }

    /**
     * Update content files.
     *
     * @param fileListDir the file list dir
     */
    private void updateContentFiles(final JsonFileListDirectory fileListDir) {
        for (JsonDocumentFileInfo dfi : fileListDir.FileList) {
            progress.incrementPrimaryCount(1);
            updateContentFile(fileListDir.Name, dfi);
        }
    }

    /**
     * Gets the exception.
     *
     * @return the exception
     */
    public final String getException() {
        return this.exception;
    }

    /**
     * Write.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final void write() throws Exception {
        progress.updateMessage(board.getResources().getString(R.string.saving_boards));
        progress.incrementPrimaryCount(1);

        if (this.databaseByteImage != null) {
            new Database(board).copyDatabase(this.databaseByteImage);
        } else {
            new Database(board).copyDatabase(new URL("https://www.mytalktools.com/dnn/"
                    + this.databasePath.replace(" ", "%20").replace("\\", "/")));
        }
        int j = 0;
        for (JsonFileListDirectory fld : this.jsonFileListDirs) {
            j += fld.FileList.size();
        }
        progress.updatePrimaryMax(j);
        for (JsonFileListDirectory fld : this.jsonFileListDirs) {
            updateContentFiles(fld);
        }
    }
}
