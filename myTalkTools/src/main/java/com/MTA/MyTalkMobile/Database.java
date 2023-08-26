/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Keep;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;

//import proguard.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class Database.
 */
@Keep
public class Database {

    /**
     * The Constant KILOBYTE.
     */
    private static final int KILOBYTE = 1024;
    /**
     * The database.
     */
    private static SQLiteDatabase database;
    /**
     * The board database.
     */
    private static String boardDatabase;
    private static boolean indexed = false;
    /**
     * The context.
     */
    private final Context context;
    /**
     * The buffer size.
     */
    private final int bufferSize = 8 * 1024;
    /**
     * The array of byte.
     */
    private final byte[] arrayOfByte = new byte[bufferSize];

    /**
     * Instantiates a new database.
     *
     * @param paramContext the param context
     */
    public Database(final Context paramContext) {
        this.context = paramContext;
        if (!file(paramContext).exists()) {
            copyDefaultDatabase();
        }
        Database.close();

        BoardOpenHelper localBoardOpenHelper =
                new BoardOpenHelper(paramContext, boardDatabase, null, 1);
        localBoardOpenHelper.getWritableDatabase();
        localBoardOpenHelper.close();
        database = paramContext.openOrCreateDatabase(boardDatabase, 0, null);
    }

    /**
     * Gets the database.
     *
     * @param paramContext the param context
     * @return the database
     */
    public static SQLiteDatabase getDatabase(final Context paramContext) {
        if (!indexed) {
            indexed = true;
            Database.indexDatabase(database, paramContext);
        }
        if (database == null) {
            database = paramContext.openOrCreateDatabase(boardDatabase, 0, null);
        }
        return database;
    }

    /**
     * Path.
     *
     * @param paramContext the param context
     * @return the string
     */
    public static String path(final Context paramContext) {
        return file(paramContext).getAbsolutePath();
    }

    private static void indexDatabase(SQLiteDatabase database, Context context) {
        try {
            String sql = "CREATE INDEX board_index ON board (iphone_board_id)";
            database.execSQL(sql);
            sql = "CREATE INDEX content_index_board ON content (board_id)";
            database.execSQL(sql);
            sql = "CREATE INDEX content_index_type ON content (content_type)";
            database.execSQL(sql);
            sql = "CREATE INDEX content_index_child_board ON content (child_board_id)";
            database.execSQL(sql);
            sql = "CREATE INDEX content_index_content ON content (iphone_content_id)";
            database.execSQL(sql);
            sql = "CREATE INDEX content_index_external ON content (external_url)";
            database.execSQL(sql);
        } catch (Exception ex) {
            // OK - its already indexed
        }
    }

    /**
     * File.
     *
     * @param paramContext the param context
     * @return the file
     */
    private static File file(final Context paramContext) {
        boardDatabase = paramContext.getResources().getString(R.string.boardDatabase);
        return paramContext.getDatabasePath(boardDatabase);
    }

    /**
     * Close.
     */
    public static void close() {
        if (database != null && database.isOpen()) {
            database.close();
            database = null;
        }
    }

    /**
     * Base64.
     *
     * @param paramContext the param context
     * @return the string
     */
    public static String base64(final Context paramContext) {
        try {
            byte[] bytes = new byte[(int) file(paramContext).length()];
            FileInputStream input = new FileInputStream(file(paramContext));
            if (input.read(bytes) != 0) {
                input.close();
                return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copy database.
     *
     * @param source the source
     */
    public final void copyDatabase(final byte[] source) {
        close();
        File localFile = file(context);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        FileOutputStream localFileOutputStream;
        try {
            localFileOutputStream = new FileOutputStream(localFile);
            localFileOutputStream.write(source, 0, source.length);
            localFileOutputStream.flush();
            localFileOutputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Copy database.
     *
     * @param source the source
     */
    public final void copyDatabase(final URL source) {
        close();
        File localFile = file(context);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        try {
            HttpClient hc = new DefaultHttpClient();
            HttpParams httpParameters = hc.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 500);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);
            HttpGet hg = new HttpGet(source.toString());
            HttpResponse hr = hc.execute(hg);
            HttpEntity entity = hr.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(localFile);
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Copy database from source.
     *
     * @param source the source
     */
    public final void copyDatabaseFromSource(final String source) {
        close();
        File localFile = file(context);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        try {
            File sourceFile = new File(localFile.getAbsolutePath().replace(boardDatabase, source));
            InputStream inputStream = Files.newInputStream(sourceFile.toPath());
            FileOutputStream outputStream = new FileOutputStream(localFile);
            int len;
            while ((len = inputStream.read(arrayOfByte)) > 0) {
                outputStream.write(arrayOfByte, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Copy database to target.
     *
     * @param target the target
     */
    public final void copyDatabaseToTarget(final String target) {
        close();
        File localFile = file(context);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        try {
            File targetFile = new File(localFile.getAbsolutePath().replace(boardDatabase, target));
            InputStream inputStream = Files.newInputStream(localFile.toPath());
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            int len;
            while ((len = inputStream.read(arrayOfByte)) > 0) {
                outputStream.write(arrayOfByte, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Copy default database.
     */
    private void copyDefaultDatabase() {
        close();
        InputStream localInputStream =
                context.getResources().openRawResource(R.raw.iphone_communication_db);
        File localFile = context.getDatabasePath(boardDatabase);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            byte[] bytes = new byte[KILOBYTE];
            while (true) {
                int i = localInputStream.read(bytes);
                if (i < 0) {
                    localFileOutputStream.flush();
                    localFileOutputStream.close();
                    localInputStream.close();
                    return;
                }
                localFileOutputStream.write(bytes, 0, i);
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * Copy database.
     */
    public final void copyDatabase() {
        close();
        InputStream localInputStream =
                context.getResources().openRawResource(R.raw.iphone_communication_db);
        File localFile = context.getDatabasePath(boardDatabase);
        if (!localFile.exists()) {
            BoardOpenHelper localBoardOpenHelper =
                    new BoardOpenHelper(context, boardDatabase, null, 1);
            localBoardOpenHelper.getWritableDatabase();
            localBoardOpenHelper.close();
        }
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
            byte[] bytes = new byte[KILOBYTE];
            while (true) {
                int i = localInputStream.read(bytes);
                if (i < 0) {
                    localFileOutputStream.flush();
                    localFileOutputStream.close();
                    localInputStream.close();
                    return;
                }
                localFileOutputStream.write(bytes, 0, i);
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }
}
