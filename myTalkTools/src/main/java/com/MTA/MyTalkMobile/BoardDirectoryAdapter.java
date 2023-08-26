package com.MTA.MyTalkMobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.MTA.MyTalkMobile.Utilities.Utility;
import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;
import com.oissela.software.multilevelexpindlistview.Utils;

//import proguard.annotation.Keep;

@Keep
class BoardDirectoryAdapter extends MultiLevelExpIndListAdapter {

    private final Context mContext;
    private final View.OnClickListener mExpandListener;
    private final View.OnClickListener mNavigateListener;

    public BoardDirectoryAdapter(Context context, View.OnClickListener expandListener, View.OnClickListener navigateListener) {
        super();
        mContext = context;
        mExpandListener = expandListener;
        mNavigateListener = navigateListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        ContentViewHolder viewHolder;
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) return null;
        try {
            v = inflater.inflate(R.layout.recyclerview_item, parent, false);
            viewHolder = new ContentViewHolder(v);
            viewHolder.boardContentsCount.setOnClickListener(mExpandListener);
            viewHolder.expandContents.setOnClickListener(mExpandListener);
            v.setOnClickListener(mNavigateListener);
            return viewHolder;
        } catch (Exception ex) {
            Log.d("", ex.toString());
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int cellWidth = 60;
        int cellHeight = 60;
        int viewType = getItemViewType(position);
        ContentViewHolder cvh = (ContentViewHolder) holder;
        BoardDirectoryItem content = (BoardDirectoryItem) getItemAt(position);
        if (content == null) return;
        cvh.view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        BoardContent _content = content.getContent();
        cvh.contentText.setText(_content.getText());

        if (content.getIndentation() == 0) {
            cvh.colorBand.setVisibility(View.GONE);
            cvh.setPaddingLeft(0);
        } else {
            cvh.colorBand.setVisibility(View.VISIBLE);
            cvh.setColorBandColor(content.getIndentation());
            int mPaddingDP = 10;
            int leftPadding = Utils.getPaddingPixels(mContext, mPaddingDP) * content.getIndentation();
            cvh.setPaddingLeft(leftPadding);
        }

        if (content.isGroup()) {
            cvh.boardContentsCount.setVisibility(View.VISIBLE);
            cvh.expandContents.setVisibility(View.VISIBLE);
            cvh.boardContentsCount.setText(Integer.toString(content.getGroupSize()));
        } else {
            cvh.boardContentsCount.setVisibility(View.GONE);
            cvh.expandContents.setVisibility(View.GONE);
        }

        try {
            try {
                Bitmap targetBmp = _content.getImage(mContext, false);
                if (targetBmp != null) {
                    Bitmap bitmap = Utility.scaleBitmapAndKeepRatio(targetBmp, cellHeight, cellWidth);
                    cvh.contentUrl.setImageBitmap(bitmap);
                }
            } catch (OutOfMemoryError ex) {
                // OK
            }
        } catch (Exception ex) {
            // ok
        }
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private static final String[] indColors = {"#000000", "#3366FF", "#E65CE6",
                "#E68A5C", "#00E68A", "#CCCC33"};
        final TextView contentText;
        final ImageView contentUrl;
        final ImageView expandContents;
        final TextView boardContentsCount;
        private final View colorBand;
        private final View view;

        ContentViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            contentText = itemView.findViewById(R.id.content_text);
            contentUrl = itemView.findViewById(R.id.content_url);
            expandContents = itemView.findViewById(R.id.expand_contents);
            colorBand = itemView.findViewById(R.id.color_band);
            boardContentsCount = itemView.findViewById(R.id.board_contents_count);
        }

        void setColorBandColor(int indentation) {
            if (indentation >= indColors.length) indentation = indColors.length - 1;
            int color = Color.parseColor(indColors[indentation]);
            colorBand.setBackgroundColor(color);
        }

        void setPaddingLeft(int paddingLeft) {
            view.setPadding(paddingLeft, 0, 0, 0);
        }
    }
}
