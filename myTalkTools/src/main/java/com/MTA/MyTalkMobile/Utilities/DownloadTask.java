/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import com.MTA.MyTalkMobile.R;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class DownloadTask.
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    /**
     * The Constant BUFFER_SIZE.
     */
    private static final int BUFFER_SIZE = 8;

    /**
     * The Constant KILOBYTE.
     */
    private static final int KILOBYTE = 1024;

    /**
     * The context.
     */
    private final WeakReference<Context> context;
    /**
     * The out.
     */
    private final String out;
    /**
     * The post event.
     */
    private final Runnable postEvent;
    /**
     * The wake lock.
     */
    private PowerManager.WakeLock wakeLock;
    /**
     * The file length.
     */
    private int fileLength;
    /**
     * The progress dialog.
     */
    private ProgressDialog progressDialog;


    /**
     * Instantiates a new download task.
     *
     * @param pContext   the context
     * @param pOut       the out
     * @param pPostEvent the post event
     */
    public DownloadTask(final Context pContext, final String pOut, final Runnable pPostEvent) {
        this.context = new WeakReference<>(pContext);
        this.out = pOut;
        this.postEvent = pPostEvent;
    }

    @Override
    protected final void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.get().getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }
        progressDialog = new ProgressDialog(context.get());
        progressDialog.setMessage(new File(out).getName());
        progressDialog.setTitle(context.get().getString(R.string.downloading_file));
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected final void onProgressUpdate(final Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(fileLength);
        progressDialog.setProgress(progress[0]);
    }

    @Override
    protected final void onPostExecute(final String result) {
        wakeLock.release();
        progressDialog.dismiss();
        if (result != null) {
            Toast.makeText(context.get(), R.string.download_error_ + result, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context.get(), R.string.file_downloaded, Toast.LENGTH_SHORT).show();
        }
        if (postEvent != null) {
            postEvent.run();
        }
    }

    @Override
    protected final String doInBackground(final String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return R.string.server_returned_http_ + connection.getResponseCode() + " "
                        + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = Files.newOutputStream(Paths.get(out));

            byte[] data = new byte[KILOBYTE * BUFFER_SIZE];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) {
                    publishProgress((int) total);
                }
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
