package com.valvesoftware.android.steam.community;

/* loaded from: classes.dex */
public class SteamDebugUtil {
    public static DebugUtilRecord newDebugUtilRecord(DebugUtilRecord debugUtilRecord, String str, String str2) {
        DebugUtilRecord debugUtilRecord2 = new DebugUtilRecord();
        debugUtilRecord2.parent = debugUtilRecord;
        debugUtilRecord2.key = str;
        debugUtilRecord2.value = str2;
        return debugUtilRecord2;
    }
}
