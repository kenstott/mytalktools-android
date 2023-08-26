package com.MTA.MyTalkMobile.Server;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by kenneth stott on 3/11/17.
 */

public class GoogleDrive {

    private static final String[] SCOPES = {DriveScopes.DRIVE, DriveScopes.DRIVE_PHOTOS_READONLY};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static GoogleDriveResults results;
    private static String query;
    private static String mimeType;
    private static com.google.api.services.drive.Drive mService = null;
    private final Board board;
    private final Activity activity;
    private final GoogleAccountCredential mCredential;

    public GoogleDrive(Board board, Activity activity, GoogleDriveResults _results, String _query, String _mimeType) {
        this.board = board;
        this.activity = activity;
        query = _query;
        mimeType = _mimeType;
        mCredential = GoogleAccountCredential.usingOAuth2(
                        board.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        results = _results;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.drive.Drive.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("MyTalkTools")
                .build();

    }

    private static void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode, Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                Board.RequestCode.REQUEST_GOOGLE_PLAY_SERVICES.ordinal());
        if (dialog != null) {
            dialog.show();
        }
    }

    public void connect() {
        board.getGoogleApiClient().connect();
    }

    public void disconnect() {
        board.getGoogleApiClient().disconnect();
    }

    public void setAccountName(String accountName) {
        SharedPreferences settings =
                board.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.apply();
        mCredential.setSelectedAccountName(accountName);
    }

    public com.google.api.services.drive.Drive getService() {
        return mService;
    }

    public void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            new MakeRequestTask(board).execute();
        }
    }

    private void chooseAccount() {

        if (EasyPermissions.hasPermissions(
                activity, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = board.getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                activity.startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        Board.RequestCode.REQUEST_ACCOUNT_PICKER.ordinal());
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    activity,
                    "This app needs to access your Google account (via Contacts).",
                    Board.RequestCode.REQUEST_PERMISSION_GET_ACCOUNTS.ordinal(),
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) return false;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode, activity);
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public interface GoogleDriveResults {
        void test(List<com.google.api.services.drive.model.File> result);
    }

    static class MakeRequestTask extends AsyncTask<Void, Void, List<com.google.api.services.drive.model.File>> {

        final WeakReference<Activity> activity;
        private Exception mLastError = null;

        MakeRequestTask(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        /**
         * Background task to call Drive API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<com.google.api.services.drive.model.File> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * @return List of files, or an empty list if no files
         * found.
         * @throws IOException IO Problem
         */
        private List<com.google.api.services.drive.model.File> getDataFromApi() throws IOException {
            FileList result = mService.files().list()
                    .setPageSize(1000)
                    .setSpaces("drive,photos")
                    .setQ("trashed != true and mimeType contains '" + mimeType + "'"
                            + (query != null && query.length() > 0
                            ? "and fullText contains '" + query.replace("'", "\\'") + "'"
                            : ""))
                    .setFields("nextPageToken, files(id, name, description, mimeType, thumbnailLink, webContentLink, webViewLink, hasThumbnail)")
                    .execute();
            return result.getFiles();
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<com.google.api.services.drive.model.File> output) {
            if (results != null) results.test(output);
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode(), activity.get());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    activity.get().startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Board.RequestCode.REQUEST_AUTHORIZATION.ordinal());
                } else {
                    new AlertDialog.Builder(activity.get())
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, (paramDialogInterface, index) -> paramDialogInterface.cancel())
                            .setMessage(mLastError.getLocalizedMessage())
                            .setIcon(R.drawable.ic_dialog_alert)
                            .setTitle(R.string.error).show();

                }
            }  // cancelled

        }
    }

}
