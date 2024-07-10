package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import java.util.Collection;
import java.util.HashMap;

/* loaded from: classes.dex */
public class PagingHorizontalScrollView extends HorizontalScrollView {
    private boolean bNeedsUpdate;
    private int mCurrentIndex;
    private GestureDetector mGestureDetector;
    private LinearLayout scrollableItemsWrapper;
    private HashMap<String, View> tagsToViews;

    public PagingHorizontalScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentIndex = 0;
        this.tagsToViews = new HashMap<>();
        this.scrollableItemsWrapper = new LinearLayout(getContext());
        this.scrollableItemsWrapper.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        this.scrollableItemsWrapper.setOrientation(0);
        addView(this.scrollableItemsWrapper);
    }

    public PagingHorizontalScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public int size() {
        LinearLayout linearLayout = this.scrollableItemsWrapper;
        if (linearLayout == null) {
            return 0;
        }
        return linearLayout.getChildCount();
    }

    @Override // android.widget.HorizontalScrollView, android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        this.bNeedsUpdate = true;
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        setLayoutParams(new FrameLayout.LayoutParams(getScreenWidth(), getHeight()));
        new Handler().postDelayed(new Runnable() { // from class: com.valvesoftware.android.steam.community.PagingHorizontalScrollView.1
            @Override // java.lang.Runnable
            public void run() {
                PagingHorizontalScrollView.this.bNeedsUpdate = true;
                PagingHorizontalScrollView.this.update();
            }
        }, 50L);
    }

    public void update() {
        if (this.bNeedsUpdate) {
            this.bNeedsUpdate = false;
            HashMap<String, View> hashMap = this.tagsToViews;
            if (hashMap == null || hashMap.size() <= 0) {
                return;
            }
            scrollTo(this.mCurrentIndex * this.tagsToViews.entrySet().iterator().next().getValue().getWidth(), 0);
        }
    }

    public void init() {
        setOnTouchListener(new View.OnTouchListener() { // from class: com.valvesoftware.android.steam.community.PagingHorizontalScrollView.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (PagingHorizontalScrollView.this.mGestureDetector.onTouchEvent(motionEvent)) {
                    return true;
                }
                if (motionEvent.getAction() != 1 && motionEvent.getAction() != 3) {
                    return false;
                }
                int scrollX = PagingHorizontalScrollView.this.getScrollX();
                int measuredWidth = view.getMeasuredWidth();
                PagingHorizontalScrollView.this.mCurrentIndex = (scrollX + (measuredWidth / 2)) / measuredWidth;
                PagingHorizontalScrollView.this.smoothScrollTo(PagingHorizontalScrollView.this.mCurrentIndex * measuredWidth, 0);
                PagingHorizontalScrollView.this.update();
                return true;
            }
        });
        this.mGestureDetector = new GestureDetector(new PagingGestureDetector());
    }

    public void addView(View view, String str) {
        this.tagsToViews.put(str, view);
        this.scrollableItemsWrapper.addView(view);
    }

    public void clear() {
        HashMap<String, View> hashMap = this.tagsToViews;
        if (hashMap != null) {
            Collection<View> values = hashMap.values();
            if (values != null && values.size() > 0) {
                for (KeyEvent.Callback callback : values) {
                    if (callback instanceof CloseableView) {
                        ((CloseableView) callback).close();
                    }
                }
            }
            this.tagsToViews.clear();
        }
        this.scrollableItemsWrapper.removeAllViews();
    }

    private int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /* loaded from: classes.dex */
    class PagingGestureDetector extends GestureDetector.SimpleOnGestureListener {
        PagingGestureDetector() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            if (motionEvent.getX() - motionEvent2.getX() > 5.0f && Math.abs(f) > 300.0f) {
                int measuredWidth = PagingHorizontalScrollView.this.getMeasuredWidth();
                PagingHorizontalScrollView.this.mCurrentIndex = PagingHorizontalScrollView.this.mCurrentIndex < PagingHorizontalScrollView.this.size() - 1 ? PagingHorizontalScrollView.this.mCurrentIndex + 1 : PagingHorizontalScrollView.this.size() - 1;
                PagingHorizontalScrollView.this.smoothScrollTo(PagingHorizontalScrollView.this.mCurrentIndex * measuredWidth, 0);
                PagingHorizontalScrollView.this.update();
                return true;
            }
            if (motionEvent2.getX() - motionEvent.getX() > 5.0f && Math.abs(f) > 300.0f) {
                int measuredWidth2 = PagingHorizontalScrollView.this.getMeasuredWidth();
                PagingHorizontalScrollView.this.mCurrentIndex = PagingHorizontalScrollView.this.mCurrentIndex > 0 ? PagingHorizontalScrollView.this.mCurrentIndex - 1 : 0;
                PagingHorizontalScrollView.this.smoothScrollTo(PagingHorizontalScrollView.this.mCurrentIndex * measuredWidth2, 0);
                PagingHorizontalScrollView.this.update();
                return true;
            }
            return false;
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            int measuredWidth = PagingHorizontalScrollView.this.getMeasuredWidth();
            double x = motionEvent.getX();
            double d = measuredWidth;
            Double.isNaN(d);
            if (x > 0.9d * d) {
                PagingHorizontalScrollView pagingHorizontalScrollView = PagingHorizontalScrollView.this;
                pagingHorizontalScrollView.mCurrentIndex = pagingHorizontalScrollView.mCurrentIndex < PagingHorizontalScrollView.this.size() - 1 ? PagingHorizontalScrollView.this.mCurrentIndex + 1 : PagingHorizontalScrollView.this.size() - 1;
                PagingHorizontalScrollView pagingHorizontalScrollView2 = PagingHorizontalScrollView.this;
                pagingHorizontalScrollView2.smoothScrollTo(pagingHorizontalScrollView2.mCurrentIndex * measuredWidth, 0);
                PagingHorizontalScrollView.this.update();
                return true;
            }
            double x2 = motionEvent.getX();
            Double.isNaN(d);
            if (x2 >= d * 0.1d) {
                return false;
            }
            PagingHorizontalScrollView pagingHorizontalScrollView3 = PagingHorizontalScrollView.this;
            pagingHorizontalScrollView3.mCurrentIndex = pagingHorizontalScrollView3.mCurrentIndex > 0 ? PagingHorizontalScrollView.this.mCurrentIndex - 1 : 0;
            PagingHorizontalScrollView pagingHorizontalScrollView4 = PagingHorizontalScrollView.this;
            pagingHorizontalScrollView4.smoothScrollTo(pagingHorizontalScrollView4.mCurrentIndex * measuredWidth, 0);
            PagingHorizontalScrollView.this.update();
            return true;
        }
    }
}
