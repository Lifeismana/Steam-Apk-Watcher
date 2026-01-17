package it.innove;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.ReactConstants;
import com.google.firebase.messaging.Constants;
import expo.modules.notifications.serverregistration.InstallationId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONException;

/* loaded from: classes3.dex */
public class Peripheral extends BluetoothGattCallback {
    private static final String CHARACTERISTIC_NOTIFICATION_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int GATT_AUTH_FAIL = 137;
    public static final int GATT_INSUFFICIENT_AUTHENTICATION = 5;
    protected volatile byte[] advertisingDataBytes;
    protected volatile int advertisingRSSI;
    private BleManager bleManager;
    private final Map<String, NotifyBufferContainer> bufferedCharacteristics;
    private final Queue<Runnable> commandQueue;
    private boolean commandQueueBusy;
    private LinkedList<Callback> connectCallbacks;
    private volatile boolean connected;
    private volatile boolean connecting;
    protected final BluetoothDevice device;
    private BluetoothGatt gatt;
    private final Handler mainHandler;
    private LinkedList<Callback> readCallbacks;
    private LinkedList<Callback> readDescriptorCallbacks;
    private LinkedList<Callback> readRSSICallbacks;
    private LinkedList<Callback> registerNotifyCallbacks;
    private LinkedList<Callback> requestMTUCallbacks;
    private LinkedList<Callback> retrieveServicesCallbacks;
    private LinkedList<Callback> writeCallbacks;
    private LinkedList<Callback> writeDescriptorCallbacks;
    private List<byte[]> writeQueue;

    public Peripheral(BluetoothDevice bluetoothDevice, int i, byte[] bArr, BleManager bleManager) {
        this.advertisingDataBytes = new byte[0];
        this.connected = false;
        this.connecting = false;
        this.connectCallbacks = new LinkedList<>();
        this.retrieveServicesCallbacks = new LinkedList<>();
        this.readCallbacks = new LinkedList<>();
        this.readDescriptorCallbacks = new LinkedList<>();
        this.writeDescriptorCallbacks = new LinkedList<>();
        this.readRSSICallbacks = new LinkedList<>();
        this.writeCallbacks = new LinkedList<>();
        this.registerNotifyCallbacks = new LinkedList<>();
        this.requestMTUCallbacks = new LinkedList<>();
        this.commandQueue = new ConcurrentLinkedQueue();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.commandQueueBusy = false;
        this.writeQueue = new ArrayList();
        this.device = bluetoothDevice;
        this.bufferedCharacteristics = new ConcurrentHashMap();
        this.advertisingRSSI = i;
        this.advertisingDataBytes = bArr;
        this.bleManager = bleManager;
    }

    public Peripheral(BluetoothDevice bluetoothDevice, BleManager bleManager) {
        this.advertisingDataBytes = new byte[0];
        this.connected = false;
        this.connecting = false;
        this.connectCallbacks = new LinkedList<>();
        this.retrieveServicesCallbacks = new LinkedList<>();
        this.readCallbacks = new LinkedList<>();
        this.readDescriptorCallbacks = new LinkedList<>();
        this.writeDescriptorCallbacks = new LinkedList<>();
        this.readRSSICallbacks = new LinkedList<>();
        this.writeCallbacks = new LinkedList<>();
        this.registerNotifyCallbacks = new LinkedList<>();
        this.requestMTUCallbacks = new LinkedList<>();
        this.commandQueue = new ConcurrentLinkedQueue();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.commandQueueBusy = false;
        this.writeQueue = new ArrayList();
        this.device = bluetoothDevice;
        this.bufferedCharacteristics = new ConcurrentHashMap();
        this.bleManager = bleManager;
    }

    private void sendConnectionEvent(BluetoothDevice bluetoothDevice, int i) {
        WritableMap writableMapCreateMap = Arguments.createMap();
        writableMapCreateMap.putString("peripheral", bluetoothDevice.getAddress());
        if (i != -1) {
            writableMapCreateMap.putInt("status", i);
        }
        this.bleManager.emitOnConnectPeripheral(writableMapCreateMap);
        Log.d(BleManager.LOG_TAG, "Peripheral connected:" + bluetoothDevice.getAddress());
    }

    private void sendDisconnectionEvent(BluetoothDevice bluetoothDevice, int i) {
        WritableMap writableMapCreateMap = Arguments.createMap();
        writableMapCreateMap.putString("peripheral", bluetoothDevice.getAddress());
        if (i != -1) {
            writableMapCreateMap.putInt("status", i);
        }
        this.bleManager.emitOnDisconnectPeripheral(writableMapCreateMap);
        Log.d(BleManager.LOG_TAG, "Peripheral disconnected:" + bluetoothDevice.getAddress());
    }

