package com.valvesoftware.android.steam.community.webrequests;

import android.graphics.Bitmap;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

/* compiled from: ImageRequestBuilder.java */
/* loaded from: classes.dex */
class CustomImageRequest extends ImageRequest {
    private final ImageResponseListener responseListener;

    public CustomImageRequest(String str, ImageResponseListener imageResponseListener, int i, int i2, Bitmap.Config config) {
        super(str, null, i, i2, config, null);
        this.responseListener = imageResponseListener;
    }

    @Override // com.android.volley.Request
    public void deliverError(VolleyError volleyError) {
        if (this.responseListener == null) {
            return;
        }
        if (volleyError.networkResponse == null) {
            new RequestErrorInfo(-1, null, null, volleyError.getMessage());
        } else {
            new RequestErrorInfo(volleyError.networkResponse.statusCode, volleyError.networkResponse.headers, volleyError.networkResponse.data, volleyError.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.volley.toolbox.ImageRequest, com.android.volley.Request
    public void deliverResponse(Bitmap bitmap) {
        ImageResponseListener imageResponseListener = this.responseListener;
        if (imageResponseListener == null) {
            return;
        }
        imageResponseListener.onSuccess(bitmap);
    }
}
