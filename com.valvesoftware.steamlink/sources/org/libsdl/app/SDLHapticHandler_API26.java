package org.libsdl.app;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import org.libsdl.app.SDLHapticHandler;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
public class SDLHapticHandler_API26 extends SDLHapticHandler {
    @Override // org.libsdl.app.SDLHapticHandler
    public void run(int i, float f, int i2) {
        VibrationEffect createOneShot;
        SDLHapticHandler.SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            Log.d("SDL", "Rtest: Vibe with intensity " + f + " for " + i2);
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
