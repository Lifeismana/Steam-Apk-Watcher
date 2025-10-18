package com.reactnativepagerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.react.uimanager.events.NativeGestureUtil;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NestedScrollableHost.kt */
@Metadata(m693d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u001b\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u0004\u0010\bJ\u0018\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\n2\u0006\u0010%\u001a\u00020\u0018H\u0002J\u0010\u0010&\u001a\u00020\u00112\u0006\u0010'\u001a\u00020(H\u0016J\u0010\u0010)\u001a\u00020*2\u0006\u0010'\u001a\u00020(H\u0002J\u0010\u0010+\u001a\u00020\u00112\u0006\u0010'\u001a\u00020(H\u0016R\u001e\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u000f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u001c8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u001f\u001a\u0004\u0018\u00010 8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b!\u0010\"¨\u0006,"}, m694d2 = {"Lcom/reactnativepagerview/NestedScrollableHost;", "Landroid/widget/FrameLayout;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "attrs", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "initialIndex", "", "getInitialIndex", "()Ljava/lang/Integer;", "setInitialIndex", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "didSetInitialIndex", "", "getDidSetInitialIndex", "()Z", "setDidSetInitialIndex", "(Z)V", "touchSlop", "initialX", "", "initialY", "nativeGestureStarted", "parentViewPager", "Landroidx/viewpager2/widget/ViewPager2;", "getParentViewPager", "()Landroidx/viewpager2/widget/ViewPager2;", "child", "Landroid/view/View;", "getChild", "()Landroid/view/View;", "canChildScroll", "orientation", "delta", "onInterceptTouchEvent", "e", "Landroid/view/MotionEvent;", "handleInterceptTouchEvent", "", "onTouchEvent", "react-native-pager-view_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
/* loaded from: classes2.dex */
public final class NestedScrollableHost extends FrameLayout {
    private boolean didSetInitialIndex;
    private Integer initialIndex;
    private float initialX;
    private float initialY;
    private boolean nativeGestureStarted;
    private int touchSlop;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NestedScrollableHost(Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public NestedScrollableHost(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.checkNotNullParameter(context, "context");
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public final Integer getInitialIndex() {
        return this.initialIndex;
    }

    public final void setInitialIndex(Integer num) {
        this.initialIndex = num;
    }

    public final boolean getDidSetInitialIndex() {
        return this.didSetInitialIndex;
    }

    public final void setDidSetInitialIndex(boolean z) {
        this.didSetInitialIndex = z;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x000d, code lost:
    
        r0 = null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final ViewPager2 getParentViewPager() {
        View view;
        Object parent = getParent();
        if (parent instanceof View) {
            view = (View) parent;
            while (view != null && !(view instanceof ViewPager2)) {
                Object parent2 = view.getParent();
                if (parent2 instanceof View) {
                    view = (View) parent2;
                }
            }
            if (view instanceof ViewPager2) {
                return (ViewPager2) view;
            }
            return null;
        }
        view = null;
    }

    private final View getChild() {
        if (getChildCount() > 0) {
            return getChildAt(0);
        }
        return null;
    }

    private final boolean canChildScroll(int orientation, float delta) {
        int i = -((int) Math.signum(delta));
        if (orientation == 0) {
            View child = getChild();
            if (child != null) {
                return child.canScrollHorizontally(i);
            }
            return false;
        }
        if (orientation == 1) {
            View child2 = getChild();
            if (child2 != null) {
                return child2.canScrollVertically(i);
            }
            return false;
        }
        throw new IllegalArgumentException();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        Intrinsics.checkNotNullParameter(e, "e");
        handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private final void handleInterceptTouchEvent(MotionEvent e) {
        ViewPager2 parentViewPager = getParentViewPager();
        Integer valueOf = parentViewPager != null ? Integer.valueOf(parentViewPager.getOrientation()) : null;
        if (e.getAction() == 0) {
            this.initialX = e.getX();
            this.initialY = e.getY();
            if (valueOf != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
                return;
            }
            return;
        }
        if (e.getAction() == 2) {
            float x = e.getX() - this.initialX;
            float y = e.getY() - this.initialY;
            boolean z = valueOf != null && valueOf.intValue() == 0;
            float abs = Math.abs(x) * (z ? 0.5f : 1.0f);
            float abs2 = Math.abs(y) * (z ? 1.0f : 0.5f);
            int i = this.touchSlop;
            if (abs > i || abs2 > i) {
                NativeGestureUtil.notifyNativeGestureStarted(this, e);
                this.nativeGestureStarted = true;
                if (valueOf == null) {
                    return;
                }
                if (z == (abs2 > abs)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return;
                }
                int intValue = valueOf.intValue();
                if (!z) {
                    x = y;
                }
                if (canChildScroll(intValue, x)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        Intrinsics.checkNotNullParameter(e, "e");
        if (e.getActionMasked() == 1 && this.nativeGestureStarted) {
            NativeGestureUtil.notifyNativeGestureEnded(this, e);
            this.nativeGestureStarted = false;
        }
        return super.onTouchEvent(e);
    }
}
