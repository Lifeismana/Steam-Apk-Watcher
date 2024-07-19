package iyegoroff.RNColorMatrixImageFilters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.views.view.ReactViewGroup;

/* loaded from: classes3.dex */
public class ColorMatrixImageFilter extends ReactViewGroup {
    private ColorMatrixColorFilter mFilter;

    public ColorMatrixImageFilter(Context context) {
        super(context);
        this.mFilter = new ColorMatrixColorFilter(new ColorMatrix());
    }

    public void setMatrix(ReadableArray readableArray) {
        int size = readableArray.size();
        float[] fArr = new float[size];
        for (int i = 0; i < size; i++) {
            fArr[i] = (float) readableArray.getDouble(i);
        }
        this.mFilter = new ColorMatrixColorFilter(fArr);
        invalidate();
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        boolean z;
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            View childAt = getChildAt(i);
            while (true) {
                z = childAt instanceof ImageView;
                if (z || !(childAt instanceof ViewGroup)) {
                    break;
                } else {
                    childAt = ((ViewGroup) childAt).getChildAt(0);
                }
            }
            if (z) {
                ((ImageView) childAt).setColorFilter(this.mFilter);
                break;
            }
            i++;
        }
        super.draw(canvas);
    }
}
