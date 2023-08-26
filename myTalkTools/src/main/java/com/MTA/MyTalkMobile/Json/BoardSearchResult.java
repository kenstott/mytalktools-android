/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class BoardSearchResult. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class BoardSearchResult {

    /**
     * The Text.
     */
    @Keep
    public String Text;

    /**
     * The Font size.
     */
    @Keep
    public int FontSize;

    /**
     * The Foreground.
     */
    @Keep
    public int Foreground;

    /**
     * The Background.
     */
    @Keep
    public int Background;

    /**
     * The Image url.
     */
    @Keep
    public String ImageUrl;

    /**
     * The Audio video url.
     */
    @Keep
    public String AudioVideoUrl;

    /**
     * The Child board id.
     */
    @Keep
    public Long ChildBoardId;

    /**
     * The Content id.
     */
    @Keep
    public Long ContentId;

    /**
     * The Content type.
     */
    @Keep
    public int ContentType;

    /**
     * The Child board column count.
     */
    @Keep
    public int ChildBoardColumnCount;

    /**
     * The Tags.
     */
    @Keep
    public String Tags;

    /**
     * The App link.
     */
    @Keep
    public String AppLink;

    /**
     * The Tts speech prompt.
     */
    @Keep
    public String TtsSpeechPrompt;

    /**
     * The Alternate tts text.
     */
    @Keep
    public String AlternateTtsText;

    /**
     * The Zoom.
     */
    @Keep
    public Boolean Zoom;

    /**
     * The Do not zoom pics.
     */
    @Keep
    public Boolean DoNotZoomPics;

    // kind of goofy - but since BoardSearchResult and ChildBoardSearchResult
    // are
    // exactly the same, but needed to do the parsing from the web service call
    // this just coerces the BoardSearchResult in ChildBoardSearchResult which
    // simplifies some of the classes needed to work with this data.

    public BoardSearchResult(final JSONObject content) {
        try {
            this.AlternateTtsText = getString(content, "AlternateTtsText");
            this.Text = getString(content, "Text");

            if (content.getBoolean("ToHome")) this.ContentType = 2;
            else if (content.getBoolean("Back")) this.ContentType = 1;
            else this.ContentType = 0;
            this.ImageUrl = getString(content, "Picture");
            if (!this.ImageUrl.equals(""))
                this.ImageUrl = "https://www.mytalktools.com/dnn/UserUploads/" + this.ImageUrl;
            this.AudioVideoUrl = getString(content, "Sound");
            if (!this.AudioVideoUrl.equals(""))
                this.AudioVideoUrl = "https://www.mytalktools.com/dnn/UserUploads/" + this.AudioVideoUrl;
            this.Background = content.getInt("Background");
            this.Foreground = content.getInt("Foreground");
            this.FontSize = content.getInt("FontSize");
            this.Zoom = content.getBoolean("Zoom");
            this.DoNotZoomPics = content.getBoolean("DoNotZoomPics");
            this.TtsSpeechPrompt = getString(content, "TtsSpeechPrompt");
            this.AppLink = getString(content, "ExternalUrl");
            this.AlternateTtsText = getString(content, "AlternateTtsText");
            this.ChildBoardId = content.getLong("ChildBoardId") == 0
                    ? content.getLong("ChildBoardLinkId")
                    : content.getLong("ChildBoardId");
            this.ContentId = content.getLong("ContentId");
            this.ChildBoardColumnCount = content.getInt("ChildBoardColumnCount");
            this.Tags = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    BoardSearchResult() {

    }

    /**
     * Child board search result.
     *
     * @return the child board search result
     */
    @Keep
    public final ChildBoardSearchResult childBoardSearchResult() {
        ChildBoardSearchResult result = new ChildBoardSearchResult();
        result.AlternateTtsText = this.AlternateTtsText;
        result.AppLink = this.AppLink;
        result.AudioVideoUrl = this.AudioVideoUrl;
        result.Background = this.Background;
        result.ChildBoardColumnCount = this.ChildBoardColumnCount;
        result.ChildBoardId = this.ChildBoardId;
        result.ContentId = this.ContentId;
        result.ContentType = this.ContentType;
        result.DoNotZoomPics = this.DoNotZoomPics;
        result.FontSize = this.FontSize;
        result.Foreground = this.Foreground;
        result.ImageUrl = this.ImageUrl;
        result.Tags = this.Tags;
        result.Text = this.Text;
        result.TtsSpeechPrompt = this.TtsSpeechPrompt;
        result.Zoom = this.Zoom;
        return result;
    }

    private String getString(JSONObject o, String key) {
        String result = null;
        try {
            result = o.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (result == null || result.equals("null")) result = null;
        return result;
    }
}
