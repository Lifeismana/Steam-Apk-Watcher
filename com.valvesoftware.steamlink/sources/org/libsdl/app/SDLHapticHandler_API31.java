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
        VibratorManager vibratorManager;
        int[] vibratorIds;
        Vibrator vibrator;
        Vibrator vibrator2;
        Vibrator vibrator3;
        InputDevice device = InputDevice.getDevice(i);
        if (device == null) {
            return;
        }
        vibratorManager = device.getVibratorManager();
        vibratorIds = vibratorManager.getVibratorIds();
        if (vibratorIds.length >= 2) {
            vibrator2 = vibratorManager.getVibrator(vibratorIds[0]);
            vibrate(vibrator2, f, i2);
            vibrator3 = vibratorManager.getVibrator(vibratorIds[1]);
            vibrate(vibrator3, f2, i2);
            return;
        }
        if (vibratorIds.length == 1) {
            vibrator = vibratorManager.getVibrator(vibratorIds[0]);
            vibrate(vibrator, (f * 0.6f) + (f2 * 0.4f), i2);
        }
    }

    private void vibrate(Vibrator vibrator, float f, int i) {
        VibrationEffect createOneShot;
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
            createOneShot = VibrationEffect.createOneShot(j, round);
            vibrator.vibrate(createOneShot);
        } catch (Exception unused) {
            vibrator.vibrate(j);
        }
    }
}
