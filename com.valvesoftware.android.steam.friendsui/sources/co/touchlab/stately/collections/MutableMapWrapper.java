package co.touchlab.stately.collections;

import androidx.exifinterface.media.ExifInterface;
import com.google.firebase.messaging.Constants;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMutableMap;

/* compiled from: ConcurrentMutableMap.kt */
@Metadata(m693d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0010'\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u001f\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010$\n\u0002\b\u0002\b\u0000\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0004\b\u0001\u0010\u00022\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0003B\u0019\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0003¢\u0006\u0002\u0010\u0005J\b\u0010\u0018\u001a\u00020\u0019H\u0016J\u0015\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u001dJ\u0015\u0010\u001e\u001a\u00020\u001b2\u0006\u0010\u001f\u001a\u00028\u0001H\u0016¢\u0006\u0002\u0010\u001dJ\u0018\u0010 \u001a\u0004\u0018\u00018\u00012\u0006\u0010\u001c\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010!J\b\u0010\"\u001a\u00020\u001bH\u0016J\u001f\u0010#\u001a\u0004\u0018\u00018\u00012\u0006\u0010\u001c\u001a\u00028\u00002\u0006\u0010\u001f\u001a\u00028\u0001H\u0016¢\u0006\u0002\u0010$J\u001e\u0010%\u001a\u00020\u00192\u0014\u0010&\u001a\u0010\u0012\u0006\b\u0001\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010'H\u0016J\u0017\u0010(\u001a\u0004\u0018\u00018\u00012\u0006\u0010\u001c\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010!R&\u0010\u0006\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\b0\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00028\u00000\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\nR&\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0005R\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00010\u00158VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006)"}, m694d2 = {"Lco/touchlab/stately/collections/MutableMapWrapper;", "K", ExifInterface.GPS_MEASUREMENT_INTERRUPTED, "", "map", "(Ljava/util/Map;)V", "entries", "", "", "getEntries", "()Ljava/util/Set;", "keys", "getKeys", "getMap$stately_concurrent_collections", "()Ljava/util/Map;", "setMap$stately_concurrent_collections", "size", "", "getSize", "()I", "values", "", "getValues", "()Ljava/util/Collection;", "clear", "", "containsKey", "", "key", "(Ljava/lang/Object;)Z", "containsValue", "value", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", "isEmpty", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "putAll", Constants.MessagePayloadKeys.FROM, "", "remove", "stately-concurrent-collections"}, m695k = 1, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class MutableMapWrapper<K, V> implements Map<K, V>, KMutableMap {
    private Map<K, V> map;

    public MutableMapWrapper(Map<K, V> map) {
        Intrinsics.checkNotNullParameter(map, "map");
        this.map = map;
    }

    @Override // java.util.Map
    public final /* bridge */ Set<Map.Entry<K, V>> entrySet() {
        return getEntries();
    }

    public final Map<K, V> getMap$stately_concurrent_collections() {
        return this.map;
    }

    @Override // java.util.Map
    public final /* bridge */ Set<K> keySet() {
        return getKeys();
    }

    public final void setMap$stately_concurrent_collections(Map<K, V> map) {
        Intrinsics.checkNotNullParameter(map, "<set-?>");
        this.map = map;
    }

    @Override // java.util.Map
    public final /* bridge */ int size() {
        return getSize();
    }

    @Override // java.util.Map
    public final /* bridge */ Collection<V> values() {
        return getValues();
    }

    public int getSize() {
        return this.map.size();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override // java.util.Map
    public V get(Object key) {
        return this.map.get(key);
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set<Map.Entry<K, V>> getEntries() {
        return this.map.entrySet();
    }

    public Set<K> getKeys() {
        return this.map.keySet();
    }

    public Collection<V> getValues() {
        return this.map.values();
    }

    @Override // java.util.Map
    public void clear() {
        this.map.clear();
    }

    @Override // java.util.Map
    public V put(K key, V value) {
        return this.map.put(key, value);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends K, ? extends V> from) {
        Intrinsics.checkNotNullParameter(from, "from");
        this.map.putAll(from);
    }

    @Override // java.util.Map
    public V remove(Object key) {
        return this.map.remove(key);
    }
}
