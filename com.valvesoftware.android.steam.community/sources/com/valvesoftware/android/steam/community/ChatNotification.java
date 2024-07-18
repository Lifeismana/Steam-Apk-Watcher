package com.valvesoftware.android.steam.community;

/* loaded from: classes.dex */
public class ChatNotification {
    String from;
    String message;
    long timeProcessed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean matches(String str, String str2, long j) {
        String str3;
        String str4 = this.from;
        return str4 != null && str4.equals(str) && (str3 = this.message) != null && str3.equals(str2) && j - this.timeProcessed < 500;
    }
}
