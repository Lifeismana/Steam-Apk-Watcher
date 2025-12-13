package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableSet;

/* compiled from: ConcurrentMutableSet.kt */
@Metadata(m995d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010#\n\u0002\b\u0006\b\u0000\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0013\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003¢\u0006\u0002\u0010\u0005R \u0010\u0004\u001a\b\u0012\u0004\u0012\u00028\u00000\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005¨\u0006\t"}, m996d2 = {"Lco/touchlab/stately/collections/MutableSetWrapper;", ExifInterface.LONGITUDE_EAST, "Lco/touchlab/stately/collections/MutableCollectionWrapper;", "", "set", "(Ljava/util/Set;)V", "getSet$stately_concurrent_collections", "()Ljava/util/Set;", "setSet$stately_concurrent_collections", "stately-concurrent-collections"}, m997k = 1, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class MutableSetWrapper<E> extends MutableCollectionWrapper<E> implements Set<E>, KMutableSet {
    private Set<E> set;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public MutableSetWrapper(Set<E> set) {
        super(set);
        Intrinsics.checkNotNullParameter(set, "set");
        this.set = set;
    }

    public final Set<E> getSet$stately_concurrent_collections() {
        return this.set;
    }

    public final void setSet$stately_concurrent_collections(Set<E> set) {
        Intrinsics.checkNotNullParameter(set, "<set-?>");
        this.set = set;
    }
}
