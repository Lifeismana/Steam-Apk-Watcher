package eightbitlab.com.blurview;

/* loaded from: classes3.dex */
public class SizeScaler {
    private static final int ROUNDING_VALUE = 64;
    private final float scaleFactor;

    public SizeScaler(float f) {
        this.scaleFactor = f;
    }

    Size scale(int i, int i2) {
        float f = i;
        int iRoundSize = roundSize(downscaleSize(f));
        return new Size(iRoundSize, (int) Math.ceil(i2 / r4), f / iRoundSize);
    }

    boolean isZeroSized(int i, int i2) {
        return downscaleSize((float) i2) == 0 || downscaleSize((float) i) == 0;
    }

    private int roundSize(int i) {
        int i2 = i % 64;
        return i2 == 0 ? i : (i - i2) + 64;
    }

    private int downscaleSize(float f) {
        return (int) Math.ceil(f / this.scaleFactor);
    }

    static class Size {
        final int height;
        final float scaleFactor;
        final int width;

        Size(int i, int i2, float f) {
            this.width = i;
            this.height = i2;
            this.scaleFactor = f;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Size size = (Size) obj;
            return this.width == size.width && this.height == size.height && Float.compare(size.scaleFactor, this.scaleFactor) == 0;
        }

        public int hashCode() {
            int i = ((this.width * 31) + this.height) * 31;
            float f = this.scaleFactor;
            return i + (f != 0.0f ? Float.floatToIntBits(f) : 0);
        }

        public String toString() {
            return "Size{width=" + this.width + ", height=" + this.height + ", scaleFactor=" + this.scaleFactor + '}';
        }
    }
}
