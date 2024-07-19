package com.reactnativepagerview;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PagerViewViewManagerImpl.kt */
@Metadata(m534d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0011\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\u000f\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\bJ\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\bJ\u0006\u0010\u0013\u001a\u00020\u0014J\u0010\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\nH\u0002J\u000e\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\bJ\u0016\u0010\u0017\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\nJ\u0016\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fJ\u001e\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0014J\u0016\u0010\u001c\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\fJ\u0016\u0010\u001e\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0004J\u0016\u0010\u001f\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\fJ\u0016\u0010 \u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0004J\u0016\u0010!\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0004J\u0016\u0010\"\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010#\u001a\u00020\fJ\u0016\u0010$\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0014R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006%"}, m535d2 = {"Lcom/reactnativepagerview/PagerViewViewManagerImpl;", "", "()V", "NAME", "", "addView", "", "host", "Lcom/reactnativepagerview/NestedScrollableHost;", "child", "Landroid/view/View;", "index", "", "getChildAt", "parent", "getChildCount", "getViewPager", "Landroidx/viewpager2/widget/ViewPager2;", "view", "needsCustomLayoutForChildren", "", "refreshViewChildrenLayout", "removeAllViews", "removeView", "removeViewAt", "setCurrentItem", "selectedTab", "scrollSmooth", "setInitialPage", "value", "setLayoutDirection", "setOffscreenPageLimit", "setOrientation", "setOverScrollMode", "setPageMargin", ViewProps.MARGIN, "setScrollEnabled", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
/* loaded from: classes2.dex */
public final class PagerViewViewManagerImpl {
    public static final PagerViewViewManagerImpl INSTANCE = new PagerViewViewManagerImpl();
    public static final String NAME = "RNCViewPager";

    public final boolean needsCustomLayoutForChildren() {
        return true;
    }

    private PagerViewViewManagerImpl() {
    }

    public final ViewPager2 getViewPager(NestedScrollableHost view) {
        Intrinsics.checkNotNullParameter(view, "view");
        if (view.getChildAt(0) instanceof ViewPager2) {
            View childAt = view.getChildAt(0);
            Intrinsics.checkNotNull(childAt, "null cannot be cast to non-null type androidx.viewpager2.widget.ViewPager2");
            return (ViewPager2) childAt;
        }
        throw new ClassNotFoundException("Could not retrieve ViewPager2 instance");
    }

    public final void setCurrentItem(ViewPager2 view, int selectedTab, boolean scrollSmooth) {
        Intrinsics.checkNotNullParameter(view, "view");
        refreshViewChildrenLayout(view);
        view.setCurrentItem(selectedTab, scrollSmooth);
    }

    public final void addView(NestedScrollableHost host, View child, int index) {
        Integer initialIndex;
        Intrinsics.checkNotNullParameter(host, "host");
        if (child == null) {
            return;
        }
        ViewPager2 viewPager = getViewPager(host);
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        if (viewPagerAdapter != null) {
            viewPagerAdapter.addChild(child, index);
        }
        if (viewPager.getCurrentItem() == index) {
            refreshViewChildrenLayout(viewPager);
        }
        if (host.getDidSetInitialIndex() || (initialIndex = host.getInitialIndex()) == null || initialIndex.intValue() != index) {
            return;
        }
        host.setDidSetInitialIndex(true);
        setCurrentItem(viewPager, index, false);
    }

    public final int getChildCount(NestedScrollableHost parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        RecyclerView.Adapter adapter = getViewPager(parent).getAdapter();
        if (adapter != null) {
            return adapter.getItemCount();
        }
        return 0;
    }

    public final View getChildAt(NestedScrollableHost parent, int index) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) getViewPager(parent).getAdapter();
        Intrinsics.checkNotNull(viewPagerAdapter);
        return viewPagerAdapter.getChildAt(index);
    }

    public final void removeView(NestedScrollableHost parent, View view) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        Intrinsics.checkNotNullParameter(view, "view");
        ViewPager2 viewPager = getViewPager(parent);
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        if (viewPagerAdapter != null) {
            viewPagerAdapter.removeChild(view);
        }
        refreshViewChildrenLayout(viewPager);
    }

    public final void removeAllViews(NestedScrollableHost parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        ViewPager2 viewPager = getViewPager(parent);
        viewPager.setUserInputEnabled(false);
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        if (viewPagerAdapter != null) {
            viewPagerAdapter.removeAll();
        }
    }

    public final void removeViewAt(NestedScrollableHost parent, int index) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        ViewPager2 viewPager = getViewPager(parent);
        ViewPagerAdapter viewPagerAdapter = (ViewPagerAdapter) viewPager.getAdapter();
        if (viewPagerAdapter != null) {
            viewPagerAdapter.removeChildAt(index);
        }
        refreshViewChildrenLayout(viewPager);
    }

    public final void setScrollEnabled(NestedScrollableHost host, boolean value) {
        Intrinsics.checkNotNullParameter(host, "host");
        getViewPager(host).setUserInputEnabled(value);
    }

    public final void setLayoutDirection(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        ViewPager2 viewPager = getViewPager(host);
        if (Intrinsics.areEqual(value, "rtl")) {
            viewPager.setLayoutDirection(1);
        } else {
            viewPager.setLayoutDirection(0);
        }
    }

    public final void setInitialPage(final NestedScrollableHost host, int value) {
        Intrinsics.checkNotNullParameter(host, "host");
        ViewPager2 viewPager = getViewPager(host);
        if (host.getInitialIndex() == null) {
            host.setInitialIndex(Integer.valueOf(value));
            viewPager.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PagerViewViewManagerImpl.setInitialPage$lambda$0(NestedScrollableHost.this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setInitialPage$lambda$0(NestedScrollableHost host) {
        Intrinsics.checkNotNullParameter(host, "$host");
        host.setDidSetInitialIndex(true);
    }

    public final void setOrientation(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        getViewPager(host).setOrientation(Intrinsics.areEqual(value, "vertical") ? 1 : 0);
    }

    public final void setOffscreenPageLimit(NestedScrollableHost host, int value) {
        Intrinsics.checkNotNullParameter(host, "host");
        getViewPager(host).setOffscreenPageLimit(value);
    }

    public final void setOverScrollMode(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        View childAt = getViewPager(host).getChildAt(0);
        if (Intrinsics.areEqual(value, "never")) {
            childAt.setOverScrollMode(2);
        } else if (Intrinsics.areEqual(value, "always")) {
            childAt.setOverScrollMode(0);
        } else {
            childAt.setOverScrollMode(1);
        }
    }

    public final void setPageMargin(NestedScrollableHost host, int margin) {
        Intrinsics.checkNotNullParameter(host, "host");
        final ViewPager2 viewPager = getViewPager(host);
        final int pixelFromDIP = (int) PixelUtil.toPixelFromDIP(margin);
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda1
            @Override // androidx.viewpager2.widget.ViewPager2.PageTransformer
            public final void transformPage(View view, float f) {
                PagerViewViewManagerImpl.setPageMargin$lambda$1(pixelFromDIP, viewPager, view, f);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setPageMargin$lambda$1(int i, ViewPager2 pager, View page, float f) {
        Intrinsics.checkNotNullParameter(pager, "$pager");
        Intrinsics.checkNotNullParameter(page, "page");
        float f2 = i * f;
        if (pager.getOrientation() == 0) {
            if (pager.getLayoutDirection() == 1) {
                f2 = -f2;
            }
            page.setTranslationX(f2);
            return;
        }
        page.setTranslationY(f2);
    }

    private final void refreshViewChildrenLayout(final View view) {
        view.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PagerViewViewManagerImpl.refreshViewChildrenLayout$lambda$2(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void refreshViewChildrenLayout$lambda$2(View view) {
        Intrinsics.checkNotNullParameter(view, "$view");
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(view.getHeight(), 1073741824));
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
}
