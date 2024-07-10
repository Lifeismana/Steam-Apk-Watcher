package com.valvesoftware.android.steam.community.webrequests;

import java.util.Map;

/* loaded from: classes.dex */
public class RequestErrorInfo {
    private final byte[] data;
    private final Map<String, String> headers;
    private final String message;
    private final int statusCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestErrorInfo(int i, Map<String, String> map, byte[] bArr, String str) {
        this.statusCode = i;
        this.headers = map;
        this.data = bArr;
        this.message = str;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMessage() {
        return this.message;
    }
}
