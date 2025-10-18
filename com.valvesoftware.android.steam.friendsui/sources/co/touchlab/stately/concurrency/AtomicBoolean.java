package co.touchlab.stately.concurrency;

import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;

/* compiled from: AtomicBoolean.kt */
@Metadata(m693d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0003H\u0002J\u0016\u0010\u000f\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u0003R\u0012\u0010\u0005\u001a\u00060\u0006j\u0002`\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010\b\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u00038F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\u0004¨\u0006\u0012"}, m694d2 = {"Lco/touchlab/stately/concurrency/AtomicBoolean;", "", "value_", "", "(Z)V", "atom", "Ljava/util/concurrent/atomic/AtomicInteger;", "Lco/touchlab/stately/concurrency/AtomicInt;", "value", "getValue", "()Z", "setValue", "boolToInt", "", "b", "compareAndSet", "expected", "new", "stately-concurrency"}, m695k = 1, m696mv = {1, 9, 0}, m698xi = 48)
/* loaded from: classes.dex */
public final class AtomicBoolean {
    private final AtomicInteger atom;

    private final int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public AtomicBoolean(boolean z) {
        this.atom = new AtomicInteger(boolToInt(z));
    }

    public final boolean getValue() {
        return AtomicIntKt.getValue(this.atom) != 0;
    }

    public final void setValue(boolean z) {
        AtomicIntKt.setValue(this.atom, boolToInt(z));
    }

    public final boolean compareAndSet(boolean expected, boolean r3) {
        return this.atom.compareAndSet(boolToInt(expected), boolToInt(r3));
    }
}
