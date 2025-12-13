package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.CollectionToArray;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableCollection;

/* compiled from: ConcurrentMutableCollection.kt */
@Metadata(m995d1 = {"\u00004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001f\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010)\n\u0002\b\u0004\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0015\u0012\u000e\u0010\u0003\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0002¢\u0006\u0002\u0010\u0004J\u0015\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u000f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00028\u00000\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0016\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u0011J\u0016\u0010\u0018\u001a\u00020\u000f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00028\u00000\u0014H\u0016J\b\u0010\u0019\u001a\u00020\u000fH\u0016J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00028\u00000\u001bH\u0096\u0002J\u0015\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0011J\u0016\u0010\u001d\u001a\u00020\u000f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00028\u00000\u0014H\u0016J\u0016\u0010\u001e\u001a\u00020\u000f2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00028\u00000\u0014H\u0016R\"\u0010\u0003\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010\u0002X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\u0004R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u00028BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0006R\u0014\u0010\n\u001a\u00020\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\r¨\u0006\u001f"}, m996d2 = {"Lco/touchlab/stately/collections/MutableCollectionWrapper;", ExifInterface.LONGITUDE_EAST, "", "_coll", "(Ljava/util/Collection;)V", "get_coll$stately_concurrent_collections", "()Ljava/util/Collection;", "set_coll$stately_concurrent_collections", "coll", "getColl", "size", "", "getSize", "()I", "add", "", "element", "(Ljava/lang/Object;)Z", "addAll", "elements", "", "clear", "", "contains", "containsAll", "isEmpty", "iterator", "", "remove", "removeAll", "retainAll", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public class MutableCollectionWrapper<E> implements Collection<E>, KMutableCollection {
    private Collection<E> _coll;

    @Override // java.util.Collection
    public Object[] toArray() {
        return CollectionToArray.toArray(this);
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        return (T[]) CollectionToArray.toArray(this, array);
    }

    public MutableCollectionWrapper(Collection<E> collection) {
        this._coll = collection;
    }

    public final Collection<E> get_coll$stately_concurrent_collections() {
        return this._coll;
    }

    public final void set_coll$stately_concurrent_collections(Collection<E> collection) {
        this._coll = collection;
    }

    @Override // java.util.Collection
    public final /* bridge */ int size() {
        return getSize();
    }

    private final Collection<E> getColl() {
        Collection<E> collection = this._coll;
        Intrinsics.checkNotNull(collection);
        return collection;
    }

    @Override // java.util.Collection
    public boolean add(E element) {
        return getColl().add(element);
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends E> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        return getColl().addAll(elements);
    }

    @Override // java.util.Collection
    public void clear() {
        getColl().clear();
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return getColl().iterator();
    }

    @Override // java.util.Collection
    public boolean remove(Object element) {
        return getColl().remove(element);
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<? extends Object> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        return getColl().removeAll(elements);
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<? extends Object> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        return getColl().retainAll(elements);
    }

    public int getSize() {
        return getColl().size();
    }

    @Override // java.util.Collection
    public boolean contains(Object element) {
        return getColl().contains(element);
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<? extends Object> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        return getColl().containsAll(elements);
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        return getColl().isEmpty();
    }
}
