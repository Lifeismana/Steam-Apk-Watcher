package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.messaging.Constants;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableMap;

/* compiled from: ConcurrentMutableMap.kt */
@Metadata(m693d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\u0010%\n\u0002\b\u0005\n\u0002\u0010#\n\u0002\u0010'\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u001f\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010$\n\u0002\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u00060\u0003j\u0002`\u00042\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0005B\u0007\b\u0016¢\u0006\u0002\u0010\u0006B-\b\u0000\u0012\u0010\b\u0002\u0010\u0007\u001a\n\u0018\u00010\u0003j\u0004\u0018\u0001`\u0004\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0005¢\u0006\u0002\u0010\tJ1\u0010\u001a\u001a\u0002H\u001b\"\u0004\b\u0002\u0010\u001b2\u001e\u0010\u001c\u001a\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0005\u0012\u0004\u0012\u0002H\u001b0\u001d¢\u0006\u0002\u0010\u001eJ\b\u0010\u001f\u001a\u00020 H\u0016J+\u0010!\u001a\u00028\u00012\u0006\u0010\"\u001a\u00028\u00002\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u001dH\u0007¢\u0006\u0004\b$\u0010%J\u0015\u0010&\u001a\u00020'2\u0006\u0010\"\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010(J\u0015\u0010)\u001a\u00020'2\u0006\u0010*\u001a\u00028\u0001H\u0016¢\u0006\u0002\u0010(J\u0018\u0010+\u001a\u0004\u0018\u00018\u00012\u0006\u0010\"\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010,J\b\u0010-\u001a\u00020'H\u0016J\u001f\u0010.\u001a\u0004\u0018\u00018\u00012\u0006\u0010\"\u001a\u00028\u00002\u0006\u0010*\u001a\u00028\u0001H\u0016¢\u0006\u0002\u0010/J\u001e\u00100\u001a\u00020 2\u0014\u00101\u001a\u0010\u0012\u0006\b\u0001\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u000102H\u0016J\u0017\u00103\u001a\u0004\u0018\u00018\u00012\u0006\u0010\"\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010,R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010\n\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\f0\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u000b8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u000eR\u0014\u0010\u0011\u001a\u00020\u00128VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0012\u0010\u0015\u001a\u00060\u0003j\u0002`\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0016\u001a\b\u0012\u0004\u0012\u00028\u00010\u00178VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u00064"}, m694d2 = {"Lco/touchlab/stately/collections/ConcurrentMutableMap;", "K", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, "", "Lco/touchlab/stately/concurrency/Synchronizable;", "", "()V", "rootArg", "del", "(Ljava/lang/Object;Ljava/util/Map;)V", "entries", "", "", "getEntries", "()Ljava/util/Set;", "keys", "getKeys", "size", "", "getSize", "()I", "syncTarget", "values", "", "getValues", "()Ljava/util/Collection;", "block", "R", "f", "Lkotlin/Function1;", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "clear", "", "computeIfAbsent", "key", "defaultValue", "safeComputeIfAbsent", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "containsKey", "", "(Ljava/lang/Object;)Z", "containsValue", "value", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", "isEmpty", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "putAll", Constants.MessagePayloadKeys.FROM, "", "remove", "stately-concurrent-collections"}, m695k = 1, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class ConcurrentMutableMap<K, V> implements Map<K, V>, KMutableMap {
    private final Map<K, V> del;
    private final Object syncTarget;

    public /* synthetic */ ConcurrentMutableMap(Object obj, Map map, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : obj, map);
    }

    @Override // java.util.Map
    public final /* bridge */ Set<Map.Entry<K, V>> entrySet() {
        return getEntries();
    }

    @Override // java.util.Map
    public final /* bridge */ Set<K> keySet() {
        return getKeys();
    }

    @Override // java.util.Map
    public final /* bridge */ int size() {
        return getSize();
    }

    @Override // java.util.Map
    public final /* bridge */ Collection<V> values() {
        return getValues();
    }

    public ConcurrentMutableMap(Object obj, Map<K, V> del) {
        Intrinsics.checkNotNullParameter(del, "del");
        this.del = del;
        this.syncTarget = obj == null ? this : obj;
    }

    public ConcurrentMutableMap() {
        this(null, new LinkedHashMap());
    }

    public int getSize() {
        Integer invoke;
        Object obj = this.syncTarget;
        Function0<Integer> function0 = new Function0<Integer>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$size$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Integer invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return Integer.valueOf(map.size());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.intValue();
    }

    public Set<Map.Entry<K, V>> getEntries() {
        ConcurrentMutableSet<Map.Entry<K, V>> invoke;
        Object obj = this.syncTarget;
        Function0<ConcurrentMutableSet<Map.Entry<K, V>>> function0 = new Function0<ConcurrentMutableSet<Map.Entry<K, V>>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$entries$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableSet<Map.Entry<K, V>> invoke() {
                Map map;
                ConcurrentMutableMap<K, V> concurrentMutableMap = this.this$0;
                map = ((ConcurrentMutableMap) concurrentMutableMap).del;
                return new ConcurrentMutableSet<>(concurrentMutableMap, map.entrySet());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    public Set<K> getKeys() {
        ConcurrentMutableSet<K> invoke;
        Object obj = this.syncTarget;
        Function0<ConcurrentMutableSet<K>> function0 = new Function0<ConcurrentMutableSet<K>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$keys$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableSet<K> invoke() {
                Map map;
                ConcurrentMutableMap<K, V> concurrentMutableMap = this.this$0;
                map = ((ConcurrentMutableMap) concurrentMutableMap).del;
                return new ConcurrentMutableSet<>(concurrentMutableMap, map.keySet());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    public Collection<V> getValues() {
        ConcurrentMutableCollection<V> invoke;
        Object obj = this.syncTarget;
        Function0<ConcurrentMutableCollection<V>> function0 = new Function0<ConcurrentMutableCollection<V>>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$values$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final ConcurrentMutableCollection<V> invoke() {
                Map map;
                ConcurrentMutableMap<K, V> concurrentMutableMap = this.this$0;
                map = ((ConcurrentMutableMap) concurrentMutableMap).del;
                return new ConcurrentMutableCollection<>(concurrentMutableMap, map.values());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Map
    public boolean containsKey(final Object key) {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$containsKey$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return Boolean.valueOf(map.containsKey(key));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Map
    public boolean containsValue(final Object value) {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$containsValue$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return Boolean.valueOf(map.containsValue(value));
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Map
    public V get(final Object key) {
        V invoke;
        Object obj = this.syncTarget;
        Function0<V> function0 = new Function0<V>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$get$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final V invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return (V) map.get(key);
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        Boolean invoke;
        Object obj = this.syncTarget;
        Function0<Boolean> function0 = new Function0<Boolean>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$isEmpty$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // kotlin.jvm.functions.Function0
            public final Boolean invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return Boolean.valueOf(map.isEmpty());
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke.booleanValue();
    }

    @Override // java.util.Map
    public void clear() {
        Object obj = this.syncTarget;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$clear$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

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
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                map.clear();
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }

    public final V safeComputeIfAbsent(final K key, final Function1<? super K, ? extends V> defaultValue) {
        V invoke;
        Intrinsics.checkNotNullParameter(defaultValue, "defaultValue");
        Object obj = this.syncTarget;
        Function0<V> function0 = new Function0<V>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$computeIfAbsent$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final V invoke() {
                Map map;
                Map map2;
                map = ((ConcurrentMutableMap) this.this$0).del;
                V v = (V) map.get(key);
                if (v != null) {
                    return v;
                }
                V invoke2 = defaultValue.invoke(key);
                map2 = ((ConcurrentMutableMap) this.this$0).del;
                map2.put(key, invoke2);
                return invoke2;
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Map
    public V put(final K key, final V value) {
        V invoke;
        Object obj = this.syncTarget;
        Function0<V> function0 = new Function0<V>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$put$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final V invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return (V) map.put(key, value);
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    @Override // java.util.Map
    public void putAll(final Map<? extends K, ? extends V> from) {
        Intrinsics.checkNotNullParameter(from, "from");
        Object obj = this.syncTarget;
        Function0<Unit> function0 = new Function0<Unit>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$putAll$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
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
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                map.putAll(from);
            }
        };
        synchronized (obj) {
            function0.invoke();
        }
    }

    @Override // java.util.Map
    public V remove(final Object key) {
        V invoke;
        Object obj = this.syncTarget;
        Function0<V> function0 = new Function0<V>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$remove$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final V invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                return (V) map.remove(key);
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }

    public final <R> R block(final Function1<? super Map<K, V>, ? extends R> f) {
        R invoke;
        Intrinsics.checkNotNullParameter(f, "f");
        Object obj = this.syncTarget;
        Function0<R> function0 = new Function0<R>(this) { // from class: co.touchlab.stately.collections.ConcurrentMutableMap$block$1
            final /* synthetic */ ConcurrentMutableMap<K, V> this$0;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            /* JADX WARN: Multi-variable type inference failed */
            {
                super(0);
                this.this$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public final R invoke() {
                Map map;
                map = ((ConcurrentMutableMap) this.this$0).del;
                MutableMapWrapper mutableMapWrapper = new MutableMapWrapper(map);
                R invoke2 = f.invoke(mutableMapWrapper);
                mutableMapWrapper.setMap$stately_concurrent_collections(new LinkedHashMap());
                return invoke2;
            }
        };
        synchronized (obj) {
            invoke = function0.invoke();
        }
        return invoke;
    }
}
