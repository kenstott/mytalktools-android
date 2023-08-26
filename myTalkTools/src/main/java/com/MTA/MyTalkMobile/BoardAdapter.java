/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Keep;

import com.MTA.MyTalkMobile.Utilities.Utility;

//import proguard.annotation.Keep;

/**
 * The Class BoardAdapter. Displays a single board in a grid.
 */
@Keep
class BoardAdapter extends BaseAdapter {

    /**
     * The Constant GO_BACK_COMMAND.
     */
    private static final int GO_BACK_COMMAND = 19;
    /**
     * The Constant GO_HOME_COMMAND.
     */
    private static final int GO_HOME_COMMAND = 18;
    /**
     * The Constant DEFAULT_CELL_HEIGHT.
     */
    private static final int DEFAULT_CELL_HEIGHT = 256;
    /**
     * The Constant DEFAULT_CELL_WIDTH.
     */
    private static final int DEFAULT_CELL_WIDTH = 256;
    /**
     * The Constant TEXT_VIEW_MAX_LINES defines the maximum rows of text displayed in a cell with no
     * image.
     */
    private static final int TEXT_VIEW_MAX_LINES = 10;
    /**
     * The board row.
     */
    private BoardRow boardRow;
    /**
     * The context.
     */
    private Context context;
    /**
     * The board.
     */
    private Board board;
    /**
     * The background color code.
     */
    private String backgroundColorCode;
    private MotionEvent motionEvent;

    /**
     * Instantiates a new board ContactAdapter.
     */
    public BoardAdapter() {
    }

    /**
     * Instantiates a new board ContactAdapter.
     *
     * @param paramContext the param context
     */
    public BoardAdapter(final Context paramContext) {
        this.context = paramContext;
    }

    /**
     * Instantiates a new board ContactAdapter.
     *
     * @param paramContext  the param context
     * @param paramBoardRow the param board row
     * @param paramBoard    the param board
     */
    public BoardAdapter(final Context paramContext, final BoardRow paramBoardRow,
                        final Board paramBoard) {
        this.context = paramContext;
        this.boardRow = paramBoardRow;
        this.board = paramBoard;
    }

    public void notifyDatasetChanged() {
        this.notifyDataSetChanged();
    }

    /**
     * Gets the board row.
     *
     * @return the board row
     */
    public final BoardRow getBoardRow() {
        return this.boardRow;
    }

