package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import expo.modules.updates.codesigning.CodeSigningAlgorithmKt;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableListIterator;

/* compiled from: ConcurrentMutableMap.kt */
@Metadata(m995d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010+\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0000\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u001f\u0012\n\u0010\u0004\u001a\u00060\u0005j\u0002`\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003¢\u0006\u0002\u0010\bJ\u0015\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\fJ\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\r\u0010\u0011\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0012J\b\u0010\u0013\u001a\u00020\u0010H\u0016J\u0015\u0010\u0014\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u00060\u0005j\u0002`\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"}, m996d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableListIterator;", ExifInterface.LONGITUDE_EAST, "Lco/touchlab/stately/collections/ConcurrentMutableIterator;", "", CodeSigningAlgorithmKt.CODE_SIGNING_METADATA_DEFAULT_KEY_ID, "", "Lco/touchlab/stately/concurrency/Synchronizable;", "del", "(Ljava/lang/Object;Ljava/util/ListIterator;)V", "add", "", "element", "(Ljava/lang/Object;)V", "hasPrevious", "", "nextIndex", "", "previous", "()Ljava/lang/Object;", "previousIndex", "set", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class ConcurrentMutableListIterator<E> extends ConcurrentMutableIterator<E> implements ListIterator<E>, KMutableListIterator {
    private final ListIterator<E> del;
    private final Object root;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ConcurrentMutableListIterator(Object root, ListIterator<E> del) {
        super(root, del);
        Intrinsics.checkNotNullParameter(root, "root");
        Intrinsics.checkNotNullParameter(del, "del");
        this.root = root;
        this.del = del;
    }

    @Override // java.util.ListIterator
    public boolean hasPrevious() {
        Boolean invoke;
        Object obj = this.root;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$hasPrevious$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                return Boolean.valueOf(listIterator.hasPrevious());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.ListIterator
    public int nextIndex() {
        Integer invoke;
        Object obj = this.root;
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$nextIndex$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                return Integer.valueOf(listIterator.nextIndex());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.intValue();
    }

    @Override // java.util.ListIterator
    public E previous() {
        E invoke;
        Object obj = this.root;
        Function0<E> function0 = new Function0<E>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$previous$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final E invoke() {
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                return (E) listIterator.previous();
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.ListIterator
    public int previousIndex() {
        Integer invoke;
        Object obj = this.root;
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$previousIndex$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                return Integer.valueOf(listIterator.previousIndex());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.intValue();
    }

    @Override // java.util.ListIterator
    public void add(final E element) {
        Object obj = this.root;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$add$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

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
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                listIterator.add(element);
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }

    @Override // java.util.ListIterator
    public void set(final E element) {
        Object obj = this.root;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableListIterator$set$1
            final /* synthetic */ ConcurrentMutableListIterator<E> this$0;

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
                ListIterator listIterator;
                listIterator = ((ConcurrentMutableListIterator) this.this$0).del;
                listIterator.set(element);
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }
}
