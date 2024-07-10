package eightbitlab.com.blurview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import kotlin.io.path.PathTreeWalk$$ExternalSyntheticApiModelOutline0;

/* loaded from: classes4.dex */
public class RenderEffectBlur implements BlurAlgorithm {
    private Context context;
    public BlurAlgorithm fallbackAlgorithm;
    private int height;
    private int width;
    private final RenderNode node = PathTreeWalk$$ExternalSyntheticApiModelOutline0.m1558m("BlurViewNode");
    private float lastBlurRadius = 1.0f;

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public boolean canModifyBitmap() {
        return true;
    }

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public float scaleFactor() {
        return 6.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setContext(Context context) {
        this.context = context;
    }

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public Bitmap blur(Bitmap bitmap, float f) {
        RecordingCanvas beginRecording;
        RenderEffect createBlurEffect;
        this.lastBlurRadius = f;
        if (bitmap.getHeight() != this.height || bitmap.getWidth() != this.width) {
            this.height = bitmap.getHeight();
            int width = bitmap.getWidth();
            this.width = width;
            this.node.setPosition(0, 0, width, this.height);
        }
        beginRecording = this.node.beginRecording();
        beginRecording.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
        this.node.endRecording();
        RenderNode renderNode = this.node;
        createBlurEffect = RenderEffect.createBlurEffect(f, f, Shader.TileMode.MIRROR);
        renderNode.setRenderEffect(createBlurEffect);
        return bitmap;
    }

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public void destroy() {
        this.node.discardDisplayList();
        BlurAlgorithm blurAlgorithm = this.fallbackAlgorithm;
        if (blurAlgorithm != null) {
            blurAlgorithm.destroy();
        }
    }

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public Bitmap.Config getSupportedBitmapConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    @Override // eightbitlab.com.blurview.BlurAlgorithm
    public void render(Canvas canvas, Bitmap bitmap) {
        if (canvas.isHardwareAccelerated()) {
            canvas.drawRenderNode(this.node);
            return;
        }
        if (this.fallbackAlgorithm == null) {
            this.fallbackAlgorithm = new RenderScriptBlur(this.context);
        }
        this.fallbackAlgorithm.blur(bitmap, this.lastBlurRadius);
        this.fallbackAlgorithm.render(canvas, bitmap);
    }
}
