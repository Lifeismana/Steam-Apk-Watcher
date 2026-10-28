package org.libsdl.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
class HIDDeviceBLESteamController extends BluetoothGattCallback implements HIDDevice {
    private static final int CHROMEBOOK_CONNECTION_CHECK_INTERVAL = 10000;
    private static final int D0G_BLE2_PID = 4358;
    private static final String TAG = "hidapi";
    private static final int TRANSPORT_AUTO = 0;
    private static final int TRANSPORT_BREDR = 1;
    private static final int TRANSPORT_LE = 2;
    private static final int TRITON_BLE_PID = 4867;
    private BluetoothDevice mDevice;
    private int mDeviceId;
    private boolean mHasEnabledNotifications;
    private boolean mHasSeenInputUpdate;
    private UUID mInputCharacteristic;
    private boolean mIsChromebook;
    private boolean mIsRegistered;
    private HIDDeviceManager mManager;
    static final UUID steamControllerService = UUID.fromString("100F6C32-1735-4313-B402-38567131E5F3");
    static final UUID inputCharacteristicD0G = UUID.fromString("100F6C33-1735-4313-B402-38567131E5F3");
    static final UUID inputCharacteristicTriton_0x45 = UUID.fromString("100F6C7A-1735-4313-B402-38567131E5F3");
    static final UUID inputCharacteristicTriton_0x47 = UUID.fromString("100F6C7C-1735-4313-B402-38567131E5F3");
    static final UUID reportCharacteristic = UUID.fromString("100F6C34-1735-4313-B402-38567131E5F3");
    private static final byte[] enterValveMode = {-64, -121, 3, 8, 7, 0};
    private boolean mIsConnected = false;
    private boolean mIsReconnecting = false;
    private boolean mFrozen = false;
    GattOperation mCurrentOperation = null;
    private int mProductId = -1;
    private int mReportId = 0;
    private HashMap<Integer, BluetoothGattCharacteristic> mOutputReportChars = new HashMap<>();
    private LinkedList<GattOperation> mOperations = new LinkedList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private BluetoothGatt mGatt = connectGatt();

    @Override // org.libsdl.app.HIDDevice
    public void close() {
    }

    @Override // org.libsdl.app.HIDDevice
    public UsbDevice getDevice() {
        return null;
    }

    @Override // org.libsdl.app.HIDDevice
    public int getVendorId() {
        return 10462;
    }

    @Override // org.libsdl.app.HIDDevice
    public int getVersion() {
        return 0;
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int i, int i2) {
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onReliableWriteCompleted(BluetoothGatt bluetoothGatt, int i) {
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean open() {
        return true;
    }

    static class GattOperation {
        int mDelayMs;
        BluetoothGatt mGatt;
        Operation mOp;
        boolean mResult;
        UUID mUuid;
        byte[] mValue;

        private enum Operation {
            CHR_READ,
            CHR_WRITE,
            ENABLE_NOTIFICATION
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid) {
            this.mResult = true;
            this.mDelayMs = 0;
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid, int i) {
            this.mResult = true;
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
            this.mDelayMs = i;
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid, byte[] bArr) {
            this.mResult = true;
            this.mDelayMs = 0;
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
            this.mValue = bArr;
        }

        private GattOperation(BluetoothGatt bluetoothGatt, Operation operation, UUID uuid, byte[] bArr, int i) {
            this.mResult = true;
            this.mGatt = bluetoothGatt;
            this.mOp = operation;
            this.mUuid = uuid;
            this.mValue = bArr;
            this.mDelayMs = i;
        }

        public void run() {
            BluetoothGattCharacteristic characteristic;
            BluetoothGattDescriptor descriptor;
            byte[] bArr;
            int iOrdinal = this.mOp.ordinal();
            if (iOrdinal == 0) {
                if (!this.mGatt.readCharacteristic(getCharacteristic(this.mUuid))) {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to read characteristic " + this.mUuid.toString());
                    this.mResult = false;
                    return;
                }
                this.mResult = true;
                return;
            }
            if (iOrdinal == 1) {
                BluetoothGattCharacteristic characteristic2 = getCharacteristic(this.mUuid);
                characteristic2.setValue(this.mValue);
                if (!this.mGatt.writeCharacteristic(characteristic2)) {
                    Log.e(HIDDeviceBLESteamController.TAG, "Unable to write characteristic " + this.mUuid.toString());
                    this.mResult = false;
                    return;
                }
                this.mResult = true;
                return;
            }
            if (iOrdinal != 2 || (characteristic = getCharacteristic(this.mUuid)) == null || (descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))) == null) {
                return;
            }
            int properties = characteristic.getProperties();
            if ((properties & 16) == 16) {
                bArr = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
            } else if ((properties & 32) == 32) {
                bArr = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
            } else {
                Log.e(HIDDeviceBLESteamController.TAG, "Unable to start notifications on input characteristic");
                this.mResult = false;
                return;
            }
            this.mGatt.setCharacteristicNotification(characteristic, true);
            descriptor.setValue(bArr);
            if (!this.mGatt.writeDescriptor(descriptor)) {
                Log.e(HIDDeviceBLESteamController.TAG, "Unable to write descriptor " + this.mUuid.toString());
                this.mResult = false;
                return;
            }
            this.mResult = true;
        }

        public boolean finish() {
            return this.mResult;
        }

        public int getDelayMs() {
            return this.mDelayMs;
        }

        private BluetoothGattCharacteristic getCharacteristic(UUID uuid) {
            BluetoothGattService service = this.mGatt.getService(HIDDeviceBLESteamController.steamControllerService);
            if (service == null) {
                return null;
            }
            return service.getCharacteristic(uuid);
        }

        public static GattOperation readCharacteristic(BluetoothGatt bluetoothGatt, UUID uuid) {
            return new GattOperation(bluetoothGatt, Operation.CHR_READ, uuid);
        }

        public static GattOperation writeCharacteristic(BluetoothGatt bluetoothGatt, UUID uuid, byte[] bArr) {
            return new GattOperation(bluetoothGatt, Operation.CHR_WRITE, uuid, bArr);
        }

        public static GattOperation enableNotification(BluetoothGatt bluetoothGatt, UUID uuid) {
            return new GattOperation(bluetoothGatt, Operation.ENABLE_NOTIFICATION, uuid);
        }

        public static GattOperation enableNotification(BluetoothGatt bluetoothGatt, UUID uuid, int i) {
            return new GattOperation(bluetoothGatt, Operation.ENABLE_NOTIFICATION, uuid, i);
        }
    }

