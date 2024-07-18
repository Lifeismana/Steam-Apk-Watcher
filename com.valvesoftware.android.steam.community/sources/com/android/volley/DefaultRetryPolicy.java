package com.android.volley;

/* loaded from: classes.dex */
public class DefaultRetryPolicy implements RetryPolicy {
    private final float mBackoffMultiplier;
    private int mCurrentRetryCount;
    private int mCurrentTimeoutMs;
    private final int mMaxNumRetries;

    public DefaultRetryPolicy() {
        this(2500, 1, 1.0f);
    }

    public DefaultRetryPolicy(int i, int i2, float f) {
        this.mCurrentTimeoutMs = i;
        this.mMaxNumRetries = i2;
        this.mBackoffMultiplier = f;
    }

    @Override // com.android.volley.RetryPolicy
    public int getCurrentTimeout() {
        return this.mCurrentTimeoutMs;
    }

    @Override // com.android.volley.RetryPolicy
    public int getCurrentRetryCount() {
        return this.mCurrentRetryCount;
    }

    @Override // com.android.volley.RetryPolicy
    public void retry(VolleyError volleyError) throws VolleyError {
        this.mCurrentRetryCount++;
        int i = this.mCurrentTimeoutMs;
        this.mCurrentTimeoutMs = i + ((int) (i * this.mBackoffMultiplier));
        if (!hasAttemptRemaining()) {
            throw volleyError;
        }
    }

    protected boolean hasAttemptRemaining() {
        return this.mCurrentRetryCount <= this.mMaxNumRetries;
    }
}
