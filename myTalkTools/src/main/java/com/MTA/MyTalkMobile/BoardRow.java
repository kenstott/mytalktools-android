/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Json.JsonLexRecord;
import com.MTA.MyTalkMobile.Json.JsonLexRecords;
import com.MTA.MyTalkMobile.Server.GetWordVariants;
import com.MTA.MyTalkMobile.Utilities.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import proguard.annotation.Keep;

// TODO: Auto-generated Javadoc

/**
 * The Class BoardRow.
 */
@Keep
public class BoardRow {

    /**
     * The Constant RANDOM_LOWER_LIMIT.
     */
    private static final int RANDOM_LOWER_LIMIT = 1;

    /**
     * The Constant RANDOM_UPPER_LIMIT.
     */
    private static final int RANDOM_UPPER_LIMIT = 3;

    /**
     * The Constant MAX_BOARD_COLUMNS.
     */
    private static final int MAX_BOARD_COLUMNS = 10;

    /**
     * The Constant EMPTY.
     */
    private static final String EMPTY = "Empty";

    /**
     * The Constant DATE_FORMAT.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * The table name.
     */
    private static final String tableName = "board";
    /**
     * The column names.
     */
    private static final String[] columnNames = {"iphone_board_id", "web_board_id", "board_name",
            "board_rows", "board_clms", "create_date", "update_date", "user_id", "sort1", "sort2",
            "sort3"};
    /**
     * The board.
     */
    private Context board;
    /**
     * The board name.
     */
    private String boardName;
    /**
     * The columns.
     */
    private int columns;
    /**
     * The contents.
     */
    private List<BoardContent> contents;
    /**
     * The create date.
     */
    private Date createDate;
    /**
     * The i phone board id.
     */
    private int iPhoneBoardId;
    /**
     * The rows.
     */
    private int rows;
    /**
     * The update date.
     */
    private Date updateDate;
    /**
     * The user id.
     */
    private int userId;
    /**
     * The web board id.
     */
    private int webBoardId;
    /**
     * The sort3.
     */
    private sortOrder sort1, sort2, sort3;
    private String url;

    /**
     * Instantiates a new board row.
     */
    public BoardRow() {
    }

    /**
     * Instantiates a new board row.
     *
     * @param paramColumns the param columns
     * @param paramRows    the param rows
     * @param paramUserId  the param user id
     * @param name         the name
     * @param paramBoard   the param board
     * @param sortOrder1   the sort order1
     * @param sortOrder2   the sort order2
     * @param sortOrder3   the sort order3
     */
    public BoardRow(final int paramColumns, final int paramRows, final int paramUserId,
                    final String name, final Context paramBoard, final sortOrder sortOrder1,
                    final sortOrder sortOrder2, final sortOrder sortOrder3) {
        this.board = paramBoard;
        setIPhoneBoardId(0);
        setBoardName(name);
        setRows(paramRows);
        setColumns(paramColumns);
        setCreateDate(Utility.getGMTDate());
        setUpdateDate(getCreateDate());
        setUserId(paramUserId);
        setWebBoardId(0);
        setSort1(sortOrder1);
        setSort2(sortOrder2);
        setSort3(sortOrder3);
    }

    /**
     * Instantiates a new board row.
     *
     * @param cursor     the cursor
     * @param paramBoard the param board
     */
    private BoardRow(final Cursor cursor, final Board paramBoard) {
        this.board = paramBoard;
        createBoardRow(cursor, this);
    }

