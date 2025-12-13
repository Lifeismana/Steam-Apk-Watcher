package co.touchlab.stately.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AtomicInt.kt */
@Metadata(m995d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\",\u0010\u0000\u001a\u00020\u0001*\u00060\u0002j\u0002`\u00032\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007¨\u0006\b"}, m996d2 = {"value", "", "Ljava/util/concurrent/atomic/AtomicInteger;", "Lco/touchlab/stately/concurrency/AtomicInt;", "getValue", "(Ljava/util/concurrent/atomic/AtomicInteger;)I", "setValue", "(Ljava/util/concurrent/atomic/AtomicInteger;I)V", "stately-concurrency"}, m997k = 2, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class AtomicIntKt {
    public static final int getValue(AtomicInteger atomicInteger) {
        Intrinsics.checkNotNullParameter(atomicInteger, "<this>");
        return atomicInteger.get();
    }

    public static final void setValue(AtomicInteger atomicInteger, int i) {
        Intrinsics.checkNotNullParameter(atomicInteger, "<this>");
        atomicInteger.set(i);
    }
}