    HIDDeviceBLESteamController(HIDDeviceManager hIDDeviceManager, BluetoothDevice bluetoothDevice) {
        this.mIsRegistered = false;
        this.mIsChromebook = false;
        this.mHasEnabledNotifications = false;
        this.mHasSeenInputUpdate = false;
        this.mManager = hIDDeviceManager;
        this.mDevice = bluetoothDevice;
        this.mDeviceId = hIDDeviceManager.getDeviceIDForIdentifier(getIdentifier());
        this.mIsRegistered = false;
        this.mIsChromebook = SDLActivity.isChromebook();
        this.mHasEnabledNotifications = false;
        this.mHasSeenInputUpdate = false;
    }

    String getIdentifier() {
        return String.format("SteamController.%s", this.mDevice.getAddress());
    }

    BluetoothGatt getGatt() {
        return this.mGatt;
    }

    private BluetoothGatt connectGatt(boolean z) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                return this.mDevice.connectGatt(this.mManager.getContext(), z, this, 2);
            } catch (Exception unused) {
                return this.mDevice.connectGatt(this.mManager.getContext(), z, this);
            }
        }
        return this.mDevice.connectGatt(this.mManager.getContext(), z, this);
    }

    private BluetoothGatt connectGatt() {
        return connectGatt(false);
    }

    protected int getConnectionState() {
        BluetoothManager bluetoothManager;
        Context context = this.mManager.getContext();
        if (context == null || (bluetoothManager = (BluetoothManager) context.getSystemService("bluetooth")) == null) {
            return 0;
        }
        return bluetoothManager.getConnectionState(this.mDevice, 7);
    }

    void reconnect() {
        if (getConnectionState() != 2) {
            this.mGatt.disconnect();
            this.mGatt = connectGatt();
        }
    }

    protected void checkConnectionForChromebookIssue() {
        if (this.mIsChromebook) {
            int connectionState = getConnectionState();
            if (connectionState == 0) {
                Log.v(TAG, "Chromebook: We have either been disconnected, or the Chromebook BtGatt.ContextMap bug has bitten us.  Attempting a disconnect/reconnect, but we may not be able to recover.");
                this.mIsReconnecting = true;
                this.mGatt.disconnect();
                this.mGatt = connectGatt(false);
            } else if (connectionState == 1) {
                Log.v(TAG, "Chromebook: We're still trying to connect.  Waiting a bit longer.");
            } else if (connectionState == 2) {
                if (!this.mIsConnected) {
                    Log.v(TAG, "Chromebook: We are in a very bad state; the controller shows as connected in the underlying Bluetooth layer, but we never received a callback.  Forcing a reconnect.");
                    this.mIsReconnecting = true;
                    this.mGatt.disconnect();
                    this.mGatt = connectGatt(false);
                } else if (isRegistered()) {
                    Log.v(TAG, "Chromebook: We are connected, and registered.  Everything's good!");
                    return;
                } else if (this.mGatt.getServices().size() > 0) {
                    Log.v(TAG, "Chromebook: We are connected to a controller, but never got our registration.  Trying to recover.");
                    probeService(this);
                } else {
                    Log.v(TAG, "Chromebook: We are connected to a controller, but never discovered services.  Trying to recover.");
                    this.mIsReconnecting = true;
                    this.mGatt.disconnect();
                    this.mGatt = connectGatt(false);
                }
            }
            this.mHandler.postDelayed(new Runnable() { // from class: org.libsdl.app.HIDDeviceBLESteamController.1
                @Override // java.lang.Runnable
                public void run() {
                    this.checkConnectionForChromebookIssue();
                }
            }, 10000L);
        }
    }

    private boolean isRegistered() {
        return this.mIsRegistered;
    }

    private void setRegistered() {
        this.mIsRegistered = true;
    }

    private boolean probeService(HIDDeviceBLESteamController hIDDeviceBLESteamController) {
        if (isRegistered()) {
            return true;
        }
        if (!this.mIsConnected) {
            return false;
        }
        Log.v(TAG, "probeService controller=" + hIDDeviceBLESteamController);
        for (BluetoothGattService bluetoothGattService : this.mGatt.getServices()) {
            if (bluetoothGattService.getUuid().equals(steamControllerService)) {
                Log.v(TAG, "Found Valve steam controller service " + bluetoothGattService.getUuid());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    if (bluetoothGattCharacteristic.getUuid().equals(inputCharacteristicTriton_0x45)) {
                        Log.v(TAG, "Found Triton input characteristic 0x45");
                        this.mProductId = TRITON_BLE_PID;
                        this.mReportId = 69;
                        this.mInputCharacteristic = bluetoothGattCharacteristic.getUuid();
                    } else if (bluetoothGattCharacteristic.getUuid().equals(inputCharacteristicTriton_0x47)) {
                        Log.v(TAG, "Found Triton input characteristic 0x47");
                        this.mProductId = TRITON_BLE_PID;
                        this.mReportId = 71;
                        this.mInputCharacteristic = bluetoothGattCharacteristic.getUuid();
                    } else if (bluetoothGattCharacteristic.getUuid().equals(inputCharacteristicD0G)) {
                        Log.v(TAG, "Found D0G input characteristic");
                        this.mProductId = D0G_BLE2_PID;
                        this.mReportId = 3;
                        this.mInputCharacteristic = bluetoothGattCharacteristic.getUuid();
                    } else {
                        Matcher matcher = Pattern.compile("100F6C([0-9A-Z]{2})", 2).matcher(bluetoothGattCharacteristic.getUuid().toString());
                        if (matcher.find()) {
                            try {
                                int i = Integer.parseInt(matcher.group(1), 16) - 53;
                                if (i >= 128) {
                                    Log.v(TAG, "Found Triton output report 0x" + Integer.toString(i, 16));
                                    this.mOutputReportChars.put(Integer.valueOf(i), bluetoothGattCharacteristic);
                                }
                            } catch (NumberFormatException e) {
                                Log.w(TAG, "Could not parse report characteristic " + bluetoothGattCharacteristic.getUuid().toString() + ": " + e.toString());
                            }
                        }
                    }
                }
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic2 : bluetoothGattService.getCharacteristics()) {
                    if (bluetoothGattCharacteristic2.getUuid().equals(this.mInputCharacteristic) && bluetoothGattCharacteristic2.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")) != null) {
                        enableNotification(bluetoothGattCharacteristic2.getUuid());
                    }
                }
                return true;
            }
        }
        if (this.mGatt.getServices().size() == 0 && this.mIsChromebook && !this.mIsReconnecting) {
            Log.e(TAG, "Chromebook: Discovered services were empty; this almost certainly means the BtGatt.ContextMap bug has bitten us.");
            this.mIsConnected = false;
            this.mIsReconnecting = true;
            this.mGatt.disconnect();
            this.mGatt = connectGatt(false);
        }
        return false;
    }

    private void finishCurrentGattOperation() {
        GattOperation gattOperation;
        synchronized (this.mOperations) {
            gattOperation = this.mCurrentOperation;
            if (gattOperation != null) {
                this.mCurrentOperation = null;
            } else {
                gattOperation = null;
            }
        }
        if (gattOperation != null && !gattOperation.finish()) {
            this.mOperations.addFirst(gattOperation);
        }
        executeNextGattOperation();
    }

    private void executeNextGattOperation() {
        synchronized (this.mOperations) {
            if (this.mCurrentOperation != null) {
                return;
            }
            if (this.mOperations.isEmpty()) {
                return;
            }
            this.mCurrentOperation = this.mOperations.removeFirst();
            Runnable runnable = new Runnable() { // from class: org.libsdl.app.HIDDeviceBLESteamController.2
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (HIDDeviceBLESteamController.this.mOperations) {
                        if (HIDDeviceBLESteamController.this.mCurrentOperation == null) {
                            Log.e(HIDDeviceBLESteamController.TAG, "Current operation null in executor?");
                        } else {
                            HIDDeviceBLESteamController.this.mCurrentOperation.run();
                        }
                    }
                }
            };
            if (this.mCurrentOperation.getDelayMs() == 0) {
                this.mHandler.post(runnable);
            } else {
                this.mHandler.postDelayed(runnable, this.mCurrentOperation.getDelayMs());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueGattOperation(GattOperation gattOperation) {
        synchronized (this.mOperations) {
            this.mOperations.add(gattOperation);
        }
        executeNextGattOperation();
    }

    private void enableNotification(final UUID uuid) {
        queueGattOperation(GattOperation.enableNotification(this.mGatt, uuid, 500));
        this.mHandler.postDelayed(new Runnable() { // from class: org.libsdl.app.HIDDeviceBLESteamController.3
            @Override // java.lang.Runnable
            public void run() {
                if (this.mHasEnabledNotifications) {
                    return;
                }
                if (!this.mHasSeenInputUpdate) {
                    this.queueGattOperation(GattOperation.enableNotification(this.mGatt, uuid, 500));
                } else {
                    Log.w(HIDDeviceBLESteamController.TAG, "WriteDescriptor has never returned, but we've seen input reports. Moving on with controller initialization.");
                    this.mHasEnabledNotifications = true;
                    this.enableValveMode();
                }
            }
        }, 1000L);
    }

    void writeCharacteristic(UUID uuid, byte[] bArr) {
        queueGattOperation(GattOperation.writeCharacteristic(this.mGatt, uuid, bArr));
    }

    void readCharacteristic(UUID uuid) {
        queueGattOperation(GattOperation.readCharacteristic(this.mGatt, uuid));
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onConnectionStateChange(final BluetoothGatt bluetoothGatt, int i, int i2) {
        this.mIsReconnecting = false;
        if (i2 != 2) {
            if (i2 == 0) {
                this.mIsConnected = false;
            }
        } else {
            this.mIsConnected = true;
            if (isRegistered()) {
                return;
            }
            this.mHandler.post(new Runnable() { // from class: org.libsdl.app.HIDDeviceBLESteamController.4
                @Override // java.lang.Runnable
                public void run() {
                    if (HIDDeviceBLESteamController.this.mManager == null) {
                        return;
                    }
                    bluetoothGatt.discoverServices();
                }
            });
        }
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
        if (i == 0) {
            if (bluetoothGatt.getServices().size() == 0) {
                Log.v(TAG, "onServicesDiscovered returned zero services; something has gone horribly wrong down in Android's Bluetooth stack.");
                this.mIsReconnecting = true;
                this.mIsConnected = false;
                bluetoothGatt.disconnect();
                this.mGatt = connectGatt(false);
                return;
            }
            if (getProductId() == TRITON_BLE_PID) {
                this.mGatt.requestMtu(517);
            }
            probeService(this);
        }
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (bluetoothGattCharacteristic.getUuid().equals(reportCharacteristic) && !this.mFrozen) {
            this.mManager.HIDDeviceReportResponse(getId(), bluetoothGattCharacteristic.getValue());
        }
        finishCurrentGattOperation();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (bluetoothGattCharacteristic.getUuid().equals(reportCharacteristic) && !isRegistered()) {
            Log.v(TAG, "Registering Steam Controller with ID: " + getId());
            this.mManager.HIDDeviceConnected(getId(), getIdentifier(), getVendorId(), getProductId(), getSerialNumber(), getVersion(), getManufacturerName(), getProductName(), 0, 0, 0, 0, true, this.mReportId);
            setRegistered();
        }
        finishCurrentGattOperation();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (!bluetoothGattCharacteristic.getUuid().equals(this.mInputCharacteristic) || this.mFrozen) {
            return;
        }
        this.mHasSeenInputUpdate = true;
        this.mManager.HIDDeviceInputReport(getId(), bluetoothGattCharacteristic.getValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void enableValveMode() {
        BluetoothGattCharacteristic characteristic;
        BluetoothGattService service = this.mGatt.getService(steamControllerService);
        if (service == null || (characteristic = service.getCharacteristic(reportCharacteristic)) == null) {
            return;
        }
        if (getProductId() == TRITON_BLE_PID) {
            Log.v(TAG, "Registering Triton Steam Controller with ID: " + getId());
            this.mManager.HIDDeviceConnected(getId(), getIdentifier(), getVendorId(), getProductId(), getSerialNumber(), getVersion(), getManufacturerName(), getProductName(), 0, 0, 0, 0, true, this.mReportId);
            setRegistered();
            return;
        }
        Log.v(TAG, "Writing report characteristic to enter valve mode");
        characteristic.setValue(enterValveMode);
        this.mGatt.writeCharacteristic(characteristic);
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
        if (bluetoothGattDescriptor.getCharacteristic().getUuid().equals(this.mInputCharacteristic)) {
            this.mHasEnabledNotifications = true;
            enableValveMode();
        }
        finishCurrentGattOperation();
    }

    @Override // org.libsdl.app.HIDDevice
    public int getId() {
        return this.mDeviceId;
    }

    @Override // org.libsdl.app.HIDDevice
    public int getProductId() {
        int i = this.mProductId;
        if (i > 0) {
            return i;
        }
        if (this.mDevice.getName().startsWith("Steam Ctrl")) {
            this.mProductId = TRITON_BLE_PID;
        } else {
            this.mProductId = D0G_BLE2_PID;
        }
        return this.mProductId;
    }

    @Override // org.libsdl.app.HIDDevice
    public String getSerialNumber() {
        return "12345";
    }

    @Override // org.libsdl.app.HIDDevice
    public String getManufacturerName() {
        return "Valve Corporation";
    }

    @Override // org.libsdl.app.HIDDevice
    public String getProductName() {
        return "Steam Controller";
    }

    @Override // org.libsdl.app.HIDDevice
    public int writeReport(byte[] bArr, boolean z) {
        if (!isRegistered()) {
            Log.e(TAG, "Attempted writeReport before Steam Controller is registered!");
            if (this.mIsConnected) {
                probeService(this);
            }
            return -1;
        }
        if (z) {
            writeCharacteristic(reportCharacteristic, Arrays.copyOfRange(bArr, 1, bArr.length - 1));
            return bArr.length;
        }
        if (getProductId() == D0G_BLE2_PID) {
            writeCharacteristic(reportCharacteristic, bArr);
            return bArr.length;
        }
        if (bArr.length > 0) {
            int i = bArr[0] & 255;
            BluetoothGattCharacteristic bluetoothGattCharacteristic = this.mOutputReportChars.get(Integer.valueOf(i));
            if (bluetoothGattCharacteristic != null) {
                writeCharacteristic(bluetoothGattCharacteristic.getUuid(), Arrays.copyOfRange(bArr, 1, bArr.length - 1));
                return bArr.length;
            }
            Log.w(TAG, "Got report write request for unknown report type 0x" + Integer.toString(i, 16));
        }
        return -1;
    }

    @Override // org.libsdl.app.HIDDevice
    public boolean readReport(byte[] bArr, boolean z) {
        if (isRegistered()) {
            if (!z) {
                return false;
            }
            readCharacteristic(reportCharacteristic);
            return true;
        }
        Log.e(TAG, "Attempted readReport before Steam Controller is registered!");
        if (this.mIsConnected) {
            probeService(this);
        }
        return false;
    }

    @Override // org.libsdl.app.HIDDevice
    public void setFrozen(boolean z) {
        this.mFrozen = z;
    }

    @Override // org.libsdl.app.HIDDevice
    public void shutdown() {
        close();
        BluetoothGatt bluetoothGatt = this.mGatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            this.mGatt = null;
        }
        this.mManager = null;
        this.mIsRegistered = false;
        this.mIsConnected = false;
        this.mOperations.clear();
    }
}
