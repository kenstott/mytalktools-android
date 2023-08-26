/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Json.GetTrialPeriod;
import com.MTA.MyTalkMobile.Json.JsonTrialCheckWrapper;
import com.MTA.MyTalkMobile.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * The Class CheckTrialPeriod.
 */
public class CheckTrialPeriod extends MyTalkWebService {

    /**
     * Instantiates a new check trial period.
     */
    public CheckTrialPeriod() {
        super("GetUserByUserName");
    }

    /**
     * Execute trial search.
     *
     * @return the gets the trial period
     */
    public final GetTrialPeriod executeTrialSearch() {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, Board.getUsername()).toString();
            Log.d("username: ", message);
            if (execute(message)) {
                JsonTrialCheckWrapper j =
                        getGson().fromJson(getJsonResponse(), JsonTrialCheckWrapper.class);
                return j.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeAsync(CheckTrialPeriod.GetResult json, Context context) {

        CheckTrialPeriod.DownloadTask d = new CheckTrialPeriod.DownloadTask();
        d.json = json;
        d.isv = this;
        d.context = new WeakReference<>(context);
        d.execute();
    }

    public interface GetResult {
        void test(GetTrialPeriod result);
    }

    private class DownloadTask extends AsyncTask<Void, Void, GetTrialPeriod> {


        public String result;
        CheckTrialPeriod.GetResult json;
        CheckTrialPeriod isv;
        WeakReference<Context> context;
        private ProgressDialog Dialog;

        @Override
        protected GetTrialPeriod doInBackground(Void... params) {
            try {
                String message =
                        new JSONObject().put(MyTalkWebService.VAR_USER_NAME, Board.getUsername()).toString();
                Log.d("username: ", message);
                if (isv.execute(message)) {
                    JsonTrialCheckWrapper j =
                            getGson().fromJson(getJsonResponse(), JsonTrialCheckWrapper.class);
                    return j.d;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(GetTrialPeriod result) {
            try {
                Dialog.dismiss();
            } catch (Exception ignore) {

            }
            json.test(result);
        }

        @Override
        protected void onPreExecute() {
            Dialog = ProgressDialog.show(this.context.get(), null, context.get().getString(R.string.checking_license), true);
        }
    }
}
