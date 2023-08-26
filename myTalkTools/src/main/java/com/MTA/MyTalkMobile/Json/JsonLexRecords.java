/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class JsonNewDatabaseResultWrapper. This is a POJO for re-hydrating server responses.
 */
@Keep
public class JsonLexRecords {

    /**
     * The d.
     */
    @Keep
    public ArrayList<JsonLexRecord> d;

    /**
     * Instantiates a new json new database result wrapper.
     */
    @Keep
    public JsonLexRecords() {

    }

    @Keep
    public JsonLexRecords(ArrayList<JsonLexRecord> d) {
        this.d = d;
    }

    @Keep
    public JsonLexRecords(List<JsonFrenchLexRecord> jsonFrenchLexRecords) {
        this.d = new ArrayList<>();
        for (JsonFrenchLexRecord jsonFrenchLexRecord : jsonFrenchLexRecords) {
            JsonLexRecord jsonLexRecord = new JsonLexRecord();
            switch (jsonFrenchLexRecord._type) {
                case "v":
                    jsonLexRecord.cat = "verb";
                    break;
                case "prep":
                    jsonLexRecord.cat = "preposition";
                    break;
                case "np":
                case "nc":
                case "ncpred":
                    jsonLexRecord.cat = "noun";
                    break;
                default:
                    jsonLexRecord.cat = jsonFrenchLexRecord._type;
                    break;
            }

            jsonLexRecord.Value = jsonFrenchLexRecord.variant;
            jsonLexRecord.unInfl = jsonFrenchLexRecord.predicate;
            jsonLexRecord.infl = jsonFrenchLexRecord.predicate;
            this.d.add(jsonLexRecord);
        }
    }

    @Keep
    public ArrayList<String> getUniqueWords() {
        ArrayList<String> result = new ArrayList<>();
        for (JsonLexRecord l : d) {
            if (!result.contains(l.Value)) result.add(l.Value);
        }
        return result;
    }

    @Keep
    public ArrayList<JsonLexRecord> getUniqueWordsAndTypes(int colorKey) {
        ArrayList<JsonLexRecord> result = new ArrayList<>();
        for (JsonLexRecord l : d) {
            JsonLexRecord test = new JsonLexRecord();
            test.Value = l.Value;
            test.colorCode = l.getColorCode(colorKey);
            if (!result.contains(test)) result.add(test);
        }
        return result;
    }
}
