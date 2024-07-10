package com.valvesoftware.android.steam.community.webrequests;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.valvesoftware.android.steam.community.Config;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class RequestBuilder {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private String accessToken;
    private final String baseUrl;
    private final boolean isPOST;
    private final Map<String, String> parameters = new LinkedHashMap();
    private ResponseListener responseListener;
    protected RetryPolicy retryPolicy;

    public abstract Request toRequest();

    public RequestBuilder(String str, boolean z) {
        this.baseUrl = str;
        this.isPOST = z;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public ResponseListener getResponseListener() {
        return this.responseListener;
    }

    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setAccessToken(String str) {
        if (!this.baseUrl.startsWith(Config.URL_WEBAPI_BASE + "/")) {
            Log.w(getClass().getName(), "Not adding access token to unknown domain \"" + this.baseUrl + "\"");
            return;
        }
        this.accessToken = str;
    }

    public void appendArray(String str, String[] strArr) {
        appendKeyValue(str, joinArgs(strArr));
    }

    public void appendSteamId(String str) {
        appendKeyValue("steamid", str);
    }

    public void appendKeyValue(String str, String str2) {
        this.parameters.put(str, str2);
    }

    public boolean isPOST() {
        return this.isPOST;
    }

    public String getFullUrl() {
        String str;
        String accessTokenQueryParam = getAccessTokenQueryParam();
        String parametersQueryString = getParametersQueryString(this.parameters);
        String str2 = "";
        if (parametersQueryString != null && parametersQueryString.length() > 0) {
            String str3 = accessTokenQueryParam.length() == 0 ? "?" : "&";
            if (isPOST()) {
                str = "";
            } else {
                str = str3 + getParametersQueryString(this.parameters);
            }
            str2 = str;
        }
        return this.baseUrl + accessTokenQueryParam + str2;
    }

    private String getAccessTokenQueryParam() {
        if (this.accessToken == null) {
            return "";
        }
        return "?access_token=" + this.accessToken;
    }

    private static String getParametersQueryString(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append("&");
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    private static String joinArgs(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        if (strArr == null) {
            return sb.toString();
        }
        if (strArr.length > 0) {
            sb.append(strArr[0]);
        }
        if (strArr.length == 1) {
            return sb.toString();
        }
        for (int i = 1; i < strArr.length; i++) {
            sb.append(",");
            sb.append(strArr[i]);
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, String> getPostParameters() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        String str = this.accessToken;
        if (str != null) {
            linkedHashMap.put("access_token", str);
        }
        linkedHashMap.putAll(this.parameters);
        return linkedHashMap;
    }
}
