/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.MTA.MyTalkMobile.Json.JsonUserRoles;
import com.MTA.MyTalkMobile.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The Class GetUserRoles.
 */
public class GetUserRoles extends MyTalkWebService {

    private final String userName;

    public GetUserRoles(final String userName) {
        super("GetRolesByUserName");
        this.userName = userName;
    }

    /**
     * Execute trial search.
     *
     * @return the gets the trial period
     */
    private ArrayList<String> execute() {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, this.userName).toString();
            if (execute(message)) {
                String response = getJsonResponse();
                JsonUserRoles j =
                        getGson().fromJson(response, JsonUserRoles.class);
                return j.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeAsync(GetUserRoles.GetJson json, Context context) {

        GetUserRoles.DownloadTask d = new GetUserRoles.DownloadTask();
        d.json = json;
        d.isv = this;
        d.context = new WeakReference<>(context);
        d.execute();
    }

    public interface GetJson {
        void test(ArrayList<String> result);
    }

    private static class DownloadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        public String result;
        GetUserRoles.GetJson json;
        GetUserRoles isv;
        WeakReference<Context> context;
        private ProgressDialog Dialog;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            return isv.execute();
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            try {
                Dialog.dismiss();
            } catch (Exception ignore) {

            }
            json.test(result);
        }

        @Override
        protected void onPreExecute() {
            Dialog = ProgressDialog.show(this.context.get(), null, context.get().getString(R.string.getting_user_roles), true);
        }
    }
}
