package it.innove;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes3.dex */
public class DefaultScanManager extends ScanManager {
    private boolean isScanning;
    private final ScanCallback mScanCallback;

    public DefaultScanManager(ReactApplicationContext reactApplicationContext, BleManager bleManager) {
        super(reactApplicationContext, bleManager);
        this.isScanning = false;
        this.mScanCallback = new ScanCallback() { // from class: it.innove.DefaultScanManager.2
            @Override // android.bluetooth.le.ScanCallback
            public void onScanResult(int i, final ScanResult scanResult) {
                UiThreadUtil.runOnUiThread(new Runnable() { // from class: it.innove.DefaultScanManager.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        DefaultScanManager.this.onDiscoveredPeripheral(scanResult);
                    }
                });
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onBatchScanResults(final List<ScanResult> list) {
                UiThreadUtil.runOnUiThread(new Runnable() { // from class: it.innove.DefaultScanManager.2.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (list.isEmpty()) {
                            return;
                        }
                        Iterator it2 = list.iterator();
                        while (it2.hasNext()) {
                            DefaultScanManager.this.onDiscoveredPeripheral((ScanResult) it2.next());
                        }
                    }
                });
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int i) {
                DefaultScanManager.this.isScanning = false;
                WritableMap writableMapCreateMap = Arguments.createMap();
                writableMapCreateMap.putInt("status", i);
                DefaultScanManager.this.bleManager.emitOnStopScan(writableMapCreateMap);
            }
        };
    }

    @Override // it.innove.ScanManager
    public void stopScan(Callback callback) {
        this.scanSessionId.incrementAndGet();
        BluetoothLeScanner bluetoothLeScanner = getBluetoothAdapter().getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(this.mScanCallback);
        }
        this.isScanning = false;
        callback.invoke(new Object[0]);
    }

    @Override // it.innove.ScanManager
    public void scan(ReadableArray readableArray, final int i, ReadableMap readableMap, Callback callback) {
        ReadableMap map;
        ScanSettings.Builder builder = new ScanSettings.Builder();
        ArrayList arrayList = new ArrayList();
        if (readableMap.hasKey("legacy")) {
            builder.setLegacy(readableMap.getBoolean("legacy"));
        }
        if (readableMap.hasKey("scanMode")) {
            builder.setScanMode(readableMap.getInt("scanMode"));
        }
        if (readableMap.hasKey("numberOfMatches")) {
            builder.setNumOfMatches(readableMap.getInt("numberOfMatches"));
        }
        if (readableMap.hasKey("matchMode")) {
            builder.setMatchMode(readableMap.getInt("matchMode"));
        }
        if (readableMap.hasKey("callbackType")) {
            builder.setCallbackType(readableMap.getInt("callbackType"));
        }
        if (readableMap.hasKey("reportDelay")) {
            builder.setReportDelay(readableMap.getInt("reportDelay"));
        }
        if (readableMap.hasKey("phy")) {
            int i2 = readableMap.getInt("phy");
            if (i2 == 3 && getBluetoothAdapter().isLeCodedPhySupported()) {
                builder.setPhy(3);
            }
            if (i2 == 2 && getBluetoothAdapter().isLe2MPhySupported()) {
                builder.setPhy(2);
            }
        }
        if (readableArray.size() > 0) {
            for (int i3 = 0; i3 < readableArray.size(); i3++) {
                arrayList.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUIDHelper.uuidFromString(readableArray.getString(i3)))).build());
                Log.d(BleManager.LOG_TAG, "Filter service: " + readableArray.getString(i3));
            }
        }
        if (readableMap.hasKey("exactAdvertisingName")) {
            ArrayList<Object> arrayList2 = readableMap.getArray("exactAdvertisingName").toArrayList();
            Log.d(BleManager.LOG_TAG, "Filter on advertising names:" + arrayList2);
            Iterator<Object> it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                arrayList.add(new ScanFilter.Builder().setDeviceName(it2.next().toString()).build());
            }
        }
        if (readableMap.hasKey("manufacturerData") && (map = readableMap.getMap("manufacturerData")) != null && map.hasKey("manufacturerId")) {
            int i4 = map.getInt("manufacturerId");
            ReadableArray array = map.getArray("manufacturerData");
            ReadableArray array2 = map.getArray("manufacturerDataMask");
            byte[] bArr = new byte[0];
            byte[] bArr2 = new byte[0];
            if (array != null) {
                bArr = new byte[array.size()];
                for (int i5 = 0; i5 < array.size(); i5++) {
                    bArr[i5] = Integer.valueOf(array.getInt(i5)).byteValue();
                }
            }
            if (array2 != null) {
                bArr2 = new byte[array2.size()];
                for (int i6 = 0; i6 < array2.size(); i6++) {
                    bArr2[i6] = Integer.valueOf(array2.getInt(i6)).byteValue();
                }
            }
            if (bArr.length != bArr2.length) {
                callback.invoke("manufacturerData and manufacturerDataMask must have the same length");
                return;
            } else {
                Log.d(BleManager.LOG_TAG, String.format("Filter on manufacturerId: %d; manufacturerData: %s; manufacturerDataMask: %s", Integer.valueOf(i4), Arrays.toString(bArr), Arrays.toString(bArr2)));
                arrayList.add(new ScanFilter.Builder().setManufacturerData(i4, bArr, bArr2).build());
            }
        }
        getBluetoothAdapter().getBluetoothLeScanner().startScan(arrayList, builder.build(), this.mScanCallback);
        this.isScanning = true;
        if (i > 0) {
            new Thread() { // from class: it.innove.DefaultScanManager.1
                private final int currentScanSession;

                {
                    this.currentScanSession = DefaultScanManager.this.scanSessionId.incrementAndGet();
                }

                @Override // java.lang.Thread, java.lang.Runnable
                public void run() throws InterruptedException {
                    try {
                        Thread.sleep(i * 1000);
                    } catch (InterruptedException unused) {
                    }
                    UiThreadUtil.runOnUiThread(new Runnable() { // from class: it.innove.DefaultScanManager.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            BluetoothAdapter bluetoothAdapter = DefaultScanManager.this.getBluetoothAdapter();
                            if (DefaultScanManager.this.scanSessionId.intValue() == C38001.this.currentScanSession) {
                                if (bluetoothAdapter.getState() == 12) {
                                    BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                                    if (bluetoothLeScanner != null) {
                                        bluetoothLeScanner.stopScan(DefaultScanManager.this.mScanCallback);
                                    }
                                    DefaultScanManager.this.isScanning = false;
                                }
                                WritableMap writableMapCreateMap = Arguments.createMap();
                                writableMapCreateMap.putInt("status", 10);
                                DefaultScanManager.this.bleManager.emitOnStopScan(writableMapCreateMap);
                            }
                        }
                    });
                }
            }.start();
        }
        callback.invoke(new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDiscoveredPeripheral(ScanResult scanResult) {
        String string;
        ScanRecord scanRecord = scanResult.getScanRecord();
        if (scanRecord != null) {
            string = scanRecord.getDeviceName();
        } else if (ActivityCompat.checkSelfPermission(this.context, "android.permission.BLUETOOTH_CONNECT") == 0) {
            string = scanResult.getDevice().getName();
        } else {
            string = scanResult.toString();
        }
        Log.i(BleManager.LOG_TAG, "DiscoverPeripheral: " + string);
        DefaultPeripheral defaultPeripheral = (DefaultPeripheral) this.bleManager.getPeripheral(scanResult.getDevice());
        if (defaultPeripheral == null) {
            defaultPeripheral = new DefaultPeripheral(this.bleManager, scanResult);
        } else {
            defaultPeripheral.updateData(scanResult);
            defaultPeripheral.updateRssi(scanResult.getRssi());
        }
        this.bleManager.savePeripheral(defaultPeripheral);
        this.bleManager.emitOnDiscoverPeripheral(defaultPeripheral.asWritableMap());
    }

    @Override // it.innove.ScanManager
    public boolean isScanning() {
        return this.isScanning;
    }

    @Override // it.innove.ScanManager
    public void setScanning(boolean z) {
        this.isScanning = z;
    }
}
