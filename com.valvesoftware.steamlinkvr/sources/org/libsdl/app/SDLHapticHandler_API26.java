package org.libsdl.app;

import android.os.VibrationEffect;
import org.libsdl.app.SDLHapticHandler;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    @Override // org.libsdl.app.SDLHapticHandler
    public void run(int i, float f, int i2) {
        SDLHapticHandler.SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            if (f == 0.0f) {
                stop(i);
                return;
            }
            int round = Math.round(f * 255.0f);
            if (round > 255) {
                round = 255;
            }
            if (round < 1) {
                stop(i);
                return;
            }
            try {
                haptic.vib.vibrate(VibrationEffect.createOneShot(i2, round));
            } catch (Exception unused) {
                haptic.vib.vibrate(i2);
            }
        }
    }
}
