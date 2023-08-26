/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import android.util.Log;

import com.MTA.MyTalkMobile.Json.JsonUserAccount;
import com.MTA.MyTalkMobile.Json.JsonUserAccounts;
import com.MTA.MyTalkMobile.Json.JsonUserAccountsWrapper;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * The Class GetUserRoles.
 */
public class GetSampleNames extends MyTalkWebService {

    public GetSampleNames() {
        super("Query");
    }

    /**
     * Execute trial search.
     *
     * @return the gets the trial period
     */
    public final ArrayList<JsonUserAccount> execute() {
        try {
            String message =
                    new JSONObject().
                            put("site", "SiteSqlServer").
                            put("query", "EXEC [dbo].[GetUsersByRolename] @PortalID = 0, @Rolename = 'Sample'").
                            toString();
            if (execute(message)) {
                String response = getJsonResponse();
                JsonUserAccountsWrapper j =
                        getGson().fromJson(response, JsonUserAccountsWrapper.class);
                String x = "{'d': " + j.d + "}";
                JsonUserAccounts jj = getGson().fromJson(x, JsonUserAccounts.class);
                Log.d("", j.d);
                return jj.d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
