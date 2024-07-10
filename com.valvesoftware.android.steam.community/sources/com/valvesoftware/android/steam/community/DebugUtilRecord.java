package com.valvesoftware.android.steam.community;

import java.util.Calendar;

/* loaded from: classes.dex */
public class DebugUtilRecord {

    /* renamed from: id */
    public long f10id;
    public String key;
    public DebugUtilRecord parent;
    public String value;
    public Calendar timestamp = Calendar.getInstance();
    public long threadid = Thread.currentThread().getId();

    public long getId() {
        return this.f10id;
    }
}
