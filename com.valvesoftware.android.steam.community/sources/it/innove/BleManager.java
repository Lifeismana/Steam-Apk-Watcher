package it.innove;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.common.base.Ascii;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlinx.coroutines.DebugKt;

/* loaded from: classes3.dex */
class BleManager extends NativeBleManagerSpec {
    private static final int ENABLE_REQUEST = 539;
    public static final String LOG_TAG = "RNBleManager";
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static ReadableMap moduleOptions;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BondRequest bondRequest;
    private final CompanionScanner companionScanner;
    private Context context;
    private Callback enableBluetoothCallback;
    private boolean forceLegacy;
    private final ActivityEventListener mActivityEventListener;
    private final BroadcastReceiver mReceiver;
    private final Map<String, Peripheral> peripherals;
    private ReactApplicationContext reactContext;
    private BondRequest removeBondRequest;
    private ScanManager scanManager;

    static /* synthetic */ void lambda$invalidate$0(Object[] objArr) {
    }

    @ReactMethod
    public void addListener(String str) {
    }

    @ReactMethod
    public void removeListeners(double d) {
    }

    private static class BondRequest {
        private Callback callback;
        private String pin;
        private String uuid;

        BondRequest(String str, Callback callback) {
            this.uuid = str;
            this.callback = callback;
        }

        BondRequest(String str, String str2, Callback callback) {
            this.uuid = str;
            this.pin = str2;
            this.callback = callback;
        }
    }

