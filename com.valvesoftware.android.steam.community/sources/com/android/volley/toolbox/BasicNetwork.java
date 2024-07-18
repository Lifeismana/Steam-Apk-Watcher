package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.ClientError;
import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/* loaded from: classes.dex */
public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;
    private final BaseHttpStack mBaseHttpStack;

    @Deprecated
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    @Deprecated
    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    @Deprecated
    public BasicNetwork(HttpStack httpStack, ByteArrayPool byteArrayPool) {
        this.mHttpStack = httpStack;
        this.mBaseHttpStack = new AdaptedHttpStack(httpStack);
        this.mPool = byteArrayPool;
    }

    public BasicNetwork(BaseHttpStack baseHttpStack) {
        this(baseHttpStack, new ByteArrayPool(4096));
    }

    public BasicNetwork(BaseHttpStack baseHttpStack, ByteArrayPool byteArrayPool) {
        this.mBaseHttpStack = baseHttpStack;
        this.mHttpStack = baseHttpStack;
        this.mPool = byteArrayPool;
    }

    /* JADX WARN: Code restructure failed: missing block: B:29:0x00aa, code lost:
    
        throw new java.io.IOException();
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0143 A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r13v1, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r19v0 */
    /* JADX WARN: Type inference failed for: r19v1, types: [java.util.List] */
    /* JADX WARN: Type inference failed for: r19v2 */
    /* JADX WARN: Type inference failed for: r19v4 */
    /* JADX WARN: Type inference failed for: r19v5 */
    @Override // com.android.volley.Network
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        ?? r19;
        HttpResponse httpResponse;
        byte[] bArr;
        byte[] inputStreamToBytes;
        BasicNetwork basicNetwork;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        while (true) {
            List<Header> emptyList = Collections.emptyList();
            try {
                try {
                    httpResponse = this.mBaseHttpStack.executeRequest(request, getCacheHeaders(request.getCacheEntry()));
                    try {
                        int statusCode = httpResponse.getStatusCode();
                        ?? headers = httpResponse.getHeaders();
                        if (statusCode == 304) {
                            Cache.Entry cacheEntry = request.getCacheEntry();
                            if (cacheEntry == null) {
                                return new NetworkResponse(304, (byte[]) null, true, SystemClock.elapsedRealtime() - elapsedRealtime, (List<Header>) headers);
                            }
                            return new NetworkResponse(304, cacheEntry.data, true, SystemClock.elapsedRealtime() - elapsedRealtime, combineHeaders(headers, cacheEntry));
                        }
                        try {
                            InputStream content = httpResponse.getContent();
                            if (content != null) {
                                try {
                                    inputStreamToBytes = inputStreamToBytes(content, httpResponse.getContentLength());
                                } catch (IOException e) {
                                    e = e;
                                    bArr = null;
                                    r19 = headers;
                                    if (httpResponse != null) {
                                    }
                                }
                            } else {
                                inputStreamToBytes = new byte[0];
                            }
                            try {
                                basicNetwork = this;
                                basicNetwork.logSlowRequests(SystemClock.elapsedRealtime() - elapsedRealtime, request, inputStreamToBytes, statusCode);
                            } catch (IOException e2) {
                                e = e2;
                                basicNetwork = headers;
                            }
                        } catch (IOException e3) {
                            e = e3;
                            emptyList = headers;
                            r19 = emptyList;
                            bArr = null;
                            if (httpResponse != null) {
                                int statusCode2 = httpResponse.getStatusCode();
                                VolleyLog.m3e("Unexpected response code %d for %s", Integer.valueOf(statusCode2), request.getUrl());
                                if (bArr != null) {
                                    NetworkResponse networkResponse = new NetworkResponse(statusCode2, bArr, false, SystemClock.elapsedRealtime() - elapsedRealtime, (List<Header>) r19);
                                    if (statusCode2 == 401 || statusCode2 == 403) {
                                        attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
                                    } else {
                                        if (statusCode2 >= 400 && statusCode2 <= 499) {
                                            throw new ClientError(networkResponse);
                                        }
                                        if (statusCode2 >= 500 && statusCode2 <= 599) {
                                            if (request.shouldRetryServerErrors()) {
                                                attemptRetryOnException("server", request, new ServerError(networkResponse));
                                            } else {
                                                throw new ServerError(networkResponse);
                                            }
                                        } else {
                                            throw new ServerError(networkResponse);
                                        }
                                    }
                                } else {
                                    attemptRetryOnException("network", request, new NetworkError());
                                }
                            } else {
                                throw new NoConnectionError(e);
                            }
                        }
                        try {
                            if (statusCode < 200 || statusCode > 299) {
                                break;
                            }
                            return new NetworkResponse(statusCode, inputStreamToBytes, false, SystemClock.elapsedRealtime() - elapsedRealtime, (List<Header>) headers);
                        } catch (IOException e4) {
                            e = e4;
                            r19 = basicNetwork;
                            bArr = inputStreamToBytes;
                            if (httpResponse != null) {
                            }
                        }
                    } catch (IOException e5) {
                        e = e5;
                    }
                } catch (IOException e6) {
                    e = e6;
                    r19 = emptyList;
                    httpResponse = null;
                    bArr = null;
                }
            } catch (MalformedURLException e7) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e7);
            } catch (SocketTimeoutException unused) {
                attemptRetryOnException("socket", request, new TimeoutError());
            }
        }
    }

    private void logSlowRequests(long j, Request<?> request, byte[] bArr, int i) {
        if (DEBUG || j > 3000) {
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(j);
            objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
            objArr[3] = Integer.valueOf(i);
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.m2d("HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]", objArr);
        }
    }

    private static void attemptRetryOnException(String str, Request<?> request, VolleyError volleyError) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int timeoutMs = request.getTimeoutMs();
        try {
            retryPolicy.retry(volleyError);
            request.addMarker(String.format("%s-retry [timeout=%s]", str, Integer.valueOf(timeoutMs)));
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", str, Integer.valueOf(timeoutMs)));
            throw e;
        }
    }

    private Map<String, String> getCacheHeaders(Cache.Entry entry) {
        if (entry == null) {
            return Collections.emptyMap();
        }
        HashMap hashMap = new HashMap();
        if (entry.etag != null) {
            hashMap.put("If-None-Match", entry.etag);
        }
        if (entry.lastModified > 0) {
            hashMap.put("If-Modified-Since", HttpHeaderParser.formatEpochAsRfc1123(entry.lastModified));
        }
        return hashMap;
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    private byte[] inputStreamToBytes(InputStream inputStream, int i) throws IOException, ServerError {
        PoolingByteArrayOutputStream poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(this.mPool, i);
        try {
            if (inputStream == null) {
                throw new ServerError();
            }
            byte[] buf = this.mPool.getBuf(1024);
            while (true) {
                int read = inputStream.read(buf);
                if (read == -1) {
                    break;
                }
                poolingByteArrayOutputStream.write(buf, 0, read);
            }
            byte[] byteArray = poolingByteArrayOutputStream.toByteArray();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused) {
                    VolleyLog.m5v("Error occurred when closing InputStream", new Object[0]);
                }
            }
            this.mPool.returnBuf(buf);
            poolingByteArrayOutputStream.close();
            return byteArray;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException unused2) {
                    VolleyLog.m5v("Error occurred when closing InputStream", new Object[0]);
                }
            }
            this.mPool.returnBuf(null);
            poolingByteArrayOutputStream.close();
            throw th;
        }
    }

    private static List<Header> combineHeaders(List<Header> list, Cache.Entry entry) {
        TreeSet treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        if (!list.isEmpty()) {
            Iterator<Header> it = list.iterator();
            while (it.hasNext()) {
                treeSet.add(it.next().getName());
            }
        }
        ArrayList arrayList = new ArrayList(list);
        if (entry.allResponseHeaders != null) {
            if (!entry.allResponseHeaders.isEmpty()) {
                for (Header header : entry.allResponseHeaders) {
                    if (!treeSet.contains(header.getName())) {
                        arrayList.add(header);
                    }
                }
            }
        } else if (!entry.responseHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry2 : entry.responseHeaders.entrySet()) {
                if (!treeSet.contains(entry2.getKey())) {
                    arrayList.add(new Header(entry2.getKey(), entry2.getValue()));
                }
            }
        }
        return arrayList;
    }
}
