package org.libsdl.app;

import android.os.Vibrator;
import java.util.ArrayList;
import java.util.Iterator;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler {
    private final ArrayList<SDLHaptic> mHaptics = new ArrayList<>();

    void rumble(int i, float f, float f2, int i2) {
    }

    /* compiled from: SDLControllerManager.java */
    static class SDLHaptic {
        int device_id;
        String name;
        Vibrator vib;

        SDLHaptic() {
        }
    }

    SDLHapticHandler() {
    }

    void run(int i, float f, int i2) {
        SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            haptic.vib.vibrate(i2);
        }
    }

    void stop(int i) {
        SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            haptic.vib.cancel();
        }
    }

    synchronized void pollHapticDevices() {
        boolean zHasVibrator;
        Vibrator vibrator = (Vibrator) SDL.getContext().getSystemService("vibrator");
        if (vibrator != null) {
            zHasVibrator = vibrator.hasVibrator();
            if (zHasVibrator && getHaptic(999999) == null) {
                SDLHaptic sDLHaptic = new SDLHaptic();
                sDLHaptic.device_id = 999999;
                sDLHaptic.name = "VIBRATOR_SERVICE";
                sDLHaptic.vib = vibrator;
                this.mHaptics.add(sDLHaptic);
                SDLControllerManager.nativeAddHaptic(sDLHaptic.device_id, sDLHaptic.name);
            }
        } else {
            zHasVibrator = false;
        }
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        ArrayList arrayList = null;
        while (it.hasNext()) {
            int i = it.next().device_id;
            if (i != 999999 || !zHasVibrator) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Integer.valueOf(i));
            }
        }
        if (arrayList != null) {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                int iIntValue = ((Integer) it2.next()).intValue();
                SDLControllerManager.nativeRemoveHaptic(iIntValue);
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mHaptics.size()) {
                        break;
                    }
                    if (this.mHaptics.get(i2).device_id == iIntValue) {
                        this.mHaptics.remove(i2);
                        break;
                    }
                    i2++;
                }
            }
        }
    }

    protected synchronized SDLHaptic getHaptic(int i) {
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        while (it.hasNext()) {
            SDLHaptic next = it.next();
            if (next.device_id == i) {
                return next;
            }
        }
        return null;
    }
}