    public ReactApplicationContext getReactContext() {
        return this.reactContext;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        private final BleManager bleManager;

        public MyBroadcastReceiver(BleManager bleManager) {
            this.bleManager = bleManager;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice;
            BluetoothDevice bluetoothDevice2;
            String str;
            Peripheral peripheral;
            Log.d(BleManager.LOG_TAG, "onReceive");
            String action = intent.getAction();
            if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                String str2 = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
                switch (intExtra) {
                    case 10:
                        BleManager.this.clearPeripherals();
                        break;
                    case 11:
                        str2 = "turning_on";
                        break;
                    case 12:
                        str2 = "on";
                        break;
                    case 13:
                        BleManager.this.disconnectPeripherals();
                        str2 = "turning_off";
                        break;
                }
                WritableMap createMap = Arguments.createMap();
                createMap.putString("state", str2);
                Log.d(BleManager.LOG_TAG, "state: ".concat(str2));
                BleManager.this.emitOnDidUpdateState(createMap);
                return;
            }
            if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) {
                int intExtra2 = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
                int intExtra3 = intent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", Integer.MIN_VALUE);
                if (Build.VERSION.SDK_INT >= 33) {
                    bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class);
                } else {
                    bluetoothDevice2 = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                }
                switch (intExtra2) {
                    case 10:
                        str = "BOND_NONE";
                        break;
                    case 11:
                        str = "BOND_BONDING";
                        break;
                    case 12:
                        str = "BOND_BONDED";
                        break;
                    default:
                        str = "UNKNOWN";
                        break;
                }
                Log.d(BleManager.LOG_TAG, "bond state: ".concat(str));
                if (BleManager.this.bondRequest != null && BleManager.this.bondRequest.uuid.equals(bluetoothDevice2.getAddress())) {
                    if (intExtra2 == 12) {
                        BleManager.this.bondRequest.callback.invoke(new Object[0]);
                        BleManager.this.bondRequest = null;
                    } else if (intExtra2 == 10 || intExtra2 == Integer.MIN_VALUE) {
                        BleManager.this.bondRequest.callback.invoke("Bond request has been denied");
                        BleManager.this.bondRequest = null;
                    }
                }
                if (intExtra2 == 12) {
                    if (!BleManager.this.forceLegacy) {
                        peripheral = new DefaultPeripheral(bluetoothDevice2, this.bleManager);
                    } else {
                        peripheral = new Peripheral(bluetoothDevice2, this.bleManager);
                    }
                    BleManager.this.emitOnPeripheralDidBond(peripheral.asWritableMap());
                }
                if (BleManager.this.removeBondRequest != null && BleManager.this.removeBondRequest.uuid.equals(bluetoothDevice2.getAddress()) && intExtra2 == 10 && intExtra3 == 12) {
                    BleManager.this.removeBondRequest.callback.invoke(new Object[0]);
                    BleManager.this.removeBondRequest = null;
                    return;
                }
                return;
            }
            if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                if (Build.VERSION.SDK_INT >= 33) {
                    bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE", BluetoothDevice.class);
                } else {
                    bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                }
                if (BleManager.this.bondRequest == null || !BleManager.this.bondRequest.uuid.equals(bluetoothDevice.getAddress()) || BleManager.this.bondRequest.pin == null) {
                    return;
                }
                bluetoothDevice.setPin(BleManager.this.bondRequest.pin.getBytes());
                bluetoothDevice.createBond();
            }
        }
    }

    public BleManager(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        BaseActivityEventListener baseActivityEventListener = new BaseActivityEventListener() { // from class: it.innove.BleManager.1
            @Override // com.facebook.react.bridge.BaseActivityEventListener, com.facebook.react.bridge.ActivityEventListener
            public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
                Log.d(BleManager.LOG_TAG, "onActivityResult");
                if (i != BleManager.ENABLE_REQUEST || BleManager.this.enableBluetoothCallback == null) {
                    return;
                }
                if (i2 == -1) {
                    BleManager.this.enableBluetoothCallback.invoke(new Object[0]);
                } else {
                    BleManager.this.enableBluetoothCallback.invoke("User refused to enable");
                }
                BleManager.this.enableBluetoothCallback = null;
            }
        };
        this.mActivityEventListener = baseActivityEventListener;
        this.peripherals = new LinkedHashMap();
        this.mReceiver = new MyBroadcastReceiver(this) { // from class: it.innove.BleManager.2
        };
        this.context = reactApplicationContext;
        this.reactContext = reactApplicationContext;
        this.companionScanner = this.context.getPackageManager().hasSystemFeature("android.software.companion_device_setup") ? new CompanionScanner(reactApplicationContext, this) : null;
        reactApplicationContext.addActivityEventListener(baseActivityEventListener);
        Log.d(LOG_TAG, "BleManager created");
    }

    @Override // it.innove.NativeBleManagerSpec, com.facebook.react.bridge.NativeModule
    public String getName() {
        return NativeBleManagerSpec.NAME;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        if (this.bluetoothAdapter == null) {
            this.bluetoothAdapter = ((BluetoothManager) this.context.getSystemService("bluetooth")).getAdapter();
        }
        return this.bluetoothAdapter;
    }

    private BluetoothManager getBluetoothManager() {
        if (this.bluetoothManager == null) {
            this.bluetoothManager = (BluetoothManager) this.context.getSystemService("bluetooth");
        }
        return this.bluetoothManager;
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void start(ReadableMap readableMap, Callback callback) {
        Log.d(LOG_TAG, "start");
        if (getBluetoothAdapter() == null) {
            Log.d(LOG_TAG, "No bluetooth support");
            callback.invoke("No bluetooth support");
            return;
        }
        this.forceLegacy = false;
        moduleOptions = readableMap;
        if (readableMap.hasKey("forceLegacy")) {
            this.forceLegacy = readableMap.getBoolean("forceLegacy");
        }
        this.scanManager = new DefaultScanManager(this.reactContext, this);
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        IntentFilter intentFilter2 = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
        intentFilter2.setPriority(1000);
        if (Build.VERSION.SDK_INT >= 34) {
            this.context.registerReceiver(this.mReceiver, intentFilter, 2);
            this.context.registerReceiver(this.mReceiver, intentFilter2, 2);
        } else {
            this.context.registerReceiver(this.mReceiver, intentFilter);
            this.context.registerReceiver(this.mReceiver, intentFilter2);
        }
        callback.invoke(new Object[0]);
        Log.d(LOG_TAG, "BleManager initialized");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void enableBluetooth(Callback callback) {
        if (getBluetoothAdapter() == null) {
            Log.d(LOG_TAG, "No bluetooth support");
            callback.invoke("No bluetooth support");
            return;
        }
        if (!getBluetoothAdapter().isEnabled()) {
            Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            if (getCurrentActivity() == null) {
                callback.invoke("Current activity not available");
                return;
            }
            this.enableBluetoothCallback = callback;
            try {
                getCurrentActivity().startActivityForResult(intent, ENABLE_REQUEST);
                return;
            } catch (Exception unused) {
                this.enableBluetoothCallback = null;
                callback.invoke("Error starting enable bluetooth activity");
                return;
            }
        }
        callback.invoke(new Object[0]);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void scan(ReadableArray readableArray, double d, boolean z, ReadableMap readableMap, Callback callback) {
        Log.d(LOG_TAG, "scan");
        if (getBluetoothAdapter() == null) {
            Log.d(LOG_TAG, "No bluetooth support");
            callback.invoke("No bluetooth support");
            return;
        }
        if (getBluetoothAdapter().isEnabled()) {
            synchronized (this.peripherals) {
                Iterator<Map.Entry<String, Peripheral>> it2 = this.peripherals.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry<String, Peripheral> next = it2.next();
                    if (!next.getValue().isConnected() && !next.getValue().isConnecting()) {
                        it2.remove();
                    }
                }
            }
            ScanManager scanManager = this.scanManager;
            if (scanManager != null) {
                scanManager.scan(readableArray, (int) d, readableMap, callback);
            }
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void companionScan(ReadableArray readableArray, ReadableMap readableMap, Callback callback) {
        CompanionScanner companionScanner = this.companionScanner;
        if (companionScanner == null) {
            callback.invoke("not supported");
        } else {
            companionScanner.scan(readableArray, readableMap, callback);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void supportsCompanion(Callback callback) {
        callback.invoke(Boolean.valueOf(this.companionScanner != null));
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void stopScan(Callback callback) {
        Log.d(LOG_TAG, "Stop scan");
        if (getBluetoothAdapter() == null) {
            Log.d(LOG_TAG, "No bluetooth support");
            callback.invoke("No bluetooth support");
        } else {
            if (!getBluetoothAdapter().isEnabled()) {
                callback.invoke(new Object[0]);
                return;
            }
            ScanManager scanManager = this.scanManager;
            if (scanManager != null) {
                scanManager.stopScan(callback);
                WritableMap createMap = Arguments.createMap();
                createMap.putInt("status", 0);
                emitOnStopScan(createMap);
            }
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void createBond(String str, String str2, Callback callback) {
        Log.d(LOG_TAG, "Request bond to: " + str);
        Iterator<BluetoothDevice> it2 = getBluetoothAdapter().getBondedDevices().iterator();
        while (it2.hasNext()) {
            if (str.equalsIgnoreCase(it2.next().getAddress())) {
                callback.invoke(new Object[0]);
                return;
            }
        }
        Peripheral retrieveOrCreatePeripheral = retrieveOrCreatePeripheral(str);
        if (retrieveOrCreatePeripheral == null) {
            callback.invoke("Invalid peripheral uuid");
            return;
        }
        if (this.bondRequest != null) {
            callback.invoke("Only allow one bond request at a time");
        } else if (retrieveOrCreatePeripheral.getDevice().createBond()) {
            Log.d(LOG_TAG, "Request bond successful for: " + str);
            this.bondRequest = new BondRequest(str, str2, callback);
        } else {
            callback.invoke("Create bond request fail");
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void removeBond(String str, Callback callback) {
        Log.d(LOG_TAG, "Remove bond to: " + str);
        Peripheral retrieveOrCreatePeripheral = retrieveOrCreatePeripheral(str);
        if (retrieveOrCreatePeripheral == null) {
            callback.invoke("Invalid peripheral uuid");
            return;
        }
        try {
            retrieveOrCreatePeripheral.getDevice().getClass().getMethod("removeBond", null).invoke(retrieveOrCreatePeripheral.getDevice(), null);
            this.removeBondRequest = new BondRequest(str, callback);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error in remove bond: " + str, e);
            callback.invoke("Remove bond request fail");
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void connect(String str, ReadableMap readableMap, Callback callback) {
        Log.d(LOG_TAG, "Connect to: " + str);
        Peripheral retrieveOrCreatePeripheral = retrieveOrCreatePeripheral(str);
        if (retrieveOrCreatePeripheral == null) {
            callback.invoke("Invalid peripheral uuid");
        } else {
            retrieveOrCreatePeripheral.connect(callback, getCurrentActivity(), readableMap);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void disconnect(String str, boolean z, Callback callback) {
        Log.d(LOG_TAG, "Disconnect from: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            peripheral.disconnect(callback, z);
        } else {
            callback.invoke("Peripheral not found");
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void startNotificationWithBuffer(String str, String str2, String str3, double d, Callback callback) {
        Log.d(LOG_TAG, "startNotification");
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            peripheral.registerNotify(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), Integer.valueOf((int) d), callback);
        } else {
            callback.invoke("Peripheral not found");
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void startNotification(String str, String str2, String str3, Callback callback) {
        Log.d(LOG_TAG, "startNotification");
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.registerNotify(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), 1, callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void stopNotification(String str, String str2, String str3, Callback callback) {
        Log.d(LOG_TAG, "stopNotification");
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.removeNotify(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void write(String str, String str2, String str3, ReadableArray readableArray, double d, Callback callback) {
        Log.d(LOG_TAG, "Write to: " + str);
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                int size = readableArray.size();
                byte[] bArr = new byte[size];
                for (int i = 0; i < readableArray.size(); i++) {
                    bArr[i] = Integer.valueOf(readableArray.getInt(i)).byteValue();
                }
                Log.d(LOG_TAG, "Message(" + size + "): " + bytesToHex(bArr));
                peripheral.write(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), bArr, Integer.valueOf((int) d), null, callback, 2);
                return;
            }
            callback.invoke("Peripheral not connected", null);
            return;
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void writeWithoutResponse(String str, String str2, String str3, ReadableArray readableArray, double d, double d2, Callback callback) {
        Log.d(LOG_TAG, "Write without response to: " + str);
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                int size = readableArray.size();
                byte[] bArr = new byte[size];
                for (int i = 0; i < readableArray.size(); i++) {
                    bArr[i] = Integer.valueOf(readableArray.getInt(i)).byteValue();
                }
                Log.d(LOG_TAG, "Message(" + size + "): " + bytesToHex(bArr));
                peripheral.write(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), bArr, Integer.valueOf((int) d), Integer.valueOf((int) d2), callback, 1);
                return;
            }
            callback.invoke("Peripheral not connected", null);
            return;
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void read(String str, String str2, String str3, Callback callback) {
        Log.d(LOG_TAG, "Read from: " + str);
        if (str2 == null || str3 == null) {
            callback.invoke("ServiceUUID and characteristicUUID required.");
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.read(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found", null);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void readDescriptor(String str, String str2, String str3, String str4, Callback callback) {
        Log.d(LOG_TAG, "Read descriptor from: " + str);
        if (str2 == null || str3 == null || str4 == null) {
            callback.invoke("ServiceUUID, CharacteristicUUID and descriptorUUID required.", null);
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral == null) {
            callback.invoke("Peripheral not found", null);
        } else if (!peripheral.isConnected()) {
            callback.invoke("Peripheral not connected", null);
        } else {
            peripheral.readDescriptor(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), UUIDHelper.uuidFromString(str4), callback);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void writeDescriptor(String str, String str2, String str3, String str4, ReadableArray readableArray, Callback callback) {
        Log.d(LOG_TAG, "Write descriptor from: " + str);
        if (str2 == null || str3 == null || str4 == null) {
            callback.invoke("ServiceUUID, CharacteristicUUID and descriptorUUID required.", null);
            return;
        }
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral == null) {
            callback.invoke("Peripheral not found", null);
            return;
        }
        if (!peripheral.isConnected()) {
            callback.invoke("Peripheral not connected", null);
            return;
        }
        int size = readableArray.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < readableArray.size(); i++) {
            bArr[i] = Integer.valueOf(readableArray.getInt(i)).byteValue();
        }
        Log.d(LOG_TAG, "Message(" + size + "): " + bytesToHex(bArr));
        peripheral.writeDescriptor(UUIDHelper.uuidFromString(str2), UUIDHelper.uuidFromString(str3), UUIDHelper.uuidFromString(str4), bArr, callback);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void retrieveServices(String str, ReadableArray readableArray, Callback callback) {
        Log.d(LOG_TAG, "Retrieve services from: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.retrieveServices(callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found", null);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void refreshCache(String str, Callback callback) {
        Log.d(LOG_TAG, "Refreshing cache for: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.refreshCache(callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void readRSSI(String str, Callback callback) {
        Log.d(LOG_TAG, "Read RSSI from: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            if (peripheral.isConnected()) {
                peripheral.readRSSI(callback);
                return;
            } else {
                callback.invoke("Peripheral not connected", null);
                return;
            }
        }
        callback.invoke("Peripheral not found", null);
    }

    public Peripheral savePeripheral(BluetoothDevice bluetoothDevice) {
        Peripheral peripheral;
        String address = bluetoothDevice.getAddress();
        synchronized (this.peripherals) {
            if (!this.peripherals.containsKey(address)) {
                if (!this.forceLegacy) {
                    peripheral = new DefaultPeripheral(bluetoothDevice, this);
                } else {
                    peripheral = new Peripheral(bluetoothDevice, this);
                }
                this.peripherals.put(bluetoothDevice.getAddress(), peripheral);
            }
        }
        return this.peripherals.get(address);
    }

    public Peripheral getPeripheral(BluetoothDevice bluetoothDevice) {
        return this.peripherals.get(bluetoothDevice.getAddress());
    }

    public Peripheral savePeripheral(Peripheral peripheral) {
        synchronized (this.peripherals) {
            this.peripherals.put(peripheral.getDevice().getAddress(), peripheral);
        }
        return peripheral;
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void checkState(Callback callback) {
        String str;
        Log.d(LOG_TAG, "checkState");
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (!this.context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            str = "unsupported";
        } else {
            if (bluetoothAdapter != null) {
                switch (bluetoothAdapter.getState()) {
                    case 11:
                        str = "turning_on";
                        break;
                    case 12:
                        str = "on";
                        break;
                    case 13:
                        ScanManager scanManager = this.scanManager;
                        if (scanManager != null) {
                            scanManager.setScanning(false);
                        }
                        str = "turning_off";
                        break;
                    default:
                        ScanManager scanManager2 = this.scanManager;
                        if (scanManager2 != null) {
                            scanManager2.setScanning(false);
                            break;
                        }
                        break;
                }
            }
            str = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
        }
        WritableMap createMap = Arguments.createMap();
        createMap.putString("state", str);
        Log.d(LOG_TAG, "state:".concat(str));
        emitOnDidUpdateState(createMap);
        callback.invoke(str);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void isScanning(Callback callback) {
        ScanManager scanManager = this.scanManager;
        if (scanManager != null) {
            callback.invoke(null, Boolean.valueOf(scanManager.isScanning()));
        } else {
            callback.invoke(null, false);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    public void getMaximumWriteValueLengthForWithoutResponse(String str, Callback callback) {
        callback.invoke("Not implemented");
    }

    @Override // it.innove.NativeBleManagerSpec
    public void getMaximumWriteValueLengthForWithResponse(String str, Callback callback) {
        callback.invoke("Not implemented");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void setName(String str) {
        getBluetoothAdapter().setName(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearPeripherals() {
        if (this.peripherals.isEmpty()) {
            return;
        }
        synchronized (this.peripherals) {
            this.peripherals.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectPeripherals() {
        if (this.peripherals.isEmpty()) {
            return;
        }
        synchronized (this.peripherals) {
            for (Peripheral peripheral : this.peripherals.values()) {
                if (peripheral.isConnected()) {
                    peripheral.disconnect(null, true);
                }
                peripheral.errorAndClearAllCallbacks("disconnected by BleManager");
                peripheral.resetQueuesAndBuffers();
            }
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void getDiscoveredPeripherals(Callback callback) {
        Log.d(LOG_TAG, "Get discovered peripherals");
        WritableArray createArray = Arguments.createArray();
        synchronized (this.peripherals) {
            Iterator<Map.Entry<String, Peripheral>> it2 = this.peripherals.entrySet().iterator();
            while (it2.hasNext()) {
                createArray.pushMap(it2.next().getValue().asWritableMap());
            }
        }
        callback.invoke(null, createArray);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void getConnectedPeripherals(ReadableArray readableArray, Callback callback) {
        Log.d(LOG_TAG, "Get connected peripherals");
        WritableArray createArray = Arguments.createArray();
        if (getBluetoothAdapter() == null) {
            Log.d(LOG_TAG, "No bluetooth support");
            callback.invoke("No bluetooth support");
        } else {
            Iterator<BluetoothDevice> it2 = getBluetoothManager().getConnectedDevices(7).iterator();
            while (it2.hasNext()) {
                createArray.pushMap(savePeripheral(it2.next()).asWritableMap());
            }
            callback.invoke(null, createArray);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    public void isPeripheralConnected(String str, Callback callback) {
        Log.d(LOG_TAG, "Checking connection state for: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            callback.invoke(null, Boolean.valueOf(peripheral.isConnected()));
        } else {
            callback.invoke("Peripheral not found");
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void getBondedPeripherals(Callback callback) {
        Peripheral peripheral;
        Log.d(LOG_TAG, "Get bonded peripherals");
        WritableArray createArray = Arguments.createArray();
        for (BluetoothDevice bluetoothDevice : getBluetoothAdapter().getBondedDevices()) {
            if (!this.forceLegacy) {
                peripheral = new DefaultPeripheral(bluetoothDevice, this);
            } else {
                peripheral = new Peripheral(bluetoothDevice, this);
            }
            createArray.pushMap(peripheral.asWritableMap());
        }
        callback.invoke(null, createArray);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void removePeripheral(String str, Callback callback) {
        Log.d(LOG_TAG, "Removing from list: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            synchronized (this.peripherals) {
                if (peripheral.isConnected()) {
                    callback.invoke("Peripheral can not be removed while connected");
                } else {
                    this.peripherals.remove(str);
                    callback.invoke(new Object[0]);
                }
            }
            return;
        }
        callback.invoke("Peripheral not found");
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void requestConnectionPriority(String str, double d, Callback callback) {
        Log.d(LOG_TAG, "Request connection priority of " + d + " from: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            peripheral.requestConnectionPriority((int) d, callback);
        } else {
            callback.invoke("Peripheral not found", null);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void requestMTU(String str, double d, Callback callback) {
        Log.d(LOG_TAG, "Request MTU of " + d + " bytes from: " + str);
        Peripheral peripheral = this.peripherals.get(str);
        if (peripheral != null) {
            peripheral.requestMTU((int) d, callback);
        } else {
            callback.invoke("Peripheral not found", null);
        }
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void getAssociatedPeripherals(Callback callback) {
        Log.d(LOG_TAG, "Get associated peripherals");
        WritableArray createArray = Arguments.createArray();
        Iterator<String> it2 = ((CompanionDeviceManager) getCompanionDeviceManager()).getAssociations().iterator();
        while (it2.hasNext()) {
            createArray.pushMap(retrieveOrCreatePeripheral(it2.next()).asWritableMap());
        }
        callback.invoke(null, createArray);
    }

    @Override // it.innove.NativeBleManagerSpec
    @ReactMethod
    public void removeAssociatedPeripheral(String str, Callback callback) {
        Log.d(LOG_TAG, "Remove associated peripheral: " + str);
        CompanionDeviceManager companionDeviceManager = (CompanionDeviceManager) getCompanionDeviceManager();
        Iterator<String> it2 = companionDeviceManager.getAssociations().iterator();
        while (it2.hasNext()) {
            if (it2.next().equals(str)) {
                companionDeviceManager.disassociate(str);
                callback.invoke(new Object[0]);
                return;
            }
        }
        callback.invoke("device not found");
    }

    public Object getCompanionDeviceManager() {
        return this.reactContext.getCurrentActivity().getSystemService("companiondevice");
    }

    public static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[bArr.length * 2];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            int i2 = i * 2;
            char[] cArr2 = hexArray;
            cArr[i2] = cArr2[(b & 255) >>> 4];
            cArr[i2 + 1] = cArr2[b & Ascii.f437SI];
        }
        return new String(cArr);
    }

    public static WritableArray bytesToWritableArray(byte[] bArr) {
        WritableArray createArray = Arguments.createArray();
        for (byte b : bArr) {
            createArray.pushInt(b & 255);
        }
        return createArray;
    }

    private Peripheral retrieveOrCreatePeripheral(String str) {
        Peripheral peripheral;
        Peripheral peripheral2 = this.peripherals.get(str);
        if (peripheral2 == null) {
            synchronized (this.peripherals) {
                if (str != null) {
                    str = str.toUpperCase();
                }
                if (BluetoothAdapter.checkBluetoothAddress(str)) {
                    BluetoothDevice remoteDevice = this.bluetoothAdapter.getRemoteDevice(str);
                    if (!this.forceLegacy) {
                        peripheral = new DefaultPeripheral(remoteDevice, this);
                    } else {
                        peripheral = new Peripheral(remoteDevice, this);
                    }
                    this.peripherals.put(str, peripheral);
                    peripheral2 = peripheral;
                }
            }
        }
        return peripheral2;
    }

    @Override // com.facebook.react.bridge.BaseJavaModule, com.facebook.react.bridge.NativeModule, com.facebook.react.turbomodule.core.interfaces.TurboModule
    public void invalidate() {
        try {
            this.context.unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Receiver not registered or already unregistered", e);
        }
        try {
            disconnectPeripherals();
        } catch (Exception e2) {
            Log.d(LOG_TAG, "Could not disconnect peripherals", e2);
        }
        ScanManager scanManager = this.scanManager;
        if (scanManager != null) {
            scanManager.stopScan(new Callback() { // from class: it.innove.BleManager$$ExternalSyntheticLambda0
                @Override // com.facebook.react.bridge.Callback
                public final void invoke(Object[] objArr) {
                    BleManager.lambda$invalidate$0(objArr);
                }
            });
        }
    }
}
