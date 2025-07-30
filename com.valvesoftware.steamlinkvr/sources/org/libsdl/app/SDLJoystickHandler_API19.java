package org.libsdl.app;

import android.view.InputDevice;
import java.util.Iterator;
import java.util.List;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLJoystickHandler_API19 extends SDLJoystickHandler_API16 {
    SDLJoystickHandler_API19() {
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    int getProductId(InputDevice inputDevice) {
        return inputDevice.getProductId();
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    int getVendorId(InputDevice inputDevice) {
        return inputDevice.getVendorId();
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    int getAxisMask(List<InputDevice.MotionRange> list) {
        boolean z = false;
        int i = list.size() >= 2 ? 3 : 0;
        if (list.size() >= 4) {
            i |= 12;
        }
        if (list.size() >= 6) {
            i |= 48;
        }
        Iterator<InputDevice.MotionRange> it = list.iterator();
        boolean z2 = false;
        while (it.hasNext()) {
            int axis = it.next().getAxis();
            if (axis == 11) {
                z = true;
            } else if (axis > 11 && axis < 14) {
                z2 = true;
            }
        }
        return (z && z2) ? i | 32768 : i;
    }

    @Override // org.libsdl.app.SDLJoystickHandler_API16
    int getButtonMask(InputDevice inputDevice) {
        int[] iArr = {1, 2, 4, 8, 16, 64, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 16, 1, 32768, 65536, 131072, 262144, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, -1, -1, -1, -1};
        boolean[] hasKeys = inputDevice.hasKeys(96, 97, 99, 100, 4, 82, 110, 108, 106, 107, 102, 103, 19, 20, 21, 22, 109, 23, 104, 105, 98, 101, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203);
        int i = 0;
        for (int i2 = 0; i2 < 38; i2++) {
            if (hasKeys[i2]) {
                i |= iArr[i2];
            }
        }
        return i;
    }
}