    public BoardRow(final int id, final Board paramBoard, String boardName, String url) {
        this.board = paramBoard;
        this.boardName = boardName;
        this.url = url;
        switch (id) {
            case Board.WORD_VARIANT_BOARD:
                createWordVariantBoard(this);
                break;
            case Board.SCHEDULED_BOARD:
                createScheduledBoardRow(this);
                break;
            case Board.LOCATIONS_BOARD:
                createLocationsBoardRow(this);
                break;
            case Board.MOST_RECENTS_BOARD:
                createMostRecentlyUsedBoardRow(this);
                break;
            case Board.MOST_USED_BOARD:
                createMostUsedBoardRow(this);
                break;
            case Board.CONTACTS_BOARD:
                createContactsBoardRow(this);
                break;
            default:
                SQLiteDatabase database = Database.getDatabase(paramBoard);
                Cursor cursor =
                        database.query("board", null, "iphone_board_id = " + id, null, null, null, null);
                if (cursor == null) createBoardRow(null, this);
                else if (cursor.moveToFirst()) {
                    createBoardRow(cursor, this);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
                break;
        }

    }

    /**
     * Instantiates a new board row.
     *
     * @param id         the id
     * @param paramBoard the param board
     */
    public BoardRow(final int id, final Board paramBoard) {
        this.board = paramBoard;
        switch (id) {
            case Board.WORD_VARIANT_BOARD:
                createWordVariantBoard(this);
                break;
            case Board.SCHEDULED_BOARD:
                createScheduledBoardRow(this);
                break;
            case Board.LOCATIONS_BOARD:
                createLocationsBoardRow(this);
                break;
            case Board.MOST_RECENTS_BOARD:
                createMostRecentlyUsedBoardRow(this);
                break;
            case Board.MOST_USED_BOARD:
                createMostUsedBoardRow(this);
                break;
            default:
                SQLiteDatabase database = Database.getDatabase(paramBoard);
                Cursor cursor = null;
                try {
                    cursor =
                            database.query("board", null, "iphone_board_id = " + id, null, null, null, null);
                } catch (Exception ignored) {

                }
                if (cursor != null && cursor.moveToFirst()) {
                    createBoardRow(cursor, this);
                }
                if ((cursor != null) && (!cursor.isClosed())) {
                    cursor.close();
                }
                break;
        }
    }

    /**
     * Gets the all board rows.
     *
     * @param board the board
     * @return the all board rows
     */
    public static List<BoardRow> getAllBoardRows(final Board board) {
        ArrayList<BoardRow> result = new ArrayList<>();
        SQLiteDatabase database = Database.getDatabase(board);
        Cursor cursor =
                database.query(tableName, columnNames, "board_name <> ''", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new BoardRow(cursor, board));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    /**
     * Creates the board row.
     *
     * @param cursor   the cursor
     * @param boardRow the board row
     */
    private void createBoardRow(final Cursor cursor, final BoardRow boardRow) {
        if (cursor == null) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        boardRow.setIPhoneBoardId(cursor.getInt(columnIndex.iPhoneBoardId.ordinal()));
        boardRow.setBoardName(cursor.getString(columnIndex.boardName.ordinal()));
        boardRow.setRows(cursor.getInt(columnIndex.rows.ordinal()));
        boardRow.setColumns(cursor.getInt(columnIndex.columns.ordinal()));
        try {
            boardRow.setCreateDate(dateFormat.parse(cursor.getString(columnIndex.createDate.ordinal())));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            boardRow.setUpdateDate(dateFormat.parse(cursor.getString(columnIndex.updateDate.ordinal())));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        boardRow.setUserId(cursor.getInt(columnIndex.userId.ordinal()));
        boardRow.setWebBoardId(cursor.getInt(columnIndex.webBoardId.ordinal()));
        boardRow.setContents(getContentList(this.iPhoneBoardId));
        boardRow.setCreateDate(cursor.getString(columnIndex.createDate.ordinal()));
        try {
            boardRow.setSort1(BoardRow.sortOrder.values()[cursor.getInt(columnIndex.sort1.ordinal())]);
            boardRow.setSort2(BoardRow.sortOrder.values()[cursor.getInt(columnIndex.sort2.ordinal())]);
            boardRow.setSort3(BoardRow.sortOrder.values()[cursor.getInt(columnIndex.sort3.ordinal())]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createWordVariantBoard(final BoardRow boardRow) {
        ArrayList<JsonLexRecord> d = new GetWordVariants(boardName, board).execute();
        if (d == null) {
            return;
        }
        JsonLexRecords j = new JsonLexRecords(d);
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(board.getApplicationContext());
        String colorKey = sp.getString(AppPreferences.PREF_KEY_COLOR_KEY, "Goosen");
        int colorKeyCode = colorKey.equals("Goosen") ? 1 : 0;
        boolean autoSpeechParts = sp.getBoolean(AppPreferences.PREF_KEY_AUTO_SPEECH_PARTS, false);
        if (autoSpeechParts) {
            List<JsonLexRecord> uniqueWords = j.getUniqueWordsAndTypes(colorKeyCode);
            int columns = uniqueWords.size() / 2;
            columns = Math.min(columns, 4);
            if (columns == 0) columns = 1;
            int rows = uniqueWords.size() / columns;
            if (uniqueWords.size() % columns > 0) rows++;
            boardRow.setColumns(columns);
            boardRow.setRows(rows);
            boardRow.iPhoneBoardId = Board.WORD_VARIANT_BOARD;
            boardRow.setCreateDate(new Date());
            boardRow.setUpdateDate(new Date());
            boardRow.setUserId(0);
            ArrayList<BoardContent> contents = new ArrayList<>();
            for (JsonLexRecord word : uniqueWords) {
                BoardContent content = new BoardContent();
                content.setText(word.Value);
                content.setBackgroundColor(word.colorCode);
                content.setBoardId(this.iPhoneBoardId);
                content.setiPhoneId(-1);
                content.setUrl(this.url);
                contents.add(content);
            }
            boardRow.setContents(contents);
            boardRow.setSort1(sortOrder.BackgroundColor);
            boardRow.setSort2(sortOrder.Alphabetic);
            boardRow.setSort3(sortOrder.NotSorted);
        } else {
            List<String> uniqueWords = j.getUniqueWords();
            int columns = uniqueWords.size() / 2;
            columns = Math.min(columns, 4);
            if (columns == 0) columns = 1;
            int rows = uniqueWords.size() / columns;
            if (uniqueWords.size() % columns > 0) rows++;
            boardRow.setColumns(columns);
            boardRow.setRows(rows);
            boardRow.iPhoneBoardId = Board.WORD_VARIANT_BOARD;
            boardRow.setCreateDate(new Date());
            boardRow.setUpdateDate(new Date());
            boardRow.setUserId(0);
            ArrayList<BoardContent> contents = new ArrayList<>();
            for (String word : uniqueWords) {
                BoardContent content = new BoardContent();
                content.setText(word);
                content.setiPhoneId(-1);
                content.setBoardId(this.iPhoneBoardId);
                contents.add(content);
            }
            boardRow.setContents(contents);
            boardRow.setSort1(sortOrder.Alphabetic);
            boardRow.setSort2(sortOrder.NotSorted);
            boardRow.setSort3(sortOrder.NotSorted);
        }
    }

    private ArrayList<BoardContent> getContacts(Context board, String[] args) {

        ArrayList<BoardContent> contentList = new ArrayList<>();
        String phoneNumber;
        String email;
        int emailType;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String photoData;
        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;
        StringBuilder output;
        ContentResolver contentResolver = board.getContentResolver();
        StringBuilder selection = null;
        for (String arg : args) {
            if (selection == null) {
                selection = new StringBuilder(ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%" + arg + "%'");
            } else {
                selection.append(" OR " + ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%").append(arg).append("%'");
            }
        }
        Cursor cursor = contentResolver.query(CONTENT_URI, null, selection != null ? selection.toString() : "", null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                BoardContent boardContent = new BoardContent();
                output = new StringBuilder();
                // Update the progress message
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                photoData = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                output.append("\n").append(name);
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    //This is to read multiple phone numbers associated with the same contact dialog
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor != null && phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        int tt = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        CharSequence t = ContactsContract.CommonDataKinds.Phone.getTypeLabel(board.getResources(), tt, "");
                        output.append("\n").append(t != null ? (t + ": ") : "").append(phoneNumber);
                    }
                    if (phoneCursor != null) phoneCursor.close();
                }
                // Read every email id associated with the contact dialog
                Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                while (emailCursor != null && emailCursor.moveToNext()) {
                    email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                    emailType = emailCursor.getInt(emailCursor.getColumnIndex(EMAIL_TYPE));
                    CharSequence t = ContactsContract.CommonDataKinds.Email.getTypeLabel(board.getResources(), emailType, "");
                    output.append("\n").append(t != null ? (t + ": ") : "").append(email);
                }
                if (emailCursor != null) emailCursor.close();

                Cursor addressCursor = contentResolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                while (addressCursor != null && addressCursor.moveToNext()) {
                    String address = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                    int tt = addressCursor.getInt(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
                    CharSequence t = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(board.getResources(), tt, "");
                    output.append("\n").append(t != null ? (t + ": ") : "").append(address);
                }
                if (addressCursor != null) addressCursor.close();

                String[] columns = {
                        ContactsContract.CommonDataKinds.Event.START_DATE,
                        ContactsContract.CommonDataKinds.Event.TYPE,
                        ContactsContract.CommonDataKinds.Event.MIMETYPE,
                };
                String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY +
                        " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + contact_id;
                String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
                Cursor birthdayCur = contentResolver.query(ContactsContract.Data.CONTENT_URI, columns, where, null, sortOrder);
                Log.d("BDAY", birthdayCur.getCount() + "");
                if (birthdayCur.getCount() > 0) {
                    while (birthdayCur.moveToNext()) {
                        String birthday = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                        output.append("\nBirthday :").append(birthday);
                        Log.d("BDAY", birthday);
                    }
                }
                birthdayCur.close();
                // Add the contact dialog to the ArrayList
                boardContent.setText(output.toString());
                boardContent.setAlternateTtsText(name);
                boardContent.setExternalUrl("contactdialog:/" + contact_id);
                if (photoData != null) {
                    boardContent.setUrl(Uri.parse(photoData).toString());
                }
                contentList.add(boardContent);
            }
            cursor.close();
        }
        return contentList;
    }

    private void createContactsBoardRow(final BoardRow boardRow) {
        String[] query = this.url.split("/");
        String[] args = {};
        if (query.length == 2) {
            args = query[1].split(",");
        }
        ArrayList<BoardContent> scheduledContent = getContacts(board, args);
        boardRow.setIPhoneBoardId(Board.CONTACTS_BOARD);
        boardRow.setBoardName(board.getString(R.string.contacts));
        boardRow.setColumns(Math.min(scheduledContent.size(), 4));
        int rows = scheduledContent.size() / 4;
        if (scheduledContent.size() % 4 > 0) rows++;
        boardRow.setRows(rows);
        boardRow.setCreateDate(new Date());
        boardRow.setUpdateDate(new Date());
        boardRow.setUserId(0);
        boardRow.setWebBoardId(Board.SCHEDULED_BOARD);
        boardRow.setContents(scheduledContent);
        boardRow.setSort1(sortOrder.Alphabetic);
        boardRow.setSort2(sortOrder.NotSorted);
        boardRow.setSort3(sortOrder.NotSorted);
    }

    private void createScheduledBoardRow(final BoardRow boardRow) {
        ArrayList<BoardContent> scheduledContent = BoardContent.getScheduledContent(board);
        boardRow.setIPhoneBoardId(Board.SCHEDULED_BOARD);
        boardRow.setBoardName(board.getString(R.string.scheduled));
        boardRow.setColumns(Math.min(scheduledContent.size(), 4));
        int rows = scheduledContent.size() / 4;
        if (scheduledContent.size() % 4 > 0) rows++;
        boardRow.setRows(rows);
        boardRow.setCreateDate(new Date());
        boardRow.setUpdateDate(new Date());
        boardRow.setUserId(0);
        boardRow.setWebBoardId(Board.SCHEDULED_BOARD);
        boardRow.setContents(scheduledContent);
        boardRow.setSort1(sortOrder.Alphabetic);
        boardRow.setSort2(sortOrder.NotSorted);
        boardRow.setSort3(sortOrder.NotSorted);
    }

    private void createLocationsBoardRow(final BoardRow boardRow) {
        ArrayList<BoardContent> locationsContent = BoardContent.getLocationContent(board);
        boardRow.setIPhoneBoardId(Board.LOCATIONS_BOARD);
        boardRow.setBoardName(board.getString(R.string.locations));
        boardRow.setColumns(Math.min(locationsContent.size(), 4));
        int rows = locationsContent.size() / 4;
        if (locationsContent.size() % 4 > 0) rows++;
        boardRow.setRows(rows);
        boardRow.setCreateDate(new Date());
        boardRow.setUpdateDate(new Date());
        boardRow.setUserId(0);
        boardRow.setWebBoardId(Board.LOCATIONS_BOARD);
        boardRow.setContents(locationsContent);
        boardRow.setSort1(sortOrder.Alphabetic);
        boardRow.setSort2(sortOrder.NotSorted);
        boardRow.setSort3(sortOrder.NotSorted);
    }

    private void createMostRecentlyUsedBoardRow(final BoardRow boardRow) {
        ArrayList<BoardContent> mostRecentlyUsed = BoardContent.getMostRecentlyUsedContent(board);
        boardRow.setIPhoneBoardId(Board.MOST_RECENTS_BOARD);
        boardRow.setBoardName(board.getString(R.string.recents));
        boardRow.setColumns(Math.min(mostRecentlyUsed.size(), 4));
        int rows = mostRecentlyUsed.size() / 4;
        if (mostRecentlyUsed.size() % 4 > 0) rows++;
        boardRow.setRows(rows);
        boardRow.setCreateDate(new Date());
        boardRow.setUpdateDate(new Date());
        boardRow.setUserId(0);
        boardRow.setWebBoardId(Board.MOST_RECENTS_BOARD);
        boardRow.setContents(mostRecentlyUsed);
        boardRow.setSort1(sortOrder.MostRecent);
        boardRow.setSort2(sortOrder.NotSorted);
        boardRow.setSort3(sortOrder.NotSorted);
    }

    private void createMostUsedBoardRow(final BoardRow boardRow) {
        ArrayList<BoardContent> mostUsed = BoardContent.getMostUsedContent(board);
        boardRow.setIPhoneBoardId(Board.MOST_USED_BOARD);
        boardRow.setBoardName(board.getString(R.string.most_used));
        boardRow.setColumns(Math.min(mostUsed.size(), 4));
        int rows = mostUsed.size() / 4;
        if (mostUsed.size() % 4 > 0) rows++;
        boardRow.setRows(rows);
        boardRow.setCreateDate(new Date());
        boardRow.setUpdateDate(new Date());
        boardRow.setUserId(0);
        boardRow.setWebBoardId(Board.MOST_USED_BOARD);
        boardRow.setContents(mostUsed);
        boardRow.setSort1(sortOrder.UseFrequency);
        boardRow.setSort2(sortOrder.NotSorted);
        boardRow.setSort3(sortOrder.NotSorted);
    }

    /**
     * Gets the content values.
     *
     * @return the content values
     */
    private ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put(columnNames[columnIndex.boardName.ordinal()], this.boardName);
        cv.put(columnNames[columnIndex.rows.ordinal()], this.rows);
        cv.put(columnNames[columnIndex.columns.ordinal()], this.columns);
        cv.put(columnNames[columnIndex.createDate.ordinal()],
                Utility.getSQLiteDateString(this.createDate));
        cv.put(columnNames[columnIndex.updateDate.ordinal()], Utility.getGMTDateSQLiteString());
        cv.put(columnNames[columnIndex.userId.ordinal()], this.userId);
        try {
            cv.put(columnNames[columnIndex.sort1.ordinal()], this.sort1.ordinal());
            cv.put(columnNames[columnIndex.sort2.ordinal()], this.sort2.ordinal());
            cv.put(columnNames[columnIndex.sort3.ordinal()], this.sort3.ordinal());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cv;
    }

    /**
     * Gets the new id.
     *
     * @return the new id
     */
    private int getNewId() {
        SQLiteDatabase database = Database.getDatabase(board);
        Cursor cursor =
                database.query(tableName, new String[]{columnNames[columnIndex.iPhoneBoardId.ordinal()]},
                        null, null, null, null, columnNames[columnIndex.iPhoneBoardId.ordinal()]);
        cursor.moveToLast();
        int newId = cursor.getInt(0) + 1;
        cursor.close();
        return newId;
    }

    /**
     * Persist.
     *
     * @param createChildContent the create child content
     * @return the long
     */
    public final long persist(final Boolean createChildContent) {
        try {
            if (this.iPhoneBoardId == 0) {
                ContentValues cv = getContentValues();
                iPhoneBoardId = getNewId();
                cv.put(columnNames[columnIndex.iPhoneBoardId.ordinal()], iPhoneBoardId);
                SQLiteDatabase database = Database.getDatabase(board);
                long rowId = database.insert(tableName, null, cv);
                if (createChildContent) {
                    for (int r = 0; r < rows; r++) {
                        for (int c = 0; c < columns; c++) {
                            BoardContent newContent =
                                    new BoardContent(0, 0, EMPTY + (r * columns + c + 1), "", "", 1, iPhoneBoardId,
                                            r, c, 0, userId, 0, 0, 0, 0, 0, 0, 0, 0, 0, "", "", "", 0);
                            newContent.persist(board);
                        }
                    }
                }
                setContents(getContentList(this.iPhoneBoardId));
                if (rowId == -1) {
                    return -1;
                }
            } else {
                long rowId =
                        Database.getDatabase(board)
                                .update(tableName, getContentValues(),
                                        columnNames[columnIndex.iPhoneBoardId.ordinal()] + " = " + this.iPhoneBoardId,
                                        null);

                if (rowId == -1) {
                    return -1;
                }
            }
            return iPhoneBoardId;
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * Adds the column.
     */
    public final void addColumn() {
        if (columns < MAX_BOARD_COLUMNS) {
            columns += 1;
            for (int r = 0; r < rows; r++) {
                BoardContent newContent =
                        new BoardContent(0, 0, "", "", "", 1, iPhoneBoardId, r, columns - 1, 0, userId, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, "", "", "", 0);
                newContent.persist(board);
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Delete column.
     */
    public final void deleteColumn() {
        if (columns > 1) {
            columns -= 1;
            for (BoardContent content : contents) {
                if (content.getColumn() == columns) {
                    content.delete(board);
                }
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Adds the row.
     */
    public final void addRow() {
        if (rows < 10) {
            rows += 1;
            for (int c = 0; c < columns; c++) {
                BoardContent newContent =
                        new BoardContent(0, 0, "", "", "", 1, iPhoneBoardId, rows - 1, c, 0, userId, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, "", "", "", 0);
                newContent.persist(board);
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Delete row.
     */
    public final void deleteRow() {
        if (rows > 1) {
            rows -= 1;
            for (BoardContent content : contents) {
                if (content.getRow() == rows) {
                    content.delete(board);
                }
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Compress.
     */
    public final void compress() {
        int row = 0, column = 0;
        if (columns > 1) {
            columns--;
            for (BoardContent content : contents) {
                content.setRow(row);
                content.setColumn(column);
                content.persist(board);
                column++;
                rows = row + 1;
                if (column == columns) {
                    column = 0;
                    row++;
                }
            }
            for (; column < columns; column++) {
                BoardContent newContent =
                        new BoardContent(0, 0, "", "", "", 1, iPhoneBoardId, row, column, 0, userId, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, "", "", "", 0);
                rows = row + 1;
                newContent.persist(board);
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Stretch.
     */
    public final void stretch() {
        int row = 0, column = 0;
        if (columns < MAX_BOARD_COLUMNS) {
            columns++;
            for (BoardContent content : contents) {
                content.setRow(row);
                content.setColumn(column);
                content.persist(board);
                column++;
                if (column == columns) {
                    column = 0;
                    row++;
                }
            }
            for (; column < columns; column++) {
                BoardContent newContent =
                        new BoardContent(0, 0, "", "", "", 1, iPhoneBoardId, row, column, 0, userId, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, "", "", "", 0);
                newContent.persist(board);
            }
            persist(true);
            setContents(getContentList(this.iPhoneBoardId));
        }
    }

    /**
     * Gets the content list.
     *
     * @param boardId the board id
     * @return the content list
     */
    private List<BoardContent> getContentList(final int boardId) {
        ArrayList<BoardContent> boardContents = new ArrayList<>();
        SQLiteDatabase localSQLiteDatabase = Database.getDatabase(board);
        /* The content table name. */
        String contentTableName = "content";
        /* The content board index. */
        String contentBoardIndex = "board_id";
        Cursor cursor =
                localSQLiteDatabase.query(contentTableName, null, contentBoardIndex + " = " + boardId,
                        null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                BoardContent localBoardContent = new BoardContent(cursor);
                boardContents.add(localBoardContent);
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return boardContents;
    }

    /**
     * Gets the board name.
     *
     * @return the board name
     */
    public final String getBoardName() {
        return this.boardName;
    }

    /**
     * Sets the board name.
     *
     * @param value the new board name
     */
    private void setBoardName(final String value) {
        this.boardName = value;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public final int getColumns() {
        return this.columns;
    }

    /**
     * Sets the columns.
     *
     * @param value the new columns
     */
    private void setColumns(final int value) {
        this.columns = value;
    }

    /**
     * Gets the contents.
     *
     * @return the contents
     */
    public final List<BoardContent> getContents() {
        return this.contents;
    }

    /**
     * Sets the contents.
     *
     * @param value the new contents
     */
    private void setContents(final List<BoardContent> value) {
        this.contents = value;
    }

    /**
     * Checks if is sorted.
     *
     * @return true, if is sorted
     */
    public final boolean isSorted() {
        return getSort1() != sortOrder.NotSorted || getSort2() != sortOrder.NotSorted
                || getSort3() != sortOrder.NotSorted;
    }

    /**
     * Compare content.
     *
     * @param type the type
     * @param arg0 the arg0
     * @param arg1 the arg1
     * @return the int
     */
    private int compareContent(final sortOrder type, final BoardContent arg0, final BoardContent arg1) {
        if (type == null) {
            return 0;
        }
        switch (type) {
            case Random:
                double lower = RANDOM_LOWER_LIMIT;
                int r = (int) ((Math.random() * ((double) RANDOM_UPPER_LIMIT - lower)) + lower);
                return r - (RANDOM_UPPER_LIMIT - RANDOM_LOWER_LIMIT);
            case Alphabetic:
                return arg0.getText().compareTo(arg1.getText());
            case UseFrequency:
                return arg1.getSessionUses() - arg0.getSessionUses();
            case MostRecent:
                return (int) (arg1.getUpdateDate().getTime() - arg0.getUpdateDate().getTime());
            case BackgroundColor:
                return arg1.getBackgroundColor() - arg0.getBackgroundColor();
            default:
                return 0;
        }
    }

    /**
     * Gets the sorted contents.
     *
     * @return the sorted contents
     */
    private List<BoardContent> getSortedContents() {
        ArrayList<BoardContent> sorted = new ArrayList<>(contents);
        Collections.sort(sorted, (arg0, arg1) -> {
            int result = compareContent(getSort1(), arg0, arg1);
            if (result != 0) {
                return result;
            }
            result = compareContent(getSort2(), arg0, arg1);
            if (result != 0) {
                return result;
            }
            return compareContent(getSort3(), arg0, arg1);
        });

        return sorted;
    }

    /**
     * Gets the creates the date.
     *
     * @return the creates the date
     */
    private Date getCreateDate() {
        return this.createDate;
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
     * Gets the rows.
     *
     * @return the rows
     */
    public final int getRows() {
        return this.rows;
    }

    /**
     * Sets the rows.
     *
     * @param value the new rows
     */
    private void setRows(final int value) {
        this.rows = value;
    }

    /**
     * Gets the update date.
     *
     * @return the update date
     */
    public final Date getUpdateDate() {
        return this.updateDate;
    }

    /**
     * Sets the update date.
     *
     * @param value the new update date
     */
    private void setUpdateDate(final Date value) {
        this.updateDate = value;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public final int getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id.
     *
     * @param value the new user id
     */
    private void setUserId(final int value) {
        this.userId = value;
    }

    /**
     * Gets the web board id.
     *
     * @return the web board id
     */
    public final int getWebBoardId() {
        return this.webBoardId;
    }

    /**
     * Sets the web board id.
     *
     * @param value the new web board id
     */
    private void setWebBoardId(final int value) {
        this.webBoardId = value;
    }

    /**
     * Gets the i phone board id.
     *
     * @return the i phone board id
     */
    public final int getIPhoneBoardId() {
        return this.iPhoneBoardId;
    }

    /**
     * Sets the i phone board id.
     *
     * @param value the new i phone board id
     */
    private void setIPhoneBoardId(final int value) {
        this.iPhoneBoardId = value;
    }

    /**
     * Gets the item.
     *
     * @param id the id
     * @return the item
     */
    public final BoardContent getItem(final int id) {
        for (BoardContent bc : this.getContents()) {
            if (bc.getiPhoneId() == id) {
                return bc;
            }
        }
        return null;
    }

    /**
     * Gets the item by index.
     *
     * @param index the index
     * @return the item by index
     */
    public final BoardContent getItemByIndex(final int index) {
        int j = index / columns;
        int m = index % columns;
        return getItem(j, m);
    }

    /**
     * Gets the sorted item.
     *
     * @param row    the row
     * @param column the column
     * @return the sorted item
     */
    public final BoardContent getSortedItem(final int row, final int column) {
        int index = row * columns + column;
        List<BoardContent> sorted = getSortedContents();
        if (sorted == null) return null;
        if (index >= sorted.size()) {
            return null;
        }
        return sorted.get(index);
    }

    /**
     * Gets the item.
     *
     * @param row    the row
     * @param column the column
     * @return the item
     */
    public final BoardContent getItem(final int row, final int column) {
        for (BoardContent localBoardContent : this.contents) {
            if ((localBoardContent.getRow() == row) && (localBoardContent.getColumn() == column)) {
                return localBoardContent;
            }
        }
        return null;
    }

    /**
     * Gets the sort2.
     *
     * @return the sort2
     */
    public final sortOrder getSort2() {
        if (sort2 == null) {
            return sortOrder.NotSorted;
        }
        return sort2;
    }

    /**
     * Sets the sort2.
     *
     * @param value the new sort2
     */
    public final void setSort2(final sortOrder value) {
        if (value == null) {
            this.sort2 = sortOrder.NotSorted;
        } else {
            this.sort2 = value;
        }
    }

    /**
     * Gets the sort1.
     *
     * @return the sort1
     */
    public final sortOrder getSort1() {
        if (sort1 == null) {
            return sortOrder.NotSorted;
        }
        return sort1;
    }

    /**
     * Sets the sort1.
     *
     * @param value the new sort1
     */
    public final void setSort1(final sortOrder value) {
        if (value == null) {
            this.sort1 = sortOrder.NotSorted;
        } else {
            this.sort1 = value;
        }
    }

    /**
     * Gets the sort3.
     *
     * @return the sort3
     */
    public final sortOrder getSort3() {
        if (sort3 == null) {
            return sortOrder.NotSorted;
        }
        return sort3;
    }

    /**
     * Sets the sort3.
     *
     * @param value the new sort3
     */
    public final void setSort3(final sortOrder value) {
        if (value == null) {
            this.sort3 = sortOrder.NotSorted;
        } else {
            this.sort3 = value;
        }
    }

    /**
     * The Enum columnIndex.
     */
    private enum columnIndex {

        /**
         * The i phone board id.
         */
        iPhoneBoardId,
        /**
         * The web board id.
         */
        webBoardId,
        /**
         * The board name.
         */
        boardName,
        /**
         * The rows.
         */
        rows,
        /**
         * The columns.
         */
        columns,
        /**
         * The create date.
         */
        createDate,
        /**
         * The update date.
         */
        updateDate,
        /**
         * The user id.
         */
        userId,
        /**
         * The sort1.
         */
        sort1,
        /**
         * The sort2.
         */
        sort2,
        /**
         * The sort3.
         */
        sort3
    }

    /**
     * The Enum sortOrder.
     */
    public enum sortOrder {

        /**
         * The Not sorted.
         */
        NotSorted,
        /**
         * The Random.
         */
        Random,
        /**
         * The Alphabetic.
         */
        Alphabetic,
        /**
         * The Use frequency.
         */
        UseFrequency,
        /**
         * The Background color.
         */
        BackgroundColor,

        MostRecent
    }
}
