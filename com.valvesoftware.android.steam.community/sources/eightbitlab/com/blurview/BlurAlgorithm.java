package eightbitlab.com.blurview;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/* loaded from: classes2.dex */
public interface BlurAlgorithm {
    Bitmap blur(Bitmap bitmap, float f);

    boolean canModifyBitmap();

    void destroy();

    Bitmap.Config getSupportedBitmapConfig();

    void render(Canvas canvas, Bitmap bitmap);

    float scaleFactor();
}
