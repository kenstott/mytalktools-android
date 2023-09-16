/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.app.Dialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Utilities.Utility;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * The Class CaptureWebPage. Assists in displaying a web page and then capturing the web page
 * in a bitmap format.
 */
@Keep
class HotspotEditor extends Dialog {

    private final BoardContent content;
    private final Board board;
    /**
     * The bitmap.
     */
    private ImageView imageView;
    private Spinner rows, columns;
    private Bitmap bitmap;
    private int trackRows, trackColumns;


    public HotspotEditor(BoardContent content, Board context) {
        super(context);
        this.content = content;
        this.board = context;
    }

    public final int getRows() {
        return trackRows;
    }

    public final int getColumns() {
        return trackColumns;
    }

    @Override
    public final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());
        setContentView(R.layout.hotspot_editor);
        imageView = findViewById(R.id.imageView);
        rows = findViewById(R.id.rowsEditText);
        columns = findViewById(R.id.columnsEditText);
        rows.setSelection(1);
        columns.setSelection(1);
        try {
            InputStream inputStream;
            File fileDir = Utility.getMyTalkFilesDir(board);
            if (content.getUrl() == null || content.getUrl().length() == 0) {
                inputStream = null;
            } else if (content.getUrl().contains("/")) {
                String str3 = content.getUrl().replace(" ", "-").replace("/", "-");
                File localFile2 = new File(fileDir.getPath() + "/" + str3);
                inputStream = Files.newInputStream(localFile2.toPath());
            } else {
                AssetManager localAssetManager1 = board.getAssets();
                inputStream = localAssetManager1.open(content.getUrl());
            }

            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions);

            imageView.setImageBitmap(Utility.drawGridOnBitmap(this.bitmap,
                    rows.getSelectedItemPosition() + 1,
                    columns.getSelectedItemPosition() + 1, 0, 0));

            if (inputStream != null) {
                inputStream.close();
            }

        } catch (Exception ignored) {
        }
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageView.setImageBitmap(Utility.drawGridOnBitmap(bitmap,
                        rows.getSelectedItemPosition() + 1,
                        columns.getSelectedItemPosition() + 1, 0, 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageView.setImageBitmap(Utility.drawGridOnBitmap(bitmap,
                        rows.getSelectedItemPosition() + 1,
                        columns.getSelectedItemPosition() + 1, 0, 0));
            }
        };
        rows.setOnItemSelectedListener(listener);
        columns.setOnItemSelectedListener(listener);
        Button ok = findViewById(R.id.okButton);
        ok.setOnClickListener(v -> {
            trackColumns = columns.getSelectedItemPosition() + 1;
            trackRows = rows.getSelectedItemPosition() + 1;
            dismiss();
        });
    }

}
