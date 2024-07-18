package com.valvesoftware.android.steam.community.webrequests;

import com.android.volley.Request;

/* loaded from: classes.dex */
public class ImageRequestBuilder extends RequestBuilder {
    private ImageResponseListener responseListener;

    @Override // com.valvesoftware.android.steam.community.webrequests.RequestBuilder
    public void setAccessToken(String str) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImageRequestBuilder(String str) {
        super(str, false);
    }

    @Override // com.valvesoftware.android.steam.community.webrequests.RequestBuilder
    public Request toRequest() {
        return new CustomImageRequest(getFullUrl(), this.responseListener, 0, 0, null);
    }

    public void setResponseListener(ImageResponseListener imageResponseListener) {
        this.responseListener = imageResponseListener;
    }
}
