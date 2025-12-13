package co.touchlab.stately.strict;

import androidx.exifinterface.media.ExifInterface;
import kotlin.Metadata;

/* compiled from: HelpersJVM.kt */
@Metadata(m995d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\u001a\u0015\u0010\u0004\u001a\u0002H\u0005\"\u0004\b\u0000\u0010\u0005*\u0002H\u0005¢\u0006\u0002\u0010\u0006\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u0007"}, m996d2 = {"strictMemoryModel", "", "getStrictMemoryModel", "()Z", "maybeFreeze", ExifInterface.GPS_DIRECTION_TRUE, "(Ljava/lang/Object;)Ljava/lang/Object;", "stately-strict"}, m997k = 2, m998mv = {1, 9, 0}, m1000xi = 48)
/* loaded from: classes.dex */
public final class HelpersJVMKt {
    private static final boolean strictMemoryModel = false;

    public static final <T> T maybeFreeze(T t) {
        return t;
    }

    public static final boolean getStrictMemoryModel() {
        return strictMemoryModel;
    }
}
