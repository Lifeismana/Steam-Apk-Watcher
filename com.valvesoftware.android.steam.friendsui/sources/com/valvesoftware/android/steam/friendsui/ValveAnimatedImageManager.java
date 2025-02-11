package com.valvesoftware.android.steam.friendsui;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ReactImageView;
import expo.modules.p008av.player.PlayerData;

/* loaded from: classes2.dex */
public class ValveAnimatedImageManager extends SimpleViewManager<ReactImageView> {
    public static final String REACT_CLASS = "ValveAnimatedImage";

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public ReactImageView createViewInstance(ThemedReactContext themedReactContext) {
        return new ReactImageView(themedReactContext, Fresco.newDraweeControllerBuilder(), null, null);
    }

    @ReactProp(name = PlayerData.STATUS_URI_KEY_PATH)
    public void setUri(ReactImageView reactImageView, String str) {
        Glide.with(reactImageView).load(str).into(reactImageView);
    }
}
