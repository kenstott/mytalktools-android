package com.MTA.MyTalkMobile;

import android.util.Log;

import androidx.annotation.Keep;

import com.oissela.software.multilevelexpindlistview.MultiLevelExpIndListAdapter;

import java.util.ArrayList;
import java.util.List;

@Keep
public class BoardDirectoryItem implements MultiLevelExpIndListAdapter.ExpIndData {

    private final int mIndentation;
    private final Board board;
    private final int contentId;
    private int mGroupSize;
    private boolean mIsGroup;
    private int childBoardId;
    private int childBoardLinkId;
    private List<BoardDirectoryItem> mChildren;

    public BoardDirectoryItem(BoardContent content, Board board, int indentationLevel) {
        this.contentId = content.getiPhoneId();
        this.board = board;
        mIndentation = indentationLevel;
        if (content.getChildBoardId() != 0) {
            BoardRow boardRow = new BoardRow(content.getChildBoardId(), board);
            List<BoardContent> contents = boardRow.getContents();
            if (contents != null) {
                this.childBoardId = content.getChildBoardId();
                mIsGroup = true;
                mGroupSize = contents.size();
            }
        }
        if (content.getChildBoardLinkId() != 0) {
            BoardRow boardRow = new BoardRow(content.getChildBoardLinkId(), board);
            List<BoardContent> contents = boardRow.getContents();
            if (contents != null) {
                this.childBoardLinkId = content.getChildBoardLinkId();
                mIsGroup = true;
                mGroupSize = contents.size();
            }
        }
    }

    @Override
    public List<? extends MultiLevelExpIndListAdapter.ExpIndData> getChildren() {
        if (mChildren != null) return mChildren;
        int id = childBoardId != 0 ? childBoardId : childBoardLinkId;
        if (id == 0) return null;
        mChildren = new ArrayList<>();
        BoardRow boardRow = new BoardRow(id, board);
        List<BoardContent> contents = boardRow.getContents();
        if (contents != null) {
            for (BoardContent child : contents) {
                if (child.getType() < 18) {
                    if (!mChildren.add(new BoardDirectoryItem(child, board, mIndentation + 1))) {
                        break;
                    }
                }
            }
        } else {
            Log.d("boardItem", "children");
        }
        return mChildren;
    }

    @Override
    public boolean isGroup() {
        return mIsGroup;
    }

    @Override
    public void setIsGroup(boolean value) {
        mIsGroup = value;
    }

    public int getGroupSize() {
        return mGroupSize;
    }

    @Override
    public void setGroupSize(int value) {
        mGroupSize = value;
    }

    public int getIndentation() {
        return mIndentation;
    }

    public BoardContent getContent() {
        return (new BoardContent(this.contentId, board));
    }
}