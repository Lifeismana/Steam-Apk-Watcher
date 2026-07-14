package org.libsdl.app;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: compiled from: SDLControllerManager.java */
/* JADX INFO: loaded from: classes.dex */
class SDLJoystickHandler {
    private final ArrayList<SDLJoystick> mJoysticks = new ArrayList<>();

    /* JADX INFO: compiled from: SDLControllerManager.java */
    static class SDLJoystick {
        Sensor accelerometerSensor;
        ArrayList<InputDevice.MotionRange> axes;
        String desc;
        int device_id;
        Sensor gyroscopeSensor;
        ArrayList<InputDevice.MotionRange> hats;
        ArrayList<Light> lights;
        LightsManager.LightsSession lightsSession;
        String name;
        SDLJoySensorListener sensorListener;
        SensorManager sensorManager;

        SDLJoystick() {
        }
    }

    /* JADX INFO: compiled from: SDLControllerManager.java */
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

    synchronized void detectDevices() {
        for (int i : InputDevice.getDeviceIds()) {
            if (SDLControllerManager.isDeviceSDLJoystick(i)) {
                deviceAdded(i);
            }
        }
    }

    void deviceAdded(int i) {
        boolean z;
        boolean z2;
        boolean z3;
        InputDevice device = InputDevice.getDevice(i);
        if (device == null) {
            return;
        }
        SDLJoystick joystick = getJoystick(i);
        if (joystick == null) {
            joystick = new SDLJoystick();
            joystick.device_id = i;
            joystick.name = device.getName();
            joystick.desc = getJoystickDescriptor(device);
            joystick.axes = new ArrayList<>();
            joystick.hats = new ArrayList<>();
            HashSet hashSet = new HashSet();
            joystick.lights = new ArrayList<>();
            List<InputDevice.MotionRange> motionRanges = device.getMotionRanges();
            Collections.sort(motionRanges, new RangeComparator());
            for (InputDevice.MotionRange motionRange : motionRanges) {
                if ((motionRange.getSource() & 16) != 0 && hashSet.add(Integer.valueOf(motionRange.getAxis()))) {
                    if (motionRange.getAxis() == 15 || motionRange.getAxis() == 16) {
                        joystick.hats.add(motionRange);
                    } else {
                        joystick.axes.add(motionRange);
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= 31) {
                LightsManager lightsManager = device.getLightsManager();
                Iterator it = lightsManager.getLights().iterator();
                while (it.hasNext()) {
                    Light lightM11m = SDLSurface$$ExternalSyntheticApiModelOutline0.m11m(it.next());
                    if (lightM11m.hasRgbControl()) {
                        joystick.lights.add(lightM11m);
                    }
                }
                if (!joystick.lights.isEmpty()) {
                    joystick.lightsSession = lightsManager.openSession();
                }
                SensorManager sensorManager = device.getSensorManager();
                if (sensorManager != null) {
                    joystick.sensorManager = sensorManager;
                    joystick.sensorListener = new SDLJoySensorListener(joystick.device_id);
                    joystick.accelerometerSensor = sensorManager.getDefaultSensor(1);
                    joystick.gyroscopeSensor = sensorManager.getDefaultSensor(4);
                }
            }
            this.mJoysticks.add(joystick);
        }
        if (Build.VERSION.SDK_INT >= 31) {
            z = device.getVibratorManager().getVibratorIds().length > 0;
            z2 = !joystick.lights.isEmpty();
            z3 = joystick.accelerometerSensor != null;
            boolean z4 = joystick.gyroscopeSensor != null;
            SDLControllerManager.nativeAddJoystick(joystick.device_id, joystick.name, joystick.desc, getVendorId(device), getProductId(device), getButtonMask(device), joystick.axes.size(), getAxisMask(joystick.axes), joystick.hats.size() / 2, z, z2, z3, z4);
        }
        z = false;
        z2 = false;
        z3 = false;
        SDLControllerManager.nativeAddJoystick(joystick.device_id, joystick.name, joystick.desc, getVendorId(device), getProductId(device), getButtonMask(device), joystick.axes.size(), getAxisMask(joystick.axes), joystick.hats.size() / 2, z, z2, z3, z4);
    }

    void deviceRemoved(int i) {
        for (int i2 = 0; i2 < this.mJoysticks.size(); i2++) {
            if (this.mJoysticks.get(i2).device_id == i) {
                SDLControllerManager.nativeRemoveJoystick(i);
                if (Build.VERSION.SDK_INT >= 31 && this.mJoysticks.get(i2).lightsSession != null) {
                    try {
                        this.mJoysticks.get(i2).lightsSession.close();
                    } catch (Exception unused) {
                    }
                    this.mJoysticks.get(i2).lightsSession = null;
                }
                this.mJoysticks.remove(i2);
                return;
            }
        }
    }

    protected synchronized SDLJoystick getJoystick(int i) {
        for (SDLJoystick sDLJoystick : this.mJoysticks) {
            if (sDLJoystick.device_id == i) {
                return sDLJoystick;
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
        int[] iArr = {1, 2, 4, 8, 16, 64, 32, 64, 128, 256, SDL.SDL_INIT_JOYSTICK, 1024, 2048, SDL.SDL_INIT_HAPTIC, SDL.SDL_INIT_GAMEPAD, 16384, 16, 1, SDL.SDL_INIT_SENSOR, SDL.SDL_INIT_CAMERA, 131072, 262144, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, -1, -1, -1, -1};
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
        LightsRequest.Builder builderM17m = SDLSurface$$ExternalSyntheticApiModelOutline0.m17m();
        LightState lightStateBuild = SDLSurface$$ExternalSyntheticApiModelOutline0.m12m().setColor(Color.rgb(i2, i3, i4)).build();
        Iterator<Light> it = joystick.lights.iterator();
        while (it.hasNext()) {
            Light lightM11m = SDLSurface$$ExternalSyntheticApiModelOutline0.m11m((Object) it.next());
            if (lightM11m.hasRgbControl()) {
                builderM17m.addLight(lightM11m, lightStateBuild);
            }
        }
        joystick.lightsSession.requestLights(builderM17m.build());
    }

    void setSensorsEnabled(int i, boolean z) {
        SDLJoystick joystick;
        if (Build.VERSION.SDK_INT < 31 || (joystick = getJoystick(i)) == null || joystick.sensorManager == null) {
            return;
        }
        if (z) {
            if (joystick.accelerometerSensor != null) {
                SDLSensorManager.registerListener(joystick.sensorManager, joystick.sensorListener, joystick.accelerometerSensor, 1);
            }
            if (joystick.gyroscopeSensor != null) {
                SDLSensorManager.registerListener(joystick.sensorManager, joystick.sensorListener, joystick.gyroscopeSensor, 1);
                return;
            }
            return;
        }
        if (joystick.accelerometerSensor != null) {
            SDLSensorManager.unregisterListener(joystick.sensorManager, joystick.sensorListener, joystick.accelerometerSensor);
        }
        if (joystick.gyroscopeSensor != null) {
            SDLSensorManager.unregisterListener(joystick.sensorManager, joystick.sensorListener, joystick.gyroscopeSensor);
        }
    }
}
