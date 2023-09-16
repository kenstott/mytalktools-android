/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.ImageView;

import com.MTA.MyTalkMobile.Json.ChildBoardSearchResult;
import com.MTA.MyTalkMobile.Utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//import proguard.annotation.Keep;

/**
 * The Class BoardContent.
 */
@SuppressWarnings("deprecation")


//@Keep
public class BoardContent {

    /**
     * The Constant GO_BACK_COMMAND.
     */
    private static final int GO_BACK_COMMAND = 19;

    /**
     * The Constant GO_HOME_COMMAND.
     */
    private static final int GO_HOME_COMMAND = 18;

    /**
     * The Constant DATE_FORMAT.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * The Constant DOT_WAV.
     */
    private static final String DOT_WAV = ".wav";
    /**
     * The Constant DOT_MP3.
     */
    private static final String DOT_MP3 = ".mp3";
    /**
     * The Constant DOT_3GP.
     */
    private static final String DOT_3GP = ".3gp";
    /**
     * The Constant DOT_MP4.
     */
    private static final String DOT_MP4 = ".mp4";
    /**
     * The Constant DOT_MOV.
     */
    private static final String DOT_MOV = ".mov";
    /**
     * The Constant TABLE_NAME.
     */
    private static final String TABLE_NAME = "content";
    /**
     * The Constant COLUMN_NAMES.
     */
    private static final String[] COLUMN_NAMES = {"iphone_content_id", "web_content_id",
            "content_name", "content_url", "content_url2", "content_type", "create_date", "update_date",
            "board_id", "row_index", "clm_index", "child_board_id", "user_id", "child_board_link",
            "total_uses", "session_uses", "background_color", "foreground_color", "font_size", "zoom",
            "do_not_add_to_phrasebar", "do_not_zoom_pics", "tts_speech", "external_url", "alternate_tts", "hot_spot_style"};
    /**
     * The board id.
     */
    private int boardId;
    /**
     * The child board id.
     */
    private int childBoardId;
    /**
     * The column.
     */
    private int column;
    /**
     * The i phone id.
     */
    private Integer iPhoneId;
    /**
     * The row.
     */
    private int row;
    /**
     * The text.
     */
    private String text;
    /**
     * The type.
     */
    private int type;
    /**
     * The url.
     */
    private String url = "";
    /**
     * The url2.
     */
    private String url2 = "";
    /**
     * The web id.
     */
    private int webId;
    /**
     * The create date.
     */
    private Date createDate;
    /**
     * The update date.
     */
    private Date updateDate;
    /**
     * The user id.
     */
    private int userId;
    /**
     * The child board link id.
     */
    private int childBoardLinkId;
    /**
     * The total uses.
     */
    private int totalUses;
    /**
     * The session uses.
     */
    private int sessionUses;
    /**
     * The background color.
     */
    private int backgroundColor;
    /**
     * The foreground color.
     */
    private int foregroundColor;
    /**
     * The font size.
     */
    private int fontSize;
    /**
     * The zoom.
     */
    private int zoom;
    /**
     * The do not zoom pics.
     */
    private int doNotZoomPics;
    /**
     * The do not add to phrase bar.
     */
    private int doNotAddToPhraseBar;
    /**
     * The tts speech prompt.
     */
    private String ttsSpeechPrompt = "";
    /**
     * The external url.
     */
    private String externalUrl = "";
    /**
     * The alternate tts text.
     */
    private String alternateTtsText = "";
    private int hotspotStyle = 0;
    private Bitmap bitmap;

    /**
     * Instantiates a new board content.
     */
    public BoardContent() {
    }

    /**
     * Instantiates a new board content.
     *
     * @param old the old
     */
    public BoardContent(final BoardContent old) {
        setiPhoneId(0);
        setWebId(0);
        setText(old.text);
        setUrl(old.url);
        setUrl2(old.url2);
        setType(old.type);
        setBoardId(old.boardId);
        setRow(old.row);
        setColumn(old.column);
        setChildBoardId(old.childBoardId);
        setUserId(old.userId);
        setCreateDate(Utility.getGMTDate());
        setUpdateDate(getCreateDate());
        setChildBoardLinkId(old.childBoardLinkId);
        setTotalUses(0);
        setSessionUses(0);
        setPhysicalBackgroundColor(old.getPhysicalBackgroundColor());
        setPhysicalForegroundColor(old.getPhysicalForegroundColor());
        setFontSize(old.fontSize);
        setZoom(old.zoom);
        setDoNotZoomPics(old.doNotZoomPics);
        setDoNotAddToPhraseBar(old.doNotAddToPhraseBar);
        setTtsSpeechPrompt(old.ttsSpeechPrompt);
        setExternalUrl(old.externalUrl);
        setAlternateTtsText(old.alternateTtsText);
        setHotspotStyle(old.hotspotStyle);
        setUpdateDate(new Date());
    }

    /**
     * Instantiates a new board content.
     *
     * @param paramIPhoneId            the param i phone id
     * @param paramWebId               the param web id
     * @param paramText                the param text
     * @param paramUrl                 the param url
     * @param paramUrl2                the param url2
     * @param paramType                the param type
     * @param paramBoardId             the param board id
     * @param paramRow                 the param row
     * @param paramColumn              the param column
     * @param paramChildBoardId        the param child board id
     * @param paramUserId              the param user id
     * @param paramChildBoardLinkId    the param child board link id
     * @param paramTotalUses           the param total uses
     * @param paramSessionUses         the param session uses
     * @param paramBackgroundColor     the param background color
     * @param paramForegroundColor     the param foreground color
     * @param paramFontSize            the param font size
     * @param paramZoom                the param zoom
     * @param paramDoNotZoomPics       the param do not zoom pics
     * @param paramDoNotAddToPhraseBar the param do not add to phrase bar
     * @param paramTtsSpeechPrompt     the param tts speech prompt
     * @param paramExternalUrl         the param external url
     * @param paramAlternateTtsText    the param alternate tts text
     */
    public BoardContent(final int paramIPhoneId, final int paramWebId, final String paramText,
                        final String paramUrl, final String paramUrl2, final int paramType, final int paramBoardId,
                        final int paramRow, final int paramColumn, final int paramChildBoardId,
                        final int paramUserId, final int paramChildBoardLinkId, final int paramTotalUses,
                        final int paramSessionUses, final int paramBackgroundColor, final int paramForegroundColor,
                        final int paramFontSize, final int paramZoom, final int paramDoNotZoomPics,
                        final int paramDoNotAddToPhraseBar, final String paramTtsSpeechPrompt,
                        final String paramExternalUrl, final String paramAlternateTtsText, final int paramHotspotStyle) {
        setiPhoneId(paramIPhoneId);
        setWebId(paramWebId);
        setText(paramText);
        setUrl(paramUrl);
        setUrl2(paramUrl2);
        setType(paramType);
        setBoardId(paramBoardId);
        setRow(paramRow);
        setColumn(paramColumn);
        setChildBoardId(paramChildBoardId);
        setUserId(paramUserId);
        setCreateDate(Utility.getGMTDate());
        setUpdateDate(getCreateDate());
        setChildBoardLinkId(paramChildBoardLinkId);
        setTotalUses(paramTotalUses);
        setSessionUses(paramSessionUses);
        setPhysicalBackgroundColor(paramBackgroundColor);
        setPhysicalForegroundColor(paramForegroundColor);
        setFontSize(paramFontSize);
        setZoom(paramZoom);
        setDoNotZoomPics(paramDoNotZoomPics);
        setDoNotAddToPhraseBar(paramDoNotAddToPhraseBar);
        setTtsSpeechPrompt(paramTtsSpeechPrompt);
        setExternalUrl(paramExternalUrl);
        setAlternateTtsText(paramAlternateTtsText);
        setHotspotStyle(paramHotspotStyle);
    }

