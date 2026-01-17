package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableList;

/* compiled from: ConcurrentMutableList.kt */
@Metadata(m995d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u001e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010+\n\u0002\b\u0007\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0007\b\u0016¢\u0006\u0002\u0010\u0004B%\b\u0000\u0012\u000e\u0010\u0005\u001a\n\u0018\u00010\u0006j\u0004\u0018\u0001`\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003¢\u0006\u0002\u0010\tJ\u001d\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u000fJ\u001e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\r2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00028\u00000\u0013H\u0016J+\u0010\u0014\u001a\u0002H\u0015\"\u0004\b\u0001\u0010\u00152\u0018\u0010\u0016\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u0003\u0012\u0004\u0012\u0002H\u00150\u0017¢\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00028\u00002\u0006\u0010\f\u001a\u00020\rH\u0096\u0002¢\u0006\u0002\u0010\u001aJ\u0015\u0010\u001b\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u001cJ\u0015\u0010\u001d\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u001cJ\u000e\u0010\u001e\u001a\b\u0012\u0004\u0012\u00028\u00000\u001fH\u0016J\u0016\u0010\u001e\u001a\b\u0012\u0004\u0012\u00028\u00000\u001f2\u0006\u0010\f\u001a\u00020\rH\u0016J\u0015\u0010 \u001a\u00028\u00002\u0006\u0010\f\u001a\u00020\rH\u0016¢\u0006\u0002\u0010\u001aJ\u001e\u0010!\u001a\u00028\u00002\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\"J\u001e\u0010#\u001a\b\u0012\u0004\u0012\u00028\u00000\u00032\u0006\u0010$\u001a\u00020\r2\u0006\u0010%\u001a\u00020\rH\u0016R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006&"}, m996d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableList;", ExifInterface.LONGITUDE_EAST, "Lco/touchlab/stately/collections/ConcurrentMutableCollection;", "", "()V", "rootArg", "", "Lco/touchlab/stately/concurrency/Synchronizable;", "del", "(Ljava/lang/Object;Ljava/util/List;)V", "add", "", "index", "", "element", "(ILjava/lang/Object;)V", "addAll", "", "elements", "", "block", "R", "f", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "get", "(I)Ljava/lang/Object;", "indexOf", "(Ljava/lang/Object;)I", "lastIndexOf", "listIterator", "", "removeAt", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", "subList", "fromIndex", "toIndex", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class ConcurrentMutableList<E> extends ConcurrentMutableCollection<E> implements List<E>, KMutableList {
    private final List<E> del;

    @Override // java.util.List
    public final /* bridge */ E remove(int i) {
        return removeAt(i);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ConcurrentMutableList(Object obj, List<E> del) {
        super(obj, del);
        Intrinsics.checkNotNullParameter(del, "del");
        this.del = del;
    }

    public ConcurrentMutableList() {
        this(null, new ArrayList());
    }

    @Override // java.util.List
    public E get(final int index) {
        E eInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<E> function0 = new Function0<E>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.get.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final E invoke() {
                return (E) ((ConcurrentMutableList) this.this$0).del.get(index);
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            eInvoke = function0.invoke();
        }
        return eInvoke;
    }

    @Override // java.util.List
    public int indexOf(final Object element) {
        Integer numInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.indexOf.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                return Integer.valueOf(((ConcurrentMutableList) this.this$0).del.indexOf(element));
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            numInvoke = function0.invoke();
        }
        return numInvoke.intValue();
    }

    @Override // java.util.List
    public int lastIndexOf(final Object element) {
        Integer numInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.lastIndexOf.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                return Integer.valueOf(((ConcurrentMutableList) this.this$0).del.lastIndexOf(element));
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            numInvoke = function0.invoke();
        }
        return numInvoke.intValue();
    }

    @Override // java.util.List
    public void add(final int index, final E element) {
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.add.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

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
                ((ConcurrentMutableList) this.this$0).del.add(index, element);
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            function0.invoke();
        }
    }

    @Override // java.util.List
    public boolean addAll(final int index, final Collection<? extends E> elements) {
        Boolean boolInvoke;
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.addAll.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                return Boolean.valueOf(((ConcurrentMutableList) this.this$0).del.addAll(index, elements));
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            boolInvoke = function0.invoke();
        }
        return boolInvoke.booleanValue();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        ConcurrentMutableListIterator<E> concurrentMutableListIteratorInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<ConcurrentMutableListIterator<E>> function0 = new Function0<ConcurrentMutableListIterator<E>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.listIterator.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableListIterator<E> invoke() {
                ConcurrentMutableList<E> concurrentMutableList = this.this$0;
                return new ConcurrentMutableListIterator<>(concurrentMutableList, ((ConcurrentMutableList) concurrentMutableList).del.listIterator());
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            concurrentMutableListIteratorInvoke = function0.invoke();
        }
        return concurrentMutableListIteratorInvoke;
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(final int index) {
        ConcurrentMutableListIterator<E> concurrentMutableListIteratorInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<ConcurrentMutableListIterator<E>> function0 = new Function0<ConcurrentMutableListIterator<E>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.listIterator.2
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableListIterator<E> invoke() {
                ConcurrentMutableList<E> concurrentMutableList = this.this$0;
                return new ConcurrentMutableListIterator<>(concurrentMutableList, ((ConcurrentMutableList) concurrentMutableList).del.listIterator(index));
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            concurrentMutableListIteratorInvoke = function0.invoke();
        }
        return concurrentMutableListIteratorInvoke;
    }

    public E removeAt(final int index) {
        E eInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<E> function0 = new Function0<E>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.removeAt.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final E invoke() {
                return (E) ((ConcurrentMutableList) this.this$0).del.remove(index);
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            eInvoke = function0.invoke();
        }
        return eInvoke;
    }

    @Override // java.util.List
    public E set(final int index, final E element) {
        E eInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<E> function0 = new Function0<E>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.set.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final E invoke() {
                return (E) ((ConcurrentMutableList) this.this$0).del.set(index, element);
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            eInvoke = function0.invoke();
        }
        return eInvoke;
    }

    @Override // java.util.List
    public List<E> subList(final int fromIndex, final int toIndex) {
        ConcurrentMutableList<E> concurrentMutableListInvoke;
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<ConcurrentMutableList<E>> function0 = new Function0<ConcurrentMutableList<E>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.subList.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableList<E> invoke() {
                ConcurrentMutableList<E> concurrentMutableList = this.this$0;
                return new ConcurrentMutableList<>(concurrentMutableList, ((ConcurrentMutableList) concurrentMutableList).del.subList(fromIndex, toIndex));
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            concurrentMutableListInvoke = function0.invoke();
        }
        return concurrentMutableListInvoke;
    }

    public final <R> R block(final Function1<? super List<E>, ? extends R> f) {
        R rInvoke;
        Intrinsics.checkNotNullParameter(f, "f");
        Object syncTarget$stately_concurrent_collections = getSyncTarget();
        Function0<R> function0 = new Function0<R>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableList.block.1
            final /* synthetic */ ConcurrentMutableList<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final R invoke() {
                MutableListWrapper mutableListWrapper = new MutableListWrapper(((ConcurrentMutableList) this.this$0).del);
                R rInvoke2 = f.invoke(mutableListWrapper);
                mutableListWrapper.setList$stately_concurrent_collections(new ArrayList());
                return rInvoke2;
            }
        };
        synchronized (syncTarget$stately_concurrent_collections) {
            rInvoke = function0.invoke();
        }
        return rInvoke;
    }
}
