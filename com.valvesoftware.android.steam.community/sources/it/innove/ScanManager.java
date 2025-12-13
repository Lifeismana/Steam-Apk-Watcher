package it.innove;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes3.dex */
public abstract class ScanManager {
    protected BleManager bleManager;
    protected BluetoothAdapter bluetoothAdapter;
    protected Context context;
    protected ReactContext reactContext;
    protected AtomicInteger scanSessionId = new AtomicInteger();

    public abstract boolean isScanning();

    public abstract void scan(ReadableArray readableArray, int i, ReadableMap readableMap, Callback callback);

    public abstract void setScanning(boolean z);

    public abstract void stopScan(Callback callback);

    public ScanManager(ReactApplicationContext reactApplicationContext, BleManager bleManager) {
        this.context = reactApplicationContext;
        this.reactContext = reactApplicationContext;
        this.bleManager = bleManager;
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        if (this.bluetoothAdapter == null) {
            this.bluetoothAdapter = ((BluetoothManager) this.context.getSystemService("bluetooth")).getAdapter();
        }
        return this.bluetoothAdapter;
    }
}
