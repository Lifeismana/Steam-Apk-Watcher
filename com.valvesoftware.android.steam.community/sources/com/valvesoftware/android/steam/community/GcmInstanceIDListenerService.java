package com.valvesoftware.android.steam.community;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

/* loaded from: classes.dex */
public class GcmInstanceIDListenerService extends InstanceIDListenerService {
    @Override // com.google.android.gms.iid.InstanceIDListenerService
    public void onTokenRefresh() {
        startService(new Intent(this, (Class<?>) GcmRegistrar.class));
    }
}
