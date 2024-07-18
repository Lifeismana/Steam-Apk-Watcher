package com.valvesoftware.android.steam.community.webrequests;

import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;

/* compiled from: JsonRequestBuilder.java */
/* loaded from: classes.dex */
abstract class CustomJsonRequest extends JsonObjectRequest {
    private final ResponseListener responseListener;

    public CustomJsonRequest(int i, String str, JSONObject jSONObject, ResponseListener responseListener) {
        super(i, str, jSONObject, null, null);
        this.responseListener = responseListener;
        setShouldCache(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.volley.toolbox.JsonRequest, com.android.volley.Request
    public void deliverResponse(JSONObject jSONObject) {
        if (this.responseListener != null) {
            JSONObject optJSONObject = jSONObject.optJSONObject("response");
            ResponseListener responseListener = this.responseListener;
            if (optJSONObject != null) {
                jSONObject = optJSONObject;
            }
            responseListener.onSuccess(jSONObject);
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.volley.toolbox.JsonObjectRequest, com.android.volley.toolbox.JsonRequest, com.android.volley.Request
    public Response<JSONObject> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            return super.parseNetworkResponse(networkResponse);
        } catch (Exception e) {
            Log.e("error", e.toString());
            return null;
        }
    }
}
