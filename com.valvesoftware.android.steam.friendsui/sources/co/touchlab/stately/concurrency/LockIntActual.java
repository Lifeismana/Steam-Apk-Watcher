package co.touchlab.stately.concurrency;

import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Lock.kt */
@Metadata(m693d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0011\u0010\u0000\u001a\u00020\u0001*\u00060\u0002j\u0002`\u0003H\u0086\b*\n\u0010\u0004\"\u00020\u00022\u00020\u0002¨\u0006\u0005"}, m694d2 = {"close", "", "Ljava/util/concurrent/locks/ReentrantLock;", "Lco/touchlab/stately/concurrency/Lock;", "Lock", "stately-concurrency"}, m695k = 2, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class LockIntActual {
    public static final void close(ReentrantLock reentrantLock) {
        Intrinsics.checkNotNullParameter(reentrantLock, "<this>");
    }
}
