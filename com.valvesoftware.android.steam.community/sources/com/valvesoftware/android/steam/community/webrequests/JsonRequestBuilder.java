package com.valvesoftware.android.steam.community.webrequests;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class JsonRequestBuilder extends RequestBuilder {
    public JsonRequestBuilder(String str, boolean z) {
        super(str, z);
    }

    @Override // com.valvesoftware.android.steam.community.webrequests.RequestBuilder
    public Request toRequest() {
        JsonObjectRequest getRequest;
        if (isPOST()) {
            getRequest = toPostRequest();
        } else {
            getRequest = toGetRequest();
        }
        if (this.retryPolicy != null) {
            getRequest.setRetryPolicy(this.retryPolicy);
        }
        return getRequest;
    }

    private JsonObjectRequest toGetRequest() {
        return new CustomJsonGetRequest(getFullUrl(), null, getResponseListener());
    }

    private JsonObjectRequest toPostRequest() {
        return new CustomJsonPostRequest(getBaseUrl(), null, getResponseListener(), getPostParameters());
    }
}
