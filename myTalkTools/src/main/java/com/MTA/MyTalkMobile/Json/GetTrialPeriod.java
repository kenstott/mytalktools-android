/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Json;

import androidx.annotation.Keep;

/**
 * The Class GetTrialPeriod. This is a POJO for use in re-hydrating server responses.
 */
@Keep
public class GetTrialPeriod {

    /**
     * The Membership.
     */
    @Keep
    public JsonMembership Membership;

    /**
     * The First name.
     */
    @Keep
    public String FirstName;

    /**
     * Instantiates a new gets the trial period.
     */
    @Keep
    GetTrialPeriod() {

    }
}
