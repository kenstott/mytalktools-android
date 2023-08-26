/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Utilities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.MTA.MyTalkMobile.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc

/**
 * The Class Utility.
 */
@SuppressLint("SimpleDateFormat")
public class Utility {

    /**
     * The Constant KILOBYTE.
     */
    private static final int KILOBYTE = 1024;

    /**
     * The Constant DATE_FORMAT.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Gets the UTC date time as date.
     *
     * @return the date
     */
    private static Date getUTC_DateTimeAsDate() {
        // note: doesn't check for null
        return stringDateToDate(getUTC_DateTimeAsString());
    }

    /**
     * Gets the ut cdatetime as string.
     *
     * @return the string
     */
    private static String getUTC_DateTimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /**
     * String date to date.
     *
     * @param strDate the str date
     * @return the date
     */
    private static Date stringDateToDate(final String strDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            dateToReturn = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    /**
     * Checks for camera.
     *
     * @param context the context
     * @return true, if successful
     */
    public static boolean hasCamera(final Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    /**
     * Gets the my talk files dir.
     *
     * @param context the context
     * @return the my talk files dir
     */
    public static File getMyTalkFilesDir(final Context context) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useExternalStorage = sp.getBoolean("externalStorage", true);
        if (useExternalStorage && context.getExternalFilesDir(null) != null) {
            return context.getExternalFilesDir(null);
        }
        return context.getFilesDir();
    }

    public static void fetch(final String address, Utility.GetResult result) {

        class DownloadTask extends AsyncTask<Void, Void, Object> {

            Utility.GetResult json;

            @Override
            protected Object doInBackground(Void... params) {
                URL url = null;
                try {
                    url = new URL(address);
                    return url.getContent();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    return url.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object result) {
                json.test(result);
            }

        }

        DownloadTask d = new DownloadTask();
        d.json = result;
        d.execute();
    }

    public static void imageOperations(final String url, GetBitmap result) {

        class DownloadTask extends AsyncTask<Void, Void, Bitmap> {

            String address;

            @Override
            protected Bitmap doInBackground(Void... params) {
                URL url;
                try {
                    url = new URL(address);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
                Object out;
                try {
                    out = url.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                InputStream is = (InputStream) out;
                BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
                bitmapOption.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, bitmapOption);
                float bitmapWidth = bitmapOption.outWidth;
                int sampleWidth = Math.round(bitmapWidth / (float) KILOBYTE);
                float bitmapHeight = bitmapOption.outHeight;
                int sampleHeight = Math.round(bitmapHeight / (float) KILOBYTE);
                int sampleSize = Math.max(sampleWidth, sampleHeight);
                BitmapFactory.Options imageOptions = new BitmapFactory.Options();
                if (sampleSize != 0) imageOptions.inSampleSize = sampleSize;
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    is = (InputStream) url.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                Bitmap b = BitmapFactory.decodeStream(is, null, imageOptions);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return b;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                result.test(bitmap);
            }
        }

        DownloadTask d = new DownloadTask();
        d.address = url;
        d.execute();
    }

    /**
     * Rotate bitmap.
     *
     * @param bitmap   the bitmap
     * @param rotation the rotation
     * @return the bitmap
     */
    public static Bitmap rotateBitmap(final Bitmap bitmap, final float rotation) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1F, 1F);
        matrix.postRotate(rotation);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap scaleBitmapAndKeepRatio(Bitmap targetBmp, int reqHeightInPixels, int reqWidthInPixels) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, targetBmp.getWidth(), targetBmp.getHeight()), new RectF(0, 0, reqWidthInPixels, reqHeightInPixels), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(targetBmp, 0, 0, targetBmp.getWidth(), targetBmp.getHeight(), m, true);
    }

