package com.valvesoftware.android.steam.community;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactHost;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactNativeHost;
import com.facebook.react.soloader.OpenSourceMergedSoMapping;
import com.facebook.soloader.SoLoader;
import expo.modules.ApplicationLifecycleDispatcher;
import expo.modules.ReactNativeHostWrapper;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MainApplication.kt */
@Metadata(m892d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u0010\u001a\u00020\rH\u0016R\u0014\u0010\u0004\u001a\u00020\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0011"}, m893d2 = {"Lcom/valvesoftware/android/steam/community/MainApplication;", "Landroid/app/Application;", "Lcom/facebook/react/ReactApplication;", "()V", "reactHost", "Lcom/facebook/react/ReactHost;", "getReactHost", "()Lcom/facebook/react/ReactHost;", "reactNativeHost", "Lcom/facebook/react/ReactNativeHost;", "getReactNativeHost", "()Lcom/facebook/react/ReactNativeHost;", "onConfigurationChanged", "", "newConfig", "Landroid/content/res/Configuration;", "onCreate", "app_release"}, m894k = 1, m895mv = {1, 9, 0}, m897xi = 48)
/* loaded from: classes3.dex */
public final class MainApplication extends Application implements ReactApplication {
    private final ReactNativeHost reactNativeHost = new ReactNativeHostWrapper(this, new DefaultReactNativeHost(this) { // from class: com.valvesoftware.android.steam.community.MainApplication$reactNativeHost$1
        private final boolean isHermesEnabled;
        private final boolean isNewArchEnabled;

        @Override // com.facebook.react.ReactNativeHost
        public boolean getUseDeveloperSupport() {
            return false;
        }

        {
            super(this);
            this.isNewArchEnabled = true;
            this.isHermesEnabled = true;
        }

        @Override // com.facebook.react.ReactNativeHost
        protected List<ReactPackage> getPackages() {
            ArrayList<ReactPackage> packages = new PackageList(this).getPackages();
            packages.add(new ValvePackage());
            Intrinsics.checkNotNull(packages);
            return packages;
        }

        @Override // com.facebook.react.ReactNativeHost
        protected String getJSMainModuleName() {
            return ".expo/.virtual-metro-entry";
        }

        @Override // com.facebook.react.defaults.DefaultReactNativeHost
        /* renamed from: isNewArchEnabled, reason: from getter */
        protected boolean getIsNewArchEnabled() {
            return this.isNewArchEnabled;
        }

        @Override // com.facebook.react.defaults.DefaultReactNativeHost
        protected Boolean isHermesEnabled() {
            return Boolean.valueOf(this.isHermesEnabled);
        }
    });

    @Override // com.facebook.react.ReactApplication
    public ReactNativeHost getReactNativeHost() {
        return this.reactNativeHost;
    }

    @Override // com.facebook.react.ReactApplication
    public ReactHost getReactHost() {
        ReactNativeHostWrapper.Companion companion = ReactNativeHostWrapper.INSTANCE;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        return companion.createReactHost(applicationContext, getReactNativeHost());
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, OpenSourceMergedSoMapping.INSTANCE);
        DefaultNewArchitectureEntryPoint.load$default(false, false, false, 7, null);
        ApplicationLifecycleDispatcher.onApplicationCreate(this);
    }

    @Override // android.app.Application, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        Intrinsics.checkNotNullParameter(newConfig, "newConfig");
        super.onConfigurationChanged(newConfig);
        ApplicationLifecycleDispatcher.onConfigurationChanged(this, newConfig);
    }
}
