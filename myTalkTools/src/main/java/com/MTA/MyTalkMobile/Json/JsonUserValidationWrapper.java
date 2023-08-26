/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class JsonUserValidationWrapper. This is a POJO for re-hydrating server responses.
 */
@Keep
public class JsonUserValidationWrapper {

    /**
     * The d.
     */
    @Keep
    private Integer d;

    /**
     * Instantiates a new json user validation wrapper.
     */
    @Keep
    public JsonUserValidationWrapper() {

    }

    /**
     * User validation.
     *
     * @return the user validation
     */
    @Keep
    public final UserValidation userValidation() {
        return UserValidation.values()[d];
    }

    /**
     * The Enum UserValidation.
     */
    @Keep
    public enum UserValidation {

        /**
         * The No_ such_ user.
         */
        No_Such_User,
        /**
         * The Bad_ password.
         */
        Bad_Password,
        /**
         * The Validated.
         */
        Validated,
        /**
         * The No_ membership_ record.
         */
        No_Membership_Record,

        /**
         * The Unknown_ error.
         */
        Unknown_Error
    }
}


