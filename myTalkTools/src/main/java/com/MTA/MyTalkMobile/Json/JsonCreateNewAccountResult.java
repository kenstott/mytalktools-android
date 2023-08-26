/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class JsonCreateNewAccountResult. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class JsonCreateNewAccountResult {

    /**
     * The d.
     */
    @Keep
    private int d;

    /**
     * Instantiates a new json create new account result.
     */
    @Keep
    public JsonCreateNewAccountResult() {

    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    @Keep
    public final CreateNewAccountResult getResult() {
        return CreateNewAccountResult.values()[d];
    }

    /**
     * The Enum CreateNewAccountResult.
     */
    @Keep
    public enum CreateNewAccountResult {

        /**
         * The Add user.
         */
        AddUser,
        /**
         * The Username already exists.
         */
        UsernameAlreadyExists,
        /**
         * The User already registered.
         */
        UserAlreadyRegistered,
        /**
         * The Duplicate email.
         */
        DuplicateEmail,

        /**
         * The Duplicate provider user key.
         */
        DuplicateProviderUserKey,
        /**
         * The Duplicate user name.
         */
        DuplicateUserName,
        /**
         * The Invalid answer.
         */
        InvalidAnswer,

        /**
         * The Invalid email.
         */
        InvalidEmail,
        /**
         * The Invalid password.
         */
        InvalidPassword,
        /**
         * The Invalid provider user key.
         */
        InvalidProviderUserKey,
        /**
         * The Invalid question.
         */
        InvalidQuestion,

        /**
         * The Invalid user name.
         */
        InvalidUserName,
        /**
         * The Provider error.
         */
        ProviderError,
        /**
         * The Success.
         */
        Success,
        /**
         * The Unexpected error.
         */
        UnexpectedError,
        /**
         * The User rejected.
         */
        UserRejected,

        /**
         * The Password mismatch.
         */
        PasswordMismatch,
        /**
         * The Add user to portal.
         */
        AddUserToPortal
    }
}
