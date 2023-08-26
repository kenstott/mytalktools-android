package com.MTA.MyTalkMobile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.core.app.NotificationCompat;

import com.MTA.MyTalkMobile.Utilities.Utility;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
@Keep
public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent != null) {
            String TAG = "geo";
            if (geofencingEvent.hasError()) {
                Log.e(TAG, String.format("Error: %d", geofencingEvent.getErrorCode()));
                return;
            }

            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL || geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

                List<Geofence> triggered = geofencingEvent.getTriggeringGeofences();

                if (triggered != null) {
                    Geofence event = triggered.get(0);
                    String id = event.getRequestId();

                    if (id.startsWith("mt-")) {

                        // get the cell
                        int childBoardId = Integer.parseInt(id.replace("mt-", ""));
                        BoardContent content = new BoardContent(Board.currentBoard, childBoardId);

                        // get our icon
                        SVG svg = SVGParser.getSVGFromResource(getResources(), R.raw.splash);
                        Drawable svgDrawable = svg.createPictureDrawable();
                        Bitmap bitmap = Utility.drawableToBitmap(svgDrawable);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle("MyTalkTools")
                                        .setSubText(content.getText())
                                        .setContentText("Location");
                        Intent newIntent = new Intent();
                        String urlString = String.format(Locale.getDefault(), "mytalktools:/content/%d", childBoardId);
                        newIntent.setData(Uri.parse(urlString));
                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                Board.currentBoard,
                                0,
                                newIntent,
                                PendingIntent.FLAG_ONE_SHOT
                        );
                        mBuilder.setContentIntent(pendingIntent);
                        try {
                            if (content.getUrl().length() > 0) {
                                int cellWidth = 200;
                                int cellHeight = 200;
                                InputStream inputStream;
                                File fileDir = Utility.getMyTalkFilesDir(Board.currentBoard);
                                if (content.getUrl() == null || content.getUrl().length() == 0) {
                                    inputStream = null;
                                } else if (content.getUrl().contains("/")) {
                                    String str3 = content.getUrl().replace(" ", "-").replace("/", "-");
                                    File localFile2 = new File(fileDir.getPath() + "/" + str3);
                                    inputStream = Files.newInputStream(localFile2.toPath());
                                } else {
                                    inputStream = Board.currentBoard.getAssets().open(content.getUrl());
                                }
                                BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
                                bitmapOption.inJustDecodeBounds = true;
                                BitmapFactory.decodeStream(inputStream, null, bitmapOption);

                                if (inputStream != null) {
                                    inputStream.close();
                                }

                                if (content.getUrl() == null || content.getUrl().length() == 0) {
                                    inputStream = null;
                                } else if (content.getUrl().contains("/")) {
                                    String str3 = content.getUrl().replace(" ", "-").replace("/", "-");
                                    File localFile2 = new File(fileDir.getPath() + "/" + str3);
                                    inputStream = Files.newInputStream(localFile2.toPath());
                                } else {
                                    AssetManager localAssetManager1 = Board.currentBoard.getAssets();
                                    inputStream = localAssetManager1.open(content.getUrl());
                                }

                                float bitmapWidth = bitmapOption.outWidth;
                                int sampleWidth = Math.round(bitmapWidth / cellWidth);
                                float bitmapHeight = bitmapOption.outHeight;
                                int sampleHeight = Math.round(bitmapHeight / (float) cellHeight);
                                int sampleSize = Math.max(sampleWidth, sampleHeight);
                                BitmapFactory.Options imageOptions = new BitmapFactory.Options();
                                imageOptions.inSampleSize = sampleSize;
                                Bitmap bitmap2 = BitmapFactory.decodeStream(inputStream, null, imageOptions);
                                mBuilder.setLargeIcon(bitmap2);

                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            }
                        } catch (Exception ex) {
                            mBuilder.setLargeIcon(bitmap);
                        }

                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        if (mNotificationManager != null)
                            mNotificationManager.notify(1, mBuilder.build());
                    }
                }
            } else {
                // Log the error.
                Log.e(TAG, "Invalid geo transition type");
            }
        }
    }
}
