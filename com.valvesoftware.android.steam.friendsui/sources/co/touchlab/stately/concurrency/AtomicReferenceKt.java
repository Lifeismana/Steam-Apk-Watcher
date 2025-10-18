package co.touchlab.stately.concurrency;

import androidx.exifinterface.media.ExifInterface;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AtomicReference.kt */
@Metadata(m693d1 = {"\u0000\u0010\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\">\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u0012\u0012\u0004\u0012\u0002H\u00010\u0002j\b\u0012\u0004\u0012\u0002H\u0001`\u00032\u0006\u0010\u0000\u001a\u00028\u00008F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007¨\u0006\b"}, m694d2 = {"value", ExifInterface.GPS_DIRECTION_TRUE, "Ljava/util/concurrent/atomic/AtomicReference;", "Lco/touchlab/stately/concurrency/AtomicReference;", "getValue", "(Ljava/util/concurrent/atomic/AtomicReference;)Ljava/lang/Object;", "setValue", "(Ljava/util/concurrent/atomic/AtomicReference;Ljava/lang/Object;)V", "stately-concurrency"}, m695k = 2, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class AtomicReferenceKt {
    public static final <T> T getValue(AtomicReference<T> atomicReference) {
        Intrinsics.checkNotNullParameter(atomicReference, "<this>");
        return atomicReference.get();
    }

    public static final <T> void setValue(AtomicReference<T> atomicReference, T t) {
        Intrinsics.checkNotNullParameter(atomicReference, "<this>");
        atomicReference.set(t);
    }
}
