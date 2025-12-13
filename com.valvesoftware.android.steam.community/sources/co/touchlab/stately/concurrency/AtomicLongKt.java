package co.touchlab.stately.concurrency;

import java.util.concurrent.atomic.AtomicLong;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AtomicLong.kt */
@Metadata(m995d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\",\u0010\u0000\u001a\u00020\u0001*\u00060\u0002j\u0002`\u00032\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007¨\u0006\b"}, m996d2 = {"value", "", "Ljava/util/concurrent/atomic/AtomicLong;", "Lco/touchlab/stately/concurrency/AtomicLong;", "getValue", "(Ljava/util/concurrent/atomic/AtomicLong;)J", "setValue", "(Ljava/util/concurrent/atomic/AtomicLong;J)V", "stately-concurrency"}, m997k = 2, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class AtomicLongKt {
    public static final long getValue(AtomicLong atomicLong) {
        Intrinsics.checkNotNullParameter(atomicLong, "<this>");
        return atomicLong.get();
    }

    public static final void setValue(AtomicLong atomicLong, long j) {
        Intrinsics.checkNotNullParameter(atomicLong, "<this>");
        atomicLong.set(j);
    }
}
