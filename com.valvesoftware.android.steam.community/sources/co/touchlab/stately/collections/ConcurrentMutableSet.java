package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableSet;

/* compiled from: ConcurrentMutableSet.kt */
@Metadata(m995d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010#\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0007\b\u0016¢\u0006\u0002\u0010\u0004B%\b\u0000\u0012\u000e\u0010\u0005\u001a\n\u0018\u00010\u0006j\u0004\u0018\u0001`\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003¢\u0006\u0002\u0010\tJ+\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0001\u0010\u000b2\u0018\u0010\f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u0003\u0012\u0004\u0012\u0002H\u000b0\r¢\u0006\u0002\u0010\u000eR\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, m996d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableSet;", ExifInterface.LONGITUDE_EAST, "Lco/touchlab/stately/collections/ConcurrentMutableCollection;", "", "()V", "rootArg", "", "Lco/touchlab/stately/concurrency/Synchronizable;", "del", "(Ljava/lang/Object;Ljava/util/Set;)V", "block", "R", "f", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class ConcurrentMutableSet<E> extends ConcurrentMutableCollection<E> implements Set<E>, KMutableSet {
    private final Set<E> del;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ConcurrentMutableSet(Object obj, Set<E> del) {
        super(obj, del);
        Intrinsics.checkNotNullParameter(del, "del");
        this.del = del;
    }

    public ConcurrentMutableSet() {
        this(null, new LinkedHashSet());
    }

    public final <R> R block(final Function1<? super Set<E>, ? extends R> f) {
        R invoke;
        Intrinsics.checkNotNullParameter(f, "f");
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<R> function0 = new Function0<R>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableSet$block$1
            final /* synthetic */ ConcurrentMutableSet<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final R invoke() {
                Set set;
                set = ((ConcurrentMutableSet) this.this$0).del;
                MutableSetWrapper mutableSetWrapper = new MutableSetWrapper(set);
                R invoke2 = f.invoke(mutableSetWrapper);
                mutableSetWrapper.setSet$stately_concurrent_collections(new LinkedHashSet());
                return invoke2;
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            invoke = function0.invoke();
        }
        return invoke;
    }
}
