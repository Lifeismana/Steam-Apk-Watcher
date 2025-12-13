package co.touchlab.stately.concurrency;

import kotlin.Metadata;

/* compiled from: ThreadRef.kt */
@Metadata(m995d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0007"}, m996d2 = {"Lco/touchlab/stately/concurrency/ThreadRef;", "", "()V", "threadRef", "", "same", "", "stately-concurrency"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class ThreadRef {
    private final long threadRef = Thread.currentThread().getId();

    public final boolean same() {
        return this.threadRef == Thread.currentThread().getId();
    }
}
