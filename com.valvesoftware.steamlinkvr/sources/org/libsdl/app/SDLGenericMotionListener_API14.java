package org.libsdl.app;

import android.view.MotionEvent;
import android.view.View;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLGenericMotionListener_API14 implements View.OnGenericMotionListener {
    public boolean inRelativeMode() {
        return false;
    }

    public void reclaimRelativeMouseModeIfNeeded() {
    }

    public boolean setRelativeMouseEnabled(boolean z) {
        return false;
    }

    public boolean supportsRelativeMouse() {
        return false;
    }

    SDLGenericMotionListener_API14() {
    }

    @Override // android.view.View.OnGenericMotionListener
    public boolean onGenericMotion(View view, MotionEvent motionEvent) {
        if (motionEvent.getSource() == 16777232) {
            return SDLControllerManager.handleJoystickMotionEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        int pointerCount = motionEvent.getPointerCount();
        boolean z = false;
        for (int i = 0; i < pointerCount; i++) {
            int toolType = motionEvent.getToolType(i);
            if (toolType == 3) {
                if (actionMasked == 7) {
                    SDLActivity.onNativeMouse(0, actionMasked, getEventX(motionEvent, i), getEventY(motionEvent, i), checkRelativeEvent(motionEvent));
                } else if (actionMasked == 8) {
                    SDLActivity.onNativeMouse(0, actionMasked, motionEvent.getAxisValue(10, i), motionEvent.getAxisValue(9, i), false);
                }
                z = true;
            } else {
                if ((toolType == 2 || toolType == 4) && (actionMasked == 7 || actionMasked == 9 || actionMasked == 10)) {
                    float x = motionEvent.getX(i);
                    float y = motionEvent.getY(i);
                    float pressure = motionEvent.getPressure(i);
                    SDLActivity.onNativePen(motionEvent.getPointerId(i), (1 << (toolType == 2 ? 0 : 30)) | (motionEvent.getButtonState() >> 4), actionMasked, x, y, pressure <= 1.0f ? pressure : 1.0f);
                    z = true;
                }
            }
        }
        return z;
    }

    public boolean checkRelativeEvent(MotionEvent motionEvent) {
        return inRelativeMode();
    }

    public float getEventX(MotionEvent motionEvent, int i) {
        return motionEvent.getX(i);
    }

    public float getEventY(MotionEvent motionEvent, int i) {
        return motionEvent.getY(i);
    }
}
