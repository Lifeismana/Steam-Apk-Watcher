package iyegoroff.RNColorMatrixImageFilters;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewManager;

@ReactModule(name = ColorMatrixImageFilterManager.REACT_CLASS)
/* loaded from: classes3.dex */
public class ColorMatrixImageFilterManager extends ReactViewManager {
    private static final String PROP_MATRIX = "matrix";
    static final String REACT_CLASS = "CMIFColorMatrixImageFilter";

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return REACT_CLASS;
    }

    @Override // com.facebook.react.views.view.ReactViewManager, com.facebook.react.uimanager.ViewManager
    public ColorMatrixImageFilter createViewInstance(ThemedReactContext themedReactContext) {
        return new ColorMatrixImageFilter(themedReactContext);
    }

    @ReactProp(name = PROP_MATRIX)
    public void setMatrix(ColorMatrixImageFilter colorMatrixImageFilter, ReadableArray readableArray) {
        colorMatrixImageFilter.setMatrix(readableArray);
    }
}
