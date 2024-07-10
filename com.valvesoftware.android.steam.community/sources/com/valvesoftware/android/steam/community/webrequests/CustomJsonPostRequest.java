package com.valvesoftware.android.steam.community.webrequests;

import java.util.Map;
import org.json.JSONObject;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: JsonRequestBuilder.java */
/* loaded from: classes.dex */
public class CustomJsonPostRequest extends CustomJsonRequest {
    private final Map<String, String> parameters;

    public CustomJsonPostRequest(String str, JSONObject jSONObject, ResponseListener responseListener, Map<String, String> map) {
        super(1, str, jSONObject, responseListener);
        this.parameters = map;
    }

    @Override // com.android.volley.Request
    public Map<String, String> getHeaders() {
        return WebRequestUtilities.getHeaders();
    }

    @Override // com.android.volley.toolbox.JsonRequest, com.android.volley.Request
    public byte[] getBody() {
        Map<String, String> map = this.parameters;
        if (map == null || map.size() <= 0) {
            return null;
        }
        return WebRequestUtilities.encodePostParameters(this.parameters, getParamsEncoding());
    }
}