    /**
     * Gets the filename.
     *
     * @param file the file
     * @return the filename
     */
    public static String getFilename(final File file) {
        String name = file.getName();
        if (name.length() > 0) {
            int dotPosition = name.lastIndexOf(".");
            if (dotPosition == -1) {
                return name;
            }
            return name.substring(0, dotPosition);
        }
        return "";
    }

    /**
     * Gets the extension.
     *
     * @param file the file
     * @return the extension
     */
    public static String getExtension(final File file) {
        String name = file.getName();
        if (name.length() > 0) {
            int dotposition = file.getName().lastIndexOf(".");
            if (dotposition == -1) {
                return "";
            }
            //name.substring(0, dotposition);
            return name.substring(dotposition + 1);
        }
        return "";
    }

    /**
     * Change extension.
     *
     * @param file           the file
     * @param paramExtension the extension
     * @param append         the append
     * @return the string
     */
    public static String changeExtension(final File file, final String paramExtension,
                                         final String append) {
        String extension = paramExtension;
        String name = file.getAbsolutePath();
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        if (name.length() > 0) {
            int dotposition = name.lastIndexOf(".");
            if (dotposition == -1) {
                return "";
            }
            String filenameWithoutExtension = name.substring(0, dotposition);
            return filenameWithoutExtension + append + extension;
        }
        return "";

    }

    /**
     * Not implemented.
     *
     * @param context the context
     */
    public static void notImplemented(final Context context) {
        new AlertDialog.Builder(context).setTitle(R.string.alert).setIcon(R.drawable.ic_dialog_info)
                .setMessage("Not implemented yet!").create().show();
    }

