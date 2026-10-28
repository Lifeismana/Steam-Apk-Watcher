package org.libsdl.app;

import android.hardware.input.InputManager;

/* JADX INFO: compiled from: SDLControllerManager.java */
/* JADX INFO: loaded from: classes.dex */
class SDLDeviceListener implements InputManager.InputDeviceListener {
    SDLDeviceListener() {
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        SDLControllerManager.mJoystickHandler.deviceChanged(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        SDLControllerManager.mJoystickHandler.deviceChanged(i);
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        SDLControllerManager.mJoystickHandler.deviceRemoved(i);
    }
}
