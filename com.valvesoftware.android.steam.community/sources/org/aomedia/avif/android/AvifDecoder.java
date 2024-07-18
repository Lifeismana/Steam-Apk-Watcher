package org.aomedia.avif.android;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

/* loaded from: classes3.dex */
public class AvifDecoder {

    /* loaded from: classes3.dex */
    public static class Info {
        public int depth;
        public int height;
        public int width;
    }

    public static native boolean decode(ByteBuffer encoded, int length, Bitmap bitmap);

    public static native boolean getInfo(ByteBuffer encoded, int length, Info info);

    private static native boolean isAvifImage(ByteBuffer encoded, int length);

    static {
        try {
            System.loadLibrary("avif_android");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private AvifDecoder() {
    }

    public static boolean isAvifImage(ByteBuffer buffer) {
        return isAvifImage(buffer, buffer.remaining());
    }
}
