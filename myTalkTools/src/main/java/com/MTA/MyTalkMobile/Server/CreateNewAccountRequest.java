/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Server;

import com.MTA.MyTalkMobile.Json.JsonCreateNewAccountResult;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc

/**
 * The Class CreateNewAccountRequest.
 */
public class CreateNewAccountRequest extends MyTalkWebService {

    /**
     * The e mail.
     */
    private final String eMail;

    /**
     * The first name.
     */
    private final String firstName;

    /**
     * The last name.
     */
    private final String lastName;

    /**
     * The password.
     */
    private final String password;

    /**
     * The username.
     */
    private final String username;

    /**
     * The uuid.
     */
    private final String uuid;

    /**
     * Instantiates a new creates the new account request.
     *
     * @param paramUsername  the param username
     * @param paramPassword  the param password
     * @param paramEMail     the param e mail
     * @param paramFirstName the param first name
     * @param paramLastName  the param last name
     * @param paramUuid      the param uuid
     */
    public CreateNewAccountRequest(final String paramUsername, final String paramPassword,
                                   final String paramEMail, final String paramFirstName, final String paramLastName,
                                   final String paramUuid) {
        super("CreateNewAccount");
        this.username = paramUsername;
        this.password = paramPassword;
        this.eMail = paramEMail;
        this.firstName = paramFirstName;
        this.lastName = paramLastName;
        this.uuid = paramUuid;
    }

    /**
     * Execute.
     *
     * @return the string
     */
    public final String execute() {
        try {
            String message =
                    new JSONObject().put(MyTalkWebService.VAR_USER_NAME, username)
                            .put(MyTalkWebService.VAR_PASSWORD, password).put(MyTalkWebService.VAR_EMAIL, eMail)
                            .put(MyTalkWebService.VAR_FIRST_NAME, firstName)
                            .put(MyTalkWebService.VAR_LAST_NAME, lastName).put(MyTalkWebService.VAR_UUID, uuid)
                            .toString();
            if (execute(message)) {
                JsonCreateNewAccountResult j =
                        getGson().fromJson(getJsonResponse(), JsonCreateNewAccountResult.class);
                return j.getResult().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
