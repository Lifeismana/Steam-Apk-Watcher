package com.valvesoftware.android.steam.community.webrequests;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: StringRequestBuilder.java */
/* loaded from: classes.dex */
class CustomStringPostRequest extends StringRequest {
    private final Map<String, String> parameters;
    private final ResponseListener responseListener;

    public CustomStringPostRequest(String str, ResponseListener responseListener, Map<String, String> map) {
        super(1, str, null, null);
        this.responseListener = responseListener;
        this.parameters = map;
        setShouldCache(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.volley.toolbox.StringRequest, com.android.volley.Request
    public void deliverResponse(String str) {
        if (this.responseListener != null) {
            if (str == null) {
                str = "";
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.putOpt("response", str);
            } catch (JSONException e) {
                e.printStackTrace();
                this.responseListener.onSuccess(null);
            }
            this.responseListener.onSuccess(jSONObject);
        }
    }

    @Override // com.android.volley.Request
    public void deliverError(VolleyError volleyError) {
        RequestErrorInfo requestErrorInfo;
        if (this.responseListener == null) {
            return;
        }
        if (volleyError.networkResponse == null) {
            requestErrorInfo = new RequestErrorInfo(-1, null, null, volleyError.getMessage());
        } else {
            requestErrorInfo = new RequestErrorInfo(volleyError.networkResponse.statusCode, volleyError.networkResponse.headers, volleyError.networkResponse.data, volleyError.getMessage());
        }
        this.responseListener.onError(requestErrorInfo);
    }

    @Override // com.android.volley.Request
    public Map<String, String> getHeaders() {
        return WebRequestUtilities.getHeaders();
    }

    @Override // com.android.volley.Request
    public byte[] getBody() {
        Map<String, String> map = this.parameters;
        if (map == null || map.size() <= 0) {
            return null;
        }
        return WebRequestUtilities.encodePostParameters(this.parameters, getParamsEncoding());
    }
}
