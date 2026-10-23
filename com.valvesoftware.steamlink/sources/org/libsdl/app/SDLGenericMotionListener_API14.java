package org.libsdl.app;

import android.os.Build;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import org.qtproject.qt5.android.EditContextView;

/* JADX INFO: compiled from: SDLControllerManager.java */
/* JADX INFO: loaded from: classes.dex */
class SDLGenericMotionListener_API14 implements View.OnGenericMotionListener, View.OnCapturedPointerListener {
    protected static final int SDL_PEN_DEVICE_TYPE_DIRECT = 1;
    protected static final int SDL_PEN_DEVICE_TYPE_INDIRECT = 2;
    protected static final int SDL_PEN_DEVICE_TYPE_UNKNOWN = 0;

    int getPenDeviceType(InputDevice inputDevice) {
        return 0;
    }

    boolean inRelativeMode() {
        return false;
    }

    @Override // android.view.View.OnCapturedPointerListener
    public boolean onCapturedPointer(View view, MotionEvent motionEvent) {
        return false;
    }

    void reclaimRelativeMouseModeIfNeeded() {
    }

    boolean setRelativeMouseEnabled(boolean z) {
        return false;
    }

    boolean supportsRelativeMouse() {
        return false;
    }

    SDLGenericMotionListener_API14() {
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0052  */
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
                int buttonState = motionEvent.getButtonState();
                boolean zCheckRelativeEvent = checkRelativeEvent(motionEvent);
                if (Build.VERSION.SDK_INT >= 26 && zCheckRelativeEvent) {
                    return false;
                }
                if (actionMasked == 2 || actionMasked == 3) {
                    SDLActivity.onNativeMouse(buttonState, actionMasked, getEventX(motionEvent, i), getEventY(motionEvent, i), zCheckRelativeEvent);
                    z = true;
                } else {
                    switch (actionMasked) {
                        case 7:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                            SDLActivity.onNativeMouse(buttonState, actionMasked, getEventX(motionEvent, i), getEventY(motionEvent, i), zCheckRelativeEvent);
                            break;
                        case EditContextView.SALL_BUTTON /* 8 */:
                            SDLActivity.onNativeMouse(buttonState, actionMasked, motionEvent.getAxisValue(10, i), motionEvent.getAxisValue(9, i), false);
                            break;
                        default:
                            continue;
                    }
                    z = true;
                }
            } else if ((toolType == 2 || toolType == 4) && (actionMasked == 7 || actionMasked == 9 || actionMasked == 10)) {
                float x = motionEvent.getX(i);
                float y = motionEvent.getY(i);
                float pressure = motionEvent.getPressure(i);
                if (pressure > 1.0f) {
                    pressure = 1.0f;
                }
                int buttonState2 = (1 << (toolType == 2 ? 0 : 30)) | (motionEvent.getButtonState() >> 4);
                if ((4 & motionEvent.getButtonState()) != 0) {
                    buttonState2 |= 8;
                }
                SDLActivity.onNativePen(motionEvent.getPointerId(i), getPenDeviceType(motionEvent.getDevice()), buttonState2, actionMasked, x, y, pressure);
                z = true;
            }
        }
        return z;
    }

    boolean checkRelativeEvent(MotionEvent motionEvent) {
        return inRelativeMode();
    }

    float getEventX(MotionEvent motionEvent, int i) {
        return motionEvent.getX(i);
    }

    float getEventY(MotionEvent motionEvent, int i) {
        return motionEvent.getY(i);
    }
}
