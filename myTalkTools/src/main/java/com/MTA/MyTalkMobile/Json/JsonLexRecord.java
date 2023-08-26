/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class JsonDocumentFileInfo. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonLexRecord {

    @Keep
    public String cat = "";
    @Keep
    public String infl = "";
    @Keep
    public String unInfl = "";
    @Keep
    public String Value = "";
    @Keep
    public int colorCode = 0;

    /**
     * Instantiates a new json document file info.
     */
    @Keep
    public JsonLexRecord() {

    }

    @Keep
    public int getColorCode(int colorKey) {
        switch (cat) {
            case "noun":
                return colorKey == 0 ? 12 : 10;
            case "adv":
            case "adj":
                return 8;
            case "pronoun":
                switch (unInfl) {
                    case "it":
                    case "those":
                        return colorKey == 0 ? 12 : 10;
                    default:
                        return 10;
                }
            case "aux":
            case "verb":
                return colorKey == 0 ? 7 : 6;
            case "preposition":
                return colorKey == 0 ? 4 : 7;
            default:
                return 0;
        }
    }

    @Keep
    public boolean equals(Object o) {
        if (o.getClass() != JsonLexRecord.class) return false;
        JsonLexRecord x = (JsonLexRecord) o;
        String cit = "";
        String eui = "";
        return x.Value.equals(this.Value)
                && x.infl.equals(cit)
                && x.infl.equals(this.cat)
                && x.infl.equals(eui)
                && x.infl.equals(this.infl)
                && x.infl.equals(this.unInfl)
                && x.colorCode == this.colorCode;
    }
}
