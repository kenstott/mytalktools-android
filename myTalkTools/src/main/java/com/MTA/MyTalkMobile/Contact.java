package com.MTA.MyTalkMobile;

import android.net.Uri;

import java.util.HashMap;

class Contact {

    public final HashMap<String, String> DetailDescriptions;
    public final HashMap<String, HashMap<String, String>> Details;
    public String ID;
    public String LookupKey;
    public String Name;
    public String Birthday;
    public String PhotoUri;
    public Uri ContentUri;

    public Contact() {
        Details = new HashMap<>();
        DetailDescriptions = new HashMap<>();
    }
}
