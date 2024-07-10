package com.android.volley;

import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class VolleyLog {
    public static String TAG = "Volley";
    public static boolean DEBUG = Log.isLoggable(TAG, 2);
    private static final String CLASS_NAME = VolleyLog.class.getName();

    /* renamed from: v */
    public static void m5v(String str, Object... objArr) {
        if (DEBUG) {
            buildMessage(str, objArr);
        }
    }

    /* renamed from: d */
    public static void m2d(String str, Object... objArr) {
        buildMessage(str, objArr);
    }

    /* renamed from: e */
    public static void m3e(String str, Object... objArr) {
        Log.e(TAG, buildMessage(str, objArr));
    }

    /* renamed from: e */
    public static void m4e(Throwable th, String str, Object... objArr) {
        Log.e(TAG, buildMessage(str, objArr), th);
    }

    public static void wtf(String str, Object... objArr) {
        Log.wtf(TAG, buildMessage(str, objArr));
    }

    private static String buildMessage(String str, Object... objArr) {
        if (objArr != null) {
            str = String.format(Locale.US, str, objArr);
        }
        StackTraceElement[] stackTrace = new Throwable().fillInStackTrace().getStackTrace();
        String str2 = "<unknown>";
        int i = 2;
        while (true) {
            if (i >= stackTrace.length) {
                break;
            }
            if (!stackTrace[i].getClassName().equals(CLASS_NAME)) {
                String className = stackTrace[i].getClassName();
                String substring = className.substring(className.lastIndexOf(46) + 1);
                str2 = substring.substring(substring.lastIndexOf(36) + 1) + "." + stackTrace[i].getMethodName();
                break;
            }
            i++;
        }
        return String.format(Locale.US, "[%d] %s: %s", Long.valueOf(Thread.currentThread().getId()), str2, str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MarkerLog {
        public static final boolean ENABLED = VolleyLog.DEBUG;
        private final List<Marker> mMarkers = new ArrayList();
        private boolean mFinished = false;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class Marker {
            public final String name;
            public final long thread;
            public final long time;

            public Marker(String str, long j, long j2) {
                this.name = str;
                this.thread = j;
                this.time = j2;
            }
        }

        public synchronized void add(String str, long j) {
            if (this.mFinished) {
                throw new IllegalStateException("Marker added to finished log");
            }
            this.mMarkers.add(new Marker(str, j, SystemClock.elapsedRealtime()));
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        public synchronized void finish(String str) {
            this.mFinished = true;
            long totalDuration = getTotalDuration();
            if (totalDuration <= 0) {
                return;
            }
            long j = this.mMarkers.get(0).time;
            VolleyLog.m2d("(%-4d ms) %s", Long.valueOf(totalDuration), str);
            for (Marker marker : this.mMarkers) {
                long j2 = marker.time;
                VolleyLog.m2d("(+%-4d) [%2d] %s", Long.valueOf(j2 - j), Long.valueOf(marker.thread), marker.name);
                j = j2;
            }
        }

        protected void finalize() throws Throwable {
            if (this.mFinished) {
                return;
            }
            finish("Request on the loose");
            VolleyLog.m3e("Marker log finalized without finish() - uncaught exit point for request", new Object[0]);
        }

        private long getTotalDuration() {
            if (this.mMarkers.size() == 0) {
                return 0L;
            }
            return this.mMarkers.get(r2.size() - 1).time - this.mMarkers.get(0).time;
        }
    }
}
