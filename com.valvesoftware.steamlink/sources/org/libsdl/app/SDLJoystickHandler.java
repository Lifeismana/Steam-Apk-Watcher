package org.libsdl.app;

import android.graphics.Color;
import android.hardware.lights.Light;
import android.hardware.lights.LightState;
import android.hardware.lights.LightsManager;
import android.hardware.lights.LightsRequest;
import android.os.Build;
import android.view.InputDevice;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* compiled from: SDLControllerManager.java */
/* loaded from: classes.dex */
class SDLJoystickHandler {
    private final ArrayList<SDLJoystick> mJoysticks = new ArrayList<>();

    /* compiled from: SDLControllerManager.java */
    static class SDLJoystick {
        ArrayList<InputDevice.MotionRange> axes;
        String desc;
        int device_id;
        ArrayList<InputDevice.MotionRange> hats;
        ArrayList<Light> lights;
        LightsManager.LightsSession lightsSession;
        String name;

        SDLJoystick() {
        }
    }

    /* compiled from: SDLControllerManager.java */
    static class RangeComparator implements Comparator<InputDevice.MotionRange> {
        RangeComparator() {
        }

        @Override // java.util.Comparator
        public int compare(InputDevice.MotionRange motionRange, InputDevice.MotionRange motionRange2) {
            int axis = motionRange.getAxis();
            int axis2 = motionRange2.getAxis();
            if (axis == 22) {
                axis = 23;
            } else if (axis == 23) {
                axis = 22;
            }
            if (axis2 == 22) {
                axis2 = 23;
            } else if (axis2 == 23) {
                axis2 = 22;
            }
            if (axis == 11) {
                axis = 13;
            } else if (axis > 11 && axis < 14) {
                axis--;
            }
            if (axis2 == 11) {
                axis2 = 13;
            } else if (axis2 > 11 && axis2 < 14) {
                axis2--;
            }
            return axis - axis2;
        }
    }

    SDLJoystickHandler() {
    }

