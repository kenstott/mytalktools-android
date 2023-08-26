/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * The Class Network. Helper methods to determine type of network media and existence of network connection.
 */
public class Network {

    /**
     * Lookup host.
     *
     * @param addressBytes the addr bytes
     * @return the int
     */
    public static int lookupHost(final byte[] addressBytes) {
        return ((addressBytes[3] & 0xff) << 24) | ((addressBytes[2] & 0xff) << 16)
                | ((addressBytes[1] & 0xff) << 8) | (addressBytes[0] & 0xff);
    }

    /**
     * Have network connection.
     *
     * @param context the context
     * @return true, if successful
     */
    public static boolean haveNetworkConnection(final Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        // if this doesn't work - there is no internet
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return false;
        }

        // double check
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        boolean haveConnectedWifi = false, haveConnectedMobile = false;
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
