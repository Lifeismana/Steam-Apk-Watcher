package org.libsdl.app;

import android.view.MotionEvent;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLGenericMotionListener_API26 extends SDLGenericMotionListener_API24 {
    private boolean mRelativeModeEnabled;

    SDLGenericMotionListener_API26() {
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API14
    public boolean supportsRelativeMouse() {
        SDLActivity.isDeXMode();
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API14
    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API14
    public boolean setRelativeMouseEnabled(boolean z) {
        SDLActivity.isDeXMode();
        if (z) {
            SDLActivity.getContentView().requestPointerCapture();
        } else {
            SDLActivity.getContentView().releasePointerCapture();
        }
        this.mRelativeModeEnabled = z;
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public void reclaimRelativeMouseModeIfNeeded() {
        if (!this.mRelativeModeEnabled || SDLActivity.isDeXMode()) {
            return;
        }
        SDLActivity.getContentView().requestPointerCapture();
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public boolean checkRelativeEvent(MotionEvent motionEvent) {
        return motionEvent.getSource() == 131076;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API14
    public float getEventX(MotionEvent motionEvent, int i) {
        return motionEvent.getX(i);
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API24, org.libsdl.app.SDLGenericMotionListener_API14
    public float getEventY(MotionEvent motionEvent, int i) {
        return motionEvent.getY(i);
    }
}
