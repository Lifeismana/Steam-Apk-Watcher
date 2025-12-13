package it.innove;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.companion.AssociationRequest;
import android.companion.BluetoothLeDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.google.firebase.messaging.Constants;

/* loaded from: classes3.dex */
public class CompanionScanner {
    public static final String LOG_TAG = "RNBleManager_Companion";
    private static final int SELECT_DEVICE_REQUEST_CODE = 540;
    private static Callback scanCallback;
    private final BleManager bleManager;
    private final ActivityEventListener mActivityEventListener;
    private final ReactContext reactContext;

    public CompanionScanner(ReactApplicationContext reactApplicationContext, BleManager bleManager) {
        BaseActivityEventListener baseActivityEventListener = new BaseActivityEventListener() { // from class: it.innove.CompanionScanner.1
            /* JADX WARN: Removed duplicated region for block: B:19:0x0098  */
            /* JADX WARN: Removed duplicated region for block: B:26:0x00b6  */
            @Override // com.facebook.react.bridge.BaseActivityEventListener, com.facebook.react.bridge.ActivityEventListener
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
                Peripheral peripheral;
                Log.d(CompanionScanner.LOG_TAG, "onActivityResult");
                if (i != CompanionScanner.SELECT_DEVICE_REQUEST_CODE) {
                    super.onActivityResult(activity, i, i2, intent);
                    return;
                }
                if (i2 == -1) {
                    Log.d(CompanionScanner.LOG_TAG, "Ok activity result");
                    Parcelable parcelableExtra = intent.getParcelableExtra("android.companion.extra.DEVICE");
                    if (parcelableExtra != null) {
                        if (parcelableExtra instanceof BluetoothDevice) {
                            peripheral = CompanionScanner.this.bleManager.savePeripheral((BluetoothDevice) parcelableExtra);
                        } else if (parcelableExtra instanceof ScanResult) {
                            peripheral = CompanionScanner.this.bleManager.savePeripheral(((ScanResult) parcelableExtra).getDevice());
                        } else {
                            Log.wtf(CompanionScanner.LOG_TAG, "Unexpected AssociationInfo device!");
                            peripheral = null;
                        }
                        if (peripheral != null && CompanionScanner.scanCallback != null) {
                            CompanionScanner.scanCallback.invoke(null, peripheral.asWritableMap());
                            CompanionScanner.scanCallback = null;
                            CompanionScanner.this.bleManager.emitOnCompanionPeripheral(peripheral.asWritableMap());
                        }
                        if (CompanionScanner.scanCallback != null) {
                            CompanionScanner.scanCallback.invoke(null, peripheral != null ? peripheral.asWritableMap() : null);
                            CompanionScanner.scanCallback = null;
                        }
                        CompanionScanner.this.bleManager.emitOnCompanionPeripheral(peripheral != null ? peripheral.asWritableMap() : null);
                    }
                    CompanionScanner.scanCallback.invoke(null, null);
                    CompanionScanner.scanCallback = null;
                    CompanionScanner.this.bleManager.emitOnCompanionPeripheral(null);
                } else {
                    Log.d(CompanionScanner.LOG_TAG, "Non-ok activity result");
                }
                peripheral = null;
                if (CompanionScanner.scanCallback != null) {
                }
                CompanionScanner.this.bleManager.emitOnCompanionPeripheral(peripheral != null ? peripheral.asWritableMap() : null);
            }
        };
        this.mActivityEventListener = baseActivityEventListener;
        this.reactContext = reactApplicationContext;
        this.bleManager = bleManager;
        reactApplicationContext.addActivityEventListener(baseActivityEventListener);
    }

    public void scan(ReadableArray readableArray, ReadableMap readableMap, Callback callback) {
        Log.d(LOG_TAG, "companion scan start");
        AssociationRequest.Builder singleDevice = new AssociationRequest.Builder().setSingleDevice(readableMap.hasKey("single") && readableMap.getBoolean("single"));
        for (int i = 0; i < readableArray.size(); i++) {
            ParcelUuid parcelUuid = new ParcelUuid(UUIDHelper.uuidFromString(readableArray.getString(i)));
            Log.d(LOG_TAG, "Filter service: " + parcelUuid);
            singleDevice = singleDevice.addDeviceFilter(new BluetoothLeDeviceFilter.Builder().setScanFilter(new ScanFilter.Builder().setServiceUuid(parcelUuid).build()).build());
        }
        AssociationRequest build = singleDevice.build();
        Callback callback2 = scanCallback;
        if (callback2 != null) {
            callback2.invoke("New scan called", null);
        }
        scanCallback = callback;
        ((CompanionDeviceManager) this.bleManager.getCompanionDeviceManager()).associate(build, new CompanionDeviceManager.Callback() { // from class: it.innove.CompanionScanner.2
            @Override // android.companion.CompanionDeviceManager.Callback
            public void onFailure(CharSequence charSequence) {
                String str;
                Log.d(CompanionScanner.LOG_TAG, "companion failure: " + ((Object) charSequence));
                if (charSequence != null) {
                    str = "Companion association failed: " + charSequence.toString();
                } else {
                    str = "Companion association failed";
                }
                if (CompanionScanner.scanCallback != null) {
                    CompanionScanner.scanCallback.invoke(str);
                    CompanionScanner.scanCallback = null;
                }
                WritableMap createMap = Arguments.createMap();
                createMap.putString(Constants.IPC_BUNDLE_KEY_SEND_ERROR, charSequence.toString());
                CompanionScanner.this.bleManager.emitOnCompanionFailure(createMap);
            }

            @Override // android.companion.CompanionDeviceManager.Callback
            public void onDeviceFound(IntentSender intentSender) {
                Log.d(CompanionScanner.LOG_TAG, "companion device found");
                try {
                    CompanionScanner.this.reactContext.getCurrentActivity().startIntentSenderForResult(intentSender, CompanionScanner.SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(CompanionScanner.LOG_TAG, "Failed to send intent: " + e.toString());
                    String str = "Failed to send intent: " + e.toString();
                    if (CompanionScanner.scanCallback != null) {
                        CompanionScanner.scanCallback.invoke(str);
                        CompanionScanner.scanCallback = null;
                    }
                    WritableMap createMap = Arguments.createMap();
                    createMap.putString(Constants.IPC_BUNDLE_KEY_SEND_ERROR, str);
                    CompanionScanner.this.bleManager.emitOnCompanionFailure(createMap);
                }
            }
        }, (Handler) null);
    }
}
