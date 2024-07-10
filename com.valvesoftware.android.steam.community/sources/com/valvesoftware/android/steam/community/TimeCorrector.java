package com.valvesoftware.android.steam.community;

import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TimeCorrector {
    private static TimeCorrector s_instance;
    private int m_attemptCount;
    private boolean m_bForceSync;
    private boolean m_bLastSyncFailed;
    private boolean m_bSynchronizing;
    private long m_lastLocalTime;
    private long m_lastProbeTime;
    private long m_lastSyncTime;
    private long m_nextRetryTime;
    private long m_timeAdjustment;
    private long m_SkewToleranceSeconds = 60;
    private long m_LargeTimeJink = 86400;
    private int m_ProbeFrequencySeconds = 3600;
    private int m_AdjustedTimeProbeFrequencySeconds = 300;
    private int m_HintProbeFrequencySeconds = 60;
    private int m_SyncTimeout = 60;
    private int m_TryAgainSeconds = 300;
    private int m_MaxAttempts = 3;

    public static TimeCorrector getInstance() {
        if (s_instance == null) {
            s_instance = new TimeCorrector();
        }
        return s_instance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public abstract class TimeRequestHandler {
        public abstract void handleResult(boolean z, long j, long j2, long j3);

        private TimeRequestHandler() {
        }
    }

    public void update() {
        if (bNeedsProbe()) {
            startSync();
        } else {
            checkForZombieSync();
        }
    }

    public long currentTimeSeconds() {
        return systemTimeSeconds() + this.m_timeAdjustment;
    }

    public boolean bUsingAdjustedTime() {
        return this.m_timeAdjustment != 0;
    }

    public void hintSync() {
        if (this.m_bSynchronizing) {
            return;
        }
        long systemTimeSeconds = systemTimeSeconds();
        long j = this.m_lastProbeTime;
        if (j == 0) {
            this.m_bForceSync = true;
        } else if (systemTimeSeconds - j >= this.m_HintProbeFrequencySeconds) {
            if (bUsingAdjustedTime() || this.m_bLastSyncFailed) {
                this.m_bForceSync = true;
            }
        }
    }

    private boolean bNeedsProbe() {
        if (this.m_bSynchronizing) {
            return false;
        }
        if (this.m_bForceSync) {
            this.m_bForceSync = false;
            return true;
        }
        boolean bLocalTimeJumped = bLocalTimeJumped();
        if (bLocalTimeJumped) {
            return bLocalTimeJumped;
        }
        long systemTimeSeconds = systemTimeSeconds();
        long j = this.m_nextRetryTime;
        if (j <= 0 || systemTimeSeconds <= j) {
            return bUsingAdjustedTime() ? systemTimeSeconds - this.m_lastProbeTime >= ((long) this.m_AdjustedTimeProbeFrequencySeconds) : systemTimeSeconds - this.m_lastProbeTime >= ((long) this.m_ProbeFrequencySeconds);
        }
        return true;
    }

    private void startSync() {
        this.m_bSynchronizing = true;
        this.m_attemptCount = 0;
        this.m_nextRetryTime = 0L;
        this.m_bLastSyncFailed = false;
        this.m_lastProbeTime = systemTimeSeconds();
        continueSync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void retrySync() {
        if (this.m_attemptCount <= this.m_MaxAttempts) {
            continueSync();
        } else {
            failedSync(this.m_TryAgainSeconds);
        }
    }

    private void checkForZombieSync() {
        if (!this.m_bSynchronizing || systemTimeSeconds() - this.m_lastProbeTime <= this.m_SyncTimeout * this.m_MaxAttempts) {
            return;
        }
        failedSync(this.m_TryAgainSeconds);
    }

    private void continueSync() {
        this.m_attemptCount++;
        getServerTime(new TimeRequestHandler() { // from class: com.valvesoftware.android.steam.community.TimeCorrector.1
            @Override // com.valvesoftware.android.steam.community.TimeCorrector.TimeRequestHandler
            public void handleResult(boolean z, long j, long j2, long j3) {
                if (z) {
                    long j4 = j2 - j3;
                    boolean z2 = j3 >= j;
                    if (j2 < 1418057957 || j2 > 4133808000L) {
                        z2 = false;
                    }
                    if (j3 - j > 10) {
                        z2 = false;
                    }
                    if (j4 < TimeCorrector.this.m_SkewToleranceSeconds) {
                        z2 = false;
                    }
                    if (!z2) {
                        j4 = 0;
                    }
                    TimeCorrector.this.successfulSync(j4);
                    return;
                }
                TimeCorrector.this.retrySync();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void successfulSync(long j) {
        this.m_bSynchronizing = false;
        this.m_timeAdjustment = j;
        this.m_lastSyncTime = systemTimeSeconds();
    }

    private void failedSync(long j) {
        this.m_timeAdjustment = 0L;
        this.m_bSynchronizing = false;
        this.m_bLastSyncFailed = true;
        this.m_nextRetryTime = systemTimeSeconds() + j;
    }

    private static long extractLongValue(JSONObject jSONObject, String str, long j, long j2, long j3) {
        if (!jSONObject.has(str)) {
            return j;
        }
        long parseLong = Long.parseLong(jSONObject.optString(str, "0"));
        if (parseLong < j2) {
            parseLong = j2;
        }
        return parseLong > j3 ? j3 : parseLong;
    }

    private static int extractIntValue(JSONObject jSONObject, String str, int i, int i2, int i3) {
        if (!jSONObject.has(str)) {
            return i;
        }
        try {
            int parseInt = Integer.parseInt(jSONObject.optString(str, "0"));
            if (parseInt < i2) {
                parseInt = i2;
            }
            return parseInt > i3 ? i3 : parseInt;
        } catch (Exception unused) {
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void extractSyncParameters(JSONObject jSONObject) {
        this.m_SkewToleranceSeconds = extractLongValue(jSONObject, "skew_tolerance_seconds", this.m_SkewToleranceSeconds, 10L, 300L);
        this.m_LargeTimeJink = extractLongValue(jSONObject, "large_time_jink", this.m_LargeTimeJink, 60L, 31536000L);
        this.m_ProbeFrequencySeconds = extractIntValue(jSONObject, "probe_frequency_seconds", this.m_ProbeFrequencySeconds, 60, 31536000);
        this.m_AdjustedTimeProbeFrequencySeconds = extractIntValue(jSONObject, "adjusted_time_probe_frequency_seconds", this.m_AdjustedTimeProbeFrequencySeconds, 60, 31536000);
        this.m_HintProbeFrequencySeconds = extractIntValue(jSONObject, "hint_probe_frequency_seconds", this.m_HintProbeFrequencySeconds, 60, 31536000);
        this.m_SyncTimeout = extractIntValue(jSONObject, "sync_timeout", this.m_SyncTimeout, 60, 31536000);
        this.m_TryAgainSeconds = extractIntValue(jSONObject, "try_again_seconds", this.m_TryAgainSeconds, 60, 31536000);
        this.m_MaxAttempts = extractIntValue(jSONObject, "max_attempts", this.m_MaxAttempts, 0, 10);
    }

    private void getServerTime(final TimeRequestHandler timeRequestHandler) {
        final long systemTimeSeconds = systemTimeSeconds();
        RequestBuilder twoFactorQueryTimeRequestBuilder = Endpoints.getTwoFactorQueryTimeRequestBuilder();
        twoFactorQueryTimeRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.TimeCorrector.2
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                if (!jSONObject.optBoolean("allow_correction", true)) {
                    timeRequestHandler.handleResult(false, systemTimeSeconds, 0L, TimeCorrector.this.systemTimeSeconds());
                    return;
                }
                long parseLong = Long.parseLong(jSONObject.optString("server_time", "0"));
                TimeCorrector.this.extractSyncParameters(jSONObject);
                if (parseLong > 0) {
                    timeRequestHandler.handleResult(true, systemTimeSeconds, parseLong, TimeCorrector.this.systemTimeSeconds());
                } else {
                    timeRequestHandler.handleResult(false, systemTimeSeconds, 0L, TimeCorrector.this.systemTimeSeconds());
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                timeRequestHandler.handleResult(false, systemTimeSeconds, 0L, TimeCorrector.this.systemTimeSeconds());
            }
        });
        SteamCommunityApplication.GetInstance().sendRequest(twoFactorQueryTimeRequestBuilder);
    }

    private boolean bLocalTimeJumped() {
        long systemTimeSeconds = systemTimeSeconds();
        long j = this.m_lastLocalTime;
        boolean z = j > 0 && Math.abs(systemTimeSeconds - j) > this.m_LargeTimeJink;
        this.m_lastLocalTime = systemTimeSeconds;
        return z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long systemTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
