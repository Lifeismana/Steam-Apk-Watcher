package org.libsdl.app;

import android.hardware.input.InputManager;
import android.os.Build;
import android.view.InputDevice;
import android.view.MotionEvent;

/* JADX INFO: loaded from: classes.dex */
public class SDLControllerManager {
    private static final String TAG = "SDLControllerManager";
    protected static SDLDeviceListener mDeviceListener;
    protected static SDLHapticHandler mHapticHandler;
    protected static SDLJoystickHandler mJoystickHandler;

    static native void nativeAddHaptic(int i, String str);

    static native void nativeAddJoystick(int i, String str, String str2, int i2, int i3, int i4, int i5, int i6, int i7, boolean z, boolean z2, boolean z3, boolean z4);

    static native void nativeRemoveHaptic(int i);

    static native void nativeRemoveJoystick(int i);

    static native void nativeSetupJNI();

    static native void onNativeHat(int i, int i2, int i3, int i4);

    static native void onNativeJoy(int i, int i2, float f);

    static native void onNativeJoySensor(int i, int i2, long j, float f, float f2, float f3);

    public static native boolean onNativePadDown(int i, int i2, int i3);

    public static native boolean onNativePadUp(int i, int i2, int i3);

    static void initialize() {
        if (mJoystickHandler == null) {
            mJoystickHandler = new SDLJoystickHandler();
        }
        if (mHapticHandler == null && SDL.isSubsystemCompiled(SDL.SDL_INIT_HAPTIC)) {
            if (Build.VERSION.SDK_INT >= 31) {
                mHapticHandler = new SDLHapticHandler_API31();
            } else if (Build.VERSION.SDK_INT >= 26) {
                mHapticHandler = new SDLHapticHandler_API26();
            } else {
                mHapticHandler = new SDLHapticHandler();
            }
        }
    }

    public static void initializeDeviceListener() {
        if (mDeviceListener == null) {
            mDeviceListener = new SDLDeviceListener();
            ((InputManager) SDL.getContext().getSystemService("input")).registerInputDeviceListener(mDeviceListener, null);
        }
    }

    public static void shutdownDeviceListener() {
        if (mDeviceListener != null) {
            ((InputManager) SDL.getContext().getSystemService("input")).unregisterInputDeviceListener(mDeviceListener);
            mDeviceListener = null;
        }
    }

    public static boolean handleJoystickMotionEvent(MotionEvent motionEvent) {
        SDLJoystickHandler sDLJoystickHandler = mJoystickHandler;
        return sDLJoystickHandler != null && sDLJoystickHandler.handleMotionEvent(motionEvent);
    }

    static void detectDevices() {
        mJoystickHandler.detectDevices();
    }

    static void joystickSetLED(int i, int i2, int i3, int i4) {
        mJoystickHandler.setLED(i, i2, i3, i4);
    }

    static void joystickSetSensorsEnabled(int i, boolean z) {
        mJoystickHandler.setSensorsEnabled(i, z);
    }

    static void detectHapticDevices() {
        SDLHapticHandler sDLHapticHandler = mHapticHandler;
        if (sDLHapticHandler != null) {
            sDLHapticHandler.detectHapticDevices();
        }
    }

    static void hapticRun(int i, float f, int i2) {
        SDLHapticHandler sDLHapticHandler = mHapticHandler;
        if (sDLHapticHandler != null) {
            sDLHapticHandler.run(i, f, i2);
        }
    }

    static void hapticRumble(int i, float f, float f2, int i2) {
        SDLHapticHandler sDLHapticHandler = mHapticHandler;
        if (sDLHapticHandler != null) {
            sDLHapticHandler.rumble(i, f, f2, i2);
        }
    }

    static void hapticStop(int i) {
        SDLHapticHandler sDLHapticHandler = mHapticHandler;
        if (sDLHapticHandler != null) {
            sDLHapticHandler.stop(i);
        }
    }

    public static boolean isDeviceSDLJoystick(InputDevice inputDevice) {
        if (inputDevice == null || inputDevice.isVirtual()) {
            return false;
        }
        int sources = inputDevice.getSources();
        return (sources & 16) != 0 || (sources & 513) == 513 || (sources & 1025) == 1025;
    }

    public static boolean isDeviceSDLJoystick(int i) {
        return isDeviceSDLJoystick(InputDevice.getDevice(i));
    }
}
