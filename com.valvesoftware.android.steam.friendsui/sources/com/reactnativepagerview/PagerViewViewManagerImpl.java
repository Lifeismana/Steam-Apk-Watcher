package com.reactnativepagerview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PagerViewViewManagerImpl.kt */
@Metadata(m693d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0013\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ \u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\rJ\u000e\u0010\u0015\u001a\u00020\r2\u0006\u0010\u0016\u001a\u00020\tJ\u0016\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\rJ\u0016\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0013J\u000e\u0010\u0019\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\tJ\u0016\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0014\u001a\u00020\rJ\u0006\u0010\u001b\u001a\u00020\u000fJ\u0016\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u000fJ\u0016\u0010\u001e\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u0005J\u0016\u0010\u001f\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\rJ\u0016\u0010 \u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u0005J\u0016\u0010!\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\rJ\u0016\u0010\"\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u0005J\u0016\u0010#\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\t2\u0006\u0010$\u001a\u00020\rJ\u0010\u0010%\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\u0013H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006&"}, m694d2 = {"Lcom/reactnativepagerview/PagerViewViewManagerImpl;", "", "<init>", "()V", "NAME", "", "getViewPager", "Landroidx/viewpager2/widget/ViewPager2;", "view", "Lcom/reactnativepagerview/NestedScrollableHost;", "setCurrentItem", "", "selectedTab", "", "scrollSmooth", "", "addView", "host", "child", "Landroid/view/View;", "index", "getChildCount", "parent", "getChildAt", "removeView", "removeAllViews", "removeViewAt", "needsCustomLayoutForChildren", "setScrollEnabled", "value", "setLayoutDirection", "setInitialPage", "setOrientation", "setOffscreenPageLimit", "setOverScrollMode", "setPageMargin", ViewProps.MARGIN, "refreshViewChildrenLayout", "react-native-pager-view_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
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
        View childAt = viewPagerAdapter != null ? viewPagerAdapter.getChildAt(index) : null;
        if (childAt != null && childAt.getParent() != null) {
            ViewParent parent2 = childAt.getParent();
            ViewGroup viewGroup = parent2 instanceof ViewGroup ? (ViewGroup) parent2 : null;
            if (viewGroup != null) {
                viewGroup.removeView(childAt);
            }
        }
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
            viewPager.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NestedScrollableHost.this.setDidSetInitialIndex(true);
                }
            });
        }
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
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda0
            @Override // androidx.viewpager2.widget.ViewPager2.PageTransformer
            public final void transformPage(View view, float f) {
                PagerViewViewManagerImpl.setPageMargin$lambda$1(pixelFromDIP, viewPager, view, f);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setPageMargin$lambda$1(int i, ViewPager2 viewPager2, View page, float f) {
        Intrinsics.checkNotNullParameter(page, "page");
        float f2 = i * f;
        if (viewPager2.getOrientation() == 0) {
            if (viewPager2.getLayoutDirection() == 1) {
                f2 = -f2;
            }
            page.setTranslationX(f2);
            return;
        }
        page.setTranslationY(f2);
    }

    private final void refreshViewChildrenLayout(final View view) {
        view.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManagerImpl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PagerViewViewManagerImpl.refreshViewChildrenLayout$lambda$2(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void refreshViewChildrenLayout$lambda$2(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(view.getHeight(), 1073741824));
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }
}
