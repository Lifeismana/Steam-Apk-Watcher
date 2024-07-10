package com.android.volley;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class Request<T> implements Comparable<Request<T>> {
    private Cache.Entry mCacheEntry;
    private boolean mCanceled;
    private final int mDefaultTrafficStatsTag;
    private Response.ErrorListener mErrorListener;
    private final VolleyLog.MarkerLog mEventLog;
    private final Object mLock;
    private final int mMethod;
    private NetworkRequestCompleteListener mRequestCompleteListener;
    private RequestQueue mRequestQueue;
    private boolean mResponseDelivered;
    private RetryPolicy mRetryPolicy;
    private Integer mSequence;
    private boolean mShouldCache;
    private boolean mShouldRetryServerErrors;
    private final String mUrl;

    /* loaded from: classes.dex */
    interface NetworkRequestCompleteListener {
        void onNoUsableResponseReceived(Request<?> request);

        void onResponseReceived(Request<?> request, Response<?> response);
    }

    /* loaded from: classes.dex */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void deliverResponse(T t);

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getParamsEncoding() {
        return "UTF-8";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract Response<T> parseNetworkResponse(NetworkResponse networkResponse);

    public Request(int i, String str, Response.ErrorListener errorListener) {
        this.mEventLog = VolleyLog.MarkerLog.ENABLED ? new VolleyLog.MarkerLog() : null;
        this.mLock = new Object();
        this.mShouldCache = true;
        this.mCanceled = false;
        this.mResponseDelivered = false;
        this.mShouldRetryServerErrors = false;
        this.mCacheEntry = null;
        this.mMethod = i;
        this.mUrl = str;
        this.mErrorListener = errorListener;
        setRetryPolicy(new DefaultRetryPolicy());
        this.mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(str);
    }

    public int getMethod() {
        return this.mMethod;
    }

    public int getTrafficStatsTag() {
        return this.mDefaultTrafficStatsTag;
    }

    private static int findDefaultTrafficStatsTag(String str) {
        Uri parse;
        String host;
        if (TextUtils.isEmpty(str) || (parse = Uri.parse(str)) == null || (host = parse.getHost()) == null) {
            return 0;
        }
        return host.hashCode();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        this.mRetryPolicy = retryPolicy;
        return this;
    }

    public void addMarker(String str) {
        if (VolleyLog.MarkerLog.ENABLED) {
            this.mEventLog.add(str, Thread.currentThread().getId());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finish(final String str) {
        RequestQueue requestQueue = this.mRequestQueue;
        if (requestQueue != null) {
            requestQueue.finish(this);
        }
        if (VolleyLog.MarkerLog.ENABLED) {
            final long id = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.volley.Request.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Request.this.mEventLog.add(str, id);
                        Request.this.mEventLog.finish(Request.this.toString());
                    }
                });
            } else {
                this.mEventLog.add(str, id);
                this.mEventLog.finish(toString());
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Request<?> setRequestQueue(RequestQueue requestQueue) {
        this.mRequestQueue = requestQueue;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Request<?> setSequence(int i) {
        this.mSequence = Integer.valueOf(i);
        return this;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public String getCacheKey() {
        String url = getUrl();
        int method = getMethod();
        if (method == 0 || method == -1) {
            return url;
        }
        return Integer.toString(method) + '-' + url;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public Request<?> setCacheEntry(Cache.Entry entry) {
        this.mCacheEntry = entry;
        return this;
    }

    public Cache.Entry getCacheEntry() {
        return this.mCacheEntry;
    }

    public void cancel() {
        synchronized (this.mLock) {
            this.mCanceled = true;
            this.mErrorListener = null;
        }
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mCanceled;
        }
        return z;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return Collections.emptyMap();
    }

    @Deprecated
    protected Map<String, String> getPostParams() throws AuthFailureError {
        return getParams();
    }

    @Deprecated
    protected String getPostParamsEncoding() {
        return getParamsEncoding();
    }

    @Deprecated
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    @Deprecated
    public byte[] getPostBody() throws AuthFailureError {
        Map<String, String> postParams = getPostParams();
        if (postParams == null || postParams.size() <= 0) {
            return null;
        }
        return encodeParameters(postParams, getPostParamsEncoding());
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params == null || params.size() <= 0) {
            return null;
        }
        return encodeParameters(params, getParamsEncoding());
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private byte[] encodeParameters(Map<String, String> map, String str) {
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    throw new IllegalArgumentException(String.format("Request#getParams() or Request#getPostParams() returned a map containing a null key or value: (%s, %s). All keys and values must be non-null.", entry.getKey(), entry.getValue()));
                }
                sb.append(URLEncoder.encode(entry.getKey(), str));
                sb.append('=');
                sb.append(URLEncoder.encode(entry.getValue(), str));
                sb.append('&');
            }
            return sb.toString().getBytes(str);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + str, e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final Request<?> setShouldCache(boolean z) {
        this.mShouldCache = z;
        return this;
    }

    public final boolean shouldCache() {
        return this.mShouldCache;
    }

    public final boolean shouldRetryServerErrors() {
        return this.mShouldRetryServerErrors;
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public final int getTimeoutMs() {
        return getRetryPolicy().getCurrentTimeout();
    }

    public RetryPolicy getRetryPolicy() {
        return this.mRetryPolicy;
    }

    public void markDelivered() {
        synchronized (this.mLock) {
            this.mResponseDelivered = true;
        }
    }

    public boolean hasHadResponseDelivered() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mResponseDelivered;
        }
        return z;
    }

    public void deliverError(VolleyError volleyError) {
        Response.ErrorListener errorListener;
        synchronized (this.mLock) {
            errorListener = this.mErrorListener;
        }
        if (errorListener != null) {
            errorListener.onErrorResponse(volleyError);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setNetworkRequestCompleteListener(NetworkRequestCompleteListener networkRequestCompleteListener) {
        synchronized (this.mLock) {
            this.mRequestCompleteListener = networkRequestCompleteListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyListenerResponseReceived(Response<?> response) {
        NetworkRequestCompleteListener networkRequestCompleteListener;
        synchronized (this.mLock) {
            networkRequestCompleteListener = this.mRequestCompleteListener;
        }
        if (networkRequestCompleteListener != null) {
            networkRequestCompleteListener.onResponseReceived(this, response);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyListenerResponseNotUsable() {
        NetworkRequestCompleteListener networkRequestCompleteListener;
        synchronized (this.mLock) {
            networkRequestCompleteListener = this.mRequestCompleteListener;
        }
        if (networkRequestCompleteListener != null) {
            networkRequestCompleteListener.onNoUsableResponseReceived(this);
        }
    }

    @Override // java.lang.Comparable
    public int compareTo(Request<T> request) {
        Priority priority = getPriority();
        Priority priority2 = request.getPriority();
        return priority == priority2 ? this.mSequence.intValue() - request.mSequence.intValue() : priority2.ordinal() - priority.ordinal();
    }

    public String toString() {
        String str = "0x" + Integer.toHexString(getTrafficStatsTag());
        StringBuilder sb = new StringBuilder();
        sb.append(isCanceled() ? "[X] " : "[ ] ");
        sb.append(getUrl());
        sb.append(" ");
        sb.append(str);
        sb.append(" ");
        sb.append(getPriority());
        sb.append(" ");
        sb.append(this.mSequence);
        return sb.toString();
    }
}
