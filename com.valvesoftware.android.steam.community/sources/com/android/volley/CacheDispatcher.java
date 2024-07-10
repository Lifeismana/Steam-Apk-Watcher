package com.android.volley;

import android.os.Process;
import com.android.volley.Cache;
import com.android.volley.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/* loaded from: classes.dex */
public class CacheDispatcher extends Thread {
    private static final boolean DEBUG = VolleyLog.DEBUG;
    private final Cache mCache;
    private final BlockingQueue<Request<?>> mCacheQueue;
    private final ResponseDelivery mDelivery;
    private final BlockingQueue<Request<?>> mNetworkQueue;
    private volatile boolean mQuit = false;
    private final WaitingRequestManager mWaitingRequestManager = new WaitingRequestManager(this);

    public CacheDispatcher(BlockingQueue<Request<?>> blockingQueue, BlockingQueue<Request<?>> blockingQueue2, Cache cache, ResponseDelivery responseDelivery) {
        this.mCacheQueue = blockingQueue;
        this.mNetworkQueue = blockingQueue2;
        this.mCache = cache;
        this.mDelivery = responseDelivery;
    }

    public void quit() {
        this.mQuit = true;
        interrupt();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        if (DEBUG) {
            VolleyLog.m5v("start new dispatcher", new Object[0]);
        }
        Process.setThreadPriority(10);
        this.mCache.initialize();
        while (true) {
            try {
                processRequest();
            } catch (InterruptedException unused) {
                if (this.mQuit) {
                    Thread.currentThread().interrupt();
                    return;
                }
                VolleyLog.m3e("Ignoring spurious interrupt of CacheDispatcher thread; use quit() to terminate it", new Object[0]);
            }
        }
    }

    private void processRequest() throws InterruptedException {
        processRequest(this.mCacheQueue.take());
    }

    void processRequest(final Request<?> request) throws InterruptedException {
        request.addMarker("cache-queue-take");
        if (request.isCanceled()) {
            request.finish("cache-discard-canceled");
            return;
        }
        Cache.Entry entry = this.mCache.get(request.getCacheKey());
        if (entry == null) {
            request.addMarker("cache-miss");
            if (this.mWaitingRequestManager.maybeAddToWaitingRequests(request)) {
                return;
            }
            this.mNetworkQueue.put(request);
            return;
        }
        if (entry.isExpired()) {
            request.addMarker("cache-hit-expired");
            request.setCacheEntry(entry);
            if (this.mWaitingRequestManager.maybeAddToWaitingRequests(request)) {
                return;
            }
            this.mNetworkQueue.put(request);
            return;
        }
        request.addMarker("cache-hit");
        Response<?> parseNetworkResponse = request.parseNetworkResponse(new NetworkResponse(entry.data, entry.responseHeaders));
        request.addMarker("cache-hit-parsed");
        if (!entry.refreshNeeded()) {
            this.mDelivery.postResponse(request, parseNetworkResponse);
            return;
        }
        request.addMarker("cache-hit-refresh-needed");
        request.setCacheEntry(entry);
        parseNetworkResponse.intermediate = true;
        if (!this.mWaitingRequestManager.maybeAddToWaitingRequests(request)) {
            this.mDelivery.postResponse(request, parseNetworkResponse, new Runnable() { // from class: com.android.volley.CacheDispatcher.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        CacheDispatcher.this.mNetworkQueue.put(request);
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        } else {
            this.mDelivery.postResponse(request, parseNetworkResponse);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class WaitingRequestManager implements Request.NetworkRequestCompleteListener {
        private final CacheDispatcher mCacheDispatcher;
        private final Map<String, List<Request<?>>> mWaitingRequests = new HashMap();

        WaitingRequestManager(CacheDispatcher cacheDispatcher) {
            this.mCacheDispatcher = cacheDispatcher;
        }

        @Override // com.android.volley.Request.NetworkRequestCompleteListener
        public void onResponseReceived(Request<?> request, Response<?> response) {
            List<Request<?>> remove;
            if (response.cacheEntry == null || response.cacheEntry.isExpired()) {
                onNoUsableResponseReceived(request);
                return;
            }
            String cacheKey = request.getCacheKey();
            synchronized (this) {
                remove = this.mWaitingRequests.remove(cacheKey);
            }
            if (remove != null) {
                if (VolleyLog.DEBUG) {
                    VolleyLog.m5v("Releasing %d waiting requests for cacheKey=%s.", Integer.valueOf(remove.size()), cacheKey);
                }
                Iterator<Request<?>> it = remove.iterator();
                while (it.hasNext()) {
                    this.mCacheDispatcher.mDelivery.postResponse(it.next(), response);
                }
            }
        }

        @Override // com.android.volley.Request.NetworkRequestCompleteListener
        public synchronized void onNoUsableResponseReceived(Request<?> request) {
            String cacheKey = request.getCacheKey();
            List<Request<?>> remove = this.mWaitingRequests.remove(cacheKey);
            if (remove != null && !remove.isEmpty()) {
                if (VolleyLog.DEBUG) {
                    VolleyLog.m5v("%d waiting requests for cacheKey=%s; resend to network", Integer.valueOf(remove.size()), cacheKey);
                }
                Request<?> remove2 = remove.remove(0);
                this.mWaitingRequests.put(cacheKey, remove);
                remove2.setNetworkRequestCompleteListener(this);
                try {
                    this.mCacheDispatcher.mNetworkQueue.put(remove2);
                } catch (InterruptedException e) {
                    VolleyLog.m3e("Couldn't add request to queue. %s", e.toString());
                    Thread.currentThread().interrupt();
                    this.mCacheDispatcher.quit();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized boolean maybeAddToWaitingRequests(Request<?> request) {
            String cacheKey = request.getCacheKey();
            if (this.mWaitingRequests.containsKey(cacheKey)) {
                List<Request<?>> list = this.mWaitingRequests.get(cacheKey);
                if (list == null) {
                    list = new ArrayList<>();
                }
                request.addMarker("waiting-for-response");
                list.add(request);
                this.mWaitingRequests.put(cacheKey, list);
                if (VolleyLog.DEBUG) {
                    VolleyLog.m2d("Request for cacheKey=%s is in flight, putting on hold.", cacheKey);
                }
                return true;
            }
            this.mWaitingRequests.put(cacheKey, null);
            request.setNetworkRequestCompleteListener(this);
            if (VolleyLog.DEBUG) {
                VolleyLog.m2d("new request, sending to network %s", cacheKey);
            }
            return false;
        }
    }
}
