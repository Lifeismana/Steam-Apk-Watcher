package com.android.installreferrer.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.installreferrer.commons.InstallReferrerCommons;
import com.google.android.finsky.externalreferrer.IGetInstallReferrerService;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class InstallReferrerClientImpl extends InstallReferrerClient {
    private static final int PLAY_STORE_MIN_APP_VER = 80837300;
    private static final String SERVICE_ACTION_NAME = "com.google.android.finsky.BIND_GET_INSTALL_REFERRER_SERVICE";
    private static final String SERVICE_NAME = "com.google.android.finsky.externalreferrer.GetInstallReferrerService";
    private static final String SERVICE_PACKAGE_NAME = "com.android.vending";
    private static final String TAG = "InstallReferrerClient";
    private final Context mApplicationContext;
    private int mClientState = 0;
    private IGetInstallReferrerService mService;
    private ServiceConnection mServiceConnection;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ClientState {
        public static final int CLOSED = 3;
        public static final int CONNECTED = 2;
        public static final int CONNECTING = 1;
        public static final int DISCONNECTED = 0;
    }

    public InstallReferrerClientImpl(Context context) {
        this.mApplicationContext = context.getApplicationContext();
    }

    @Override // com.android.installreferrer.api.InstallReferrerClient
    public boolean isReady() {
        return (this.mClientState != 2 || this.mService == null || this.mServiceConnection == null) ? false : true;
    }

    @Override // com.android.installreferrer.api.InstallReferrerClient
    public void startConnection(InstallReferrerStateListener installReferrerStateListener) {
        if (isReady()) {
            InstallReferrerCommons.logVerbose(TAG, "Service connection is valid. No need to re-initialize.");
            installReferrerStateListener.onInstallReferrerSetupFinished(0);
            return;
        }
        int i = this.mClientState;
        if (i == 1) {
            InstallReferrerCommons.logWarn(TAG, "Client is already in the process of connecting to the service.");
            installReferrerStateListener.onInstallReferrerSetupFinished(3);
            return;
        }
        if (i == 3) {
            InstallReferrerCommons.logWarn(TAG, "Client was already closed and can't be reused. Please create another instance.");
            installReferrerStateListener.onInstallReferrerSetupFinished(3);
            return;
        }
        InstallReferrerCommons.logVerbose(TAG, "Starting install referrer service setup.");
        this.mServiceConnection = new InstallReferrerServiceConnection(installReferrerStateListener);
        Intent intent = new Intent(SERVICE_ACTION_NAME);
        intent.setComponent(new ComponentName("com.android.vending", SERVICE_NAME));
        List<ResolveInfo> queryIntentServices = this.mApplicationContext.getPackageManager().queryIntentServices(intent, 0);
        if (queryIntentServices != null && !queryIntentServices.isEmpty()) {
            ResolveInfo resolveInfo = queryIntentServices.get(0);
            if (resolveInfo.serviceInfo != null) {
                String str = resolveInfo.serviceInfo.packageName;
                String str2 = resolveInfo.serviceInfo.name;
                if ("com.android.vending".equals(str) && str2 != null && isPlayStoreCompatible()) {
                    if (this.mApplicationContext.bindService(new Intent(intent), this.mServiceConnection, 1)) {
                        InstallReferrerCommons.logVerbose(TAG, "Service was bonded successfully.");
                        return;
                    }
                    InstallReferrerCommons.logWarn(TAG, "Connection to service is blocked.");
                    this.mClientState = 0;
                    installReferrerStateListener.onInstallReferrerSetupFinished(1);
                    return;
                }
                InstallReferrerCommons.logWarn(TAG, "Play Store missing or incompatible. Version 8.3.73 or later required.");
                this.mClientState = 0;
                installReferrerStateListener.onInstallReferrerSetupFinished(2);
                return;
            }
        }
        this.mClientState = 0;
        InstallReferrerCommons.logVerbose(TAG, "Install Referrer service unavailable on device.");
        installReferrerStateListener.onInstallReferrerSetupFinished(2);
    }

    @Override // com.android.installreferrer.api.InstallReferrerClient
    public void endConnection() {
        this.mClientState = 3;
        if (this.mServiceConnection != null) {
            InstallReferrerCommons.logVerbose(TAG, "Unbinding from service.");
            this.mApplicationContext.unbindService(this.mServiceConnection);
            this.mServiceConnection = null;
        }
        this.mService = null;
    }

    @Override // com.android.installreferrer.api.InstallReferrerClient
    public ReferrerDetails getInstallReferrer() throws RemoteException {
        if (!isReady()) {
            throw new IllegalStateException("Service not connected. Please start a connection before using the service.");
        }
        Bundle bundle = new Bundle();
        bundle.putString("package_name", this.mApplicationContext.getPackageName());
        try {
            return new ReferrerDetails(this.mService.getInstallReferrer(bundle));
        } catch (RemoteException e) {
            InstallReferrerCommons.logWarn(TAG, "RemoteException getting install referrer information");
            this.mClientState = 0;
            throw e;
        }
    }

    private boolean isPlayStoreCompatible() {
        try {
            return this.mApplicationContext.getPackageManager().getPackageInfo("com.android.vending", 128).versionCode >= PLAY_STORE_MIN_APP_VER;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    /* loaded from: classes.dex */
    private final class InstallReferrerServiceConnection implements ServiceConnection {
        private final InstallReferrerStateListener mListener;

        private InstallReferrerServiceConnection(InstallReferrerStateListener installReferrerStateListener) {
            if (installReferrerStateListener == null) {
                throw new RuntimeException("Please specify a listener to know when setup is done.");
            }
            this.mListener = installReferrerStateListener;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            InstallReferrerCommons.logVerbose(InstallReferrerClientImpl.TAG, "Install Referrer service connected.");
            InstallReferrerClientImpl.this.mService = IGetInstallReferrerService.Stub.asInterface(iBinder);
            InstallReferrerClientImpl.this.mClientState = 2;
            this.mListener.onInstallReferrerSetupFinished(0);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            InstallReferrerCommons.logWarn(InstallReferrerClientImpl.TAG, "Install Referrer service disconnected.");
            InstallReferrerClientImpl.this.mService = null;
            InstallReferrerClientImpl.this.mClientState = 0;
            this.mListener.onInstallReferrerServiceDisconnected();
        }
    }
}
