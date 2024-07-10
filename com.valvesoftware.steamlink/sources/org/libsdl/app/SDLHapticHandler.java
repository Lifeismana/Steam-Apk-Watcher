package org.libsdl.app;

import android.os.Vibrator;
import android.view.InputDevice;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
public class SDLHapticHandler {
    private final ArrayList<SDLHaptic> mHaptics = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: SDLControllerManager.java */
    /* loaded from: classes.dex */
    public static class SDLHaptic {
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
        int[] deviceIds = InputDevice.getDeviceIds();
        int length = deviceIds.length;
        while (true) {
            length--;
            if (length <= -1) {
                break;
            }
            if (getHaptic(deviceIds[length]) == null) {
                InputDevice device = InputDevice.getDevice(deviceIds[length]);
                Vibrator vibrator = device.getVibrator();
                if (vibrator.hasVibrator()) {
                    SDLHaptic sDLHaptic = new SDLHaptic();
                    sDLHaptic.device_id = deviceIds[length];
                    sDLHaptic.name = device.getName();
                    sDLHaptic.vib = vibrator;
                    this.mHaptics.add(sDLHaptic);
                    SDLControllerManager.nativeAddHaptic(sDLHaptic.device_id, sDLHaptic.name);
                }
            }
        }
        Vibrator vibrator2 = (Vibrator) SDL.getContext().getSystemService("vibrator");
        if (vibrator2 != null) {
            z = vibrator2.hasVibrator();
            if (z && getHaptic(999999) == null) {
                SDLHaptic sDLHaptic2 = new SDLHaptic();
                sDLHaptic2.device_id = 999999;
                sDLHaptic2.name = "VIBRATOR_SERVICE";
                sDLHaptic2.vib = vibrator2;
                this.mHaptics.add(sDLHaptic2);
                SDLControllerManager.nativeAddHaptic(sDLHaptic2.device_id, sDLHaptic2.name);
            }
        } else {
            z = false;
        }
        ArrayList arrayList = null;
        Iterator<SDLHaptic> it = this.mHaptics.iterator();
        while (it.hasNext()) {
            int i = it.next().device_id;
            int i2 = 0;
            while (i2 < deviceIds.length && i != deviceIds[i2]) {
                i2++;
            }
            if (i != 999999 || !z) {
                if (i2 == deviceIds.length) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(Integer.valueOf(i));
                }
            }
        }
        if (arrayList != null) {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                int intValue = ((Integer) it2.next()).intValue();
                SDLControllerManager.nativeRemoveHaptic(intValue);
                int i3 = 0;
                while (true) {
                    if (i3 >= this.mHaptics.size()) {
                        break;
                    }
                    if (this.mHaptics.get(i3).device_id == intValue) {
                        this.mHaptics.remove(i3);
                        break;
                    }
                    i3++;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SDLHaptic getHaptic(int i) {
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
