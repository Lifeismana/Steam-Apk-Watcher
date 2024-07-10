package org.libsdl.app;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build;
import android.util.Log;
import java.util.Arrays;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class HIDDeviceUSB implements HIDDevice {
    private static final String TAG = "hidapi";
    protected UsbDeviceConnection mConnection;
    protected UsbDevice mDevice;
    protected int mDeviceId;
    protected boolean mFrozen;
    protected UsbEndpoint mInputEndpoint;
    protected InputThread mInputThread;
    protected int mInterface;
    protected int mInterfaceIndex;
    protected HIDDeviceManager mManager;
    protected UsbEndpoint mOutputEndpoint;
    protected boolean mRunning = false;

    @Override // org.libsdl.app.HIDDevice
    public int getVersion() {
        return 0;
    }

    public HIDDeviceUSB(HIDDeviceManager hIDDeviceManager, UsbDevice usbDevice, int i) {
        this.mManager = hIDDeviceManager;
        this.mDevice = usbDevice;
        this.mInterfaceIndex = i;
        this.mInterface = usbDevice.getInterface(i).getId();
        this.mDeviceId = hIDDeviceManager.getDeviceIDForIdentifier(getIdentifier());
    }

    public String getIdentifier() {
        return String.format("%s/%x/%x/%d", this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getVendorId()), Integer.valueOf(this.mDevice.getProductId()), Integer.valueOf(this.mInterfaceIndex));
    }

    @Override // org.libsdl.app.HIDDevice
    public int getId() {
        return this.mDeviceId;
    }

    @Override // org.libsdl.app.HIDDevice
    public int getVendorId() {
        return this.mDevice.getVendorId();
    }

    @Override // org.libsdl.app.HIDDevice
    public int getProductId() {
        return this.mDevice.getProductId();
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x0010 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:7:? A[RETURN, SYNTHETIC] */
    @Override // org.libsdl.app.HIDDevice
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public String getSerialNumber() {
        String serialNumber;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                serialNumber = this.mDevice.getSerialNumber();
            } catch (SecurityException unused) {
            }
            return serialNumber != null ? "" : serialNumber;
        }
        serialNumber = null;
        if (serialNumber != null) {
        }
    }

    @Override // org.libsdl.app.HIDDevice
    public String getManufacturerName() {
        String manufacturerName = Build.VERSION.SDK_INT >= 21 ? this.mDevice.getManufacturerName() : null;
        return manufacturerName == null ? String.format("%x", Integer.valueOf(getVendorId())) : manufacturerName;
    }

    @Override // org.libsdl.app.HIDDevice
    public String getProductName() {
        String productName = Build.VERSION.SDK_INT >= 21 ? this.mDevice.getProductName() : null;
        return productName == null ? String.format("%x", Integer.valueOf(getProductId())) : productName;
    }

    @Override // org.libsdl.app.HIDDevice
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    public String getDeviceName() {
        return getManufacturerName() + " " + getProductName() + "(0x" + String.format("%x", Integer.valueOf(getVendorId())) + "/0x" + String.format("%x", Integer.valueOf(getProductId())) + ")";
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean open() {
        UsbDeviceConnection openDevice = this.mManager.getUSBManager().openDevice(this.mDevice);
        this.mConnection = openDevice;
        if (openDevice == null) {
            Log.w(TAG, "Unable to open USB device " + getDeviceName());
            return false;
        }
        UsbInterface usbInterface = this.mDevice.getInterface(this.mInterfaceIndex);
        if (!this.mConnection.claimInterface(usbInterface, true)) {
            Log.w(TAG, "Failed to claim interfaces on USB device " + getDeviceName());
            close();
            return false;
        }
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            int direction = endpoint.getDirection();
            if (direction != 0) {
                if (direction == 128 && this.mInputEndpoint == null) {
                    this.mInputEndpoint = endpoint;
                }
            } else if (this.mOutputEndpoint == null) {
                this.mOutputEndpoint = endpoint;
            }
        }
        if (this.mInputEndpoint == null || this.mOutputEndpoint == null) {
            Log.w(TAG, "Missing required endpoint on USB device " + getDeviceName());
            close();
            return false;
        }
        this.mRunning = true;
        InputThread inputThread = new InputThread();
        this.mInputThread = inputThread;
        inputThread.start();
        return true;
    }

    @Override // org.libsdl.app.HIDDevice
    public int sendFeatureReport(byte[] bArr) {
        int i;
        int length = bArr.length;
        boolean z = false;
        byte b = bArr[0];
        if (b == 0) {
            length--;
            z = true;
            i = 1;
        } else {
            i = 0;
        }
        int controlTransfer = this.mConnection.controlTransfer(33, 9, b | 768, this.mInterface, bArr, i, length, 1000);
        if (controlTransfer >= 0) {
            return z ? length + 1 : length;
        }
        Log.w(TAG, "sendFeatureReport() returned " + controlTransfer + " on device " + getDeviceName());
        return -1;
    }

    @Override // org.libsdl.app.HIDDevice
    public int sendOutputReport(byte[] bArr) {
        int bulkTransfer = this.mConnection.bulkTransfer(this.mOutputEndpoint, bArr, bArr.length, 1000);
        if (bulkTransfer != bArr.length) {
            Log.w(TAG, "sendOutputReport() returned " + bulkTransfer + " on device " + getDeviceName());
        }
        return bulkTransfer;
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean getFeatureReport(byte[] bArr) {
        int i;
        boolean z;
        int length = bArr.length;
        byte b = bArr[0];
        if (b == 0) {
            length--;
            i = 1;
            z = true;
        } else {
            i = 0;
            z = false;
        }
        int controlTransfer = this.mConnection.controlTransfer(161, 1, b | 768, this.mInterface, bArr, i, length, 1000);
        if (controlTransfer < 0) {
            Log.w(TAG, "getFeatureReport() returned " + controlTransfer + " on device " + getDeviceName());
            return false;
        }
        if (z) {
            controlTransfer++;
            length++;
        }
        if (controlTransfer != length) {
            bArr = Arrays.copyOfRange(bArr, 0, controlTransfer);
        }
        this.mManager.HIDDeviceFeatureReport(this.mDeviceId, bArr);
        return true;
    }

    @Override // org.libsdl.app.HIDDevice
    public void close() {
        this.mRunning = false;
        if (this.mInputThread != null) {
            while (this.mInputThread.isAlive()) {
                this.mInputThread.interrupt();
                try {
                    this.mInputThread.join();
                } catch (InterruptedException unused) {
                }
            }
            this.mInputThread = null;
        }
        if (this.mConnection != null) {
            this.mConnection.releaseInterface(this.mDevice.getInterface(this.mInterfaceIndex));
            this.mConnection.close();
            this.mConnection = null;
        }
    }

    @Override // org.libsdl.app.HIDDevice
    public void shutdown() {
        close();
        this.mManager = null;
    }

    @Override // org.libsdl.app.HIDDevice
    public void setFrozen(boolean z) {
        this.mFrozen = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class InputThread extends Thread {
        protected InputThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int maxPacketSize = HIDDeviceUSB.this.mInputEndpoint.getMaxPacketSize();
            byte[] bArr = new byte[maxPacketSize];
            while (HIDDeviceUSB.this.mRunning) {
                try {
                    int bulkTransfer = HIDDeviceUSB.this.mConnection.bulkTransfer(HIDDeviceUSB.this.mInputEndpoint, bArr, maxPacketSize, 1000);
                    if (bulkTransfer > 0) {
                        byte[] copyOfRange = bulkTransfer == maxPacketSize ? bArr : Arrays.copyOfRange(bArr, 0, bulkTransfer);
                        if (!HIDDeviceUSB.this.mFrozen) {
                            HIDDeviceUSB.this.mManager.HIDDeviceInputReport(HIDDeviceUSB.this.mDeviceId, copyOfRange);
                        }
                    }
                } catch (Exception e) {
                    Log.v(HIDDeviceUSB.TAG, "Exception in UsbDeviceConnection bulktransfer: " + e);
                    return;
                }
            }
        }
    }
}
