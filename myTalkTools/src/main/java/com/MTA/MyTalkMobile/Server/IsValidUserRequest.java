/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.MTA.MyTalkMobile.Board;
import com.MTA.MyTalkMobile.Json.JsonUserValidationWrapper;
import com.MTA.MyTalkMobile.R;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

// TODO: Auto-generated Javadoc

/**
 * The Class IsValidUserRequest.
 */
public class IsValidUserRequest extends MyTalkWebService {

    /**
     * The password.
     */
    private static String password;

    /**
     * The username.
     */
    private static String username;
    // --Commented out by Inspection (6/18/18, 7:36 PM):private String result;

    /**
     * Instantiates a new checks if is valid user request.
     *
     * @param userName      the user name
     * @param paramPassword the param password
     */
    public IsValidUserRequest(final String userName, final String paramPassword) {
        super("IsValidUser");
        username = userName;
        password = paramPassword;
    }

// --Commented out by Inspection START (6/18/18, 7:36 PM):
//    /**
//     * Execute.
//     *
//     * @return the string
//     */
//    public final String execute() {
//        String response = "";
//        String message = "";
//        try {
//            message =
//                    new JSONObject().put(Board.USERNAME, username).put(Board.PASSWORD, password).toString();
//            if (execute(message)) {
//                response = getJsonResponse();
//                JsonUserValidationWrapper j =
//                        getGson().fromJson(response, JsonUserValidationWrapper.class);
//                return j.userValidation().toString().replace("_", " ");
//            }
//        } catch (IOException exception) {
//            return exception.getMessage() + ", " + message + ", " + response;
//        } catch (JSONException exception) {
//            return exception.getMessage() + "," + message + ", " + response;
//        } catch(JsonSyntaxException exception) {
//            return exception.getMessage() + "," + message + ", " + response;
//        } catch(Exception exception) {
//            return exception.getMessage() + "," + message + ", " + response;
//        }
//        return null;
//    }
// --Commented out by Inspection STOP (6/18/18, 7:36 PM)

    public void executeAsync(GetJson json, Context context) {

        DownloadTask d = new DownloadTask();
        d.json = json;
        d.isv = this;
        d.context = new WeakReference<>(context);
        d.execute();
    }

    public interface GetJson {
        void test(String result);
    }

    private class DownloadTask extends AsyncTask<Void, Void, String> {


        // --Commented out by Inspection (6/18/18, 7:36 PM):public String result;
        GetJson json;
        IsValidUserRequest isv;
        WeakReference<Context> context;
        private ProgressDialog Dialog;

        @Override
        protected String doInBackground(Void... params) {
            String response = "";
            String message = "";
            try {
                message =
                        new JSONObject().put(Board.USERNAME, username).put(Board.PASSWORD, password).toString();
                if (isv.execute(message)) {
                    response = getJsonResponse();
                    JsonUserValidationWrapper j =
                            getGson().fromJson(response, JsonUserValidationWrapper.class);
                    return j.userValidation().toString().replace("_", " ");
                }
            } catch (IOException exception) {
                return exception.getMessage() + ", " + message + ", " + response;
            } catch (Exception exception) {
                return exception.getMessage() + "," + message + ", " + response;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Dialog.dismiss();
            json.test(result);
        }

        @Override
        protected void onPreExecute() {
            Dialog = ProgressDialog.show(this.context.get(), null, context.get().getString(R.string.validating_user), true);
        }
    }

}
