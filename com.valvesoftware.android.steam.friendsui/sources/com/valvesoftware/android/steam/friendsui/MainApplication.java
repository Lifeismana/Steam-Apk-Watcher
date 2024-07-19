package com.valvesoftware.android.steam.friendsui;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.soloader.SoLoader;
import expo.modules.ApplicationLifecycleDispatcher;
import expo.modules.ReactNativeHostWrapper;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class MainApplication extends Application implements ReactApplication {
    private final ReactNativeHost mReactNativeHost = new ReactNativeHostWrapper(this, new DefaultReactNativeHost(this) { // from class: com.valvesoftware.android.steam.friendsui.MainApplication.1
        @Override // com.facebook.react.ReactNativeHost
        protected String getJSMainModuleName() {
            return "index";
        }

        @Override // com.facebook.react.ReactNativeHost
        public boolean getUseDeveloperSupport() {
            return false;
        }

        @Override // com.facebook.react.defaults.DefaultReactNativeHost
        protected boolean isNewArchEnabled() {
            return false;
        }

        @Override // com.facebook.react.ReactNativeHost
        protected List<ReactPackage> getPackages() {
            ArrayList<ReactPackage> packages = new PackageList(this).getPackages();
            packages.add(new ValveHelpersPackage());
            return packages;
        }

        @Override // com.facebook.react.defaults.DefaultReactNativeHost
        protected Boolean isHermesEnabled() {
            return true;
        }
    });

    @Override // com.facebook.react.ReactApplication
    public ReactNativeHost getReactNativeHost() {
        return this.mReactNativeHost;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        SoLoader.init((Context) this, false);
        ReactFeatureFlags.unstable_useRuntimeSchedulerAlways = false;
        ReactNativeFlipper.initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
        ApplicationLifecycleDispatcher.onApplicationCreate(this);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ApplicationLifecycleDispatcher.onConfigurationChanged(this, configuration);
    }
}