    /**
     * Sets the board row.
     *
     * @param value the new board row
     */
    public final void setBoardRow(final BoardRow value) {
        this.boardRow = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    public final int getCount() {
        int i = this.boardRow.getColumns();
        int j = this.boardRow.getRows();
        return i * j;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    public final BoardContent getItem(final int index) {
        int i = this.boardRow.getColumns();
        int j = index / i;
        int m = index % i;
        if (this.boardRow.isSorted()) {
            return this.boardRow.getSortedItem(j, m);
        }
        return this.boardRow.getItem(j, m);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    public final long getItemId(final int index) {
        if (getItem(index) == null) return -1;
        return getItem(index).getiPhoneId();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public final View getView(final int index, final View view, final ViewGroup viewGroup) {

        final BoardContent content = getItem(index);
        final BoardAdapter ba = this;
        final SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(board.getBaseContext());
        int maxRows = Integer.parseInt(sp.getString(AppPreferences.PREF_KEY_MAXIMUM_ROWS, "3"));
        int defaultFontSize =
                Integer.parseInt(sp.getString(AppPreferences.PREF_KEY_DEFAULT_FONT_SIZE, "10"));
        int marginWidth = Integer.parseInt(sp.getString(AppPreferences.PREF_KEY_MARGIN_WIDTH, "0"));
        boolean useMarginForColorCoding =
                sp.getBoolean(AppPreferences.PREF_KEY_USE_MARGIN_FOR_COLOR_CODING, false);
        boolean hotspotsVisible = sp.getBoolean(AppPreferences.PREF_KEY_HOTSPOTS_VISIBLE, false);
        backgroundColorCode =
                sp.getString(AppPreferences.PREF_KEY_BACKGROUND_COLOR, "Goosen, Fitzgerald");
        String colorScheme = sp.getString(AppPreferences.PREF_KEY_COLOR_SCHEME, "black,white");
        int foreground = Color.WHITE;
        if (colorScheme.contentEquals("black,white")) {
            foreground = Color.BLACK;
        }
        int b = Color.WHITE;
        if (foreground == Color.WHITE) {
            b = Color.BLACK;
        }
        final int background = b;
        int displayRows = Math.min(this.boardRow.getRows(), maxRows);
        final GridView gridView = viewGroup.findViewById(R.id.mainGrid);
        gridView.setBackgroundColor(foreground);
        RelativeLayout parentRelativeLayout = new RelativeLayout(context);
        LinearLayout linearLayout = new android.widget.LinearLayout(context);
        linearLayout.removeAllViews();

        if (content == null || ((View) gridView.getParent()).getWidth() == 0) {
            return linearLayout;
        }

        DragListener dragListener = new DragListener(content, gridView, background);
        linearLayout.setOnDragListener(dragListener);
        linearLayout.setOnLongClickListener(new LongClickListener(content, parentRelativeLayout));
        if (Board.getIsLoggedIn() || (!Board.getIsLoggedIn() && !content.getHidden() && !content.getExternalUrl().equals("x"))) {
            linearLayout.setOnClickListener(new ClickListener(gridView, ba, index));
        }
        linearLayout.setOnTouchListener(new TouchListener());
        int altFontSize = content.getFontSize();
        Integer altBackground = ColorKeyAdapter.getColor(content.getBackgroundColor());
        Integer altForeground = ColorKeyAdapter.getColor(content.getForegroundColor());
        ImageView imageView = new android.widget.ImageView(context);
        FrameLayout frameLayout = new android.widget.FrameLayout(context);
        /* start parent cell change */
        parentRelativeLayout.setPadding(marginWidth, marginWidth, marginWidth, marginWidth);
        int backgroundCellColor = altBackground;
        if (content.getBackgroundColor() == 0 && !useMarginForColorCoding) {
            backgroundCellColor = background;
        }
        if (board.getSelectedScanPosition() != null && index == board.getSelectedScanPosition() && board.getSelectedHotspotScanPosition() == null) {
            backgroundCellColor = Color.GRAY;
        }
        parentRelativeLayout.setBackgroundColor(backgroundCellColor);
        parentRelativeLayout.addView(linearLayout);
        ImageView parentImageView = new android.widget.ImageView(context);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        parentImageView.setLayoutParams(params);
        parentRelativeLayout.addView(parentImageView);
        /* end change */

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(backgroundCellColor);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setBaselineAligned(false);
        linearLayout.setTag(content);
        gridView.setHorizontalSpacing(2);
        gridView.setVerticalSpacing(2);
        gridView.setVerticalFadingEdgeEnabled(true);
        gridView.setPadding(1, 1, 1, 1);

        TextView textView = new android.widget.TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        if (altFontSize == 0) {
            textView.setTextSize(defaultFontSize);
        } else {
            textView.setTextSize(altFontSize);
        }
        int foregroundCellColor = altForeground;
        if (content.getForegroundColor() == 0) {
            foregroundCellColor = foreground;
        }
        textView.setTextColor(foregroundCellColor);
        textView.setMaxLines(TEXT_VIEW_MAX_LINES);
        linearLayout.addView(textView);
        if (Board.getIsLoggedIn() || (!Board.getIsLoggedIn() && !content.getHidden() && !content.getExternalUrl().equals("x"))) {
            if (content.getChildBoardId() != 0) {
                parentImageView.setImageResource(R.drawable.rotate);
            }
            if (content.getChildBoardLinkId() != 0) {
                parentImageView.setImageResource(R.drawable.rotate_grey);
            }
        }

        int gridHeight = gridView.getHeight();
        int gridWidth = ((View) gridView.getParent()).getWidth();
        int boardColumns = this.boardRow.getColumns();
        int cellWidth = (gridWidth / boardColumns);
        int cellHeight = (gridHeight / displayRows);

        if (cellWidth < 0) {
            cellWidth = DEFAULT_CELL_WIDTH;
        } else {
            cellWidth = cellWidth - marginWidth * 2;
        }
        if (cellHeight < 0) {
            cellHeight = DEFAULT_CELL_HEIGHT;
        } else {
            cellHeight = cellHeight - marginWidth * 2;
        }

        RelativeLayout.LayoutParams localLayoutParams =
                new RelativeLayout.LayoutParams(cellWidth, cellHeight);
        linearLayout.setLayoutParams(localLayoutParams);

        try {
            if (Board.getIsLoggedIn() || (!Board.getIsLoggedIn() && !content.getHidden() && !content.getExternalUrl().equals("x"))) {

                textView.setText(content.getText());
                Bitmap contentBitmap = content.getImage(board, true);
                Bitmap bitmap = null;

                if (contentBitmap != null) {
                    int imageDisplayHeight = cellHeight - textView.getHeight();
                    if (content.getHotspotStyle() == 1 && (Board.getIsEditable() || hotspotsVisible)) {
                        bitmap = Utility.scaleBitmapAndKeepRatio(contentBitmap, imageDisplayHeight, cellWidth);
                        BoardRow boardRow = content.getChildBoard(board);
                        if (boardRow != null) {
                            imageView.setImageBitmap(Utility.drawGridOnBitmap(bitmap, boardRow.getRows(), boardRow.getColumns(), 0, 0));
                        }
                    } else if (content.getHotspotStyle() == 1 && board.getSelectedHotspotScanPosition() != null) {
                        bitmap = Utility.scaleBitmapAndKeepRatio(contentBitmap, imageDisplayHeight, cellWidth);
                        BoardRow boardRow = content.getChildBoard(board);
                        if (boardRow != null) {
                            int p = board.getSelectedHotspotScanPosition();
                            int nc = boardRow.getColumns();
                            int rr = p / nc;
                            int cc = p % nc;
                            imageView.setImageBitmap(Utility.drawGridOnBitmap(bitmap, boardRow.getRows(), boardRow.getColumns(), rr + 1, cc + 1));
                        }
                    } else {
                        bitmap = contentBitmap;
                        imageView.setImageBitmap(contentBitmap);
                    }
                }
                textView.setMaxWidth(cellWidth);
                textView.measure(cellWidth, cellHeight);
                int frameHeight = cellHeight - textView.getMeasuredHeight();
                imageView.setMaxHeight(frameHeight);
                imageView.setPadding(1, 1, 1, 1);
                imageView.setId(R.id.imageView);
                imageView.setBackgroundColor(Color.TRANSPARENT);
                if (useMarginForColorCoding) {
                    imageView.setBackgroundColor(background);
                    textView.setBackgroundColor(background);
                }
                imageView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));

                FrameLayout.LayoutParams layoutParams =
                        new android.widget.FrameLayout.LayoutParams(cellWidth, frameHeight);
                frameLayout.setLayoutParams(layoutParams);

                if (content.getType() == GO_HOME_COMMAND) {
                    imageView.setImageResource(R.drawable.home);
                    textView.setText(R.string.to_home);
                    frameLayout.addView(imageView);
                    linearLayout.removeAllViews();
                    linearLayout.addView(frameLayout);
                    if (content.getText().length() > 0) {
                        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        linearLayout.addView(textView);
                    }
                } else if (content.getType() == GO_BACK_COMMAND) {
                    imageView.setImageResource(R.drawable.back);
                    textView.setText(R.string.back);
                    frameLayout.addView(imageView);
                    linearLayout.removeAllViews();
                    linearLayout.addView(frameLayout);
                    if (content.getText().length() > 0) {
                        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        linearLayout.addView(textView);
                    }
                } else if (bitmap != null) {
                    frameLayout.addView(imageView);
                    linearLayout.removeAllViews();
                    frameLayout.setForegroundGravity(Gravity.TOP);
                    linearLayout.addView(frameLayout, 0);
                    if (content.getText().length() > 0) {
                        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        linearLayout.addView(textView);
                    }
                }
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            return parentRelativeLayout;
        } catch (Exception localIOException) {
            localIOException.printStackTrace();
        }
        return parentRelativeLayout;
    }

    /**
     * Gets the background color code.
     *
     * @return the background color code
     */
    public final String getBackgroundColorCode() {
        return backgroundColorCode;
    }

    /**
     * The listener interface for receiving click events. The class that is interested in processing a
     * click event implements this interface, and the object created with that class is registered
     * with a component using the component's <code>addClickListener</code> method. When the click
     * event occurs, that object's appropriate method is invoked.
     */

    private final class ClickListener implements View.OnClickListener {

        /**
         * The grid view.
         */
        private final GridView gridView;

        /**
         * The ba.
         */
        private final BoardAdapter ba;

        /**
         * The index.
         */
        private final int index;

        /**
         * Instantiates a new click listener.
         *
         * @param paramGridView the param grid view
         * @param boardAdapter  the board ContactAdapter
         * @param paramIndex    the param index
         */
        private ClickListener(final GridView paramGridView, final BoardAdapter boardAdapter,
                              final int paramIndex) {
            this.gridView = paramGridView;
            this.ba = boardAdapter;
            this.index = paramIndex;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(final View v) {
            board.itemClick(ba, null, gridView, index, 0L);
        }
    }

    /**
     * The listener interface for receiving touch events. The class that is interested in processing a
     * touch event implements this interface, and the object created with that class is registered
     * with a component using the component's <code>addTouchListener</code> method. When the touch
     * event occurs, that object's appropriate method is invoked.
     */
    private final class TouchListener implements View.OnTouchListener {

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
         */
        @Override
        public boolean onTouch(final View v, final MotionEvent ev) {
            motionEvent = ev;
            return false;
        }
    }

    /**
     * The listener interface for receiving longClick events. The class that is interested in
     * processing a longClick event implements this interface, and the object created with that class
     * is registered with a component using the component's <code>addLongClickListener</code> method.
     * When the longClick event occurs, that object's appropriate method is invoked.
     */
    private final class LongClickListener implements View.OnLongClickListener {

        /**
         * The content.
         */
        private final BoardContent content;
        private final View view;

        /**
         * Instantiates a new long click listener.
         *
         * @param paramContent the param content
         */
        private LongClickListener(final BoardContent paramContent, View paramView) {
            this.content = paramContent;
            this.view = paramView;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
         */
        @Override
        public boolean onLongClick(final View v) {
            if (Board.getIsEditable()) {
                board.setSelectedItem(content);
                board.setSelectedHotspotItem(null);
                if (content.getHotspotStyle() == 1 && content.getChildBoardId() != 0) {
                    ImageView iv = view.findViewById(R.id.imageView);
                    if (iv != null) {
                        int[] loc = {0, 0};
                        iv.getLocationOnScreen(loc);
                        float imageX = motionEvent.getX() - loc[0];
                        float imageY = motionEvent.getY() - loc[1];
                        float imageWidth = iv.getWidth();
                        float imageHeight = iv.getHeight();
                        BoardRow boardRow = content.getChildBoard(board);
                        if (boardRow != null) {
                            int rows = boardRow.getRows();
                            int columns = boardRow.getColumns();
                            int r = (int) (Math.floor(imageY / imageHeight * rows) + 1);
                            int c = (int) (Math.floor(imageX / imageWidth * columns) + 1);
                            Bitmap bitmap = Utility.drawableToBitmap(iv.getDrawable());
                            Bitmap n = Utility.drawGridOnBitmap(bitmap, rows, columns, r, c);
                            iv.setImageBitmap(n);
                            board.setSelectedHotspotItem(content.getHotspotContent(board, imageX, imageY, imageWidth, imageHeight));
                        }
                    }
                }
                ClipData data = ClipData.newPlainText("", "");
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
            }
            return true;
        }
    }

    /**
     * The listener interface for receiving drag events. The class that is interested in processing a
     * drag event implements this interface, and the object created with that class is registered with
     * a component using the component's <code>addDragListener</code> method. When the drag event
     * occurs, that object's appropriate method is invoked.
     *
     * @see DragEvent
     */
    private final class DragListener implements View.OnDragListener {

        /**
         * The content.
         */
        private final BoardContent content;

        /**
         * The grid view.
         */
        private final GridView gridView;

        /**
         * The background.
         */
        private final int background;

        /**
         * Instantiates a new drag listener.
         *
         * @param paramContent    the param content
         * @param paramGridView   the param grid view
         * @param paramBackground the param background
         */
        private DragListener(final BoardContent paramContent, final GridView paramGridView,
                             final int paramBackground) {
            this.content = paramContent;
            this.gridView = paramGridView;
            this.background = paramBackground;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnDragListener#onDrag(android.view.View, android.view.DragEvent)
         */
        @Override
        public boolean onDrag(final View v, final DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.argb(155, 100, 200, 255));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(background);
                    break;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(background);
                    if (content.getiPhoneId() == board.getSelectedItem().getiPhoneId()) {
                        gridView.performLongClick();
                    } else {
                        if (!boardRow.isSorted()) {
                            int touchedRow = board.getSelectedItem().getRow();
                            int touchedColumn = board.getSelectedItem().getColumn();
                            board.getSelectedItem().setRow(content.getRow());
                            board.getSelectedItem().setColumn(content.getColumn());
                            content.setRow(touchedRow);
                            content.setColumn(touchedColumn);
                            board.getSelectedItem().persist(board);
                            content.persist(board);
                            boardRow.persist(true);
                        }
                    }
                    gridView.invalidateViews();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    }

}
