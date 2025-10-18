package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import expo.modules.updates.codesigning.CodeSigningAlgorithmKt;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableIterator;

/* compiled from: ConcurrentMutableCollection.kt */
@Metadata(m693d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0010)\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00060\u0002j\u0002`\u00032\b\u0012\u0004\u0012\u0002H\u00010\u0004B\u001f\u0012\n\u0010\u0005\u001a\u00060\u0002j\u0002`\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004¢\u0006\u0002\u0010\u0007J\t\u0010\b\u001a\u00020\tH\u0096\u0002J\u000e\u0010\n\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u000bJ\b\u0010\f\u001a\u00020\rH\u0016R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u00060\u0002j\u0002`\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, m694d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableIterator;", ExifInterface.LONGITUDE_EAST, "", "Lco/touchlab/stately/concurrency/Synchronizable;", "", CodeSigningAlgorithmKt.CODE_SIGNING_METADATA_DEFAULT_KEY_ID, "del", "(Ljava/lang/Object;Ljava/util/Iterator;)V", "hasNext", "", "next", "()Ljava/lang/Object;", "remove", "", "stately-concurrent-collections"}, m695k = 1, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public class ConcurrentMutableIterator<E> implements Iterator<E>, KMutableIterator {
    private final Iterator<E> del;
    private final Object root;

    /* JADX WARN: Multi-variable type inference failed */
    public ConcurrentMutableIterator(Object root, Iterator<? extends E> del) {
        Intrinsics.checkNotNullParameter(root, "root");
        Intrinsics.checkNotNullParameter(del, "del");
        this.root = root;
        this.del = del;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        Boolean invoke;
        Object obj = this.root;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableIterator$hasNext$1
            final /* synthetic */ ConcurrentMutableIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Iterator it;
                it = ((ConcurrentMutableIterator) this.this$0).del;
                return Boolean.valueOf(it.hasNext());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Iterator
    public E next() {
        E invoke;
        Object obj = this.root;
        Function0<E> function0 = new Function0<E>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableIterator$next$1
            final /* synthetic */ ConcurrentMutableIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final E invoke() {
                Iterator it;
                it = ((ConcurrentMutableIterator) this.this$0).del;
                return (E) it.next();
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Iterator
    public void remove() {
        Object obj = this.root;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableIterator$remove$1
            final /* synthetic */ ConcurrentMutableIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Unit invoke() {
                invoke2();
                return Unit.INSTANCE;
            }

            /* renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2() {
                Iterator it;
                it = ((ConcurrentMutableIterator) this.this$0).del;
                it.remove();
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }
}
