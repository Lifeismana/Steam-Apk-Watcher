package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;

/* loaded from: classes.dex */
public class ImageRequest extends Request<Bitmap> {
    private static final Object sDecodeLock = new Object();
    private final Bitmap.Config mDecodeConfig;
    private Response.Listener<Bitmap> mListener;
    private final Object mLock;
    private final int mMaxHeight;
    private final int mMaxWidth;
    private final ImageView.ScaleType mScaleType;

    public ImageRequest(String str, Response.Listener<Bitmap> listener, int i, int i2, ImageView.ScaleType scaleType, Bitmap.Config config, Response.ErrorListener errorListener) {
        super(0, str, errorListener);
        this.mLock = new Object();
        setRetryPolicy(new DefaultRetryPolicy(1000, 2, 2.0f));
        this.mListener = listener;
        this.mDecodeConfig = config;
        this.mMaxWidth = i;
        this.mMaxHeight = i2;
        this.mScaleType = scaleType;
    }

    @Deprecated
    public ImageRequest(String str, Response.Listener<Bitmap> listener, int i, int i2, Bitmap.Config config, Response.ErrorListener errorListener) {
        this(str, listener, i, i2, ImageView.ScaleType.CENTER_INSIDE, config, errorListener);
    }

    @Override // com.android.volley.Request
    public Request.Priority getPriority() {
        return Request.Priority.LOW;
    }

    private static int getResizedDimension(int i, int i2, int i3, int i4, ImageView.ScaleType scaleType) {
        if (i == 0 && i2 == 0) {
            return i3;
        }
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            return i == 0 ? i3 : i;
        }
        if (i == 0) {
            double d = i2;
            double d2 = i4;
            Double.isNaN(d);
            Double.isNaN(d2);
            double d3 = i3;
            Double.isNaN(d3);
            return (int) (d3 * (d / d2));
        }
        if (i2 == 0) {
            return i;
        }
        double d4 = i4;
        double d5 = i3;
        Double.isNaN(d4);
        Double.isNaN(d5);
        double d6 = d4 / d5;
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            double d7 = i;
            Double.isNaN(d7);
            double d8 = i2;
            if (d7 * d6 >= d8) {
                return i;
            }
            Double.isNaN(d8);
            return (int) (d8 / d6);
        }
        double d9 = i;
        Double.isNaN(d9);
        double d10 = i2;
        if (d9 * d6 <= d10) {
            return i;
        }
        Double.isNaN(d10);
        return (int) (d10 / d6);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.volley.Request
    public Response<Bitmap> parseNetworkResponse(NetworkResponse networkResponse) {
        Response<Bitmap> doParse;
        synchronized (sDecodeLock) {
            try {
                try {
                    doParse = doParse(networkResponse);
                } catch (OutOfMemoryError e) {
                    VolleyLog.m3e("Caught OOM for %d byte image, url=%s", Integer.valueOf(networkResponse.data.length), getUrl());
                    return Response.error(new ParseError(e));
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return doParse;
    }

    private Response<Bitmap> doParse(NetworkResponse networkResponse) {
        Bitmap decodeByteArray;
        byte[] bArr = networkResponse.data;
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (this.mMaxWidth == 0 && this.mMaxHeight == 0) {
            options.inPreferredConfig = this.mDecodeConfig;
            decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
        } else {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            int i = options.outWidth;
            int i2 = options.outHeight;
            int resizedDimension = getResizedDimension(this.mMaxWidth, this.mMaxHeight, i, i2, this.mScaleType);
            int resizedDimension2 = getResizedDimension(this.mMaxHeight, this.mMaxWidth, i2, i, this.mScaleType);
            options.inJustDecodeBounds = false;
            options.inSampleSize = findBestSampleSize(i, i2, resizedDimension, resizedDimension2);
            decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
            if (decodeByteArray != null && (decodeByteArray.getWidth() > resizedDimension || decodeByteArray.getHeight() > resizedDimension2)) {
                Bitmap createScaledBitmap = Bitmap.createScaledBitmap(decodeByteArray, resizedDimension, resizedDimension2, true);
                decodeByteArray.recycle();
                decodeByteArray = createScaledBitmap;
            }
        }
        if (decodeByteArray == null) {
            return Response.error(new ParseError(networkResponse));
        }
        return Response.success(decodeByteArray, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    @Override // com.android.volley.Request
    public void cancel() {
        super.cancel();
        synchronized (this.mLock) {
            this.mListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.volley.Request
    public void deliverResponse(Bitmap bitmap) {
        Response.Listener<Bitmap> listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener != null) {
            listener.onResponse(bitmap);
        }
    }

    static int findBestSampleSize(int i, int i2, int i3, int i4) {
        double d = i;
        double d2 = i3;
        Double.isNaN(d);
        Double.isNaN(d2);
        double d3 = i2;
        double d4 = i4;
        Double.isNaN(d3);
        Double.isNaN(d4);
        double min = Math.min(d / d2, d3 / d4);
        float f = 1.0f;
        while (true) {
            float f2 = 2.0f * f;
            if (f2 > min) {
                return (int) f;
            }
            f = f2;
        }
    }
}
