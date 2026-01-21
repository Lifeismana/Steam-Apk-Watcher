package org.libsdl.app;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import java.util.Arrays;
import java.util.Locale;

/* loaded from: classes.dex */
class HIDDeviceUSB implements HIDDevice {
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

    String getIdentifier() {
        return String.format(Locale.ENGLISH, "%s/%x/%x/%d", this.mDevice.getDeviceName(), Integer.valueOf(this.mDevice.getVendorId()), Integer.valueOf(this.mDevice.getProductId()), Integer.valueOf(this.mInterfaceIndex));
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

    @Override // org.libsdl.app.HIDDevice
    public String getSerialNumber() {
        String serialNumber;
        try {
            serialNumber = this.mDevice.getSerialNumber();
        } catch (SecurityException unused) {
            serialNumber = null;
        }
        return serialNumber == null ? "" : serialNumber;
    }

    @Override // org.libsdl.app.HIDDevice
    public String getManufacturerName() {
        String manufacturerName = this.mDevice.getManufacturerName();
        return manufacturerName == null ? String.format("%x", Integer.valueOf(getVendorId())) : manufacturerName;
    }

    @Override // org.libsdl.app.HIDDevice
    public String getProductName() {
        String productName = this.mDevice.getProductName();
        return productName == null ? String.format("%x", Integer.valueOf(getProductId())) : productName;
    }

    @Override // org.libsdl.app.HIDDevice
    public UsbDevice getDevice() {
        return this.mDevice;
    }

    String getDeviceName() {
        return getManufacturerName() + " " + getProductName() + "(0x" + String.format("%x", Integer.valueOf(getVendorId())) + "/0x" + String.format("%x", Integer.valueOf(getProductId())) + ")";
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean open() {
        UsbDeviceConnection usbDeviceConnectionOpenDevice = this.mManager.getUSBManager().openDevice(this.mDevice);
        this.mConnection = usbDeviceConnectionOpenDevice;
        if (usbDeviceConnectionOpenDevice == null) {
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
    public int writeReport(byte[] bArr, boolean z) {
        int i;
        boolean z2;
        int i2;
        UsbDeviceConnection usbDeviceConnection = this.mConnection;
        if (usbDeviceConnection == null) {
            Log.w(TAG, "writeReport() called with no device connection");
            return -1;
        }
        if (z) {
            int length = bArr.length;
            byte b = bArr[0];
            if (b == 0) {
                i = length - 1;
                z2 = true;
                i2 = 1;
            } else {
                i = length;
                z2 = false;
                i2 = 0;
            }
            int iControlTransfer = usbDeviceConnection.controlTransfer(33, 9, b | 768, this.mInterface, bArr, i2, i, 1000);
            if (iControlTransfer >= 0) {
                return z2 ? i + 1 : i;
            }
            Log.w(TAG, "writeFeatureReport() returned " + iControlTransfer + " on device " + getDeviceName());
            return -1;
        }
        int iBulkTransfer = usbDeviceConnection.bulkTransfer(this.mOutputEndpoint, bArr, bArr.length, 1000);
        if (iBulkTransfer != bArr.length) {
            Log.w(TAG, "writeOutputReport() returned " + iBulkTransfer + " on device " + getDeviceName());
        }
        return iBulkTransfer;
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean readReport(byte[] bArr, boolean z) {
        int i;
        boolean z2;
        int length = bArr.length;
        byte b = bArr[0];
        int i2 = length;
        UsbDeviceConnection usbDeviceConnection = this.mConnection;
        if (usbDeviceConnection == null) {
            Log.w(TAG, "readReport() called with no device connection");
            return false;
        }
        if (b == 0) {
            i2--;
            i = 1;
            z2 = true;
        } else {
            i = 0;
            z2 = false;
        }
        int i3 = i2;
        int iControlTransfer = usbDeviceConnection.controlTransfer(161, 1, ((z ? 3 : 1) << 8) | b, this.mInterface, bArr, i, i3, 1000);
        if (iControlTransfer < 0) {
            Log.w(TAG, "getFeatureReport() returned " + iControlTransfer + " on device " + getDeviceName());
            return false;
        }
        if (z2) {
            iControlTransfer++;
            i3++;
        }
        this.mManager.HIDDeviceReportResponse(this.mDeviceId, iControlTransfer == i3 ? bArr : Arrays.copyOfRange(bArr, 0, iControlTransfer));
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

    protected class InputThread extends Thread {
        protected InputThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int maxPacketSize = HIDDeviceUSB.this.mInputEndpoint.getMaxPacketSize();
            byte[] bArr = new byte[maxPacketSize];
            while (HIDDeviceUSB.this.mRunning) {
                try {
                    int iBulkTransfer = HIDDeviceUSB.this.mConnection.bulkTransfer(HIDDeviceUSB.this.mInputEndpoint, bArr, maxPacketSize, 1000);
                    if (iBulkTransfer > 0) {
                        byte[] bArrCopyOfRange = iBulkTransfer == maxPacketSize ? bArr : Arrays.copyOfRange(bArr, 0, iBulkTransfer);
                        if (!HIDDeviceUSB.this.mFrozen) {
                            HIDDeviceUSB.this.mManager.HIDDeviceInputReport(HIDDeviceUSB.this.mDeviceId, bArrCopyOfRange);
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
