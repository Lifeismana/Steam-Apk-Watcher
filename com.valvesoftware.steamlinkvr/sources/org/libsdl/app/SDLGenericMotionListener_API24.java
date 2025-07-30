package org.libsdl.app;

import android.view.MotionEvent;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLGenericMotionListener_API24 extends SDLGenericMotionListener_API14 {
    private boolean mRelativeModeEnabled;

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public boolean supportsRelativeMouse() {
        return true;
    }

    SDLGenericMotionListener_API24() {
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public boolean inRelativeMode() {
        return this.mRelativeModeEnabled;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public boolean setRelativeMouseEnabled(boolean z) {
        this.mRelativeModeEnabled = z;
        return true;
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public float getEventX(MotionEvent motionEvent, int i) {
        if (this.mRelativeModeEnabled && motionEvent.getToolType(i) == 3) {
            return motionEvent.getAxisValue(27, i);
        }
        return motionEvent.getX(i);
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    public float getEventY(MotionEvent motionEvent, int i) {
        if (this.mRelativeModeEnabled && motionEvent.getToolType(i) == 3) {
            return motionEvent.getAxisValue(28, i);
        }
        return motionEvent.getY(i);
    }
}
