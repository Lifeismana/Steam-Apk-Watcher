package eightbitlab.com.blurview;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/* loaded from: classes3.dex */
class NoOpController implements BlurController {
    @Override // eightbitlab.com.blurview.BlurController
    public void destroy() {
    }

    @Override // eightbitlab.com.blurview.BlurController
    public boolean draw(Canvas canvas) {
        return true;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurAutoUpdate(boolean z) {
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurEnabled(boolean z) {
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setBlurRadius(float f) {
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setFrameClearDrawable(Drawable drawable) {
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurViewFacade
    public BlurViewFacade setOverlayColor(int i) {
        return this;
    }

    @Override // eightbitlab.com.blurview.BlurController
    public void updateBlurViewSize() {
    }
}
