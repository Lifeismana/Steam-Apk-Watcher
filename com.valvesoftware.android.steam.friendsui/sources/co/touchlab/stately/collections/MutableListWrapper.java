package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableList;

/* compiled from: ConcurrentMutableList.kt */
@Metadata(m693d1 = {"\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010!\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u001e\n\u0002\b\u0006\n\u0002\u0010+\n\u0002\b\u0007\b\u0000\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0013\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003¢\u0006\u0002\u0010\u0005J\u001d\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u000eJ\u001e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00028\u00000\u0012H\u0016J\u0016\u0010\u0013\u001a\u00028\u00002\u0006\u0010\u000b\u001a\u00020\fH\u0096\u0002¢\u0006\u0002\u0010\u0014J\u0015\u0010\u0015\u001a\u00020\f2\u0006\u0010\r\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0016J\u0015\u0010\u0017\u001a\u00020\f2\u0006\u0010\r\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0016J\u000e\u0010\u0018\u001a\b\u0012\u0004\u0012\u00028\u00000\u0019H\u0016J\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00028\u00000\u00192\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0015\u0010\u001a\u001a\u00028\u00002\u0006\u0010\u000b\u001a\u00020\fH\u0016¢\u0006\u0002\u0010\u0014J\u001e\u0010\u001b\u001a\u00028\u00002\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u001cJ\u001e\u0010\u001d\u001a\b\u0012\u0004\u0012\u00028\u00000\u00032\u0006\u0010\u001e\u001a\u00020\f2\u0006\u0010\u001f\u001a\u00020\fH\u0016R \u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005¨\u0006 "}, m694d2 = {"Lco/touchlab/stately/collections/MutableListWrapper;", ExifInterface.LONGITUDE_EAST, "Lco/touchlab/stately/collections/MutableCollectionWrapper;", "", "list", "(Ljava/util/List;)V", "getList$stately_concurrent_collections", "()Ljava/util/List;", "setList$stately_concurrent_collections", "add", "", "index", "", "element", "(ILjava/lang/Object;)V", "addAll", "", "elements", "", "get", "(I)Ljava/lang/Object;", "indexOf", "(Ljava/lang/Object;)I", "lastIndexOf", "listIterator", "", "removeAt", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", "subList", "fromIndex", "toIndex", "stately-concurrent-collections"}, m695k = 1, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class MutableListWrapper<E> extends MutableCollectionWrapper<E> implements List<E>, KMutableList {
    private List<E> list;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public MutableListWrapper(List<E> list) {
        super(list);
        Intrinsics.checkNotNullParameter(list, "list");
        this.list = list;
    }

    public final List<E> getList$stately_concurrent_collections() {
        return this.list;
    }

    @Override // java.util.List
    public final /* bridge */ E remove(int i) {
        return removeAt(i);
    }

    public final void setList$stately_concurrent_collections(List<E> list) {
        Intrinsics.checkNotNullParameter(list, "<set-?>");
        this.list = list;
    }

    @Override // java.util.List
    public E get(int index) {
        return this.list.get(index);
    }

    @Override // java.util.List
    public int indexOf(Object element) {
        return this.list.indexOf(element);
    }

    @Override // java.util.List
    public int lastIndexOf(Object element) {
        return this.list.lastIndexOf(element);
    }

    @Override // java.util.List
    public void add(int index, E element) {
        this.list.add(index, element);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        return this.list.addAll(index, elements);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return this.list.listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return this.list.listIterator(index);
    }

    public E removeAt(int index) {
        return this.list.remove(index);
    }

    @Override // java.util.List
    public E set(int index, E element) {
        return this.list.set(index, element);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return this.list.subList(fromIndex, toIndex);
    }
}
