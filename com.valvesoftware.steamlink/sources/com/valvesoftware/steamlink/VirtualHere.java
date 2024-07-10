package com.valvesoftware.steamlink;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.libsdl.app.SDL;

/* loaded from: classes.dex */
public class VirtualHere {
    private static final String TAG = "VirtualHere";
    private static final String VHDAEMON_PACKAGE = "com.virtualhere.androidserver";
    private static VirtualHere sInstance;
    private static int sInstanceRefCount;
    private Context mContext;
    private VirtualHereDevice[] mDevices;
    private boolean mDevicesChanged;
    private boolean mIsBound;
    private boolean mIsSharing;
    private int mNumLicensedDevices;
    private Messenger mService;
    private LinkedHashMap<Integer, VirtualHereDevice> mHashDevices = new LinkedHashMap<>();
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private final ServiceConnection mConnection = new ServiceConnection() { // from class: com.valvesoftware.steamlink.VirtualHere.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VirtualHere.this.mService = new Messenger(iBinder);
            try {
                Log.v(VirtualHere.TAG, "Service connected, registering as client");
                Message obtain = Message.obtain((Handler) null, VHDAEMON_MSGS.REGISTER_CLIENT.ordinal());
                obtain.replyTo = VirtualHere.this.mMessenger;
                VirtualHere.this.mService.send(obtain);
                Message obtain2 = Message.obtain((Handler) null, VHDAEMON_MSGS.GET_DEVICE_LIST.ordinal());
                obtain2.replyTo = VirtualHere.this.mMessenger;
                VirtualHere.this.mService.send(obtain2);
                if (VirtualHere.this.mIsSharing) {
                    VirtualHere virtualHere = VirtualHere.this;
                    virtualHere.startSharing(virtualHere.mNumLicensedDevices);
                }
            } catch (RemoteException unused) {
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.v(VirtualHere.TAG, "Service disconnected");
            VirtualHere.this.mService = null;
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum VHDAEMON_MSGS {
        REGISTER_CLIENT,
        UNREGISTER_CLIENT,
        GET_DAEMON_STATE,
        GET_DAEMON_STATE_ACK,
        STOP_SERVER_PROCESS,
        SET_IGNORES,
        GET_IGNORES,
        IGNORES,
        SET_REVERSE_CLIENTS,
        GET_REVERSE_CLIENTS,
        GET_EASYFIND_ADDRESS,
        GET_EASYFIND_STATUS,
        ENABLE_EASYFIND,
        DISABLE_EASYFIND,
        REVERSE_CLIENTS,
        SET_LICENSE,
        GET_LICENSE,
        LICENSE,
        SHUTDOWN,
        SET_WIFI_LOCK,
        UNSET_WIFI_LOCK,
        SET_WAKE_LOCK,
        UNSET_WAKE_LOCK,
        SET_SPEED,
        GET_DEVICE_LIST,
        DEVICE_LIST,
        DEVICE_EVENT,
        SET_STEAM_LICENSE
    }

    public static boolean isInstalled() {
        return (SDL.getContext() == null || SDL.getContext().getPackageManager().getLaunchIntentForPackage(VHDAEMON_PACKAGE) == null) ? false : true;
    }

    public static VirtualHere acquire(Context context) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        if (sInstanceRefCount == 0) {
            sInstance = new VirtualHere(context);
        }
        sInstanceRefCount++;
        sInstance.connectService();
        return sInstance;
    }

    public static void release(VirtualHere virtualHere) {
        VirtualHere virtualHere2 = sInstance;
        if (virtualHere == virtualHere2) {
            int i = sInstanceRefCount - 1;
            sInstanceRefCount = i;
            if (i == 0) {
                virtualHere2.close();
                sInstance = null;
            }
        }
    }

    /* loaded from: classes.dex */
    private class VirtualHereDevice {
        public int mAddress;
        public int mPID;
        public String mProduct;
        public int mVID;
        public String mVendor;

        private VirtualHereDevice() {
        }
    }

    /* loaded from: classes.dex */
    class IncomingHandler extends Handler {
        IncomingHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == VHDAEMON_MSGS.DEVICE_LIST.ordinal()) {
                Bundle data = message.getData();
                synchronized (this) {
                    VirtualHere.this.mHashDevices.clear();
                    for (String str : data.keySet()) {
                        ArrayList<String> stringArrayList = data.getStringArrayList(str);
                        VirtualHereDevice virtualHereDevice = new VirtualHereDevice();
                        virtualHereDevice.mAddress = Integer.parseInt(str);
                        virtualHereDevice.mVendor = stringArrayList.get(2);
                        virtualHereDevice.mProduct = stringArrayList.get(3);
                        virtualHereDevice.mVID = Integer.parseInt(stringArrayList.get(0));
                        virtualHereDevice.mPID = Integer.parseInt(stringArrayList.get(1));
                        VirtualHere.this.mHashDevices.put(Integer.valueOf(virtualHereDevice.mAddress), virtualHereDevice);
                    }
                    VirtualHere.this.mDevicesChanged = true;
                }
                return;
            }
            if (message.what == VHDAEMON_MSGS.DEVICE_EVENT.ordinal()) {
                Bundle data2 = message.getData();
                if (data2.getString("event").equals("ADDED")) {
                    VirtualHereDevice virtualHereDevice2 = new VirtualHereDevice();
                    virtualHereDevice2.mAddress = data2.getInt("address");
                    virtualHereDevice2.mVendor = data2.getString("manufacturer");
                    virtualHereDevice2.mProduct = data2.getString("product");
                    virtualHereDevice2.mVID = data2.getInt("vendorId");
                    virtualHereDevice2.mPID = data2.getInt("productId");
                    synchronized (this) {
                        VirtualHere.this.mHashDevices.put(Integer.valueOf(virtualHereDevice2.mAddress), virtualHereDevice2);
                        VirtualHere.this.mDevicesChanged = true;
                    }
                    return;
                }
                if (data2.getString("event").equals("REMOVED")) {
                    int i = data2.getInt("address");
                    synchronized (this) {
                        VirtualHere.this.mHashDevices.remove(Integer.valueOf(i));
                        VirtualHere.this.mDevicesChanged = true;
                    }
                    return;
                }
                return;
            }
            super.handleMessage(message);
        }
    }

    private VirtualHere(Context context) {
        this.mContext = context;
    }

    private void close() {
        disconnectService();
    }

    private boolean isConnected() {
        return this.mIsBound;
    }

    private void connectService() {
        if (isConnected()) {
            return;
        }
        Log.v(TAG, "Connecting to service...");
        Intent intent = new Intent();
        intent.setClassName(VHDAEMON_PACKAGE, "com.virtualhere.androidserver.DaemonService");
        boolean bindService = this.mContext.bindService(intent, this.mConnection, 1);
        this.mIsBound = bindService;
        if (bindService) {
            Log.v(TAG, "Connection attempt succeeded");
        } else {
            Log.v(TAG, "Connection attempt failed");
        }
    }

    private void disconnectService() {
        if (isConnected()) {
            if (this.mService != null) {
                try {
                    Message obtain = Message.obtain((Handler) null, VHDAEMON_MSGS.UNREGISTER_CLIENT.ordinal());
                    obtain.replyTo = this.mMessenger;
                    this.mService.send(obtain);
                } catch (RemoteException unused) {
                }
            }
            this.mContext.unbindService(this.mConnection);
            this.mIsBound = false;
        }
    }

    public void startSharing(int i) {
        this.mIsSharing = true;
        this.mNumLicensedDevices = i;
        if (isConnected() && this.mService != null) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("client", getClass().getName());
                bundle.putString("licensed_devices", Integer.toString(this.mNumLicensedDevices));
                Message obtain = Message.obtain((Handler) null, VHDAEMON_MSGS.SET_STEAM_LICENSE.ordinal());
                obtain.replyTo = this.mMessenger;
                obtain.setData(bundle);
                this.mService.send(obtain);
            } catch (RemoteException unused) {
            }
        }
    }

    public void stopSharing() {
        this.mIsSharing = false;
        if (isConnected() && this.mService != null) {
            try {
                Message obtain = Message.obtain((Handler) null, VHDAEMON_MSGS.SET_STEAM_LICENSE.ordinal());
                obtain.replyTo = this.mMessenger;
                this.mService.send(obtain);
            } catch (RemoteException unused) {
            }
        }
    }

    public boolean updateDevices() {
        if (!this.mDevicesChanged) {
            return false;
        }
        synchronized (this) {
            this.mDevices = (VirtualHereDevice[]) this.mHashDevices.values().toArray(new VirtualHereDevice[0]);
            this.mDevicesChanged = false;
        }
        return true;
    }

    public int getNumDevices() {
        VirtualHereDevice[] virtualHereDeviceArr = this.mDevices;
        if (virtualHereDeviceArr == null) {
            return 0;
        }
        return virtualHereDeviceArr.length;
    }

    public int getDeviceAddress(int i) {
        return this.mDevices[i].mAddress;
    }

    public String getDeviceVendor(int i) {
        return this.mDevices[i].mVendor;
    }

    public String getDeviceProduct(int i) {
        return this.mDevices[i].mProduct;
    }

    public int getDeviceVendorId(int i) {
        return this.mDevices[i].mVID;
    }

    public int getDeviceProductId(int i) {
        return this.mDevices[i].mPID;
    }
}
