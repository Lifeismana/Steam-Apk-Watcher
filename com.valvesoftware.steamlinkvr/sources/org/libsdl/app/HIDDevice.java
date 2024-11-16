package org.libsdl.app;

import android.hardware.usb.UsbDevice;

/* loaded from: classes.dex */
interface HIDDevice {
    void close();

    UsbDevice getDevice();

    int getId();

    String getManufacturerName();

    int getProductId();

    String getProductName();

    String getSerialNumber();

    int getVendorId();

    int getVersion();

    boolean open();

    boolean readReport(byte[] bArr, boolean z);

    void setFrozen(boolean z);

    void shutdown();

    int writeReport(byte[] bArr, boolean z);
}