    public void connect(final Callback callback, final Activity activity, final ReadableMap readableMap) {
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$connect$0(callback, readableMap, activity);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$connect$0(Callback callback, ReadableMap readableMap, Activity activity) {
        if (!this.connected) {
            BluetoothDevice device = getDevice();
            this.connectCallbacks.addLast(callback);
            this.connecting = true;
            Log.d(BleManager.LOG_TAG, " Is Or Greater than M $mBluetoothDevice");
            boolean z = readableMap.hasKey("autoconnect") ? readableMap.getBoolean("autoconnect") : false;
            if (!z && readableMap.hasKey("phy")) {
                this.gatt = device.connectGatt(activity, false, this, 2, readableMap.getInt("phy"));
                return;
            } else {
                this.gatt = device.connectGatt(activity, z, this, 2);
                return;
            }
        }
        if (this.gatt != null) {
            callback.invoke(new Object[0]);
        } else {
            callback.invoke("BluetoothGatt is null");
        }
    }

    public void disconnect(final Callback callback, final boolean z) {
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$disconnect$1(z, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disconnect$1(boolean z, Callback callback) {
        errorAndClearAllCallbacks("Disconnect called before the command completed");
        resetQueuesAndBuffers();
        this.connected = false;
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            try {
                bluetoothGatt.disconnect();
                if (z) {
                    this.gatt.close();
                    this.gatt = null;
                    sendDisconnectionEvent(this.device, 0);
                }
                Log.d(BleManager.LOG_TAG, "Disconnect");
            } catch (Exception e) {
                sendDisconnectionEvent(this.device, 257);
                Log.d(BleManager.LOG_TAG, "Error on disconnect", e);
            }
        } else {
            Log.d(BleManager.LOG_TAG, "GATT is null");
        }
        if (callback != null) {
            callback.invoke(new Object[0]);
        }
    }

    public WritableMap asWritableMap() {
        WritableMap writableMapCreateMap = Arguments.createMap();
        WritableMap writableMapCreateMap2 = Arguments.createMap();
        try {
            writableMapCreateMap.putString("name", this.device.getName());
            writableMapCreateMap.putString("id", this.device.getAddress());
            writableMapCreateMap.putInt("rssi", this.advertisingRSSI);
            String name = this.device.getName();
            if (name != null) {
                writableMapCreateMap2.putString("localName", name);
            }
            writableMapCreateMap2.putMap(Constants.MessagePayloadKeys.RAW_DATA, byteArrayToWritableMap(this.advertisingDataBytes));
            writableMapCreateMap2.putBoolean("isConnectable", true);
            writableMapCreateMap.putMap("advertising", writableMapCreateMap2);
        } catch (Exception e) {
            Log.e(BleManager.LOG_TAG, "Unexpected error on asWritableMap", e);
        }
        return writableMapCreateMap;
    }

    public WritableMap asWritableMap(BluetoothGatt bluetoothGatt) {
        Iterator<BluetoothGattService> it2;
        WritableMap writableMapAsWritableMap = asWritableMap();
        WritableArray writableArrayCreateArray = Arguments.createArray();
        WritableArray writableArrayCreateArray2 = Arguments.createArray();
        if (this.connected && bluetoothGatt != null) {
            Iterator<BluetoothGattService> it3 = bluetoothGatt.getServices().iterator();
            while (it3.hasNext()) {
                BluetoothGattService next = it3.next();
                WritableMap writableMapCreateMap = Arguments.createMap();
                writableMapCreateMap.putString(InstallationId.LEGACY_PREFERENCES_UUID_KEY, UUIDHelper.uuidToString(next.getUuid()));
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : next.getCharacteristics()) {
                    WritableMap writableMapCreateMap2 = Arguments.createMap();
                    writableMapCreateMap2.putString(NotificationCompat.CATEGORY_SERVICE, UUIDHelper.uuidToString(next.getUuid()));
                    writableMapCreateMap2.putString("characteristic", UUIDHelper.uuidToString(bluetoothGattCharacteristic.getUuid()));
                    writableMapCreateMap2.putMap("properties", Helper.decodeProperties(bluetoothGattCharacteristic));
                    if (bluetoothGattCharacteristic.getPermissions() > 0) {
                        writableMapCreateMap2.putMap("permissions", Helper.decodePermissions(bluetoothGattCharacteristic));
                    }
                    WritableArray writableArrayCreateArray3 = Arguments.createArray();
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                        WritableMap writableMapCreateMap3 = Arguments.createMap();
                        writableMapCreateMap3.putString(InstallationId.LEGACY_PREFERENCES_UUID_KEY, UUIDHelper.uuidToString(bluetoothGattDescriptor.getUuid()));
                        if (bluetoothGattDescriptor.getValue() != null) {
                            it2 = it3;
                            writableMapCreateMap3.putString("value", Base64.encodeToString(bluetoothGattDescriptor.getValue(), 2));
                        } else {
                            it2 = it3;
                            writableMapCreateMap3.putString("value", null);
                        }
                        if (bluetoothGattDescriptor.getPermissions() > 0) {
                            writableMapCreateMap3.putMap("permissions", Helper.decodePermissions(bluetoothGattDescriptor));
                        }
                        writableArrayCreateArray3.pushMap(writableMapCreateMap3);
                        it3 = it2;
                    }
                    Iterator<BluetoothGattService> it4 = it3;
                    if (writableArrayCreateArray3.size() > 0) {
                        writableMapCreateMap2.putArray("descriptors", writableArrayCreateArray3);
                    }
                    writableArrayCreateArray2.pushMap(writableMapCreateMap2);
                    it3 = it4;
                }
                writableArrayCreateArray.pushMap(writableMapCreateMap);
            }
            writableMapAsWritableMap.putArray("services", writableArrayCreateArray);
            writableMapAsWritableMap.putArray("characteristics", writableArrayCreateArray2);
        }
        return writableMapAsWritableMap;
    }

