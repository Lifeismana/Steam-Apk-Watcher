package com.valvesoftware.android.steam.community.model;

/* loaded from: classes.dex */
public enum PollStatus {
    OK,
    TIMEOUT,
    NOT_LOGGED_ON;

    public static PollStatus getValueFromString(String str) {
        if (str == null) {
            return null;
        }
        if (str.equalsIgnoreCase("ok")) {
            return OK;
        }
        if (str.equalsIgnoreCase("timeout")) {
            return TIMEOUT;
        }
        if (str.equalsIgnoreCase("not logged on")) {
            return NOT_LOGGED_ON;
        }
        return null;
    }
}