    void pollInputDevices() {
        boolean z;
        boolean z2;
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int i : deviceIds) {
            if (SDLControllerManager.isDeviceSDLJoystick(i) && getJoystick(i) == null) {
                InputDevice device = InputDevice.getDevice(i);
                SDLJoystick sDLJoystick = new SDLJoystick();
                sDLJoystick.device_id = i;
                sDLJoystick.name = device.getName();
                sDLJoystick.desc = getJoystickDescriptor(device);
                sDLJoystick.axes = new ArrayList<>();
                sDLJoystick.hats = new ArrayList<>();
                sDLJoystick.lights = new ArrayList<>();
                List<InputDevice.MotionRange> motionRanges = device.getMotionRanges();
                Collections.sort(motionRanges, new RangeComparator());
                for (InputDevice.MotionRange motionRange : motionRanges) {
                    if ((motionRange.getSource() & 16) != 0) {
                        if (motionRange.getAxis() == 15 || motionRange.getAxis() == 16) {
                            sDLJoystick.hats.add(motionRange);
                        } else {
                            sDLJoystick.axes.add(motionRange);
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= 31) {
                    boolean z3 = device.getVibratorManager().getVibratorIds().length > 0;
                    LightsManager lightsManager = device.getLightsManager();
                    Iterator it = lightsManager.getLights().iterator();
                    while (it.hasNext()) {
                        Light lightM10m = SDLSurface$$ExternalSyntheticApiModelOutline0.m10m(it.next());
                        if (lightM10m.hasRgbControl()) {
                            sDLJoystick.lights.add(lightM10m);
                        }
                    }
                    if (sDLJoystick.lights.isEmpty()) {
                        z = z3;
                    } else {
                        sDLJoystick.lightsSession = lightsManager.openSession();
                        z = z3;
                        z2 = true;
                        this.mJoysticks.add(sDLJoystick);
                        SDLControllerManager.nativeAddJoystick(sDLJoystick.device_id, sDLJoystick.name, sDLJoystick.desc, getVendorId(device), getProductId(device), getButtonMask(device), sDLJoystick.axes.size(), getAxisMask(sDLJoystick.axes), sDLJoystick.hats.size() / 2, z, z2);
                    }
                } else {
                    z = false;
                }
                z2 = false;
                this.mJoysticks.add(sDLJoystick);
                SDLControllerManager.nativeAddJoystick(sDLJoystick.device_id, sDLJoystick.name, sDLJoystick.desc, getVendorId(device), getProductId(device), getButtonMask(device), sDLJoystick.axes.size(), getAxisMask(sDLJoystick.axes), sDLJoystick.hats.size() / 2, z, z2);
            }
        }
        Iterator<SDLJoystick> it2 = this.mJoysticks.iterator();
        ArrayList arrayList = null;
        while (it2.hasNext()) {
            int i2 = it2.next().device_id;
            int i3 = 0;
            while (i3 < deviceIds.length && i2 != deviceIds[i3]) {
                i3++;
            }
            if (i3 == deviceIds.length) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(Integer.valueOf(i2));
            }
        }
        if (arrayList != null) {
            Iterator it3 = arrayList.iterator();
            while (it3.hasNext()) {
                int iIntValue = ((Integer) it3.next()).intValue();
                SDLControllerManager.nativeRemoveJoystick(iIntValue);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.mJoysticks.size()) {
                        break;
                    }
                    if (this.mJoysticks.get(i4).device_id == iIntValue) {
                        if (Build.VERSION.SDK_INT >= 31 && this.mJoysticks.get(i4).lightsSession != null) {
                            try {
                                this.mJoysticks.get(i4).lightsSession.close();
                            } catch (Exception unused) {
                            }
                            this.mJoysticks.get(i4).lightsSession = null;
                        }
                        this.mJoysticks.remove(i4);
                    } else {
                        i4++;
                    }
                }
            }
        }
    }

    protected SDLJoystick getJoystick(int i) {
        Iterator<SDLJoystick> it = this.mJoysticks.iterator();
        while (it.hasNext()) {
            SDLJoystick next = it.next();
            if (next.device_id == i) {
                return next;
            }
        }
        return null;
    }

    boolean handleMotionEvent(MotionEvent motionEvent) {
        SDLJoystick joystick;
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getActionMasked() == 2 && (joystick = getJoystick(motionEvent.getDeviceId())) != null) {
            for (int i = 0; i < joystick.axes.size(); i++) {
                InputDevice.MotionRange motionRange = joystick.axes.get(i);
                SDLControllerManager.onNativeJoy(joystick.device_id, i, (((motionEvent.getAxisValue(motionRange.getAxis(), actionIndex) - motionRange.getMin()) / motionRange.getRange()) * 2.0f) - 1.0f);
            }
            for (int i2 = 0; i2 < joystick.hats.size() / 2; i2++) {
                int i3 = i2 * 2;
                SDLControllerManager.onNativeHat(joystick.device_id, i2, Math.round(motionEvent.getAxisValue(joystick.hats.get(i3).getAxis(), actionIndex)), Math.round(motionEvent.getAxisValue(joystick.hats.get(i3 + 1).getAxis(), actionIndex)));
            }
        }
        return true;
    }

    String getJoystickDescriptor(InputDevice inputDevice) {
        String descriptor = inputDevice.getDescriptor();
        return (descriptor == null || descriptor.isEmpty()) ? inputDevice.getName() : descriptor;
    }

    int getProductId(InputDevice inputDevice) {
        return inputDevice.getProductId();
    }

    int getVendorId(InputDevice inputDevice) {
        return inputDevice.getVendorId();
    }

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
        return (z && z2) ? 32768 | i : i;
    }

    int getButtonMask(InputDevice inputDevice) {
        int[] iArr = {1, 2, 4, 8, 16, 64, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 16, 1, 32768, 65536, 131072, 262144, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, -1, -1, -1, -1};
        boolean[] zArrHasKeys = inputDevice.hasKeys(96, 97, 99, 100, 4, 82, 110, 108, 106, 107, 102, 103, 19, 20, 21, 22, 109, 23, 104, 105, 98, 101, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203);
        int i = 0;
        for (int i2 = 0; i2 < 38; i2++) {
            if (zArrHasKeys[i2]) {
                i |= iArr[i2];
            }
        }
        return i;
    }

    void setLED(int i, int i2, int i3, int i4) {
        SDLJoystick joystick;
        if (Build.VERSION.SDK_INT < 31 || (joystick = getJoystick(i)) == null || joystick.lights.isEmpty()) {
            return;
        }
        LightsRequest.Builder builderM16m = SDLSurface$$ExternalSyntheticApiModelOutline0.m16m();
        LightState lightStateBuild = SDLSurface$$ExternalSyntheticApiModelOutline0.m11m().setColor(Color.rgb(i2, i3, i4)).build();
        Iterator<Light> it = joystick.lights.iterator();
        while (it.hasNext()) {
            Light lightM10m = SDLSurface$$ExternalSyntheticApiModelOutline0.m10m((Object) it.next());
            if (lightM10m.hasRgbControl()) {
                builderM16m.addLight(lightM10m, lightStateBuild);
            }
        }
        joystick.lightsSession.requestLights(builderM16m.build());
    }
}