    static WritableMap byteArrayToWritableMap(byte[] bArr) throws JSONException {
        WritableMap writableMapCreateMap = Arguments.createMap();
        writableMapCreateMap.putString("CDVType", "ArrayBuffer");
        writableMapCreateMap.putString("data", bArr != null ? Base64.encodeToString(bArr, 2) : null);
        writableMapCreateMap.putArray("bytes", bArr != null ? BleManager.bytesToWritableArray(bArr) : null);
        return writableMapCreateMap;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isConnecting() {
        return this.connecting;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onServicesDiscovered(final BluetoothGatt bluetoothGatt, final int i) {
        super.onServicesDiscovered(bluetoothGatt, i);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onServicesDiscovered$2(bluetoothGatt, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onServicesDiscovered$2(BluetoothGatt bluetoothGatt, int i) {
        if (bluetoothGatt == null) {
            Iterator<Callback> it2 = this.retrieveServicesCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("Error during service retrieval: gatt is null");
            }
        } else if (i == 0) {
            Iterator<Callback> it3 = this.retrieveServicesCallbacks.iterator();
            while (it3.hasNext()) {
                it3.next().invoke(null, asWritableMap(bluetoothGatt));
            }
        } else {
            Iterator<Callback> it4 = this.retrieveServicesCallbacks.iterator();
            while (it4.hasNext()) {
                it4.next().invoke("Error during service retrieval.");
            }
        }
        this.retrieveServicesCallbacks.clear();
        completedCommand();
    }

    public void errorAndClearAllCallbacks(String str) {
        Iterator<Callback> it2 = this.writeCallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().invoke(str);
        }
        this.writeCallbacks.clear();
        Iterator<Callback> it3 = this.retrieveServicesCallbacks.iterator();
        while (it3.hasNext()) {
            it3.next().invoke(str);
        }
        this.retrieveServicesCallbacks.clear();
        Iterator<Callback> it4 = this.readRSSICallbacks.iterator();
        while (it4.hasNext()) {
            it4.next().invoke(str);
        }
        this.readRSSICallbacks.clear();
        Iterator<Callback> it5 = this.registerNotifyCallbacks.iterator();
        while (it5.hasNext()) {
            it5.next().invoke(str);
        }
        this.registerNotifyCallbacks.clear();
        Iterator<Callback> it6 = this.requestMTUCallbacks.iterator();
        while (it6.hasNext()) {
            it6.next().invoke(str);
        }
        this.requestMTUCallbacks.clear();
        Iterator<Callback> it7 = this.readCallbacks.iterator();
        while (it7.hasNext()) {
            it7.next().invoke(str);
        }
        this.readCallbacks.clear();
        Iterator<Callback> it8 = this.readDescriptorCallbacks.iterator();
        while (it8.hasNext()) {
            it8.next().invoke(str);
        }
        this.readDescriptorCallbacks.clear();
        Iterator<Callback> it9 = this.writeDescriptorCallbacks.iterator();
        while (it9.hasNext()) {
            it9.next().invoke(str);
        }
        this.writeDescriptorCallbacks.clear();
        Iterator<Callback> it10 = this.connectCallbacks.iterator();
        while (it10.hasNext()) {
            it10.next().invoke(str);
        }
        this.connectCallbacks.clear();
    }

    public void resetQueuesAndBuffers() {
        this.writeQueue.clear();
        this.commandQueue.clear();
        this.commandQueueBusy = false;
        this.connected = false;
        clearBuffers();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onConnectionStateChange(final BluetoothGatt bluetoothGatt, final int i, final int i2) {
        Log.d(BleManager.LOG_TAG, "onConnectionStateChange to " + i2 + " on peripheral: " + this.device.getAddress() + " with status " + i);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onConnectionStateChange$3(bluetoothGatt, i, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnectionStateChange$3(BluetoothGatt bluetoothGatt, int i, int i2) {
        this.gatt = bluetoothGatt;
        if (bluetoothGatt != null && i != 0) {
            bluetoothGatt.close();
        }
        this.connecting = false;
        if (i2 == 2 && i == 0) {
            this.connected = true;
            sendConnectionEvent(this.device, i);
            Log.d(BleManager.LOG_TAG, "Connected to: " + this.device.getAddress());
            Iterator<Callback> it2 = this.connectCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke(new Object[0]);
            }
            this.connectCallbacks.clear();
            return;
        }
        if (i2 == 0 || i != 0) {
            errorAndClearAllCallbacks("Device disconnected");
            resetQueuesAndBuffers();
            BluetoothGatt bluetoothGatt2 = this.gatt;
            if (bluetoothGatt2 != null) {
                bluetoothGatt2.disconnect();
                this.gatt.close();
            }
            this.gatt = null;
            sendDisconnectionEvent(this.device, 0);
        }
    }

    public void updateRssi(int i) {
        this.advertisingRSSI = i;
    }

    public void updateData(byte[] bArr) {
        this.advertisingDataBytes = bArr;
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (Build.VERSION.SDK_INT < 33) {
            super.onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic);
            onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic, bluetoothGattCharacteristic.getValue());
        }
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        byte[] bArr2;
        if (Build.VERSION.SDK_INT >= 33) {
            super.onCharacteristicChanged(bluetoothGatt, bluetoothGattCharacteristic, bArr);
        }
        try {
            String string = bluetoothGattCharacteristic.getUuid().toString();
            String string2 = bluetoothGattCharacteristic.getService().getUuid().toString();
            NotifyBufferContainer notifyBufferContainer = this.bufferedCharacteristics.get(bufferedCharacteristicsKey(string2, string));
            while (bArr != null) {
                if (notifyBufferContainer != null) {
                    byte[] bArrPut = notifyBufferContainer.put(bArr);
                    if (!notifyBufferContainer.isBufferFull()) {
                        return;
                    }
                    byte[] bArrArray = notifyBufferContainer.items.array();
                    notifyBufferContainer.resetBuffer();
                    bArr2 = bArrPut;
                    bArr = bArrArray;
                } else {
                    bArr2 = null;
                }
                WritableMap writableMapCreateMap = Arguments.createMap();
                writableMapCreateMap.putString("peripheral", this.device.getAddress());
                writableMapCreateMap.putString("characteristic", string);
                writableMapCreateMap.putString(NotificationCompat.CATEGORY_SERVICE, string2);
                writableMapCreateMap.putArray("value", BleManager.bytesToWritableArray(bArr));
                this.bleManager.emitOnDidUpdateValueForCharacteristic(writableMapCreateMap);
                bArr = bArr2;
            }
        } catch (Exception e) {
            Log.d(BleManager.LOG_TAG, "onCharacteristicChanged ERROR: " + e);
        }
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (Build.VERSION.SDK_INT < 33) {
            super.onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, i);
            onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, bluetoothGattCharacteristic.getValue(), i);
        }
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicRead(BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final byte[] bArr, final int i) {
        if (Build.VERSION.SDK_INT >= 33) {
            super.onCharacteristicRead(bluetoothGatt, bluetoothGattCharacteristic, bArr, i);
        }
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onCharacteristicRead$4(i, bluetoothGattCharacteristic, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCharacteristicRead$4(int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
        if (i != 0) {
            if (i == 137 || i == 5) {
                Log.d(BleManager.LOG_TAG, "Read needs bonding");
            }
            Iterator<Callback> it2 = this.readCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("Error reading " + bluetoothGattCharacteristic.getUuid() + " status=" + i, null);
            }
            this.readCallbacks.clear();
        } else if (!this.readCallbacks.isEmpty()) {
            byte[] bArrCopyOf = copyOf(bArr);
            Iterator<Callback> it3 = this.readCallbacks.iterator();
            while (it3.hasNext()) {
                it3.next().invoke(null, BleManager.bytesToWritableArray(bArrCopyOf));
            }
            this.readCallbacks.clear();
        }
        completedCommand();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, final BluetoothGattCharacteristic bluetoothGattCharacteristic, final int i) {
        super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onCharacteristicWrite$5(bluetoothGattCharacteristic, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCharacteristicWrite$5(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
        if (this.writeQueue.size() > 0) {
            byte[] bArr = this.writeQueue.get(0);
            this.writeQueue.remove(0);
            doWrite(bluetoothGattCharacteristic, bArr, null);
        } else if (i != 0) {
            if (i == 137 || i == 5) {
                Log.d(BleManager.LOG_TAG, "Write needs bonding");
                return;
            }
            Iterator<Callback> it2 = this.writeCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("Error writing " + bluetoothGattCharacteristic.getUuid() + " status=" + i, null);
            }
            this.writeCallbacks.clear();
        } else if (!this.writeCallbacks.isEmpty()) {
            Iterator<Callback> it3 = this.writeCallbacks.iterator();
            while (it3.hasNext()) {
                it3.next().invoke(new Object[0]);
            }
            this.writeCallbacks.clear();
        }
        completedCommand();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, final int i) {
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDescriptorWrite$6(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDescriptorWrite$6(int i) {
        if (!this.registerNotifyCallbacks.isEmpty()) {
            if (i == 0) {
                Iterator<Callback> it2 = this.registerNotifyCallbacks.iterator();
                while (it2.hasNext()) {
                    it2.next().invoke(new Object[0]);
                }
                Log.d(BleManager.LOG_TAG, "onDescriptorWrite success");
            } else {
                Iterator<Callback> it3 = this.registerNotifyCallbacks.iterator();
                while (it3.hasNext()) {
                    it3.next().invoke("Error writing descriptor status=" + i, null);
                }
                Log.e(BleManager.LOG_TAG, "Error writing descriptor status=" + i);
            }
            this.registerNotifyCallbacks.clear();
        } else if (!this.writeDescriptorCallbacks.isEmpty()) {
            if (i == 0) {
                Iterator<Callback> it4 = this.writeDescriptorCallbacks.iterator();
                while (it4.hasNext()) {
                    it4.next().invoke(new Object[0]);
                }
                Log.d(BleManager.LOG_TAG, "onDescriptorWrite success");
            } else {
                Iterator<Callback> it5 = this.writeDescriptorCallbacks.iterator();
                while (it5.hasNext()) {
                    it5.next().invoke("Error writing descriptor status=" + i, null);
                }
                Log.e(BleManager.LOG_TAG, "Error writing descriptor status=" + i);
            }
            this.writeDescriptorCallbacks.clear();
        } else {
            Log.e(BleManager.LOG_TAG, "onDescriptorWrite with no callback");
        }
        completedCommand();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onDescriptorRead(BluetoothGatt bluetoothGatt, final BluetoothGattDescriptor bluetoothGattDescriptor, final int i) {
        super.onDescriptorRead(bluetoothGatt, bluetoothGattDescriptor, i);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onDescriptorRead$7(i, bluetoothGattDescriptor);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onDescriptorRead$7(int i, BluetoothGattDescriptor bluetoothGattDescriptor) {
        if (i != 0) {
            if (i == 137 || i == 5) {
                Log.d(BleManager.LOG_TAG, "Read needs bonding");
            }
            Iterator<Callback> it2 = this.readDescriptorCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("Error reading descriptor " + bluetoothGattDescriptor.getUuid() + " status=" + i, null);
            }
            this.readDescriptorCallbacks.clear();
        } else if (!this.readDescriptorCallbacks.isEmpty()) {
            byte[] bArrCopyOf = copyOf(bluetoothGattDescriptor.getValue());
            Iterator<Callback> it3 = this.readDescriptorCallbacks.iterator();
            while (it3.hasNext()) {
                it3.next().invoke(null, BleManager.bytesToWritableArray(bArrCopyOf));
            }
            this.readDescriptorCallbacks.clear();
        }
        completedCommand();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, final int i, final int i2) {
        super.onReadRemoteRssi(bluetoothGatt, i, i2);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onReadRemoteRssi$8(i2, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onReadRemoteRssi$8(int i, int i2) {
        if (!this.readRSSICallbacks.isEmpty()) {
            if (i == 0) {
                updateRssi(i2);
                Iterator<Callback> it2 = this.readRSSICallbacks.iterator();
                while (it2.hasNext()) {
                    it2.next().invoke(null, Integer.valueOf(i2));
                }
            } else {
                Iterator<Callback> it3 = this.readRSSICallbacks.iterator();
                while (it3.hasNext()) {
                    it3.next().invoke("Error reading RSSI status=" + i, null);
                }
            }
            this.readRSSICallbacks.clear();
        }
        completedCommand();
    }

    private String bufferedCharacteristicsKey(String str, String str2) {
        return str + "-" + str2;
    }

    private void clearBuffers() {
        Iterator<Map.Entry<String, NotifyBufferContainer>> it2 = this.bufferedCharacteristics.entrySet().iterator();
        while (it2.hasNext()) {
            it2.next().getValue().resetBuffer();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:50:0x0119  */
    /* JADX WARN: Removed duplicated region for block: B:66:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void setNotify(UUID uuid, UUID uuid2, Boolean bool, Callback callback) {
        BluetoothGatt bluetoothGatt;
        byte[] bArr;
        boolean characteristicNotification;
        if (!isConnected() || (bluetoothGatt = this.gatt) == null) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicFindNotifyCharacteristic = findNotifyCharacteristic(bluetoothGatt.getService(uuid), uuid2);
        if (bluetoothGattCharacteristicFindNotifyCharacteristic == null) {
            callback.invoke("Characteristic " + uuid2 + " not found");
            completedCommand();
            return;
        }
        if (!this.gatt.setCharacteristicNotification(bluetoothGattCharacteristicFindNotifyCharacteristic, bool.booleanValue())) {
            callback.invoke("Failed to register notification for " + uuid2);
            completedCommand();
            return;
        }
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristicFindNotifyCharacteristic.getDescriptor(UUIDHelper.uuidFromString(CHARACTERISTIC_NOTIFICATION_CONFIG));
        if (descriptor == null) {
            callback.invoke("Set notification failed for " + uuid2);
            completedCommand();
            return;
        }
        if ((bluetoothGattCharacteristicFindNotifyCharacteristic.getProperties() & 16) != 0) {
            Log.d(BleManager.LOG_TAG, "Characteristic " + uuid2 + " set NOTIFY");
            bArr = bool.booleanValue() ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        } else if ((bluetoothGattCharacteristicFindNotifyCharacteristic.getProperties() & 32) != 0) {
            Log.d(BleManager.LOG_TAG, "Characteristic " + uuid2 + " set INDICATE");
            bArr = bool.booleanValue() ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        } else {
            String str = "Characteristic " + uuid2 + " does not have NOTIFY or INDICATE property set";
            Log.d(BleManager.LOG_TAG, str);
            callback.invoke(str);
            completedCommand();
            return;
        }
        if (!bool.booleanValue()) {
            bArr = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        }
        try {
            characteristicNotification = this.gatt.setCharacteristicNotification(bluetoothGattCharacteristicFindNotifyCharacteristic, bool.booleanValue());
        } catch (Exception e) {
            e = e;
        }
        try {
            this.registerNotifyCallbacks.addLast(callback);
            if (Build.VERSION.SDK_INT >= 33) {
                characteristicNotification &= this.gatt.writeDescriptor(descriptor, bArr) == 0;
            } else {
                descriptor.setValue(bArr);
                characteristicNotification &= this.gatt.writeDescriptor(descriptor);
            }
        } catch (Exception e2) {
            e = e2;
            z = characteristicNotification;
            Log.d(BleManager.LOG_TAG, "Exception in setNotify", e);
            characteristicNotification = z;
            if (characteristicNotification) {
            }
        }
        if (characteristicNotification) {
            Iterator<Callback> it2 = this.registerNotifyCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("writeDescriptor failed for descriptor: " + descriptor.getUuid(), null);
            }
            this.registerNotifyCallbacks.clear();
            completedCommand();
        }
    }

    public void registerNotify(final UUID uuid, final UUID uuid2, final Integer num, final Callback callback) {
        if (enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$registerNotify$9(num, uuid, uuid2, callback);
            }
        })) {
            return;
        }
        Log.e(BleManager.LOG_TAG, "Could not enqueue setNotify command to register notify");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerNotify$9(Integer num, UUID uuid, UUID uuid2, Callback callback) {
        Log.d(BleManager.LOG_TAG, "registerNotify");
        if (num.intValue() > 1) {
            Log.d(BleManager.LOG_TAG, "registerNotify using buffer");
            this.bufferedCharacteristics.put(bufferedCharacteristicsKey(uuid.toString(), uuid2.toString()), new NotifyBufferContainer(num.intValue()));
        }
        setNotify(uuid, uuid2, true, callback);
    }

    public void removeNotify(final UUID uuid, final UUID uuid2, final Callback callback) {
        if (enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeNotify$10(uuid, uuid2, callback);
            }
        })) {
            return;
        }
        Log.e(BleManager.LOG_TAG, "Could not enqueue setNotify command to remove notify");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeNotify$10(UUID uuid, UUID uuid2, Callback callback) {
        Log.d(BleManager.LOG_TAG, "removeNotify");
        String strBufferedCharacteristicsKey = bufferedCharacteristicsKey(uuid.toString(), uuid2.toString());
        if (this.bufferedCharacteristics.containsKey(strBufferedCharacteristicsKey)) {
            this.bufferedCharacteristics.get(strBufferedCharacteristicsKey);
            this.bufferedCharacteristics.remove(strBufferedCharacteristicsKey);
        }
        setNotify(uuid, uuid2, false, callback);
    }

    private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid) {
        try {
            List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {
                if ((bluetoothGattCharacteristic.getProperties() & 16) != 0 && uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                    return bluetoothGattCharacteristic;
                }
            }
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic2 : characteristics) {
                if ((bluetoothGattCharacteristic2.getProperties() & 32) != 0 && uuid.equals(bluetoothGattCharacteristic2.getUuid())) {
                    return bluetoothGattCharacteristic2;
                }
            }
            return bluetoothGattService.getCharacteristic(uuid);
        } catch (Exception e) {
            Log.e(BleManager.LOG_TAG, "Error retriving characteristic " + uuid, e);
            return null;
        }
    }

    public void read(final UUID uuid, final UUID uuid2, final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$read$11(callback, uuid, uuid2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$read$11(Callback callback, UUID uuid, UUID uuid2) {
        BluetoothGatt bluetoothGatt;
        if (!isConnected() || (bluetoothGatt = this.gatt) == null) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicFindReadableCharacteristic = findReadableCharacteristic(bluetoothGatt.getService(uuid), uuid2);
        if (bluetoothGattCharacteristicFindReadableCharacteristic == null) {
            callback.invoke("Characteristic " + uuid2 + " not found.", null);
            completedCommand();
            return;
        }
        this.readCallbacks.addLast(callback);
        if (this.gatt.readCharacteristic(bluetoothGattCharacteristicFindReadableCharacteristic)) {
            return;
        }
        Iterator<Callback> it2 = this.readCallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().invoke("Read failed", null);
        }
        this.readCallbacks.clear();
        completedCommand();
    }

    public void readDescriptor(final UUID uuid, final UUID uuid2, final UUID uuid3, final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$readDescriptor$12(callback, uuid, uuid2, uuid3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readDescriptor$12(Callback callback, UUID uuid, UUID uuid2, UUID uuid3) {
        BluetoothGatt bluetoothGatt;
        if (!isConnected() || (bluetoothGatt = this.gatt) == null) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicFindReadableCharacteristic = findReadableCharacteristic(bluetoothGatt.getService(uuid), uuid2);
        if (bluetoothGattCharacteristicFindReadableCharacteristic == null) {
            callback.invoke("Characteristic " + uuid2 + " not found.", null);
            completedCommand();
            return;
        }
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristicFindReadableCharacteristic.getDescriptor(uuid3);
        if (descriptor == null) {
            callback.invoke("Read descriptor failed for " + uuid3, null);
            completedCommand();
            return;
        }
        if ((descriptor.getPermissions() & 7) != 0) {
            callback.invoke("Read descriptor failed for " + uuid3 + ": Descriptor is missing read permission", null);
            completedCommand();
            return;
        }
        this.readDescriptorCallbacks.addLast(callback);
        if (this.gatt.readDescriptor(descriptor)) {
            return;
        }
        Iterator<Callback> it2 = this.readDescriptorCallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().invoke("Reading descriptor failed", null);
        }
        this.readDescriptorCallbacks.clear();
        completedCommand();
    }

    public void writeDescriptor(final UUID uuid, final UUID uuid2, final UUID uuid3, final byte[] bArr, final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$writeDescriptor$13(callback, uuid, uuid2, uuid3, bArr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$writeDescriptor$13(Callback callback, UUID uuid, UUID uuid2, UUID uuid3, byte[] bArr) {
        BluetoothGatt bluetoothGatt;
        boolean zWriteDescriptor;
        if (!isConnected() || (bluetoothGatt = this.gatt) == null) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicFindCharacteristic = findCharacteristic(bluetoothGatt.getService(uuid), uuid2);
        if (bluetoothGattCharacteristicFindCharacteristic == null) {
            callback.invoke("Characteristic " + uuid2 + " not found.");
            completedCommand();
            return;
        }
        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristicFindCharacteristic.getDescriptor(uuid3);
        if (descriptor == null) {
            callback.invoke("Read descriptor failed for " + uuid3, null);
            completedCommand();
            return;
        }
        this.writeDescriptorCallbacks.add(callback);
        if (Build.VERSION.SDK_INT >= 33) {
            zWriteDescriptor = this.gatt.writeDescriptor(descriptor, bArr) == 0;
        } else {
            descriptor.setValue(bArr);
            zWriteDescriptor = this.gatt.writeDescriptor(descriptor);
        }
        if (!zWriteDescriptor) {
            Iterator<Callback> it2 = this.writeDescriptorCallbacks.iterator();
            while (it2.hasNext()) {
                it2.next().invoke("writeDescriptor failed for descriptor: " + descriptor.getUuid(), null);
            }
            this.writeDescriptorCallbacks.clear();
        }
        completedCommand();
    }

    private byte[] copyOf(byte[] bArr) {
        if (bArr == null) {
            return new byte[0];
        }
        int length = bArr.length;
        byte[] bArr2 = new byte[length];
        System.arraycopy(bArr, 0, bArr2, 0, length);
        return bArr2;
    }

    private boolean enqueue(Runnable runnable) {
        boolean zAdd = this.commandQueue.add(runnable);
        if (zAdd) {
            nextCommand();
        } else {
            Log.d(BleManager.LOG_TAG, "could not enqueue command");
        }
        return zAdd;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void completedCommand() {
        this.commandQueue.poll();
        this.commandQueueBusy = false;
        nextCommand();
    }

    private void nextCommand() {
        synchronized (this) {
            if (this.commandQueueBusy) {
                Log.d(BleManager.LOG_TAG, "Command queue busy");
                return;
            }
            final Runnable runnablePeek = this.commandQueue.peek();
            if (runnablePeek == null) {
                Log.d(BleManager.LOG_TAG, "Command queue empty");
                return;
            }
            if (this.gatt == null) {
                Log.d(BleManager.LOG_TAG, "Error, gatt is null. Fill all callbacks with an error");
                errorAndClearAllCallbacks("Gatt is null");
                resetQueuesAndBuffers();
            } else {
                this.commandQueueBusy = true;
                this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            runnablePeek.run();
                        } catch (Exception unused) {
                            Log.d(BleManager.LOG_TAG, "Error, command exception");
                            Peripheral.this.completedCommand();
                        }
                    }
                });
            }
        }
    }

    public void readRSSI(final Callback callback) {
        if (enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$readRSSI$14(callback);
            }
        })) {
            return;
        }
        Log.d(BleManager.LOG_TAG, "Could not queue readRemoteRssi command");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$readRSSI$14(Callback callback) {
        if (!isConnected()) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        if (this.gatt == null) {
            callback.invoke("BluetoothGatt is null", null);
            completedCommand();
            return;
        }
        this.readRSSICallbacks.addLast(callback);
        if (this.gatt.readRemoteRssi()) {
            return;
        }
        Iterator<Callback> it2 = this.readRSSICallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().invoke("Read RSSI failed", null);
        }
        this.readRSSICallbacks.clear();
        completedCommand();
    }

    public void refreshCache(final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$refreshCache$15(callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshCache$15(Callback callback) {
        BluetoothGatt bluetoothGatt;
        try {
            try {
                bluetoothGatt = this.gatt;
            } catch (Exception e) {
                Log.e(ReactConstants.TAG, "An exception occured while refreshing device");
                callback.invoke(e.getMessage());
            }
            if (bluetoothGatt == null) {
                throw new Exception("gatt is null");
            }
            callback.invoke(null, Boolean.valueOf(((Boolean) bluetoothGatt.getClass().getMethod("refresh", new Class[0]).invoke(this.gatt, new Object[0])).booleanValue()));
        } finally {
            completedCommand();
        }
    }

    public void retrieveServices(final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$retrieveServices$16(callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$retrieveServices$16(Callback callback) {
        if (!isConnected()) {
            callback.invoke("Device is not connected", null);
            completedCommand();
        } else if (this.gatt == null) {
            callback.invoke("BluetoothGatt is null", null);
            completedCommand();
        } else {
            this.retrieveServicesCallbacks.addLast(callback);
            this.gatt.discoverServices();
        }
    }

    private BluetoothGattCharacteristic findReadableCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid) {
        if (bluetoothGattService == null) {
            return null;
        }
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
            if ((bluetoothGattCharacteristic.getProperties() & 2) != 0 && uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                return bluetoothGattCharacteristic;
            }
        }
        return bluetoothGattService.getCharacteristic(uuid);
    }

    private BluetoothGattCharacteristic findCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid) {
        if (bluetoothGattService != null) {
            return bluetoothGattService.getCharacteristic(uuid);
        }
        return null;
    }

    public boolean doWrite(final BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr, final Callback callback) {
        final byte[] bArrCopyOf = copyOf(bArr);
        return enqueue(new Runnable() { // from class: it.innove.Peripheral.2
            @Override // java.lang.Runnable
            public void run() {
                bluetoothGattCharacteristic.setValue(bArrCopyOf);
                if (bluetoothGattCharacteristic.getWriteType() == 2 && callback != null) {
                    Peripheral.this.writeCallbacks.addLast(callback);
                }
                if (Peripheral.this.gatt.writeCharacteristic(bluetoothGattCharacteristic)) {
                    return;
                }
                Iterator it2 = Peripheral.this.writeCallbacks.iterator();
                while (it2.hasNext()) {
                    ((Callback) it2.next()).invoke("Write failed", null);
                }
                Peripheral.this.writeCallbacks.clear();
                Peripheral.this.completedCommand();
            }
        });
    }

    public void write(final UUID uuid, final UUID uuid2, final byte[] bArr, final Integer num, final Integer num2, final Callback callback, final int i) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() throws Exception {
                this.f$0.lambda$write$17(callback, uuid, uuid2, i, bArr, num, num2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$write$17(Callback callback, UUID uuid, UUID uuid2, int i, byte[] bArr, Integer num, Integer num2) throws Exception {
        BluetoothGatt bluetoothGatt;
        boolean z;
        byte[] bArrCopyOfRange = null;
        if (!isConnected() || (bluetoothGatt = this.gatt) == null) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristicFindWritableCharacteristic = findWritableCharacteristic(bluetoothGatt.getService(uuid), uuid2, i);
        if (bluetoothGattCharacteristicFindWritableCharacteristic == null) {
            callback.invoke("Characteristic " + uuid2 + " not found.");
            completedCommand();
            return;
        }
        bluetoothGattCharacteristicFindWritableCharacteristic.setWriteType(i);
        boolean z2 = true;
        if (bArr.length <= num.intValue()) {
            if (!doWrite(bluetoothGattCharacteristicFindWritableCharacteristic, bArr, callback)) {
                callback.invoke("Write failed");
            } else if (1 == i) {
                callback.invoke(new Object[0]);
            }
        } else {
            int length = bArr.length;
            ArrayList arrayList = new ArrayList();
            int iIntValue = 0;
            while (iIntValue < length && length - iIntValue > num.intValue()) {
                if (iIntValue == 0) {
                    bArrCopyOfRange = Arrays.copyOfRange(bArr, iIntValue, num.intValue() + iIntValue);
                } else {
                    arrayList.add(Arrays.copyOfRange(bArr, iIntValue, num.intValue() + iIntValue));
                }
                iIntValue += num.intValue();
            }
            if (iIntValue < length) {
                arrayList.add(Arrays.copyOfRange(bArr, iIntValue, bArr.length));
            }
            if (2 == i) {
                this.writeQueue.addAll(arrayList);
                if (!doWrite(bluetoothGattCharacteristicFindWritableCharacteristic, bArrCopyOfRange, callback)) {
                    this.writeQueue.clear();
                    callback.invoke("Write failed");
                }
            } else {
                try {
                    if (doWrite(bluetoothGattCharacteristicFindWritableCharacteristic, bArrCopyOfRange, callback)) {
                        z = false;
                    } else {
                        callback.invoke("Write failed");
                        z = true;
                    }
                    if (!z) {
                        Thread.sleep(num2.intValue());
                        Iterator it2 = arrayList.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                z2 = z;
                                break;
                            } else {
                                if (!doWrite(bluetoothGattCharacteristicFindWritableCharacteristic, (byte[]) it2.next(), callback)) {
                                    callback.invoke("Write failed");
                                    break;
                                }
                                Thread.sleep(num2.intValue());
                            }
                        }
                        if (!z2) {
                            callback.invoke(new Object[0]);
                        }
                    }
                } catch (InterruptedException unused) {
                    callback.invoke("Error during writing");
                }
            }
        }
        completedCommand();
    }

    public void requestConnectionPriority(final int i, final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$requestConnectionPriority$18(i, callback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestConnectionPriority$18(int i, Callback callback) {
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            callback.invoke(null, Boolean.valueOf(bluetoothGatt.requestConnectionPriority(i)));
        } else {
            callback.invoke("BluetoothGatt is null", null);
        }
        completedCommand();
    }

    public void requestMTU(final int i, final Callback callback) {
        enqueue(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$requestMTU$19(callback, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestMTU$19(Callback callback, int i) {
        if (!isConnected()) {
            callback.invoke("Device is not connected", null);
            completedCommand();
            return;
        }
        if (this.gatt == null) {
            callback.invoke("BluetoothGatt is null", null);
            completedCommand();
            return;
        }
        this.requestMTUCallbacks.addLast(callback);
        if (this.gatt.requestMtu(i)) {
            return;
        }
        Iterator<Callback> it2 = this.requestMTUCallbacks.iterator();
        while (it2.hasNext()) {
            it2.next().invoke("Request MTU failed", null);
        }
        this.requestMTUCallbacks.clear();
        completedCommand();
    }

    @Override // android.bluetooth.BluetoothGattCallback
    public void onMtuChanged(BluetoothGatt bluetoothGatt, final int i, final int i2) {
        super.onMtuChanged(bluetoothGatt, i, i2);
        this.mainHandler.post(new Runnable() { // from class: it.innove.Peripheral$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onMtuChanged$20(i2, i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onMtuChanged$20(int i, int i2) {
        if (!this.requestMTUCallbacks.isEmpty()) {
            if (i == 0) {
                Iterator<Callback> it2 = this.requestMTUCallbacks.iterator();
                while (it2.hasNext()) {
                    it2.next().invoke(null, Integer.valueOf(i2));
                }
            } else {
                Iterator<Callback> it3 = this.requestMTUCallbacks.iterator();
                while (it3.hasNext()) {
                    it3.next().invoke("Error requesting MTU status = " + i, null);
                }
            }
            this.requestMTUCallbacks.clear();
        }
        completedCommand();
    }

    private BluetoothGattCharacteristic findWritableCharacteristic(BluetoothGattService bluetoothGattService, UUID uuid, int i) throws Exception {
        int i2 = i == 1 ? 4 : 8;
        try {
            if (bluetoothGattService == null) {
                throw new Exception("Service is null.");
            }
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                if ((bluetoothGattCharacteristic.getProperties() & i2) != 0 && uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                    return bluetoothGattCharacteristic;
                }
            }
            return bluetoothGattService.getCharacteristic(uuid);
        } catch (Exception e) {
            Log.e(BleManager.LOG_TAG, "Error on findWritableCharacteristic", e);
            return null;
        }
    }

    private String generateHashKey(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return generateHashKey(bluetoothGattCharacteristic.getService().getUuid(), bluetoothGattCharacteristic);
    }

    private String generateHashKey(UUID uuid, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return uuid + "|" + bluetoothGattCharacteristic.getUuid() + "|" + bluetoothGattCharacteristic.getInstanceId();
    }
}
