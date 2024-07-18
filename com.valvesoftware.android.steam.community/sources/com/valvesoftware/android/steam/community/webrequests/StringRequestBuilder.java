package com.valvesoftware.android.steam.community.webrequests;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

/* loaded from: classes.dex */
public class StringRequestBuilder extends RequestBuilder {
    public StringRequestBuilder(String str, boolean z) {
        super(str, z);
    }

    @Override // com.valvesoftware.android.steam.community.webrequests.RequestBuilder
    public Request toRequest() {
        StringRequest getRequest;
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

    private StringRequest toGetRequest() {
        throw new UnsupportedOperationException("not yet implemented");
    }

    private StringRequest toPostRequest() {
        return new CustomStringPostRequest(getBaseUrl(), getResponseListener(), getPostParameters());
    }
}
