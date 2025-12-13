package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableCollection;

/* compiled from: ConcurrentMutableCollection.kt */
@Metadata(m995d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u001f\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010)\n\u0002\b\u0004\b\u0016\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00060\u0002j\u0002`\u00032\b\u0012\u0004\u0012\u0002H\u00010\u0004B'\b\u0000\u0012\u0010\b\u0002\u0010\u0005\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u0003\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004¢\u0006\u0002\u0010\u0007J\u0015\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u0015H\u0016J+\u0010\u0016\u001a\u0002H\u0017\"\u0004\b\u0001\u0010\u00172\u0018\u0010\u0018\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u0004\u0012\u0004\u0012\u0002H\u00170\u0019¢\u0006\u0002\u0010\u001aJ\b\u0010\u001b\u001a\u00020\u001cH\u0016J\u0016\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u0012J\u0016\u0010\u001e\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u0015H\u0016J\b\u0010\u001f\u001a\u00020\u0010H\u0016J\u000f\u0010 \u001a\b\u0012\u0004\u0012\u00028\u00000!H\u0096\u0002J\u0015\u0010\"\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0012J\u0016\u0010#\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u0015H\u0016J\u0016\u0010$\u001a\u00020\u00102\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u0015H\u0016R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0018\u0010\f\u001a\u00060\u0002j\u0002`\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006%"}, m996d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableCollection;", ExifInterface.LONGITUDE_EAST, "", "Lco/touchlab/stately/concurrency/Synchronizable;", "", "rootArg", "del", "(Ljava/lang/Object;Ljava/util/Collection;)V", "size", "", "getSize", "()I", "syncTarget", "getSyncTarget$stately_concurrent_collections", "()Ljava/lang/Object;", "add", "", "element", "(Ljava/lang/Object;)Z", "addAll", "elements", "", "blockCollection", "R", "f", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "clear", "", "contains", "containsAll", "isEmpty", "iterator", "", "remove", "removeAll", "retainAll", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public class ConcurrentMutableCollection<E> implements Collection<E>, KMutableCollection {
    private final Collection<E> del;
    private final Object syncTarget;

    @Override // java.util.Collection
    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        return (T[]) CollectionToArray.toArray(this, array);
    }

    public /* synthetic */ ConcurrentMutableCollection(Object obj, Collection collection, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : obj, collection);
    }

    @Override // java.util.Collection
    public final /* bridge */ int size() {
        return getSize();
    }

    public ConcurrentMutableCollection(Object obj, Collection<E> del) {
        Intrinsics.checkNotNullParameter(del, "del");
        this.del = del;
        this.syncTarget = obj == null ? this : obj;
    }

    /* renamed from: getSyncTarget$stately_concurrent_collections, reason: from getter */
    public final Object getSyncTarget() {
        return this.syncTarget;
    }

    public int getSize() {
        Integer invoke;
        Object obj = this.syncTarget;
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$size$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Integer.valueOf(collection.size());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.intValue();
    }

    @Override // java.util.Collection
    public boolean contains(final Object element) {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$contains$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.contains(element));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean containsAll(final Collection<? extends Object> elements) {
        Boolean invoke;
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$containsAll$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.containsAll(elements));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$isEmpty$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.isEmpty());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean add(final E element) {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$add$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.add(element));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean addAll(final Collection<? extends E> elements) {
        Boolean invoke;
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$addAll$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.addAll(elements));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public void clear() {
        Object obj = this.syncTarget;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$clear$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

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
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                collection.clear();
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        ConcurrentMutableIterator<E> invoke;
        Object obj = this.syncTarget;
        Function0<ConcurrentMutableIterator<E>> function0 = new Function0<ConcurrentMutableIterator<E>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$iterator$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableIterator<E> invoke() {
                Collection collection;
                Object syncTarget = this.this$0.getSyncTarget();
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return new ConcurrentMutableIterator<>(syncTarget, collection.iterator());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Collection
    public boolean remove(final Object element) {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$remove$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.remove(element));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean removeAll(final Collection<? extends Object> elements) {
        Boolean invoke;
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$removeAll$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.removeAll(elements));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Collection
    public boolean retainAll(final Collection<? extends Object> elements) {
        Boolean invoke;
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$retainAll$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                return Boolean.valueOf(collection.retainAll(elements));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    public final <R> R blockCollection(final Function1<? super Collection<E>, ? extends R> f) {
        R invoke;
        Intrinsics.checkNotNullParameter(f, "f");
        Object obj = this.syncTarget;
        Function0<R> function0 = new Function0<R>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableCollection$blockCollection$1
            final /* synthetic */ ConcurrentMutableCollection<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final R invoke() {
                Collection collection;
                collection = ((ConcurrentMutableCollection) this.this$0).del;
                MutableCollectionWrapper mutableCollectionWrapper = new MutableCollectionWrapper(collection);
                R invoke2 = f.invoke(mutableCollectionWrapper);
                mutableCollectionWrapper.set_coll$stately_concurrent_collections(null);
                return invoke2;
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }
}
