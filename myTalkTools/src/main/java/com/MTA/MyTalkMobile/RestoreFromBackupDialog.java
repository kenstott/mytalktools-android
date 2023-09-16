/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Json.JsonDocumentFileInfo;
import com.MTA.MyTalkMobile.Server.GetDocumentFileListRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

// TODO: Auto-generated Javadoc

/**
 * The Class RestoreFromBackupDialog.
 */
@Keep
class RestoreFromBackupDialog extends Dialog {

    /**
     * The user name.
     */
    private static String userName = null;

    /**
     * The board name.
     */
    private static String boardName = null;

    /**
     * The ContactAdapter.
     */
    private final BackupAdapter adapter;

    /**
     * The inflater.
     */
    private final LayoutInflater inflater;

    /**
     * The board.
     */
    private final Board board;

    /**
     * Instantiates a new restore from backup dialog.
     *
     * @param paramBoard     the param board
     * @param paramUserName  the param user name
     * @param paramBoardName the param board name
     */
    public RestoreFromBackupDialog(final Board paramBoard, final String paramUserName,
                                   final String paramBoardName) {
        super(paramBoard);
        this.board = paramBoard;
        userName = paramUserName;
        boardName = paramBoardName;
        this.inflater = (LayoutInflater) paramBoard.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.adapter = new BackupAdapter(paramBoard);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(false);
        paramBoard.setRestoreBackupFilename(null);
    }

    /**
     * Restore backup.
     *
     * @param fileInfo the file info
     */
    private void restoreBackup(final JsonDocumentFileInfo fileInfo) {
        this.board.setRestoreBackupFilename(fileInfo);
        cancel();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.restore_from_backup);
        setTitle(R.string.select_a_backup_to_restore);
        /* The list view. */
        ListView listView = findViewById(R.id.restoreFromBackupListView);
        listView.setAdapter(adapter);
        new GetFileList(adapter).execute();
    }

    /**
     * The Class GetFileList.
     */
    static class GetFileList extends AsyncTask<Void, JsonDocumentFileInfo, Void> {

        /**
         * The Constant SQLITE_SEARCH_PATTERN.
         */
        private static final String SQLITE_SEARCH_PATTERN = "*.sqlite";
        /**
         * The Constant PRIVATE_LIBRARY.
         */
        private static final String PRIVATE_LIBRARY = "Private Library";
        final BackupAdapter adapter;

        GetFileList(BackupAdapter adapter) {
            this.adapter = adapter;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Void doInBackground(final Void... params) {
            /* The backups. */
            List<JsonDocumentFileInfo> backups = new GetDocumentFileListRequest(userName, PRIVATE_LIBRARY, boardName
                    + SQLITE_SEARCH_PATTERN).execute();
            if (backups == null) return null;
            Collections.sort(backups, (arg0, arg1) -> {
                if (arg0 != null && arg1 != null) {
                    return arg1.CreationTimeUtc.compareTo(arg0.CreationTimeUtc);
                }
                return 0;
            });
            JsonDocumentFileInfo[] arr = {};
            publishProgress(backups.toArray(arr));
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(final JsonDocumentFileInfo... items) {
            for (JsonDocumentFileInfo ir : items) {
                adapter.add(ir);
            }
        }

    }

    /**
     * The Class BackupAdapter.
     */
    class BackupAdapter extends ArrayAdapter<JsonDocumentFileInfo> {

        /**
         * The Constant TIME_FORMAT.
         */
        private static final String TIME_FORMAT = "%tT";

        /**
         * The Constant DAY_FORMAT.
         */
        private static final String DAY_FORMAT = "%tA";

        /**
         * The Constant DATE_FORMAT.
         */
        private static final String DATE_FORMAT = "%te";

        /**
         * The Constant MONTH_FORMAT.
         */
        private static final String MONTH_FORMAT = "%tB";

        /**
         * The Constant YEAR_FORMAT.
         */
        private static final String YEAR_FORMAT = "%tY";

        /**
         * The resource.
         */
        private final int resource;

        /**
         * Instantiates a new backup ContactAdapter.
         *
         * @param context the context
         */
        BackupAdapter(final Context context) {
            super(context, R.layout.restore_from_backup_item);
            this.resource = R.layout.restore_from_backup_item;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @NonNull
        @Override
        public View getView(final int arg0, final View paramConvertView, @NonNull final ViewGroup arg2) {
            View convertView = paramConvertView;
            final JsonDocumentFileInfo fileInfo = getItem(arg0);
            if (convertView == null) {
                convertView = inflater.inflate(this.resource, null, false);
            }
            if (fileInfo != null) {
                TextView year = convertView.findViewById(R.id.restoreFromBackupYear);
                TextView month = convertView.findViewById(R.id.restoreFromBackupMonth);
                TextView date = convertView.findViewById(R.id.restoreFromBackupDate);
                TextView day = convertView.findViewById(R.id.restoreFromBackupDay);
                TextView time = convertView.findViewById(R.id.restoreFromBackupTime);
                year.setText(String.format(Locale.getDefault(), YEAR_FORMAT, fileInfo.CreationTimeUtc));
                month.setText(String.format(Locale.getDefault(), MONTH_FORMAT, fileInfo.CreationTimeUtc));
                date.setText(String.format(Locale.getDefault(), DATE_FORMAT, fileInfo.CreationTimeUtc));
                day.setText(String.format(Locale.getDefault(), DAY_FORMAT, fileInfo.CreationTimeUtc));
                time.setText(String.format(Locale.getDefault(), TIME_FORMAT, fileInfo.CreationTimeUtc));
                convertView.setOnClickListener(arg01 -> restoreBackup(fileInfo));
            }
            return convertView;
        }

    }
}
