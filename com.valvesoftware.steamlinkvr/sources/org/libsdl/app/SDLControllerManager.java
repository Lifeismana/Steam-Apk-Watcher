package org.libsdl.app;

import android.view.InputDevice;
import android.view.MotionEvent;

/* loaded from: classes.dex */
public class SDLControllerManager {
    private static final String TAG = "SDLControllerManager";
    protected static SDLHapticHandler mHapticHandler;
    protected static SDLJoystickHandler mJoystickHandler;

    public static native int nativeAddHaptic(int i, String str);

    public static native int nativeAddJoystick(int i, String str, String str2, int i2, int i3, boolean z, int i4, int i5, int i6, int i7);

    public static native int nativeRemoveHaptic(int i);

    public static native int nativeRemoveJoystick(int i);

    public static native int nativeSetupJNI();

    public static native void onNativeHat(int i, int i2, int i3, int i4);

    public static native void onNativeJoy(int i, int i2, float f);

    public static native int onNativePadDown(int i, int i2);

    public static native int onNativePadUp(int i, int i2);

    public static void initialize() {
        if (mJoystickHandler == null) {
            mJoystickHandler = new SDLJoystickHandler_API19();
        }
        if (mHapticHandler == null) {
            mHapticHandler = new SDLHapticHandler_API26();
        }
    }

    public static boolean handleJoystickMotionEvent(MotionEvent motionEvent) {
        return mJoystickHandler.handleMotionEvent(motionEvent);
    }

    public static void pollInputDevices() {
        mJoystickHandler.pollInputDevices();
    }

    public static void pollHapticDevices() {
        mHapticHandler.pollHapticDevices();
    }

    public static void hapticRun(int i, float f, int i2) {
        mHapticHandler.run(i, f, i2);
    }

    public static void hapticStop(int i) {
        mHapticHandler.stop(i);
    }

    public static boolean isDeviceSDLJoystick(int i) {
        InputDevice device = InputDevice.getDevice(i);
        if (device == null || i < 0) {
            return false;
        }
        int sources = device.getSources();
        return (sources & 16) != 0 || (sources & 513) == 513 || (sources & 1025) == 1025;
    }
}
