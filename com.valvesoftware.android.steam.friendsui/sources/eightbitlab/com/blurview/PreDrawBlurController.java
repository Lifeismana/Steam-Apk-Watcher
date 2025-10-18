package eightbitlab.com.blurview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import eightbitlab.com.blurview.SizeScaler;

/* loaded from: classes3.dex */
public final class PreDrawBlurController implements BlurController {
    public static final int TRANSPARENT = 0;
    private final BlurAlgorithm blurAlgorithm;
    final View blurView;
    private Drawable frameClearDrawable;
    private boolean initialized;
    private Bitmap internalBitmap;
    private BlurViewCanvas internalCanvas;
    private int overlayColor;
    private final ViewGroup rootView;
    private float blurRadius = 16.0f;
    private final int[] rootLocation = new int[2];
    private final int[] blurViewLocation = new int[2];
    private final ViewTreeObserver.OnPreDrawListener drawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: eightbitlab.com.blurview.PreDrawBlurController.1
        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            PreDrawBlurController.this.updateBlur();
            return true;
        }
    };
    private boolean blurEnabled = true;

    public PreDrawBlurController(View view, ViewGroup viewGroup, int i, BlurAlgorithm blurAlgorithm) {
        this.rootView = viewGroup;
        this.blurView = view;
        this.overlayColor = i;
        this.blurAlgorithm = blurAlgorithm;
        if (blurAlgorithm instanceof RenderEffectBlur) {
            ((RenderEffectBlur) blurAlgorithm).setContext(view.getContext());
        }
        init(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    void init(int i, int i2) {
        setBlurAutoUpdate(true);
        SizeScaler sizeScaler = new SizeScaler(this.blurAlgorithm.scaleFactor());
        if (sizeScaler.isZeroSized(i, i2)) {
            this.blurView.setWillNotDraw(true);
            return;
        }
        this.blurView.setWillNotDraw(false);
        SizeScaler.Size scale = sizeScaler.scale(i, i2);
        this.internalBitmap = Bitmap.createBitmap(scale.width, scale.height, this.blurAlgorithm.getSupportedBitmapConfig());
        this.internalCanvas = new BlurViewCanvas(this.internalBitmap);
        this.initialized = true;
        updateBlur();
    }

    void updateBlur() {
        if (this.blurEnabled && this.initialized) {
            Drawable drawable = this.frameClearDrawable;
            if (drawable == null) {
                this.internalBitmap.eraseColor(0);
            } else {
                drawable.draw(this.internalCanvas);
            }
            this.internalCanvas.save();
            setupInternalCanvasMatrix();
            this.rootView.draw(this.internalCanvas);
            this.internalCanvas.restore();
            blurAndSave();
        }
    }

    private void setupInternalCanvasMatrix() {
        this.rootView.getLocationOnScreen(this.rootLocation);
        this.blurView.getLocationOnScreen(this.blurViewLocation);
        int[] iArr = this.blurViewLocation;
        int i = iArr[0];
        int[] iArr2 = this.rootLocation;
        int i2 = i - iArr2[0];
        int i3 = iArr[1] - iArr2[1];
        float height = this.blurView.getHeight() / this.internalBitmap.getHeight();
        float width = this.blurView.getWidth() / this.internalBitmap.getWidth();
        this.internalCanvas.translate((-i2) / width, (-i3) / height);
        this.internalCanvas.scale(1.0f / width, 1.0f / height);
    }

    @Override // eightbitlab.com.blurview.BlurController
    public boolean draw(Canvas canvas) {
        if (this.blurEnabled && this.initialized) {
            if (canvas instanceof BlurViewCanvas) {
                return false;
            }
            float width = this.blurView.getWidth() / this.internalBitmap.getWidth();
            canvas.save();
            canvas.scale(width, this.blurView.getHeight() / this.internalBitmap.getHeight());
            this.blurAlgorithm.render(canvas, this.internalBitmap);
            canvas.restore();
            int i = this.overlayColor;
            if (i != 0) {
                canvas.drawColor(i);
            }
        }
        return true;
    }

    private void blurAndSave() {
        this.internalBitmap = this.blurAlgorithm.blur(this.internalBitmap, this.blurRadius);
        if (this.blurAlgorithm.canModifyBitmap()) {
            return;
        }
        this.internalCanvas.setBitmap(this.internalBitmap);
    }

    @Override // eightbitlab.com.blurview.BlurController
    public void updateBlurViewSize() {
        init(this.blurView.getMeasuredWidth(), this.blurView.getMeasuredHeight());
    }

    @Override // eightbitlab.com.blurview.BlurController
    public void destroy() {
        setBlurAutoUpdate(false);
        this.blurAlgorithm.destroy();
        this.initialized = false;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurRadius(float f) {
        this.blurRadius = f;
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setFrameClearDrawable(Drawable drawable) {
        this.frameClearDrawable = drawable;
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurEnabled(boolean z) {
        this.blurEnabled = z;
        setBlurAutoUpdate(z);
        this.blurView.invalidate();
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurAutoUpdate(boolean z) {
        this.rootView.getViewTreeObserver().removeOnPreDrawListener(this.drawListener);
        this.blurView.getViewTreeObserver().removeOnPreDrawListener(this.drawListener);
        if (z) {
            this.rootView.getViewTreeObserver().addOnPreDrawListener(this.drawListener);
            if (this.rootView.getWindowId() != this.blurView.getWindowId()) {
                this.blurView.getViewTreeObserver().addOnPreDrawListener(this.drawListener);
            }
        }
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setOverlayColor(int i) {
        if (this.overlayColor != i) {
            this.overlayColor = i;
            this.blurView.invalidate();
        }
        return this;
    }
}
