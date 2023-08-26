/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.MTA.MyTalkMobile.Json.JsonFrenchLexRecord;
import com.MTA.MyTalkMobile.Json.JsonLexRecord;
import com.MTA.MyTalkMobile.Json.JsonLexRecords;
import com.MTA.MyTalkMobile.R;
import com.MTA.MyTalkMobile.Utilities.Utility;
import com.google.gson.reflect.TypeToken;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Class GetUserRoles.
 */
public class GetWordVariants extends MyTalkWebService {

    private final String word;
    private final String filename;
    private final Context context;

    public GetWordVariants(String word, Context context) {
        super("PartOfSpeechWithDefinitions");
        this.word = word;
        this.filename = word + ".variants";
        this.context = context;
    }

    private String readDefinitionFile() {
        try {
            File fileDir = Utility.getMyTalkFilesDir(context);
            File file = new File(fileDir.getPath() + "/" + this.filename);
            InputStream inputStream = Files.newInputStream(file.toPath());
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferReader.readLine()) != null) {
                sb.append(line);
            }
            inputStreamReader.close();
            return sb.toString();
        } catch (Exception ex) {
            return null;
        }
    }

    private void writeDefinitionFile(String contents) {
        try {
            File fileDir = Utility.getMyTalkFilesDir(context);
            File file = new File(fileDir.getPath() + "/" + this.filename);
            OutputStream outputStream = Files.newOutputStream(file.toPath());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(contents);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Execute trial search.
     *
     * @return the gets the trial period
     */
    public final ArrayList<JsonLexRecord> execute() {
        try {
            String response = readDefinitionFile();
            if (response != null) {
                if (Locale.getDefault().getLanguage().startsWith("fr")) {
                    Type listType = new TypeToken<List<JsonFrenchLexRecord>>() {
                    }.getType();
                    ArrayList<JsonFrenchLexRecord> j = getGson().fromJson(response, listType);
                    JsonLexRecords jj = new JsonLexRecords(j);
                    return jj.d;
                }
                JsonLexRecords j =
                        getGson().fromJson(response, JsonLexRecords.class);
                return j.d;
            }
            String message = new JSONObject().put("word", this.word).toString();
            if (Locale.getDefault().getLanguage().startsWith("fr")) {
                setP(new HttpPost("https://www.mytalktools.com/dnn/sync.asmx/FrenchVariants"));
                if (execute(message)) {
                    response = getJsonResponse();
                    response = response != null ? response.replaceAll("\"type\"", "\"_type\"").replace("{\"d\":null}", "") : null;
                    writeDefinitionFile(response);
                    Type listType = new TypeToken<List<JsonFrenchLexRecord>>() {
                    }.getType();
                    ArrayList<JsonFrenchLexRecord> j = getGson().fromJson(response, listType);
                    if (j == null) return new ArrayList<>();
                    JsonLexRecords jj = new JsonLexRecords(j);

                    return jj.d;
                }
            }
            setP(new HttpPost("https://www.mytalktools.com/dnn/lexicon.asmx/PartOfSpeechWithDefinitions"));
            if (execute(message)) {
                response = getJsonResponse();
                writeDefinitionFile(response);
                JsonLexRecords j =
                        getGson().fromJson(response, JsonLexRecords.class);
                return j.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeAsync(GetWordVariants.GetJson json, Context context) {

        GetWordVariants.DownloadTask d = new GetWordVariants.DownloadTask();
        d.json = json;
        d.isv = this;
        d.context = new WeakReference<>(context);
        d.execute();
    }

    public interface GetJson {
        void test(ArrayList<JsonLexRecord> result);
    }

    private static class DownloadTask extends AsyncTask<Object, Object, ArrayList<JsonLexRecord>> {

        private GetWordVariants.GetJson json;
        private GetWordVariants isv;
        private WeakReference<Context> context;
        private ProgressDialog Dialog;

        @Override
        protected ArrayList<JsonLexRecord> doInBackground(Object... params) {
            return isv.execute();
        }

        @Override
        protected void onPostExecute(ArrayList<JsonLexRecord> result) {
            Dialog.dismiss();
            json.test(result);
        }

        @Override
        protected void onPreExecute() {
            Dialog = ProgressDialog.show(this.context.get(), null, context.get().getString(R.string.getting_variants), true);
        }
    }


}
