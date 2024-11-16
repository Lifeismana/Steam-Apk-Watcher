package org.libsdl.app;

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
class SDLJoystickHandler_API16 extends SDLJoystickHandler {
    private final ArrayList<SDLJoystick> mJoysticks = new ArrayList<>();

    public int getAxisMask(List<InputDevice.MotionRange> list) {
        return -1;
    }

    public int getButtonMask(InputDevice inputDevice) {
        return -1;
    }

    public int getProductId(InputDevice inputDevice) {
        return 0;
    }

    public int getVendorId(InputDevice inputDevice) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: SDLControllerManager.java */
    /* loaded from: classes.dex */
    public static class SDLJoystick {
        public ArrayList<InputDevice.MotionRange> axes;
        public String desc;
        public int device_id;
        public ArrayList<InputDevice.MotionRange> hats;
        public String name;

        SDLJoystick() {
        }
    }

    /* compiled from: SDLControllerManager.java */
    /* loaded from: classes.dex */
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

    @Override // org.libsdl.app.SDLJoystickHandler
    public void pollInputDevices() {
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
                boolean z = Build.VERSION.SDK_INT >= 31 && device.getVibratorManager().getVibratorIds().length > 0;
                this.mJoysticks.add(sDLJoystick);
                SDLControllerManager.nativeAddJoystick(sDLJoystick.device_id, sDLJoystick.name, sDLJoystick.desc, getVendorId(device), getProductId(device), getButtonMask(device), sDLJoystick.axes.size(), getAxisMask(sDLJoystick.axes), sDLJoystick.hats.size() / 2, z);
            }
        }
        Iterator<SDLJoystick> it = this.mJoysticks.iterator();
        ArrayList arrayList = null;
        while (it.hasNext()) {
            int i2 = it.next().device_id;
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
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                int intValue = ((Integer) it2.next()).intValue();
                SDLControllerManager.nativeRemoveJoystick(intValue);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.mJoysticks.size()) {
                        break;
                    }
                    if (this.mJoysticks.get(i4).device_id == intValue) {
                        this.mJoysticks.remove(i4);
                        break;
                    }
                    i4++;
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

    @Override // org.libsdl.app.SDLJoystickHandler
    public boolean handleMotionEvent(MotionEvent motionEvent) {
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

    public String getJoystickDescriptor(InputDevice inputDevice) {
        String descriptor = inputDevice.getDescriptor();
        return (descriptor == null || descriptor.isEmpty()) ? inputDevice.getName() : descriptor;
    }
}
