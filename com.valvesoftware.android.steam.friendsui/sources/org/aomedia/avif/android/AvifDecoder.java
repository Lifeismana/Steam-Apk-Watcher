package org.aomedia.avif.android;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

/* loaded from: classes3.dex */
public class AvifDecoder {
    private boolean alphaPresent;
    private long decoder;
    private int depth;
    private int frameCount;
    private double[] frameDurations;
    private int height;
    private int repetitionCount;
    private int width;

    public static class Info {
        public boolean alphaPresent;
        public int depth;
        public int height;
        public int width;
    }

    private native long createDecoder(ByteBuffer encoded, int length, int threads);

    public static native boolean decode(ByteBuffer encoded, int length, Bitmap bitmap, int threads);

    private native void destroyDecoder(long decoder);

    public static native boolean getInfo(ByteBuffer encoded, int length, Info info);

    private static native boolean isAvifImage(ByteBuffer encoded, int length);

    private native int nextFrame(long decoder, Bitmap bitmap);

    private native int nextFrameIndex(long decoder);

    private native int nthFrame(long decoder, int n, Bitmap bitmap);

    public static native String resultToString(int result);

    public static native String versionString();

    static {
        try {
            System.loadLibrary("avif_android");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private AvifDecoder(ByteBuffer encoded, int threads) {
        this.decoder = createDecoder(encoded, encoded.remaining(), threads);
    }

    public static boolean isAvifImage(ByteBuffer buffer) {
        return isAvifImage(buffer, buffer.remaining());
    }

    public static boolean decode(ByteBuffer encoded, int length, Bitmap bitmap) {
        return decode(encoded, length, bitmap, 0);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public boolean getAlphaPresent() {
        return this.alphaPresent;
    }

    public int getFrameCount() {
        return this.frameCount;
    }

    public int getRepetitionCount() {
        return this.repetitionCount;
    }

    public double[] getFrameDurations() {
        return this.frameDurations;
    }

    public void release() {
        long j = this.decoder;
        if (j != 0) {
            destroyDecoder(j);
        }
        this.decoder = 0L;
    }

    public static AvifDecoder create(ByteBuffer encoded) {
        return create(encoded, 1);
    }

    public static AvifDecoder create(ByteBuffer encoded, int threads) {
        AvifDecoder avifDecoder = new AvifDecoder(encoded, threads);
        if (avifDecoder.decoder == 0) {
            return null;
        }
        return avifDecoder;
    }

    public int nextFrame(Bitmap bitmap) {
        return nextFrame(this.decoder, bitmap);
    }

    public int nextFrameIndex() {
        return nextFrameIndex(this.decoder);
    }

    public int nthFrame(int n, Bitmap bitmap) {
        return nthFrame(this.decoder, n, bitmap);
    }
}
