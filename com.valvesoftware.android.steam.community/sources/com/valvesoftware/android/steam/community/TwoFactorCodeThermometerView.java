package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/* loaded from: classes.dex */
public class TwoFactorCodeThermometerView extends View {
    Paint mAlertPaint;
    Paint mBackgroundPaint;
    boolean mDanger;
    Paint mNormalPaint;
    Paint mOutlinePaint;
    int mValue;

    public TwoFactorCodeThermometerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mOutlinePaint = new Paint();
        this.mOutlinePaint.setColor(getResources().getColor(R.color.twofactorcode_thermometer_outline));
        this.mOutlinePaint.setStrokeWidth(3.0f);
        this.mOutlinePaint.setStyle(Paint.Style.STROKE);
        this.mBackgroundPaint = new Paint();
        this.mBackgroundPaint.setColor(getResources().getColor(R.color.twofactorcode_thermometer_background));
        this.mBackgroundPaint.setStrokeWidth(3.0f);
        this.mBackgroundPaint.setStyle(Paint.Style.FILL);
        this.mNormalPaint = new Paint();
        this.mNormalPaint.setColor(getResources().getColor(R.color.twofactorcode_thermometer_normal));
        this.mNormalPaint.setStyle(Paint.Style.FILL);
        this.mAlertPaint = new Paint();
        this.mAlertPaint.setColor(getResources().getColor(R.color.twofactorcode_alert));
        this.mAlertPaint.setStyle(Paint.Style.FILL);
    }

    public void setValue(int i, boolean z) {
        this.mValue = i;
        this.mDanger = z;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int width = (getWidth() * 2) / 3;
        int width2 = getWidth() / 2;
        int height = getHeight() / 2;
        int i = width / 2;
        float f = width2 - i;
        float f2 = height - 10;
        float f3 = i + width2;
        float f4 = height + 10;
        canvas.drawRect(f, f2, f3, f4, this.mOutlinePaint);
        canvas.drawRect(f, f2, f3, f4, this.mBackgroundPaint);
        int i2 = ((int) (width * (this.mValue / 30.0f))) / 2;
        canvas.drawRect(width2 - i2, f2, width2 + i2, f4, this.mDanger ? this.mAlertPaint : this.mNormalPaint);
    }
}
