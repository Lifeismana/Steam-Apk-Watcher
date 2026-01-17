package it.innove;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/* loaded from: classes3.dex */
public class Helper {
    public static WritableMap decodeProperties(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        WritableMap writableMapCreateMap = Arguments.createMap();
        int properties = bluetoothGattCharacteristic.getProperties();
        if ((properties & 1) != 0) {
            writableMapCreateMap.putString("Broadcast", "Broadcast");
        }
        if ((properties & 2) != 0) {
            writableMapCreateMap.putString("Read", "Read");
        }
        if ((properties & 4) != 0) {
            writableMapCreateMap.putString("WriteWithoutResponse", "WriteWithoutResponse");
        }
        if ((properties & 8) != 0) {
            writableMapCreateMap.putString("Write", "Write");
        }
        if ((properties & 16) != 0) {
            writableMapCreateMap.putString("Notify", "Notify");
        }
        if ((properties & 32) != 0) {
            writableMapCreateMap.putString("Indicate", "Indicate");
        }
        if ((properties & 64) != 0) {
            writableMapCreateMap.putString("AuthenticateSignedWrites", "AuthenticateSignedWrites");
        }
        if ((properties & 128) != 0) {
            writableMapCreateMap.putString("ExtendedProperties", "ExtendedProperties");
        }
        return writableMapCreateMap;
    }

    public static WritableMap decodePermissions(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        WritableMap writableMapCreateMap = Arguments.createMap();
        int permissions = bluetoothGattCharacteristic.getPermissions();
        if ((permissions & 1) != 0) {
            writableMapCreateMap.putString("Read", "Read");
        }
        if ((permissions & 16) != 0) {
            writableMapCreateMap.putString("Write", "Write");
        }
        if ((permissions & 2) != 0) {
            writableMapCreateMap.putString("ReadEncrypted", "ReadEncrypted");
        }
        if ((permissions & 32) != 0) {
            writableMapCreateMap.putString("WriteEncrypted", "WriteEncrypted");
        }
        if ((permissions & 4) != 0) {
            writableMapCreateMap.putString("ReadEncryptedMITM", "ReadEncryptedMITM");
        }
        if ((permissions & 64) != 0) {
            writableMapCreateMap.putString("WriteEncryptedMITM", "WriteEncryptedMITM");
        }
        if ((permissions & 128) != 0) {
            writableMapCreateMap.putString("WriteSigned", "WriteSigned");
        }
        if ((permissions & 256) != 0) {
            writableMapCreateMap.putString("WriteSignedMITM", "WriteSignedMITM");
        }
        return writableMapCreateMap;
    }

    public static WritableMap decodePermissions(BluetoothGattDescriptor bluetoothGattDescriptor) {
        WritableMap writableMapCreateMap = Arguments.createMap();
        int permissions = bluetoothGattDescriptor.getPermissions();
        if ((permissions & 1) != 0) {
            writableMapCreateMap.putString("Read", "Read");
        }
        if ((permissions & 16) != 0) {
            writableMapCreateMap.putString("Write", "Write");
        }
        if ((permissions & 2) != 0) {
            writableMapCreateMap.putString("ReadEncrypted", "ReadEncrypted");
        }
        if ((permissions & 32) != 0) {
            writableMapCreateMap.putString("WriteEncrypted", "WriteEncrypted");
        }
        if ((permissions & 4) != 0) {
            writableMapCreateMap.putString("ReadEncryptedMITM", "ReadEncryptedMITM");
        }
        if ((permissions & 64) != 0) {
            writableMapCreateMap.putString("WriteEncryptedMITM", "WriteEncryptedMITM");
        }
        if ((permissions & 128) != 0) {
            writableMapCreateMap.putString("WriteSigned", "WriteSigned");
        }
        if ((permissions & 256) != 0) {
            writableMapCreateMap.putString("WriteSignedMITM", "WriteSignedMITM");
        }
        return writableMapCreateMap;
    }
}
