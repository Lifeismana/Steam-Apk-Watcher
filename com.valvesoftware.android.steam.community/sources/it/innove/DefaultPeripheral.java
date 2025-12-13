package it.innove;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.ParcelUuid;
import android.util.SparseArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.firebase.messaging.Constants;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes3.dex */
public class DefaultPeripheral extends Peripheral {
    private ScanRecord advertisingData;
    private ScanResult scanResult;

    public DefaultPeripheral(BleManager bleManager, ScanResult scanResult) {
        super(scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes(), bleManager);
        this.advertisingData = scanResult.getScanRecord();
        this.scanResult = scanResult;
    }

    public DefaultPeripheral(BluetoothDevice bluetoothDevice, BleManager bleManager) {
        super(bluetoothDevice, bleManager);
    }

    @Override // it.innove.Peripheral
    public WritableMap asWritableMap() {
        WritableMap asWritableMap = super.asWritableMap();
        WritableMap createMap = Arguments.createMap();
        try {
            asWritableMap.putString("name", this.device.getName());
            asWritableMap.putString("id", this.device.getAddress());
            asWritableMap.putInt("rssi", this.advertisingRSSI);
            createMap.putMap(Constants.MessagePayloadKeys.RAW_DATA, byteArrayToWritableMap(this.advertisingDataBytes));
            ScanResult scanResult = this.scanResult;
            if (scanResult != null) {
                createMap.putBoolean("isConnectable", scanResult.isConnectable());
            }
            ScanRecord scanRecord = this.advertisingData;
            if (scanRecord != null) {
                String deviceName = scanRecord.getDeviceName();
                if (deviceName != null) {
                    createMap.putString("localName", deviceName.replace("\u0000", ""));
                }
                WritableArray createArray = Arguments.createArray();
                if (this.advertisingData.getServiceUuids() != null && this.advertisingData.getServiceUuids().size() != 0) {
                    Iterator<ParcelUuid> it2 = this.advertisingData.getServiceUuids().iterator();
                    while (it2.hasNext()) {
                        createArray.pushString(UUIDHelper.uuidToString(it2.next().getUuid()));
                    }
                }
                createMap.putArray("serviceUUIDs", createArray);
                WritableMap createMap2 = Arguments.createMap();
                if (this.advertisingData.getServiceData() != null) {
                    for (Map.Entry<ParcelUuid, byte[]> entry : this.advertisingData.getServiceData().entrySet()) {
                        if (entry.getValue() != null) {
                            createMap2.putMap(UUIDHelper.uuidToString(entry.getKey().getUuid()), byteArrayToWritableMap(entry.getValue()));
                        }
                    }
                }
                createMap.putMap("serviceData", createMap2);
                WritableMap createMap3 = Arguments.createMap();
                SparseArray<byte[]> manufacturerSpecificData = this.advertisingData.getManufacturerSpecificData();
                byte[] bArr = new byte[0];
                if (manufacturerSpecificData != null && manufacturerSpecificData.size() > 0) {
                    int keyAt = manufacturerSpecificData.keyAt(0);
                    byte[] valueAt = manufacturerSpecificData.valueAt(0);
                    createMap3.putMap(String.format("%04x", Integer.valueOf(keyAt)), byteArrayToWritableMap(valueAt));
                    ByteBuffer allocate = ByteBuffer.allocate(4);
                    allocate.putInt(keyAt);
                    byte[] array = allocate.array();
                    byte[] bArr2 = new byte[array.length + valueAt.length];
                    System.arraycopy(array, 0, bArr2, 0, array.length);
                    System.arraycopy(valueAt, 0, bArr2, array.length, valueAt.length);
                    bArr = bArr2;
                }
                createMap.putMap("manufacturerData", createMap3);
                createMap.putMap("manufacturerRawData", byteArrayToWritableMap(bArr));
                createMap.putInt("txPowerLevel", this.advertisingData.getTxPowerLevel());
            }
            asWritableMap.putMap("advertising", createMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asWritableMap;
    }

    public void updateData(ScanResult scanResult) {
        ScanRecord scanRecord = scanResult.getScanRecord();
        this.advertisingData = scanRecord;
        this.advertisingDataBytes = scanRecord.getBytes();
    }
}