    /**
     * Alert.
     *
     * @param message the message
     * @param context the context
     * @return the alert dialog
     */
    public static AlertDialog alert(final String message, final Context context) {
        AlertDialog result =
                new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.ok, null)
                        .create();
        result.show();
        return result;
    }

    /**
     * Alert.
     *
     * @param message the message
     * @param context the context
     * @return the alert dialog
     */
    public static AlertDialog alert(final int message, final Context context) {
        AlertDialog result =
                new AlertDialog.Builder(context).setMessage(message).setPositiveButton(R.string.ok, null)
                        .create();
        result.show();
        return result;
    }

    /**
     * Gets the SQ lite date string.
     *
     * @param date the date
     * @return the SQ lite date string
     */
    public static String getSQLiteDateString(final Date date) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat();
        localSimpleDateFormat.applyPattern("yyyy-MM-dd HH:mm:ss");
        return localSimpleDateFormat.format(date);
    }

    /**
     * Gets the GMT date sq lite string.
     *
     * @return the GMT date sq lite string
     */
    public static String getGMTDateSQLiteString() {
        return getUTC_DateTimeAsString();
    }

    /**
     * Gets the GMT date.
     *
     * @return the GMT date
     */
    public static Date getGMTDate() {
        return getUTC_DateTimeAsDate();
    }

    /**
     * Checks if is 3gp file video.
     *
     * @param mediaFile the media file
     * @return true, if is 3gp file video
     */
    public static boolean is3gpFileVideo(final File mediaFile) {
        int height = 0;
        try {
            MediaPlayer mp = new MediaPlayer();
            FileInputStream fs;
            FileDescriptor fd;
            fs = new FileInputStream(mediaFile);
            fd = fs.getFD();
            mp.setDataSource(fd);
            mp.prepare();
            height = mp.getVideoHeight();
            mp.release();
            fs.close();
        } catch (Exception e) {
            Log.e("", "Exception trying to determine if 3gp file is video.", e);
        }
        return height > 0;
    }

    /**
     * Decode uri.
     *
     * @param selectedImage the selected image
     * @param context       the context
     * @return the bitmap
     * @throws FileNotFoundException the file not found exception
     */
    public static Bitmap decodeUri(final Uri selectedImage, final Context context)
            throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory
                .decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int requiredSize = 1024;

        // Find the correct scale value. It should be the power of 2.
        int widthTmp = o.outWidth, heightTmp = o.outHeight;
        int scale = 1;
        while (widthTmp / 2 >= requiredSize && heightTmp / 2 >= requiredSize) {
            widthTmp /= 2;
            heightTmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage),
                null, o2);

    }

    /**
     * Copy file from internet.
     *
     * @param in  the in
     * @param out the out
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void copyFileFromInternet(final String in, final String out) throws Exception {
        final int bufferSize = 1024 * 64;
        final byte[] arrayOfByte = new byte[bufferSize];
        HttpClient hc = new DefaultHttpClient();
        HttpParams httpParameters = hc.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 500);
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
        HttpConnectionParams.setTcpNoDelay(httpParameters, true);
        HttpGet hg = new HttpGet(in);
        HttpResponse hr = hc.execute(hg);
        HttpEntity entity = hr.getEntity();
        InputStream inputStream = entity.getContent();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        File file = new File(out);
        FileOutputStream outputStream = new FileOutputStream(file);
        while (true) {
            int i = bufferedInputStream.read(arrayOfByte, 0, bufferSize);
            if (i == -1) {
                outputStream.flush();
                outputStream.close();
                bufferedInputStream.close();
                inputStream.close();
                return;
            }
            outputStream.write(arrayOfByte, 0, i);
        }
    }

    /**
     * Strip.
     *
     * @param value the value
     * @return the string
     */
    public static String strip(final String value) {
        return value.replace(" ", "").replace(".", "").replace(",", "").replace("?", "")
                .replace(":", "").replace(";", "").replace("&", "").replace("$", "").replace("@", "")
                .replace("!", "").replace("~", "").replace("\"", "").replace("\\", "").replace("/", "")
                .replace(">", "").replace("<", "").replace("-", "").replace("|", "");
    }

    /**
     * Make filename unique.
     *
     * @param filename the filename
     * @return the string
     */
    public static String makeFilenameUnique(final String filename) {
        int count = 0;
        String newFilename = filename;
        File file = new File(filename);
        String path = file.getParent();
        String name = getFilename(file);
        String extension = getExtension(file);

        while (new File(newFilename).exists()) {
            newFilename = path + "/" + name + (++count) + "." + extension;
        }
        return newFilename;
    }

    /**
     * Copy file.
     *
     * @param fileIn  the file in
     * @param fileOut the file out
     */
    public static void copyFile(final String fileIn, final String fileOut) {
        try {
            FileInputStream in = new FileInputStream(fileIn);
            File newFile = new File(fileOut);
            if (newFile.createNewFile()) {
                FileOutputStream out = new FileOutputStream(newFile);
                byte[] buf = new byte[KILOBYTE * 16];
                int i;
                while ((i = in.read(buf)) != -1) {
                    out.write(buf, 0, i);
                }
                in.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawGridOnBitmap(Bitmap bitmap, float r, float c, int highlightRow, int highlightColumn) {
        try {
            Bitmap b = bitmap.copy(bitmap.getConfig(), true);
            Canvas canvas = new Canvas(b);
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setStrokeWidth(2);
            float width = canvas.getWidth() / c;
            for (int x = 0; x <= c; x++) {
                canvas.drawLine(x * width, 0, x * width, canvas.getWidth(), p);
            }
            float height = canvas.getHeight() / r;
            for (int x = 0; x <= r; x++) {
                canvas.drawLine(0, x * height, canvas.getHeight(), x * height, p);
            }

            if (highlightColumn > 0 && highlightRow > 0) {
                p.setAlpha(100);
                canvas.drawRect((highlightColumn - 1) * width, (highlightRow - 1) * height, highlightColumn * width, highlightRow * height, p);
            }
            return b;
        } catch (Exception ex) {
            Log.d("", ex.toString());
        }
        return null;
    }

    public interface GetResult {
        void test(Object result);
    }

    public interface GetBitmap {
        void test(Bitmap result);
    }

    public interface GetSound {
        void test(byte[] result);
    }
}
