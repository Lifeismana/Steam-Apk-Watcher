package org.libsdl.app;

import android.os.VibrationEffect;
import android.os.Vibrator;
import org.libsdl.app.SDLHapticHandler;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler_API26 extends SDLHapticHandler {
    SDLHapticHandler_API26() {
    }

    @Override // org.libsdl.app.SDLHapticHandler
    public void run(int i, float f, int i2) {
        VibrationEffect createOneShot;
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
                Vibrator vibrator = haptic.vib;
                createOneShot = VibrationEffect.createOneShot(i2, round);
                vibrator.vibrate(createOneShot);
            } catch (Exception unused) {
                haptic.vib.vibrate(i2);
            }
        }
    }
}
