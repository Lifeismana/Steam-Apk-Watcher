package com.valvesoftware.android.steam.friendsui;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes3.dex */
public class ValveHelpersPackage implements ReactPackage {
    @Override // com.facebook.react.ReactPackage
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return new ArrayList();
    }

    @Override // com.facebook.react.ReactPackage
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ValveHelpersModule(reactApplicationContext));
        arrayList.add(new ValveNotificationsModule(reactApplicationContext));
        return arrayList;
    }
}
