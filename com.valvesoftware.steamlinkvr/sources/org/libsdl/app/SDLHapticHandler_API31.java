package org.libsdl.app;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.view.InputDevice;
import org.libsdl.app.SDLHapticHandler;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler_API31 extends SDLHapticHandler {
    SDLHapticHandler_API31() {
    }

    @Override // org.libsdl.app.SDLHapticHandler
    public void run(int i, float f, int i2) {
        SDLHapticHandler.SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            vibrate(haptic.vib, f, i2);
        }
    }

    @Override // org.libsdl.app.SDLHapticHandler
    public void rumble(int i, float f, float f2, int i2) {
        InputDevice device = InputDevice.getDevice(i);
        if (device == null) {
            return;
        }
        VibratorManager vibratorManager = device.getVibratorManager();
        int[] vibratorIds = vibratorManager.getVibratorIds();
        if (vibratorIds.length >= 2) {
            vibrate(vibratorManager.getVibrator(vibratorIds[0]), f, i2);
            vibrate(vibratorManager.getVibrator(vibratorIds[1]), f2, i2);
        } else if (vibratorIds.length == 1) {
            vibrate(vibratorManager.getVibrator(vibratorIds[0]), (f * 0.6f) + (f2 * 0.4f), i2);
        }
    }

    private void vibrate(Vibrator vibrator, float f, int i) {
        if (f == 0.0f) {
            vibrator.cancel();
            return;
        }
        int round = Math.round(f * 255.0f);
        if (round > 255) {
            round = 255;
        }
        if (round < 1) {
            vibrator.cancel();
            return;
        }
        long j = i;
        try {
            vibrator.vibrate(VibrationEffect.createOneShot(j, round));
        } catch (Exception unused) {
            vibrator.vibrate(j);
        }
    }
}
