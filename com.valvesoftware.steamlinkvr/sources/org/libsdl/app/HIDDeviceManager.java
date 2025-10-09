package org.libsdl.app;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class HIDDeviceManager {
    private static final String ACTION_USB_PERMISSION = "org.libsdl.app.USB_PERMISSION";
    private static final String TAG = "hidapi";
    private static HIDDeviceManager sManager;
    private static int sManagerRefCount;
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    private Handler mHandler;
    private boolean mIsChromebook;
    private List<BluetoothDevice> mLastBluetoothDevices;
    private int mNextDeviceId;
    private SharedPreferences mSharedPreferences;
    private UsbManager mUsbManager;
    private HashMap<Integer, HIDDevice> mDevicesById = new HashMap<>();
    private HashMap<BluetoothDevice, HIDDeviceBLESteamController> mBluetoothDevices = new HashMap<>();
    private final BroadcastReceiver mUsbBroadcast = new BroadcastReceiver() { // from class: org.libsdl.app.HIDDeviceManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                HIDDeviceManager.this.handleUsbDeviceAttached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                HIDDeviceManager.this.handleUsbDeviceDetached((UsbDevice) intent.getParcelableExtra("device"));
            } else if (action.equals(HIDDeviceManager.ACTION_USB_PERMISSION)) {
                HIDDeviceManager.this.handleUsbDevicePermission((UsbDevice) intent.getParcelableExtra("device"), intent.getBooleanExtra("permission", false));
            }
        }
    };
    private final BroadcastReceiver mBluetoothBroadcast = new BroadcastReceiver() { // from class: org.libsdl.app.HIDDeviceManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device connected: " + bluetoothDevice);
                if (HIDDeviceManager.this.isSteamController(bluetoothDevice)) {
                    HIDDeviceManager.this.connectBluetoothDevice(bluetoothDevice);
                }
            }
            if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                BluetoothDevice bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Log.d(HIDDeviceManager.TAG, "Bluetooth device disconnected: " + bluetoothDevice2);
                HIDDeviceManager.this.disconnectBluetoothDevice(bluetoothDevice2);
            }
        }
    };

    private native void HIDDeviceRegisterCallback();

    private native void HIDDeviceReleaseCallback();

    native void HIDDeviceConnected(int i, String str, int i2, int i3, String str2, int i4, String str3, String str4, int i5, int i6, int i7, int i8, boolean z);

    native void HIDDeviceDisconnected(int i);

    native void HIDDeviceInputReport(int i, byte[] bArr);

    native void HIDDeviceOpenPending(int i);

    native void HIDDeviceOpenResult(int i, boolean z);

    native void HIDDeviceReportResponse(int i, byte[] bArr);

    public static HIDDeviceManager acquire(Context context) {
        if (sManagerRefCount == 0) {
            sManager = new HIDDeviceManager(context);
        }
        sManagerRefCount++;
        return sManager;
    }

    public static void release(HIDDeviceManager hIDDeviceManager) {
        HIDDeviceManager hIDDeviceManager2 = sManager;
        if (hIDDeviceManager == hIDDeviceManager2) {
            int i = sManagerRefCount - 1;
            sManagerRefCount = i;
            if (i == 0) {
                hIDDeviceManager2.close();
                sManager = null;
            }
        }
    }

    private HIDDeviceManager(Context context) {
        this.mNextDeviceId = 0;
        this.mSharedPreferences = null;
        this.mIsChromebook = false;
        this.mContext = context;
        HIDDeviceRegisterCallback();
        this.mSharedPreferences = this.mContext.getSharedPreferences(TAG, 0);
        this.mIsChromebook = SDLActivity.isChromebook();
        this.mNextDeviceId = this.mSharedPreferences.getInt("next_device_id", 0);
    }

    Context getContext() {
        return this.mContext;
    }

    int getDeviceIDForIdentifier(String str) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        int i = this.mSharedPreferences.getInt(str, 0);
        if (i == 0) {
            i = this.mNextDeviceId;
            int i2 = i + 1;
            this.mNextDeviceId = i2;
            edit.putInt("next_device_id", i2);
        }
        edit.putInt(str, i);
        edit.commit();
        return i;
    }

    private void initializeUSB() {
        UsbManager usbManager = (UsbManager) this.mContext.getSystemService("usb");
        this.mUsbManager = usbManager;
        if (usbManager == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        intentFilter.addAction(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 33) {
            this.mContext.registerReceiver(this.mUsbBroadcast, intentFilter, 2);
        } else {
            this.mContext.registerReceiver(this.mUsbBroadcast, intentFilter);
        }
        Iterator<UsbDevice> it = this.mUsbManager.getDeviceList().values().iterator();
        while (it.hasNext()) {
            handleUsbDeviceAttached(it.next());
        }
    }

    UsbManager getUSBManager() {
        return this.mUsbManager;
    }

    private void shutdownUSB() {
        try {
            this.mContext.unregisterReceiver(this.mUsbBroadcast);
        } catch (Exception unused) {
        }
    }

    private boolean isHIDDeviceInterface(UsbDevice usbDevice, UsbInterface usbInterface) {
        return usbInterface.getInterfaceClass() == 3 || isXbox360Controller(usbDevice, usbInterface) || isXboxOneController(usbDevice, usbInterface);
    }

    private boolean isXbox360Controller(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] iArr = {121, 1103, 1118, 1133, 1390, 1699, 1848, 2047, 3695, 3853, 4152, 4553, 4779, 5168, 5227, 5426, 5604, 5678, 5769, 6473, 7085, 8406, 9414, 11298, 11720, 39046};
        if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 93 && (usbInterface.getInterfaceProtocol() == 1 || usbInterface.getInterfaceProtocol() == 129)) {
            int vendorId = usbDevice.getVendorId();
            for (int i = 0; i < 26; i++) {
                if (vendorId == iArr[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isXboxOneController(UsbDevice usbDevice, UsbInterface usbInterface) {
        int[] iArr = {1008, 1103, 1118, 1848, 2821, 3695, 3853, 4341, 5426, 8406, 9414, 10571, 11720, 11812, 11925, 12933, 13623, 13932};
        if (usbInterface.getId() == 0 && usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 71 && usbInterface.getInterfaceProtocol() == 208) {
            int vendorId = usbDevice.getVendorId();
            for (int i = 0; i < 18; i++) {
                if (vendorId == iArr[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUsbDeviceAttached(UsbDevice usbDevice) {
        connectHIDDeviceUSB(usbDevice);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUsbDeviceDetached(UsbDevice usbDevice) {
        ArrayList arrayList = new ArrayList();
        for (HIDDevice hIDDevice : this.mDevicesById.values()) {
            if (usbDevice.equals(hIDDevice.getDevice())) {
                arrayList.add(Integer.valueOf(hIDDevice.getId()));
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            HIDDevice hIDDevice2 = this.mDevicesById.get(Integer.valueOf(intValue));
            this.mDevicesById.remove(Integer.valueOf(intValue));
            hIDDevice2.shutdown();
            HIDDeviceDisconnected(intValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleUsbDevicePermission(UsbDevice usbDevice, boolean z) {
        for (HIDDevice hIDDevice : this.mDevicesById.values()) {
            if (usbDevice.equals(hIDDevice.getDevice())) {
                HIDDeviceOpenResult(hIDDevice.getId(), z ? hIDDevice.open() : false);
            }
        }
    }

    private void connectHIDDeviceUSB(UsbDevice usbDevice) {
        HIDDeviceManager hIDDeviceManager = this;
        synchronized (this) {
            int i = 0;
            int i2 = 0;
            while (i2 < usbDevice.getInterfaceCount()) {
                UsbInterface usbInterface = usbDevice.getInterface(i2);
                if (hIDDeviceManager.isHIDDeviceInterface(usbDevice, usbInterface)) {
                    int id = 1 << usbInterface.getId();
                    if ((i & id) == 0) {
                        int i3 = i | id;
                        HIDDeviceUSB hIDDeviceUSB = new HIDDeviceUSB(hIDDeviceManager, usbDevice, i2);
                        int id2 = hIDDeviceUSB.getId();
                        hIDDeviceManager.mDevicesById.put(Integer.valueOf(id2), hIDDeviceUSB);
                        hIDDeviceManager.HIDDeviceConnected(id2, hIDDeviceUSB.getIdentifier(), hIDDeviceUSB.getVendorId(), hIDDeviceUSB.getProductId(), hIDDeviceUSB.getSerialNumber(), hIDDeviceUSB.getVersion(), hIDDeviceUSB.getManufacturerName(), hIDDeviceUSB.getProductName(), usbInterface.getId(), usbInterface.getInterfaceClass(), usbInterface.getInterfaceSubclass(), usbInterface.getInterfaceProtocol(), false);
                        i = i3;
                    }
                }
                i2++;
                hIDDeviceManager = this;
            }
        }
    }

    private void initializeBluetooth() {
        BluetoothAdapter adapter;
        Log.d(TAG, "Initializing Bluetooth");
        if (Build.VERSION.SDK_INT >= 31 && this.mContext.getPackageManager().checkPermission("android.permission.BLUETOOTH_CONNECT", this.mContext.getPackageName()) != 0) {
            Log.d(TAG, "Couldn't initialize Bluetooth, missing android.permission.BLUETOOTH_CONNECT");
            return;
        }
        if (Build.VERSION.SDK_INT <= 30 && this.mContext.getPackageManager().checkPermission("android.permission.BLUETOOTH", this.mContext.getPackageName()) != 0) {
            Log.d(TAG, "Couldn't initialize Bluetooth, missing android.permission.BLUETOOTH");
            return;
        }
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Log.d(TAG, "Couldn't initialize Bluetooth, this version of Android does not support Bluetooth LE");
            return;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        this.mBluetoothManager = bluetoothManager;
        if (bluetoothManager == null || (adapter = bluetoothManager.getAdapter()) == null) {
            return;
        }
        for (BluetoothDevice bluetoothDevice : adapter.getBondedDevices()) {
            Log.d(TAG, "Bluetooth device available: " + bluetoothDevice);
            if (isSteamController(bluetoothDevice)) {
                connectBluetoothDevice(bluetoothDevice);
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        if (Build.VERSION.SDK_INT >= 33) {
            this.mContext.registerReceiver(this.mBluetoothBroadcast, intentFilter, 2);
        } else {
            this.mContext.registerReceiver(this.mBluetoothBroadcast, intentFilter);
        }
        if (this.mIsChromebook) {
            this.mHandler = new Handler(Looper.getMainLooper());
            this.mLastBluetoothDevices = new ArrayList();
        }
    }

    private void shutdownBluetooth() {
        try {
            this.mContext.unregisterReceiver(this.mBluetoothBroadcast);
        } catch (Exception unused) {
        }
    }

    void chromebookConnectionHandler() {
        if (this.mIsChromebook) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<BluetoothDevice> connectedDevices = this.mBluetoothManager.getConnectedDevices(7);
            for (BluetoothDevice bluetoothDevice : connectedDevices) {
                if (!this.mLastBluetoothDevices.contains(bluetoothDevice)) {
                    arrayList2.add(bluetoothDevice);
                }
            }
            for (BluetoothDevice bluetoothDevice2 : this.mLastBluetoothDevices) {
                if (!connectedDevices.contains(bluetoothDevice2)) {
                    arrayList.add(bluetoothDevice2);
                }
            }
            this.mLastBluetoothDevices = connectedDevices;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                disconnectBluetoothDevice((BluetoothDevice) it.next());
            }
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                connectBluetoothDevice((BluetoothDevice) it2.next());
            }
            this.mHandler.postDelayed(new Runnable() { // from class: org.libsdl.app.HIDDeviceManager.3
                @Override // java.lang.Runnable
                public void run() {
                    this.chromebookConnectionHandler();
                }
            }, 10000L);
        }
    }

    boolean connectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        Log.v(TAG, "connectBluetoothDevice device=" + bluetoothDevice);
        synchronized (this) {
            if (this.mBluetoothDevices.containsKey(bluetoothDevice)) {
                Log.v(TAG, "Steam controller with address " + bluetoothDevice + " already exists, attempting reconnect");
                this.mBluetoothDevices.get(bluetoothDevice).reconnect();
                return false;
            }
            HIDDeviceBLESteamController hIDDeviceBLESteamController = new HIDDeviceBLESteamController(this, bluetoothDevice);
            int id = hIDDeviceBLESteamController.getId();
            this.mBluetoothDevices.put(bluetoothDevice, hIDDeviceBLESteamController);
            this.mDevicesById.put(Integer.valueOf(id), hIDDeviceBLESteamController);
            return true;
        }
    }

    void disconnectBluetoothDevice(BluetoothDevice bluetoothDevice) {
        synchronized (this) {
            HIDDeviceBLESteamController hIDDeviceBLESteamController = this.mBluetoothDevices.get(bluetoothDevice);
            if (hIDDeviceBLESteamController == null) {
                return;
            }
            int id = hIDDeviceBLESteamController.getId();
            this.mBluetoothDevices.remove(bluetoothDevice);
            this.mDevicesById.remove(Integer.valueOf(id));
            hIDDeviceBLESteamController.shutdown();
            HIDDeviceDisconnected(id);
        }
    }

    boolean isSteamController(BluetoothDevice bluetoothDevice) {
        return (bluetoothDevice == null || bluetoothDevice.getName() == null || !bluetoothDevice.getName().equals("SteamController") || (bluetoothDevice.getType() & 2) == 0) ? false : true;
    }

    private void close() {
        shutdownUSB();
        shutdownBluetooth();
        synchronized (this) {
            Iterator<HIDDevice> it = this.mDevicesById.values().iterator();
            while (it.hasNext()) {
                it.next().shutdown();
            }
            this.mDevicesById.clear();
            this.mBluetoothDevices.clear();
            HIDDeviceReleaseCallback();
        }
    }

    public void setFrozen(boolean z) {
        synchronized (this) {
            Iterator<HIDDevice> it = this.mDevicesById.values().iterator();
            while (it.hasNext()) {
                it.next().setFrozen(z);
            }
        }
    }

    private HIDDevice getDevice(int i) {
        HIDDevice hIDDevice;
        synchronized (this) {
            hIDDevice = this.mDevicesById.get(Integer.valueOf(i));
            if (hIDDevice == null) {
                Log.v(TAG, "No device for id: " + i);
                Log.v(TAG, "Available devices: " + this.mDevicesById.keySet());
            }
        }
        return hIDDevice;
    }

    boolean initialize(boolean z, boolean z2) {
        Log.v(TAG, "initialize(" + z + ", " + z2 + ")");
        if (z) {
            initializeUSB();
        }
        if (!z2) {
            return true;
        }
        initializeBluetooth();
        return true;
    }

    boolean openDevice(int i) {
        Log.v(TAG, "openDevice deviceID=" + i);
        HIDDevice device = getDevice(i);
        if (device == null) {
            HIDDeviceDisconnected(i);
            return false;
        }
        UsbDevice device2 = device.getDevice();
        if (device2 != null && !this.mUsbManager.hasPermission(device2)) {
            HIDDeviceOpenPending(i);
            try {
                int i2 = Build.VERSION.SDK_INT >= 31 ? 33554432 : 0;
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                intent.setPackage(this.mContext.getPackageName());
                this.mUsbManager.requestPermission(device2, PendingIntent.getBroadcast(this.mContext, 0, intent, i2));
            } catch (Exception unused) {
                Log.v(TAG, "Couldn't request permission for USB device " + device2);
                HIDDeviceOpenResult(i, false);
            }
            return false;
        }
        try {
            return device.open();
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return false;
        }
    }

    int writeReport(int i, byte[] bArr, boolean z) {
        try {
            HIDDevice device = getDevice(i);
            if (device == null) {
                HIDDeviceDisconnected(i);
                return -1;
            }
            return device.writeReport(bArr, z);
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return -1;
        }
    }

    boolean readReport(int i, byte[] bArr, boolean z) {
        try {
            HIDDevice device = getDevice(i);
            if (device == null) {
                HIDDeviceDisconnected(i);
                return false;
            }
            return device.readReport(bArr, z);
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
            return false;
        }
    }

    void closeDevice(int i) {
        try {
            Log.v(TAG, "closeDevice deviceID=" + i);
            HIDDevice device = getDevice(i);
            if (device == null) {
                HIDDeviceDisconnected(i);
            } else {
                device.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Got exception: " + Log.getStackTraceString(e));
        }
    }
}