    /**
     * Instantiates a new board content.
     *
     * @param cursor the cursor
     */
    public BoardContent(final Cursor cursor) {
        readCursor(cursor);
    }

    /**
     * Instantiates a new board content.
     *
     * @param rowIndex     the row index
     * @param colIndex     the col index
     * @param paramBoardId the param board id
     * @param context      the context
     */
    public BoardContent(final Integer rowIndex, final Integer colIndex, final Integer paramBoardId,
                        final Context context) {
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES, COLUMN_NAMES[columnIndex.row.ordinal()]
                                + " = ? AND " + COLUMN_NAMES[columnIndex.column.ordinal()] + "= ? AND "
                                + COLUMN_NAMES[columnIndex.boardId.ordinal()] + " = ?",
                        new String[]{rowIndex.toString(), colIndex.toString(), paramBoardId.toString()}, null,
                        null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            readCursor(cursor);
        }
        cursor.close();
    }

    /**
     * Instantiates a new board content.
     *
     * @param context the context
     * @param id      the id
     */
    public BoardContent(final Context context, final Integer id) {
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.childBoardId.ordinal()] + " = ?",
                        new String[]{id.toString()}, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            readCursor(cursor);
        }
        cursor.close();
    }

    /**
     * Instantiates a new board content.
     *
     * @param id      the id
     * @param context the context
     */
    public BoardContent(final Integer id, final Context context) {
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.iPhoneId.ordinal()] + " = ?", new String[]{id.toString()},
                        null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            readCursor(cursor);
        }
        cursor.close();
    }

    public static ArrayList<BoardContent> getScheduledContent(Context context) {
        ArrayList<BoardContent> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.externalUrl.ordinal()] + " LIKE ?",
                        new String[]{"mtschedule:/%"}, null, null, null, null);
        while (cursor.moveToNext()) {
            BoardContent content = new BoardContent();
            readCursor(content, cursor);
            result.add(content);
        }
        cursor.close();
        return result;
    }

    public static ArrayList<BoardContent> getLocationContent(Context context) {
        ArrayList<BoardContent> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.externalUrl.ordinal()] + " LIKE ?",
                        new String[]{"mtgeo:/%"}, null, null, null, null);
        while (cursor.moveToNext()) {
            BoardContent content = new BoardContent();
            readCursor(content, cursor);
            result.add(content);
        }
        cursor.close();
        return result;
    }

    public static ArrayList<BoardContent> getMostUsedContent(Context context) {
        ArrayList<BoardContent> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.totalUses.ordinal()] + " > ? AND " + COLUMN_NAMES[columnIndex.type.ordinal()] + " < 18",
                        new String[]{"0"}, null, null, COLUMN_NAMES[columnIndex.totalUses.ordinal()] + " DESC", "40");
        while (cursor.moveToNext()) {
            BoardContent content = new BoardContent();
            readCursor(content, cursor);
            result.add(content);
        }
        cursor.close();
        return result;
    }

    public static ArrayList<BoardContent> getMostRecentlyUsedContent(Context context) {
        ArrayList<BoardContent> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(false, TABLE_NAME, COLUMN_NAMES,
                        COLUMN_NAMES[columnIndex.totalUses.ordinal()] + " > ? AND " + COLUMN_NAMES[columnIndex.type.ordinal()] + " < 18",
                        new String[]{"0"}, null, null, "datetime(" + COLUMN_NAMES[columnIndex.updateDate.ordinal()] + ") DESC", "40");
        while (cursor.moveToNext()) {
            BoardContent content = new BoardContent();
            readCursor(content, cursor);
            result.add(content);
        }
        cursor.close();
        return result;
    }

    /**
     * Find cells.
     *
     * @param context the context
     * @param query   the query
     * @return the array list
     */
    public static ArrayList<BoardContent> findCells(final Context context, final String query) {
        ArrayList<BoardContent> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(TABLE_NAME, COLUMN_NAMES, "content_name LIKE " + "'%" + query + "%'", null,
                        null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new BoardContent(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    private static void readCursor(BoardContent content, final Cursor cursor) {
        content.setiPhoneId(cursor.getInt(columnIndex.iPhoneId.ordinal()));
        content.setWebId(cursor.getInt(columnIndex.webId.ordinal()));
        content.setText(cursor.getString(columnIndex.text.ordinal()));
        content.setUrl(cursor.getString(columnIndex.url.ordinal()));
        String str3 = cursor.getString(columnIndex.url2.ordinal());
        content.setUrl2(str3.replace(DOT_MOV, DOT_MP4));
        content.setType(cursor.getInt(columnIndex.type.ordinal()));
        content.setBoardId(cursor.getInt(columnIndex.boardId.ordinal()));
        content.setRow(cursor.getInt(columnIndex.row.ordinal()));
        content.setColumn(cursor.getInt(columnIndex.column.ordinal()));
        content.setChildBoardId(cursor.getInt(columnIndex.childBoardId.ordinal()));
        content.setUserId(cursor.getInt(columnIndex.userId.ordinal()));
        content.setCreateDate(cursor.getString(columnIndex.createDate.ordinal()));
        content.setUpdateDate(cursor.getString(columnIndex.updateDate.ordinal()));
        try {
            content.setChildBoardLinkId(cursor.getInt(columnIndex.childBoardLinkId.ordinal()));
            content.setTotalUses(cursor.getInt(columnIndex.totalUses.ordinal()));
            content.setSessionUses(cursor.getInt(columnIndex.sessionUses.ordinal()));
            content.setPhysicalBackgroundColor(cursor.getInt(columnIndex.backgroundColor.ordinal()));
            content.setPhysicalForegroundColor(cursor.getInt(columnIndex.foregroundColor.ordinal()));
            content.setFontSize(cursor.getInt(columnIndex.fontSize.ordinal()));
            content.setZoom(cursor.getInt(columnIndex.zoom.ordinal()));
            content.setDoNotZoomPics(cursor.getInt(columnIndex.doNotZoomPics.ordinal()));
            content.setDoNotAddToPhraseBar(cursor.getInt(columnIndex.doNotAddToPhraseBar.ordinal()));
            content.setExternalUrl(cursor.getString(columnIndex.externalUrl.ordinal()));
            content.setTtsSpeechPrompt(cursor.getString(columnIndex.ttsSpeechPrompt.ordinal()));
            content.setAlternateTtsText(cursor.getString(columnIndex.alternateTtsText.ordinal()));
            content.setHotspotStyle(cursor.getInt(columnIndex.hotspotStyle.ordinal()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Copy content.
     *
     * @param content the content
     */
    public final void copyContent(final JSONObject content) {
        try {
            setText(content.getString("Text"));
            if (content.getBoolean("ToHome")) setType(GO_HOME_COMMAND);
            else if (content.getBoolean("Back")) setType(GO_BACK_COMMAND);
            else setType(1);
            setUrl(content.getString("Picture"));
            setUrl2(content.getString("Sound"));
            setPhysicalBackgroundColor(content.getInt("Background"));
            setPhysicalForegroundColor(content.getInt("Foreground"));
            setFontSize(content.getInt("FontSize"));
            setZoom(content.getBoolean("Zoom"));
            setHotspotStyle(content.getBoolean("HotSpotStyle") ? 1 : 0);
            setDoNotZoomPics(content.getBoolean("DoNotZoomPics"));
            setDoNotAddToPhraseBar(content.getBoolean("DoNotAddToPhraseBar"));
            setTtsSpeechPrompt(content.getString("TtsSpeechPrompt"));
            setExternalUrl(content.getString("ExternalUrl"));
            setAlternateTtsText(content.getString("AlternateTtsText"));
            if (externalUrl.equals("null")) externalUrl = null;
            if (text.equals("null")) text = null;
            if (url.equals("null")) url = null;
            if (url2.equals("null")) url2 = null;
            if (ttsSpeechPrompt.equals("null")) ttsSpeechPrompt = null;
            if (alternateTtsText.equals("null")) alternateTtsText = null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public final void copyContent(final ChildBoardSearchResult content) {
        setText(content.Text);
        switch (content.ContentType) {
            case 1:
                setType(GO_BACK_COMMAND);
                break;
            case 2:
                setType(GO_HOME_COMMAND);
                break;
            default:
                setType(1);
                break;
        }
        setPhysicalBackgroundColor(content.Background);
        setPhysicalForegroundColor(content.Foreground);
        setFontSize(content.FontSize);
        setZoom(content.Zoom);
        setDoNotZoomPics(content.DoNotZoomPics);
        setTtsSpeechPrompt(content.TtsSpeechPrompt);
        setExternalUrl(content.AppLink);
        setAlternateTtsText(content.AlternateTtsText);
    }

    /**
     * Read cursor.
     *
     * @param cursor the cursor
     */
    private void readCursor(final Cursor cursor) {
        readCursor(this, cursor);
    }

    /**
     * Gets the new id.
     *
     * @param context the context
     * @return the new id
     */
    private int getNewId(final Context context) {
        SQLiteDatabase database = Database.getDatabase(context);
        Cursor cursor =
                database.query(TABLE_NAME, new String[]{COLUMN_NAMES[columnIndex.iPhoneId.ordinal()]},
                        null, null, null, null, COLUMN_NAMES[columnIndex.iPhoneId.ordinal()]);
        cursor.moveToLast();
        int newId = cursor.getInt(0) + 1;
        cursor.close();
        return newId;
    }

    /**
     * Gets the content values.
     *
     * @return the content values
     */
    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(COLUMN_NAMES[columnIndex.type.ordinal()], this.type);
        cv.put(COLUMN_NAMES[columnIndex.text.ordinal()], this.text);
        cv.put(COLUMN_NAMES[columnIndex.url.ordinal()], this.url);
        if (this.url2.endsWith(DOT_MP4)) {
            cv.put(COLUMN_NAMES[columnIndex.url2.ordinal()], this.url2.replace(DOT_MP4, DOT_MOV));
        } else {
            cv.put(COLUMN_NAMES[columnIndex.url2.ordinal()], this.url2);
        }
        cv.put(COLUMN_NAMES[columnIndex.webId.ordinal()], this.webId);
        cv.put(COLUMN_NAMES[columnIndex.boardId.ordinal()], this.boardId);
        cv.put(COLUMN_NAMES[columnIndex.row.ordinal()], this.row);
        cv.put(COLUMN_NAMES[columnIndex.column.ordinal()], this.column);
        cv.put(COLUMN_NAMES[columnIndex.childBoardId.ordinal()], this.childBoardId);
        cv.put(COLUMN_NAMES[columnIndex.userId.ordinal()], this.userId);
        cv.put(COLUMN_NAMES[columnIndex.createDate.ordinal()],
                Utility.getSQLiteDateString(this.createDate));
        cv.put(COLUMN_NAMES[columnIndex.updateDate.ordinal()], Utility.getGMTDateSQLiteString());
        try {
            cv.put(COLUMN_NAMES[columnIndex.childBoardLinkId.ordinal()], this.childBoardLinkId);
            cv.put(COLUMN_NAMES[columnIndex.totalUses.ordinal()], this.totalUses);
            cv.put(COLUMN_NAMES[columnIndex.sessionUses.ordinal()], this.sessionUses);
            cv.put(COLUMN_NAMES[columnIndex.backgroundColor.ordinal()], this.getPhysicalBackgroundColor());
            cv.put(COLUMN_NAMES[columnIndex.foregroundColor.ordinal()], this.getPhysicalForegroundColor());
            cv.put(COLUMN_NAMES[columnIndex.fontSize.ordinal()], this.fontSize);
            cv.put(COLUMN_NAMES[columnIndex.zoom.ordinal()], this.zoom);
            cv.put(COLUMN_NAMES[columnIndex.doNotZoomPics.ordinal()], this.doNotZoomPics);
            cv.put(COLUMN_NAMES[columnIndex.doNotAddToPhraseBar.ordinal()], this.doNotAddToPhraseBar);
            cv.put(COLUMN_NAMES[columnIndex.externalUrl.ordinal()], this.externalUrl);
            cv.put(COLUMN_NAMES[columnIndex.ttsSpeechPrompt.ordinal()], this.ttsSpeechPrompt);
            cv.put(COLUMN_NAMES[columnIndex.alternateTtsText.ordinal()], this.alternateTtsText);
            cv.put(COLUMN_NAMES[columnIndex.hotspotStyle.ordinal()], this.hotspotStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cv;
    }

    /**
     * Gets the media type.
     *
     * @param context the context
     * @return the media type
     */
    public final MediaType getMediaType(final Context context) {
        if (url2.endsWith(context.getString(R.string.mpeg_movie_extension))) {
            return MediaType.movie;
        }
        if (url2.endsWith(DOT_3GP)) {
            return MediaType.sound;
        }
        if (url2.endsWith(DOT_MP3)) {
            return MediaType.sound;
        }
        if (url2.endsWith(DOT_WAV)) {
            return MediaType.sound;
        }
        return MediaType.unknown;
    }

    /**
     * Show picture.
     *
     * @param context the context
     */
    public final void showPicture(final Context context) {
        Intent localIntent = new Intent(context.getApplicationContext(), ZoomPic.class);
        localIntent.putExtra(ZoomPic.INTENT_EXTRA_BOARD_ID, boardId);
        localIntent.putExtra(ZoomPic.INTENT_EXTRA_CONTENT_ID, iPhoneId);
        context.startActivity(localIntent);
    }

    /**
     * clear media.
     */
    public final void clearMedia() {
        this.text = "";
        this.url = "";
        this.url2 = "";
        this.type = 1;
    }

    /**
     * Copy media.
     *
     * @param c the c
     */
    public final void copyMedia(final BoardContent c) {
        c.text = this.text;
        c.url = this.url;
        c.url2 = this.url2;
        c.type = this.type;
        // c.childBoardId = this.childBoardId;
    }

    /**
     * Copy.
     *
     * @return the board content
     */
    private BoardContent copy() {
        BoardContent c = new BoardContent();
        c.text = this.text;
        c.url = this.url;
        c.url2 = this.url2;
        c.type = this.type;
        c.row = this.row;
        c.column = this.column;
        c.childBoardId = this.childBoardId;
        c.userId = this.userId;
        c.iPhoneId = 0;
        c.webId = 0;
        return c;
    }

    /**
     * Delete.
     *
     * @param context the context
     */
    public final void delete(final Context context) {

        try {
            SQLiteDatabase database = Database.getDatabase(context);
            database.delete(TABLE_NAME, COLUMN_NAMES[columnIndex.iPhoneId.ordinal()] + "= ?",
                    new String[]{iPhoneId.toString()});
        } catch (Exception err) {
            // OK
        }
    }

    /**
     * Persist.
     *
     * @param context the context
     */
    public final void persist(final Context context) {
        try {
            if (this.iPhoneId == 0) {
                ContentValues cv = getContentValues();
                iPhoneId = getNewId(context);
                cv.put(COLUMN_NAMES[columnIndex.iPhoneId.ordinal()], iPhoneId);
                SQLiteDatabase database = Database.getDatabase(context);
                System.out.print(cv);
                database.insert(TABLE_NAME, null, cv);
            } else {
                Database.getDatabase(context).update(TABLE_NAME, getContentValues(),
                        COLUMN_NAMES[columnIndex.iPhoneId.ordinal()] + " = " + this.iPhoneId, null);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public BoardRow getChildBoard(Board board) {
        if (childBoardId == 0 && childBoardLinkId == 0) return null;
        return new BoardRow(childBoardId == 0 ? childBoardLinkId : childBoardId, board);
    }

    public BoardContent getHotspotContent(Board board, float x, float y, float width, float height) {
        BoardRow boardRow = this.getChildBoard(board);
        int rows = boardRow.getRows();
        int columns = boardRow.getColumns();
        int r = (int) (Math.floor(y / height * rows));
        int c = (int) (Math.floor(x / width * columns));
        return boardRow.getItem(r, c);
    }

    public BoardContent mergeHotspot(Board board, BoardContent hotspot) {
        if (hotspot == null) return this;
        BoardContent combinedContent = hotspot.copy();
        if (combinedContent.externalUrl.length() == 0)
            combinedContent.externalUrl = this.externalUrl;
        if (combinedContent.url.length() == 0) combinedContent.url = this.url;
        if (combinedContent.alternateTtsText.length() == 0)
            combinedContent.alternateTtsText = this.alternateTtsText;
        if (combinedContent.text.length() == 0) combinedContent.text = this.text;
        else if (combinedContent.zoom == 0 && this.zoom == 1) combinedContent.zoom = 1;
        else if (combinedContent.zoom == 1 && this.zoom == 1) combinedContent.zoom = 0;
        if (combinedContent.url2.length() == 0 && board.canTTS() && combinedContent.text.length() == 0)
            combinedContent.url2 = this.url2;
        else if (combinedContent.url2.length() == 0 && !board.canTTS())
            combinedContent.url2 = this.url2;
        return combinedContent;
    }

    public final Bitmap getImage(Context board, boolean cache) {
        if (bitmap != null) return bitmap;
        Bitmap tBitmap = null;
        InputStream inputStream = null;
        String localFilename;
        File localFile = null;
        AssetFileDescriptor afd = null;
        FileDescriptor fileDescriptor = null;
        File fileDir = Utility.getMyTalkFilesDir(board);
        if (getUrl() != null && getUrl().length() != 0) {
            if (getUrl().contains("/")) {
                localFilename = getUrl().replace(" ", "-").replace("/", "-");
                localFile = new File(fileDir.getPath() + "/" + localFilename);
                try {
                    inputStream = new FileInputStream(localFile);
                } catch (FileNotFoundException e) {
                    try {
                        afd = board.getContentResolver().
                                openAssetFileDescriptor(Uri.parse(getUrl()), "r");
                        if (afd != null) {
                            fileDescriptor = afd.getFileDescriptor();
                            if (fileDescriptor == null) {
                                Log.d("d", "could not get a file descriptor");
                            }
                            localFile = null;
                        }
                    } catch (FileNotFoundException e1) {
                        return null;
                    }
                }
            } else {
                try {
                    inputStream = board.getAssets().open(getUrl());
                } catch (IOException e) {
                    return null;
                }
            }
        }

        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inJustDecodeBounds = true;

        if (fileDescriptor != null) {
            // Decodes the bitmap
            BitmapFactory.decodeFileDescriptor(
                    fileDescriptor, null, bitmapOption);
            try {
                afd.close();
            } catch (IOException e) {
                return null;
            }
        } else {
            BitmapFactory.decodeStream(inputStream, null, bitmapOption);
        }

        try {
            if (inputStream != null) inputStream.close();
        } catch (IOException e) {
            return null;
        }

        if (localFile != null) {
            try {
                inputStream = new FileInputStream(localFile);
            } catch (FileNotFoundException e) {
                return null;
            }

        } else if (afd != null) {
            try {
                afd = board.getContentResolver().
                        openAssetFileDescriptor(Uri.parse(getUrl()), "r");
                if (afd != null) {
                    fileDescriptor = afd.getFileDescriptor();
                    if (fileDescriptor == null) {
                        Log.d("d", "could not get a file descriptor");
                    }
                    afd.close();
                }
            } catch (IOException e) {
                return null;
            }
        } else {
            AssetManager localAssetManager1 = board.getAssets();
            try {
                inputStream = localAssetManager1.open(getUrl());
            } catch (IOException e) {
                return null;
            }
        }

        float bitmapWidth = bitmapOption.outWidth;
        int sampleWidth = Math.round(bitmapWidth / 1000);
        float bitmapHeight = bitmapOption.outHeight;
        int sampleHeight = Math.round(bitmapHeight / 1000);
        int sampleSize = Math.max(sampleWidth, sampleHeight);
        BitmapFactory.Options imageOptions = new BitmapFactory.Options();
        imageOptions.inSampleSize = sampleSize;
        for (int x = 0; tBitmap == null; x++) {
            try {
                if (fileDescriptor != null) {
                    tBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, imageOptions);
                } else if (inputStream != null) {
                    tBitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);
                }
            } catch (OutOfMemoryError ex) {
                System.gc();
                if (fileDescriptor != null) {
                    tBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, imageOptions);
                } else {
                    tBitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);
                }
            }
            if (x == 10) break;
        }
        if (cache) bitmap = tBitmap;
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch(IOException ignore) {}
        }
        return tBitmap;
    }

    /**
     * Gets the board id.
     *
     * @return the board id
     */
    public final int getBoardId() {
        return this.boardId;
    }

    /**
     * Sets the board id.
     *
     * @param value the new board id
     */
    public final void setBoardId(final int value) {
        this.boardId = value;
    }

    /**
     * Gets the child board id.
     *
     * @return the child board id
     */
    public final int getChildBoardId() {
        return this.childBoardId;
    }

    /**
     * Sets the child board id.
     *
     * @param value the new child board id
     */
    public final void setChildBoardId(final int value) {
        this.childBoardId = value;
    }

    /**
     * Gets the column.
     *
     * @return the column
     */
    public final int getColumn() {
        return this.column;
    }

    /**
     * Sets the column.
     *
     * @param value the new column
     */
    public final void setColumn(final int value) {
        this.column = value;
    }

    /**
     * Gets the row.
     *
     * @return the row
     */
    public final int getRow() {
        return this.row;
    }

    /**
     * Sets the row.
     *
     * @param value the new row
     */
    public final void setRow(final int value) {
        this.row = value;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public final String getText() {
        if (this.text == null) {
            return "";
        }
        if (
                this.text.equals("Empty") ||
                        this.text.equals("Empty0") ||
                        this.text.equals("Empty1") ||
                        this.text.equals("Empty2") ||
                        this.text.equals("Empty3") ||
                        this.text.equals("Empty4") ||
                        this.text.equals("Empty5") ||
                        this.text.equals("Empty6") ||
                        this.text.equals("Empty7") ||
                        this.text.equals("Empty8") ||
                        this.text.equals("Empty9") ||
                        this.text.equals("Empty10")
        ) return "";
        return this.text;
    }

    /**
     * Sets the text.
     *
     * @param value the new text
     */
    public final void setText(final String value) {
        if (value == null) {
            this.text = "";
        } else {
            this.text = value;
        }
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public final int getType() {
        return this.type;
    }

    /**
     * Sets the type.
     *
     * @param value the new type
     */
    public final void setType(final int value) {
        this.type = value;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public final String getUrl() {
        if (this.url == null) {
            return "";
        }
        return this.url;
    }

    /**
     * Sets the url.
     *
     * @param value the new url
     */
    public final void setUrl(final String value) {
        this.bitmap = null;
        if (value == null) {
            this.url = "";
        } else {
            this.url = value;
        }
    }

    /**
     * Gets the url2.
     *
     * @return the url2
     */
    public final String getUrl2() {
        if (this.url2 == null) {
            return "";
        }
        return this.url2;
    }

    /**
     * Sets the url2.
     *
     * @param value the new url2
     */
    public final void setUrl2(final String value) {
        if (value == null) {
            this.url2 = "";
        } else {
            this.url2 = value;
        }
    }

    /**
     * Gets the web id.
     *
     * @return the web id
     */
    public final int getWebId() {
        return this.webId;
    }

    /**
     * Sets the web id.
     *
     * @param value the new web id
     */
    private void setWebId(final int value) {
        this.webId = value;
    }

    /**
     * Gets the i phone id.
     *
     * @return the i phone id
     */
    public final int getiPhoneId() {
        if (this.iPhoneId == null) return -1;
        return this.iPhoneId;
    }

    /**
     * Sets the i phone id.
     *
     * @param value the new i phone id
     */
    final void setiPhoneId(final int value) {
        this.iPhoneId = value;
    }

    public final int getHotspotStyle() {
        if (this.childBoardId == 0) return 0;
        return this.hotspotStyle;
    }

    public final void setHotspotStyle(final int value) {
        this.hotspotStyle = value;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public final int getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param value the new user id
     */
    public final void setUserId(final int value) {
        this.userId = value;
    }

    /**
     * Gets the creates the date.
     *
     * @return the creates the date
     */
    private Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the creates the date.
     *
     * @param value the new creates the date
     */
    private void setCreateDate(final Date value) {
        this.createDate = value;
    }

    /**
     * Sets the creates the date.
     *
     * @param value the new creates the date
     */
    private void setCreateDate(final String value) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            this.createDate = localSimpleDateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the update date.
     *
     * @return the update date
     */
    public final Date getUpdateDate() {
        return updateDate;
    }

    private void setUpdateDate(final Date value) {
        this.createDate = value;
    }

    /**
     * Sets the update date.
     *
     * @param value the new update date
     */
    private void setUpdateDate(final String value) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        try {
            this.updateDate = localSimpleDateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the child board link id.
     *
     * @return the child board link id
     */
    public final int getChildBoardLinkId() {
        return childBoardLinkId;
    }

    /**
     * Sets the child board link id.
     *
     * @param value the new child board link id
     */
    public final void setChildBoardLinkId(final int value) {
        this.childBoardLinkId = value;
    }

    /**
     * Gets the total uses.
     *
     * @return the total uses
     */
    public final int getTotalUses() {
        return totalUses;
    }

    /**
     * Sets the total uses.
     *
     * @param value the new total uses
     */
    public final void setTotalUses(final int value) {
        this.totalUses = value;
    }

    /**
     * Gets the session uses.
     *
     * @return the session uses
     */
    public final int getSessionUses() {
        return sessionUses;
    }

    /**
     * Sets the session uses.
     *
     * @param value the new session uses
     */
    private void setSessionUses(final int value) {
        this.sessionUses = value;
    }

    /**
     * Gets the background color.
     *
     * @return the background color
     */
    public final int getBackgroundColor() {
        return backgroundColor & 0x00FF;
    }

    /**
     * Sets the background color.
     *
     * @param value the new background color
     */
    public final void setBackgroundColor(final int value) {
        backgroundColor = (backgroundColor & 0xFF00) + (value & 0x00FF);
    }

    public final int getPhysicalBackgroundColor() {
        return backgroundColor;
    }

    public final void setPhysicalBackgroundColor(final int value) {
        backgroundColor = value;
    }

    /**
     * Gets the foreground color.
     *
     * @return the foreground color
     */
    public final int getForegroundColor() {
        return foregroundColor & 0x00FF;
    }

    /**
     * Sets the foreground color.
     *
     * @param value the new foreground color
     */
    public final void setForegroundColor(final int value) {
        foregroundColor = (foregroundColor & 0xFF00) + (value & 0x00FF);
    }

    public final int getPhysicalForegroundColor() {
        return foregroundColor;
    }

    public final void setPhysicalForegroundColor(final int value) {
        foregroundColor = value;
    }

    public final boolean getHidden() {
        return (foregroundColor & ForegroundColor.hidden.getForegroundColorCode()) == ForegroundColor.hidden.getForegroundColorCode();
    }

    public final void setHidden(boolean value) {
        this.foregroundColor = value ? this.foregroundColor | ForegroundColor.hidden.getForegroundColorCode() : this.foregroundColor & ~ForegroundColor.hidden.getForegroundColorCode();
    }

    public final boolean getNegate() {
        return (foregroundColor & ForegroundColor.negate.getForegroundColorCode()) == ForegroundColor.negate.getForegroundColorCode();
    }

    public final boolean getPositive() {
        return (foregroundColor & ForegroundColor.positive.getForegroundColorCode()) == ForegroundColor.positive.getForegroundColorCode();
    }

    public final boolean getAlternateTTSVoice() {
        return (foregroundColor & ForegroundColor.alternateTTSVoice.getForegroundColorCode()) == ForegroundColor.alternateTTSVoice.getForegroundColorCode();
    }

    public final void setAlternateTTSVoice(boolean value) {
        this.foregroundColor = value ? this.foregroundColor | ForegroundColor.alternateTTSVoice.getForegroundColorCode() : this.foregroundColor & ~ForegroundColor.alternateTTSVoice.getForegroundColorCode();
    }

    public final boolean getPopupStyleChildBoard() {
        return (foregroundColor & ForegroundColor.popupStyleChildBoard.getForegroundColorCode()) == ForegroundColor.popupStyleChildBoard.getForegroundColorCode();
    }

    public final boolean getNoRepeats() {
        return (foregroundColor & ForegroundColor.noRepeats.getForegroundColorCode()) == ForegroundColor.noRepeats.getForegroundColorCode();
    }

    public final boolean getNoRepeatsOnChildren() {
        return (foregroundColor & ForegroundColor.noRepeatsOnChildren.getForegroundColorCode()) == ForegroundColor.noRepeatsOnChildren.getForegroundColorCode();
    }

    public final boolean getTop() {
        return (backgroundColor & BackgroundColor.top.getBackgroundColorCode()) == BackgroundColor.top.getBackgroundColorCode();
    }

    public final boolean getBottom() {
        return (backgroundColor & BackgroundColor.bottom.getBackgroundColorCode()) == BackgroundColor.bottom.getBackgroundColorCode();
    }

    public final boolean getLeft() {
        return (backgroundColor & BackgroundColor.left.getBackgroundColorCode()) == BackgroundColor.left.getBackgroundColorCode();
    }

    public final boolean getRight() {
        return (backgroundColor & BackgroundColor.right.getBackgroundColorCode()) == BackgroundColor.right.getBackgroundColorCode();
    }

    public final boolean getOverlay() {
        return (backgroundColor & BackgroundColor.overlay.getBackgroundColorCode()) == BackgroundColor.overlay.getBackgroundColorCode();
    }

    /**
     * Gets the font size.
     *
     * @return the font size
     */
    public final int getFontSize() {
        return fontSize;
    }

    /**
     * Sets the font size.
     *
     * @param value the new font size
     */
    public final void setFontSize(final int value) {
        this.fontSize = value;
    }

    /**
     * Gets the zoom.
     *
     * @return the zoom
     */
    public final int getZoom() {
        return zoom;
    }

    /**
     * Sets the zoom.
     *
     * @param value the new zoom
     */
    public final void setZoom(final boolean value) {
        if (value) {
            this.zoom = 1;
        } else {
            this.zoom = 0;
        }
    }

    /**
     * Sets the zoom.
     *
     * @param value the new zoom
     */
    private void setZoom(final int value) {
        this.zoom = value;
    }

    /**
     * Gets the do not zoom pics.
     *
     * @return the do not zoom pics
     */
    public final int getDoNotZoomPics() {
        return doNotZoomPics;
    }

    /**
     * Sets the do not zoom pics.
     *
     * @param value the new do not zoom pics
     */
    public final void setDoNotZoomPics(final boolean value) {
        if (value) {
            this.doNotZoomPics = 1;
        } else {
            this.doNotZoomPics = 0;
        }
    }

    /**
     * Sets the do not zoom pics.
     *
     * @param value the new do not zoom pics
     */
    private void setDoNotZoomPics(final int value) {
        this.doNotZoomPics = value;
    }

    /**
     * Gets the do not add to phrase bar.
     *
     * @return the do not add to phrase bar
     */
    public final int getDoNotAddToPhraseBar() {
        return doNotAddToPhraseBar;
    }

    /**
     * Sets the do not add to phrase bar.
     *
     * @param value the new do not add to phrase bar
     */
    public final void setDoNotAddToPhraseBar(final boolean value) {
        if (value) {
            this.doNotAddToPhraseBar = 1;
        } else {
            this.doNotAddToPhraseBar = 0;
        }
    }

    /**
     * Sets the do not add to phrase bar.
     *
     * @param value the new do not add to phrase bar
     */
    private void setDoNotAddToPhraseBar(final int value) {
        this.doNotAddToPhraseBar = value;
    }

    /**
     * Gets the tts speech prompt.
     *
     * @return the tts speech prompt
     */
    public final String getTtsSpeechPrompt() {
        if (this.ttsSpeechPrompt == null) {
            return "";
        } else {
            return this.ttsSpeechPrompt;
        }
    }

    /**
     * Sets the tts speech prompt.
     *
     * @param value the new tts speech prompt
     */
    public final void setTtsSpeechPrompt(final String value) {
        if (value == null) {
            this.ttsSpeechPrompt = "";
        } else {
            this.ttsSpeechPrompt = value;
        }
    }

    /**
     * Gets the external url.
     *
     * @return the external url
     */
    public final String getExternalUrl() {
        if (this.externalUrl == null) {
            return "";
        } else {
            return externalUrl;
        }
    }

    /**
     * Sets the external url.
     *
     * @param value the new external url
     */
    final void setExternalUrl(final String value) {
        if (value == null) {
            this.externalUrl = "";
        } else {
            this.externalUrl = value;
        }
    }

    /**
     * Gets the alternate tts text.
     *
     * @return the alternate tts text
     */
    public final String getAlternateTtsText() {
        return alternateTtsText;
    }

    /**
     * Sets the alternate tts text.
     *
     * @param value the new alternate tts text
     */
    public final void setAlternateTtsText(final String value) {
        if (value == null) {
            this.alternateTtsText = "";
        } else {
            this.alternateTtsText = value;
        }
    }

    /**
     * Play movie.
     *
     * @param context the context
     */
    public final void playMovie(final Context context) {
        Intent localIntent = new Intent(context.getApplicationContext(), VideoPlayerController.class);
        File fileDir = Utility.getMyTalkFilesDir(context);
        String str3 = url2.replace(" ", "-").replace("/", "-");
        String fileName = fileDir.getPath() + "/" + str3;
        localIntent.putExtra(VideoPlayerController.INTENT_EXTRA_PATH, fileName);
        localIntent.putExtra(VideoPlayerController.INTENT_EXTRA_TITLE, text);
        context.startActivity(localIntent);
    }

    /**
     * Navigate to child board.
     *
     * @param context    the context
     * @param isEditable the is editable
     */
    public final void navigateToChildBoard(final Board context, final boolean isEditable) {
        Intent localIntent = new Intent(context, Board.class);
        localIntent.setAction("com.MTA.MyTalkMobile.Board");
        localIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, getChildBoardId());
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, text);
        localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
        context.startActivityForResult(localIntent, Board.RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                null);
    }

    public final void navigateToLinkedChildBoard(final Board context, final boolean isEditable) {
        Intent localIntent = new Intent(context.getApplicationContext(), Board.class);
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, getChildBoardLinkId());
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, text);
        localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
        context.startActivityForResult(localIntent, Board.RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                null);
    }

    public final void navigateToWordVariationBoard(final Board context, final boolean isEditable) {
        Intent localIntent = new Intent(context.getApplicationContext(), Board.class);
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, Board.WORD_VARIANT_BOARD);
        localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME, text);
        localIntent.putExtra(Board.INTENT_EXTRA_CELL_PIC, url);
        localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, isEditable);
        context.startActivityForResult(localIntent, Board.RequestCode.CHILD_BOARD_NAVIGATE.ordinal(),
                null);
    }

    /**
     * Play sound.
     *
     * @param context     the context
     * @param mediaPlayer the media player
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final void playSound(final Context context, final MediaPlayer mediaPlayer, final Runnable callback)
            throws Exception {
        File fileDir = Utility.getMyTalkFilesDir(context);
        if (url2.contains("/")) {
            String str3 = url2.replace(" ", "-").replace("/", "-");
            String fileName = fileDir.getPath() + "/" + str3;
            FileInputStream fileInputStream = new FileInputStream(fileName);
            mediaPlayer.reset();
            if (callback != null) {
                mediaPlayer.setOnCompletionListener(mp -> callback.run());
            }
            mediaPlayer.setDataSource(fileInputStream.getFD());
            mediaPlayer.prepareAsync();
            fileInputStream.close();
        } else {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor afd = assetManager.openFd(url2);
            FileDescriptor fd = afd.getFileDescriptor();
            mediaPlayer.reset();
            if (callback != null) {
                mediaPlayer.setOnCompletionListener(mp -> callback.run());
            }
            mediaPlayer.setDataSource(fd, afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepareAsync();
            afd.close();
        }
    }

    /**
     * Checks if the cell can be voiced.
     *
     * @param context the context
     * @return true, if it can be voiced
     */
    private boolean CanQueueBeVoiced(final Context context) {
        boolean hasSound = url2 != null && !url2.isEmpty() && getMediaType(context) == MediaType.sound;
        boolean hasText = (ttsSpeechPrompt != null && !ttsSpeechPrompt.isEmpty()) ||
                getTextToBeVoiced() != null;
        return hasSound || hasText;
    }

    public final boolean CanBeVoiced(final Context context) {
        boolean hasSound = url2 != null && !url2.isEmpty() && getMediaType(context) == MediaType.sound;
        boolean hasText = getTextToBeVoiced() != null;
        return hasSound || hasText;
    }

    /**
     * Gets the text to be voiced.
     *
     * @return the text to be voiced
     */
    private String getTextToBeVoiced() {
        if (alternateTtsText != null && !alternateTtsText.isEmpty()) {
            return alternateTtsText;
        }
        if (text != null && !text.isEmpty()) {
            if (text.equalsIgnoreCase("Empty") ||
                    text.equalsIgnoreCase("Empty0") ||
                    text.equalsIgnoreCase("Empty1") ||
                    text.equalsIgnoreCase("Empty2") ||
                    text.equalsIgnoreCase("Empty3") ||
                    text.equalsIgnoreCase("Empty4") ||
                    text.equalsIgnoreCase("Empty5") ||
                    text.equalsIgnoreCase("Empty6") ||
                    text.equalsIgnoreCase("Empty7") ||
                    text.equalsIgnoreCase("Empty8") ||
                    text.equalsIgnoreCase("Empty9") ||
                    text.equalsIgnoreCase("Empty10"))
                return null;
            return text;
        }
        return null;
    }

    /**
     * Checks if it is possible to zoom the cell.
     *
     * @param context    the context
     * @param zoomPic    the zoom pic
     * @param phraseMode the phrase mode
     * @return true, if the cell can be zoomed
     */
    public final boolean canBeZoomed(final Context context, final boolean zoomPic,
                                     final boolean phraseMode) {
        return doNotZoomPics == 0 && // if cell allows zooming
                url != null && !url.isEmpty() // and it has a picture
                && (zoomPic || zoom == 1) && // and global zoom OR cell
                // zoom is ON
                !phraseMode && // and we are not in phrase mode
                getChildBoardId() == 0 // and there is no child board
                && getMediaType(context) != MediaType.movie;
    }

    /**
     * Voice.
     *
     * @param context     the context
     * @param mediaPlayer the media player
     * @param tts         the tts
     */
    public final void voice(final Board context, final MediaPlayer mediaPlayer,
                            final TextToSpeech tts, final Runnable callback) {
        if (CanBeVoiced(context)) {
            HashMap<String, String> hashTts = new HashMap<>();
            hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
            UtteranceProgressListener listener = new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    callback.run();
                }

                @Override
                public void onError(String utteranceId) {
                    callback.run();
                }
            };
            if (getMediaType(context) == MediaType.sound) {
                try {
                    playSound(context, mediaPlayer, callback);
                } catch (Exception e) {
                    if (getTextToBeVoiced() != null && tts != null) {
                        if (callback != null) {
                            tts.setOnUtteranceProgressListener(listener);
                        }
                        context
                                .getVoice(!getAlternateTTSVoice())
                                .speak(getTextToBeVoiced(), 2, hashTts);
                    }
                }
            } else if (getTextToBeVoiced() != null && tts != null) {
                if (callback != null) {
                    tts.setOnUtteranceProgressListener(listener);
                }
                context.getVoice(!getAlternateTTSVoice()).speak(getTextToBeVoiced(), 2, hashTts);
            }
        }
    }

    public final void voiceQueue(final Board context, final MediaPlayer mediaPlayer) {
        if (CanQueueBeVoiced(context)) {
            HashMap<String, String> hashTts = new HashMap<>();
            hashTts.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "id");
            if (ttsSpeechPrompt != null && !ttsSpeechPrompt.isEmpty()) {
                context.getVoice(!getAlternateTTSVoice()).speak(ttsSpeechPrompt, TextToSpeech.QUEUE_FLUSH, hashTts);
            } else {
                if (getMediaType(context) == MediaType.sound) {
                    try {
                        playSound(context, mediaPlayer, null);
                    } catch (Exception e) {
                        context.getVoice(!getAlternateTTSVoice()).speak(getTextToBeVoiced(), 2, hashTts);
                    }
                } else if (getTextToBeVoiced() != null) {
                    context.getVoice(!getAlternateTTSVoice()).speak(getTextToBeVoiced(), 2, hashTts);
                }
            }
        }
    }

    /**
     * Gets the image.
     *
     * @param context    the context
     * @param cellWidth  the cell width
     * @param cellHeight the cell height
     * @return the image
     */
    public final Bitmap getImage(final Context context, final int cellWidth, final int cellHeight) {
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            File fileDir = Utility.getMyTalkFilesDir(context);
            if (url == null || url.length() == 0) {
                return null;
            } else if (url.contains("/")) {
                String str3 = url.replace(" ", "-").replace("/", "-");
                File localFile2 = new File(fileDir.getPath() + "/" + str3);
                inputStream = Files.newInputStream(localFile2.toPath());
            } else {
                inputStream = context.getAssets().open(url);
            }
            BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
            bitmapOption.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, bitmapOption);

            inputStream.close();

            if (url == null || url.length() == 0) {
                return null;
            } else if (url.contains("/")) {
                String str3 = url.replace(" ", "-").replace("/", "-");
                File localFile2 = new File(fileDir.getPath() + "/" + str3);
                inputStream = Files.newInputStream(localFile2.toPath());
            } else {
                AssetManager localAssetManager1 = context.getAssets();
                inputStream = localAssetManager1.open(url);
            }

            float bitmapWidth = bitmapOption.outWidth;
            int sampleWidth = Math.round(bitmapWidth / cellWidth);
            float bitmapHeight = bitmapOption.outHeight;
            int sampleHeight = Math.round(bitmapHeight / (float) cellHeight);
            int sampleSize = Math.max(sampleWidth, sampleHeight);
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);

            inputStream.close();

            if (type == GO_HOME_COMMAND) {
                ImageView imageView = new ImageView(context);
                imageView.setMaxHeight(cellHeight);
                imageView.setMinimumHeight(cellHeight);
                imageView.setMaxWidth(cellWidth);
                imageView.setMinimumWidth(cellWidth);
                imageView.setDrawingCacheEnabled(true);
                imageView.setImageResource(R.drawable.home);
                bitmap = imageView.getDrawingCache();
                return bitmap;
            } else if (type == GO_BACK_COMMAND) {
                ImageView imageView = new ImageView(context);
                imageView.setMaxHeight(cellHeight);
                imageView.setMinimumHeight(cellHeight);
                imageView.setMaxWidth(cellWidth);
                imageView.setMinimumWidth(cellWidth);
                imageView.setDrawingCacheEnabled(true);
                imageView.setImageResource(R.drawable.back);
                bitmap = imageView.getDrawingCache();
                return bitmap;
            }
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public enum BackgroundColor {
        Default(0),
        black(1),
        darkGray(2),
        lightGray(3),
        white(4),
        gray(5),
        pink(6),
        green(7),
        blue(8),
        cyan(9),
        yellow(10),
        magenta(11),
        orange(12),
        purple(13),
        brown(14),
        clear(15),
        red(16),

        // overloaded bit flags
        top(0x0001 << 8),
        bottom(0x0001 << 9),
        left(0x0001 << 10),
        right(0x0001 << 11),
        overlay(0x0001 << 12);

        private final int backgroundColorCode;

        BackgroundColor(int backgroundColorCode) {
            this.backgroundColorCode = backgroundColorCode;
        }

        int getBackgroundColorCode() {
            return this.backgroundColorCode;
        }

    }

    enum ForegroundColor {
        Default(0),
        black(1),
        darkGray(2),
        lightGray(3),
        white(4),
        gray(5),
        pink(6),
        green(7),
        blue(8),
        cyan(9),
        yellow(10),
        magenta(11),
        orange(12),
        purple(13),
        brown(14),
        clear(15),
        red(16),
        hidden(0x0001 << 8),
        negate(0x0001 << 9),
        noRepeatsOnChildren(0x0001 << 10),
        noRepeats(0x0001 << 11),
        positive(0x0001 << 12),
        alternateTTSVoice(0x0001 << 13),
        popupStyleChildBoard(0x0001 << 14);

        private final int foregroundColorCode;

        ForegroundColor(int foregroundColorCode) {
            this.foregroundColorCode = foregroundColorCode;
        }

        int getForegroundColorCode() {
            return this.foregroundColorCode;
        }

    }

    /**
     * The Enum MediaType.
     */
    public enum MediaType {

        /**
         * The unknown.
         */
        unknown,
        /**
         * The movie.
         */
        movie,
        /**
         * The sound.
         */
        sound
    }

    /**
     * The Enum columnIndex.
     */
    private enum columnIndex {

        /**
         * The i phone id.
         */
        iPhoneId,
        /**
         * The web id.
         */
        webId,
        /**
         * The text.
         */
        text,
        /**
         * The url.
         */
        url,
        /**
         * The url2.
         */
        url2,

        /**
         * The type.
         */
        type,
        /**
         * The create date.
         */
        createDate,
        /**
         * The update date.
         */
        updateDate,
        /**
         * The board id.
         */
        boardId,

        /**
         * The row.
         */
        row,
        /**
         * The column.
         */
        column,
        /**
         * The child board id.
         */
        childBoardId,
        /**
         * The user id.
         */
        userId,
        /**
         * The child board link id.
         */
        childBoardLinkId,

        /**
         * The total uses.
         */
        totalUses,
        /**
         * The session uses.
         */
        sessionUses,
        /**
         * The background color.
         */
        backgroundColor,
        /**
         * The foreground color.
         */
        foregroundColor,

        /**
         * The font size.
         */
        fontSize,
        /**
         * The zoom.
         */
        zoom,
        /**
         * The do not add to phrase bar.
         */
        doNotAddToPhraseBar,

        /**
         * The do not zoom pics.
         */
        doNotZoomPics,
        /**
         * The tts speech prompt.
         */
        ttsSpeechPrompt,
        /**
         * The external url.
         */
        externalUrl,
        /**
         * The alternate tts text.
         */
        alternateTtsText,
        hotspotStyle
    }
}
