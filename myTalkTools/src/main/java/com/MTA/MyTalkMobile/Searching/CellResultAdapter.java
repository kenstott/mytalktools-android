/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Searching;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.BoardContent;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Search;

/**
 * The Class CellResultAdapter.
 */
@Keep
public class CellResultAdapter extends ArrayAdapter<BoardContent> {

    /**
     * The Constant IMAGE_CELL_WIDTH_PROPORTION.
     */
    private static final int IMAGE_CELL_WIDTH_PROPORTION = 4;
    /**
     * The Constant GO_HOME_COMMAND.
     */
    private static final int GO_HOME_COMMAND = 18;
    /**
     * The Constant GO_BACK_COMMAND.
     */
    private static final int GO_BACK_COMMAND = 19;
    /**
     * The search.
     */
    private final Search search;
    /**
     * The list results.
     */
    private final ListView listResults;
    /**
     * The query.
     */
    private final String query;

    /**
     * Instantiates a new cell result ContactAdapter.
     *
     * @param context          the context
     * @param layout           the unused
     * @param paramListResults the param list results
     * @param paramQuery       the param query
     */
    public CellResultAdapter(final Search context, final int layout, final ListView paramListResults,
                             final String paramQuery) {
        super(context, layout);
        this.search = context;
        this.listResults = paramListResults;
        this.query = paramQuery;
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @NonNull
    @Override
    public final View getView(final int position, final View convertView, @NonNull final ViewGroup parent) {

        LinearLayout linearLayout;
        if (convertView != null) {
            linearLayout = (LinearLayout) convertView;
        } else {
            linearLayout = new LinearLayout(search);
        }
        linearLayout.removeAllViews();
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        final BoardContent boardContent = getItem(position);
        if (boardContent != null) {
            ImageView imageView = new ImageView(search);
            TextView textView = new TextView(search);
            FrameLayout frameLayout = new FrameLayout(search);
            textView.setText(boardContent.getText());
            int cellWidth = listResults.getWidth() / IMAGE_CELL_WIDTH_PROPORTION;
            Bitmap bitmap = boardContent.getImage(search, cellWidth, cellWidth);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            textView.setMaxWidth(cellWidth);
            textView.measure(cellWidth, cellWidth);
            int frameHeight = cellWidth - textView.getMeasuredHeight();
            imageView.setMaxHeight(frameHeight);
            imageView.setPadding(1, 1, 1, 1);
            imageView.setBackgroundColor(Color.TRANSPARENT);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));

            FrameLayout.LayoutParams layoutParams =
                    new android.widget.FrameLayout.LayoutParams(cellWidth, frameHeight);
            frameLayout.setLayoutParams(layoutParams);

            if (boardContent.getType() == GO_HOME_COMMAND) {
                textView.setText(R.string.to_home);
                frameLayout.addView(imageView);
                linearLayout.removeAllViews();
                linearLayout.addView(frameLayout);
                if (boardContent.getText().length() > 0) {
                    textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    linearLayout.addView(textView);
                }
            } else if (boardContent.getType() == GO_BACK_COMMAND) {
                textView.setText(R.string.back);
                frameLayout.addView(imageView);
                linearLayout.removeAllViews();
                linearLayout.addView(frameLayout);
                if (boardContent.getText().length() > 0) {
                    textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    linearLayout.addView(textView);
                }
            } else if (bitmap != null) {
                frameLayout.addView(imageView);
                linearLayout.removeAllViews();
                frameLayout.setForegroundGravity(Gravity.TOP);
                linearLayout.addView(frameLayout, 0);
                if (boardContent.getText().length() > 0) {
                    textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                    linearLayout.addView(textView);
                }
            }
            linearLayout.setOnClickListener(arg0 -> {
                search.finish();
                Intent localIntent = new Intent(Intent.ACTION_VIEW, null, search.getApplicationContext(), Board.class);
                localIntent.putExtra(Board.INTENT_EXTRA_BOARD_ID, boardContent.getBoardId());
                localIntent.putExtra(Board.INTENT_EXTRA_BOARD_NAME,
                        getContext().getString(R.string.searched_for_) + query);
                localIntent.putExtra(Board.INTENT_EXTRA_IS_EDITABLE, Board.getIsEditable());
                search.startActivity(localIntent);
            });
        }
        return linearLayout;
    }
}
