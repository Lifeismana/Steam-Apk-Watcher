package org.libsdl.app;

import android.os.Build;
import android.os.VibrationEffect;
import org.libsdl.app.SDLHapticHandler;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    @Override // org.libsdl.app.SDLHapticHandler
    void run(int i, float f, int i2) {
        SDLHapticHandler.SDLHaptic haptic;
        if (Build.VERSION.SDK_INT >= 26 && (haptic = getHaptic(i)) != null) {
            if (f == 0.0f) {
                stop(i);
                return;
            }
            int iRound = Math.round(f * 255.0f);
            if (iRound > 255) {
                iRound = 255;
            }
            if (iRound < 1) {
                stop(i);
                return;
            }
            try {
                haptic.vib.vibrate(VibrationEffect.createOneShot(i2, iRound));
            } catch (Exception unused) {
                haptic.vib.vibrate(i2);
            }
        }
    }
}
