package org.libsdl.app;

import android.os.Vibrator;
import java.util.ArrayList;
import java.util.Iterator;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLHapticHandler {
    private final ArrayList<SDLHaptic> mHaptics = new ArrayList<>();

    public void rumble(int i, float f, float f2, int i2) {
    }

    /* compiled from: SDLControllerManager.java */
    static class SDLHaptic {
        public int device_id;
        public String name;
        public Vibrator vib;

        SDLHaptic() {
        }
    }

    public void run(int i, float f, int i2) {
        SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            haptic.vib.vibrate(i2);
        }
    }

    public void stop(int i) {
        SDLHaptic haptic = getHaptic(i);
        if (haptic != null) {
            haptic.vib.cancel();
        }
    }

    public void pollHapticDevices() {
        boolean z;
        Vibrator vibrator = (Vibrator) SDL.getContext().getSystemService("vibrator");
        if (vibrator != null) {
            z = vibrator.hasVibrator();
            if (z && getHaptic(999999) == null) {
                SDLHaptic sDLHaptic = new SDLHaptic();
                sDLHaptic.device_id = 999999;
                sDLHaptic.name = "VIBRATOR_SERVICE";
                sDLHaptic.vib = vibrator;
                this.mHaptics.add(sDLHaptic);
                SDLControllerManager.nativeAddHaptic(sDLHaptic.device_id, sDLHaptic.name);
            }
        } else {
            z = false;
        }
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        ArrayList arrayList = null;
        while (it.hasNext()) {
            int i = it.next().device_id;
            if (i != 999999 || !z) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Integer.valueOf(i));
            }
        }
        if (arrayList != null) {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                int intValue = ((Integer) it2.next()).intValue();
                SDLControllerManager.nativeRemoveHaptic(intValue);
                int i2 = 0;
                while (true) {
                    if (i2 >= this.mHaptics.size()) {
                        break;
                    }
                    if (this.mHaptics.get(i2).device_id == intValue) {
                        this.mHaptics.remove(i2);
                        break;
                    }
                    i2++;
                }
            }
        }
    }

    protected SDLHaptic getHaptic(int i) {
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
