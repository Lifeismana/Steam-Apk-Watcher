package co.touchlab.stately.concurrency;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Synchronizable.kt */
@Metadata(m693d1 = {"\u0000\u0016\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a/\u0010\u0000\u001a\u0002H\u0001\"\u0004\b\u0000\u0010\u0001*\u00060\u0002j\u0002`\u00032\u000e\b\b\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0005H\u0086\bø\u0001\u0000¢\u0006\u0002\u0010\u0006*\n\u0010\u0007\"\u00020\u00022\u00020\u0002\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\b"}, m694d2 = {"synchronize", "R", "", "Lco/touchlab/stately/concurrency/Synchronizable;", "block", "Lkotlin/Function0;", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "Synchronizable", "stately-concurrency"}, m695k = 2, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class SynchronizableKt {
    public static final <R> R synchronize(Object obj, Function0<? extends R> block) {
        R invoke;
        Intrinsics.checkNotNullParameter(obj, "<this>");
        Intrinsics.checkNotNullParameter(block, "block");
        synchronized (obj) {
            invoke = block.invoke();
        }
        return invoke;
    }
}
