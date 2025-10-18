package co.touchlab.stately.concurrency;

import androidx.exifinterface.media.ExifInterface;
import java.util.concurrent.locks.ReentrantLock;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Lock.kt */
@Metadata(m693d1 = {"\u0000\u0016\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a-\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u00060\u0002j\u0002`\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0005H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u0006\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0007"}, m694d2 = {"withLock", ExifInterface.GPS_DIRECTION_TRUE, "Ljava/util/concurrent/locks/ReentrantLock;", "Lco/touchlab/stately/concurrency/Lock;", "block", "Lkotlin/Function0;", "(Ljava/util/concurrent/locks/ReentrantLock;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "stately-concurrency"}, m695k = 2, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class LockKt {
    public static final <T> T withLock(ReentrantLock reentrantLock, Function0<? extends T> block) {
        Intrinsics.checkNotNullParameter(reentrantLock, "<this>");
        Intrinsics.checkNotNullParameter(block, "block");
        reentrantLock.lock();
        try {
            return block.invoke();
        } finally {
            reentrantLock.unlock();
        }
    }
}
