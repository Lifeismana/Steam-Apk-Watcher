package org.libsdl.app;

import android.view.InputDevice;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLGenericMotionListener_API29 extends SDLGenericMotionListener_API26 {
    SDLGenericMotionListener_API29() {
    }

    @Override // org.libsdl.app.SDLGenericMotionListener_API14
    int getPenDeviceType(InputDevice inputDevice) {
        if (inputDevice == null) {
            return 0;
        }
        return inputDevice.isExternal() ? 2 : 1;
    }
}
