package iyegoroff.RNColorMatrixImageFilters;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.ThemedReactContext;

/* loaded from: classes3.dex */
public class ColorMatrixImageFilterManagerImpl {
    public static final String MATRIX_PROP = "matrix";
    public static final String NAME = "CMIFColorMatrixImageFilter";

    public static ColorMatrixImageFilter createViewInstance(ThemedReactContext themedReactContext) {
        return new ColorMatrixImageFilter(themedReactContext);
    }

    public static void setMatrix(ColorMatrixImageFilter colorMatrixImageFilter, ReadableArray readableArray) {
        colorMatrixImageFilter.setMatrix(readableArray);
    }
}
