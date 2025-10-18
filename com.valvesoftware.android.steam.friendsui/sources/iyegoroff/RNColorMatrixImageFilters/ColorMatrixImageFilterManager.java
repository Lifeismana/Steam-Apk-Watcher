package iyegoroff.RNColorMatrixImageFilters;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.viewmanagers.CMIFColorMatrixImageFilterManagerDelegate;
import com.facebook.react.viewmanagers.CMIFColorMatrixImageFilterManagerInterface;
import com.facebook.react.views.view.ReactViewManager;

@ReactModule(name = ColorMatrixImageFilterManagerImpl.NAME)
/* loaded from: classes3.dex */
public class ColorMatrixImageFilterManager extends ReactViewManager implements CMIFColorMatrixImageFilterManagerInterface<ColorMatrixImageFilter> {
    private final ViewManagerDelegate<ColorMatrixImageFilter> mDelegate = new CMIFColorMatrixImageFilterManagerDelegate(this);

    public ColorMatrixImageFilterManager(ReactApplicationContext reactApplicationContext) {
    }

    @Override // com.facebook.react.uimanager.ViewManager
    protected ViewManagerDelegate getDelegate() {
        return this.mDelegate;
    }

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return ColorMatrixImageFilterManagerImpl.NAME;
    }

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager
    public ColorMatrixImageFilter createViewInstance(ThemedReactContext themedReactContext) {
        return ColorMatrixImageFilterManagerImpl.createViewInstance(themedReactContext);
    }

    @Override // com.facebook.react.viewmanagers.CMIFColorMatrixImageFilterManagerInterface
    @ReactProp(name = ColorMatrixImageFilterManagerImpl.MATRIX_PROP)
    public void setMatrix(ColorMatrixImageFilter colorMatrixImageFilter, ReadableArray readableArray) {
        ColorMatrixImageFilterManagerImpl.setMatrix(colorMatrixImageFilter, readableArray);
    }
}
