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
        WritableMap writableMapAsWritableMap = super.asWritableMap();
        WritableMap writableMapCreateMap = Arguments.createMap();
        try {
            writableMapAsWritableMap.putString("name", this.device.getName());
            writableMapAsWritableMap.putString("id", this.device.getAddress());
            writableMapAsWritableMap.putInt("rssi", this.advertisingRSSI);
            writableMapCreateMap.putMap(Constants.MessagePayloadKeys.RAW_DATA, byteArrayToWritableMap(this.advertisingDataBytes));
            ScanResult scanResult = this.scanResult;
            if (scanResult != null) {
                writableMapCreateMap.putBoolean("isConnectable", scanResult.isConnectable());
            }
            ScanRecord scanRecord = this.advertisingData;
            if (scanRecord != null) {
                String deviceName = scanRecord.getDeviceName();
                if (deviceName != null) {
                    writableMapCreateMap.putString("localName", deviceName.replace("\u0000", ""));
                }
                WritableArray writableArrayCreateArray = Arguments.createArray();
                if (this.advertisingData.getServiceUuids() != null && this.advertisingData.getServiceUuids().size() != 0) {
                    Iterator<ParcelUuid> it2 = this.advertisingData.getServiceUuids().iterator();
                    while (it2.hasNext()) {
                        writableArrayCreateArray.pushString(UUIDHelper.uuidToString(it2.next().getUuid()));
                    }
                }
                writableMapCreateMap.putArray("serviceUUIDs", writableArrayCreateArray);
                WritableMap writableMapCreateMap2 = Arguments.createMap();
                if (this.advertisingData.getServiceData() != null) {
                    for (Map.Entry<ParcelUuid, byte[]> entry : this.advertisingData.getServiceData().entrySet()) {
                        if (entry.getValue() != null) {
                            writableMapCreateMap2.putMap(UUIDHelper.uuidToString(entry.getKey().getUuid()), byteArrayToWritableMap(entry.getValue()));
                        }
                    }
                }
                writableMapCreateMap.putMap("serviceData", writableMapCreateMap2);
                WritableMap writableMapCreateMap3 = Arguments.createMap();
                SparseArray<byte[]> manufacturerSpecificData = this.advertisingData.getManufacturerSpecificData();
                byte[] bArr = new byte[0];
                if (manufacturerSpecificData != null && manufacturerSpecificData.size() > 0) {
                    int iKeyAt = manufacturerSpecificData.keyAt(0);
                    byte[] bArrValueAt = manufacturerSpecificData.valueAt(0);
                    writableMapCreateMap3.putMap(String.format("%04x", Integer.valueOf(iKeyAt)), byteArrayToWritableMap(bArrValueAt));
                    ByteBuffer byteBufferAllocate = ByteBuffer.allocate(4);
                    byteBufferAllocate.putInt(iKeyAt);
                    byte[] bArrArray = byteBufferAllocate.array();
                    byte[] bArr2 = new byte[bArrArray.length + bArrValueAt.length];
                    System.arraycopy(bArrArray, 0, bArr2, 0, bArrArray.length);
                    System.arraycopy(bArrValueAt, 0, bArr2, bArrArray.length, bArrValueAt.length);
                    bArr = bArr2;
                }
                writableMapCreateMap.putMap("manufacturerData", writableMapCreateMap3);
                writableMapCreateMap.putMap("manufacturerRawData", byteArrayToWritableMap(bArr));
                writableMapCreateMap.putInt("txPowerLevel", this.advertisingData.getTxPowerLevel());
            }
            writableMapAsWritableMap.putMap("advertising", writableMapCreateMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writableMapAsWritableMap;
    }

    public void updateData(ScanResult scanResult) {
        ScanRecord scanRecord = scanResult.getScanRecord();
        this.advertisingData = scanRecord;
        this.advertisingDataBytes = scanRecord.getBytes();
    }
}
