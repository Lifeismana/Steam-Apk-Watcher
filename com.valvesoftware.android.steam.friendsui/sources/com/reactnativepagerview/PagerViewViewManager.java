package com.reactnativepagerview;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.viewmanagers.RNCViewPagerManagerDelegate;
import com.facebook.react.viewmanagers.RNCViewPagerManagerInterface;
import com.facebook.soloader.SoLoader;
import com.reactnativepagerview.event.PageScrollEvent;
import com.reactnativepagerview.event.PageScrollStateChangedEvent;
import com.reactnativepagerview.event.PageSelectedEvent;
import expo.modules.updates.codesigning.CodeSigningAlgorithmKt;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PagerViewViewManager.kt */
@ReactModule(name = PagerViewViewManagerImpl.NAME)
@Metadata(m693d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010%\n\u0002\u0010$\n\u0002\b\u0002\b\u0007\u0018\u0000 72\b\u0012\u0004\u0012\u00020\u00020\u00012\b\u0012\u0004\u0012\u00020\u00020\u0003:\u00017B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00020\u0007H\u0014J\b\u0010\t\u001a\u00020\nH\u0016J\"\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\n2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J \u0010\u0014\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\u00022\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u001b\u001a\u00020\u0002H\u0016J\u0018\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0018\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u001e\u001a\u00020\u0017H\u0016J\u0010\u0010\u001f\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u0002H\u0016J\u0018\u0010 \u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u00022\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\b\u0010!\u001a\u00020\"H\u0016J\u001a\u0010#\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010$\u001a\u00020\"H\u0017J\u001c\u0010%\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\b\u0010$\u001a\u0004\u0018\u00010\nH\u0017J\u001a\u0010&\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010$\u001a\u00020\u0019H\u0017J\u001c\u0010'\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\b\u0010$\u001a\u0004\u0018\u00010\nH\u0017J\u001a\u0010(\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010$\u001a\u00020\u0019H\u0017J\u001a\u0010)\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010$\u001a\u00020\u0019H\u0017J\u001c\u0010*\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\b\u0010$\u001a\u0004\u0018\u00010\nH\u0017J\u001a\u0010+\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010$\u001a\u00020\"H\u0017J\u001c\u0010,\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\b\u0010$\u001a\u0004\u0018\u00010\nH\u0017J \u0010-\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u00022\u0006\u0010.\u001a\u00020\u00192\u0006\u0010/\u001a\u00020\"J\u001a\u00100\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010.\u001a\u00020\u0019H\u0016J\u001a\u00101\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u0010.\u001a\u00020\u0019H\u0016J\u001a\u00102\u001a\u00020\f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u00022\u0006\u00103\u001a\u00020\"H\u0016J \u00104\u001a\u001a\u0012\u0004\u0012\u00020\n\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0605H\u0016R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00068"}, m694d2 = {"Lcom/reactnativepagerview/PagerViewViewManager;", "Lcom/facebook/react/uimanager/ViewGroupManager;", "Lcom/reactnativepagerview/NestedScrollableHost;", "Lcom/facebook/react/viewmanagers/RNCViewPagerManagerInterface;", "<init>", "()V", "mDelegate", "Lcom/facebook/react/uimanager/ViewManagerDelegate;", "getDelegate", "getName", "", "receiveCommand", "", CodeSigningAlgorithmKt.CODE_SIGNING_METADATA_DEFAULT_KEY_ID, "commandId", "args", "Lcom/facebook/react/bridge/ReadableArray;", "createViewInstance", "reactContext", "Lcom/facebook/react/uimanager/ThemedReactContext;", "addView", "host", "child", "Landroid/view/View;", "index", "", "getChildCount", "parent", "getChildAt", "removeView", "view", "removeAllViews", "removeViewAt", "needsCustomLayoutForChildren", "", "setScrollEnabled", "value", "setLayoutDirection", "setInitialPage", "setOrientation", "setOffscreenPageLimit", "setPageMargin", "setOverScrollMode", "setOverdrag", "setKeyboardDismissMode", "goTo", "selectedPage", "scrollWithAnimation", "setPage", "setPageWithoutAnimation", "setScrollEnabledImperatively", "scrollEnabled", "getExportedCustomDirectEventTypeConstants", "", "", "Companion", "react-native-pager-view_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
/* loaded from: classes2.dex */
public final class PagerViewViewManager extends ViewGroupManager<NestedScrollableHost> implements RNCViewPagerManagerInterface<NestedScrollableHost> {
    private final ViewManagerDelegate<NestedScrollableHost> mDelegate = new RNCViewPagerManagerDelegate(this);

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(name = "keyboardDismissMode")
    public void setKeyboardDismissMode(NestedScrollableHost view, String value) {
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(name = "overdrag")
    public void setOverdrag(NestedScrollableHost view, boolean value) {
    }

    static {
        if (BuildConfig.CODEGEN_MODULE_REGISTRATION != null) {
            SoLoader.loadLibrary(BuildConfig.CODEGEN_MODULE_REGISTRATION);
        }
    }

    @Override // com.facebook.react.uimanager.ViewManager
    protected ViewManagerDelegate<NestedScrollableHost> getDelegate() {
        return this.mDelegate;
    }

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return PagerViewViewManagerImpl.NAME;
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public void receiveCommand(NestedScrollableHost root, String commandId, ReadableArray args) {
        Intrinsics.checkNotNullParameter(root, "root");
        Intrinsics.checkNotNullParameter(commandId, "commandId");
        this.mDelegate.kotlinCompat$receiveCommand(root, commandId, args);
    }

    @Override // com.facebook.react.uimanager.ViewManager
    public NestedScrollableHost createViewInstance(final ThemedReactContext reactContext) {
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        ThemedReactContext themedReactContext = reactContext;
        final NestedScrollableHost nestedScrollableHost = new NestedScrollableHost(themedReactContext);
        nestedScrollableHost.setId(View.generateViewId());
        nestedScrollableHost.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        nestedScrollableHost.setSaveEnabled(false);
        final ViewPager2 viewPager2 = new ViewPager2(themedReactContext);
        viewPager2.setAdapter(new ViewPagerAdapter());
        viewPager2.setSaveEnabled(false);
        viewPager2.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PagerViewViewManager.createViewInstance$lambda$0(ViewPager2.this, reactContext, nestedScrollableHost);
            }
        });
        nestedScrollableHost.addView(viewPager2);
        return nestedScrollableHost;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void createViewInstance$lambda$0(ViewPager2 viewPager2, final ThemedReactContext themedReactContext, final NestedScrollableHost nestedScrollableHost) {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.reactnativepagerview.PagerViewViewManager$createViewInstance$1$1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(ThemedReactContext.this, nestedScrollableHost.getId());
                if (eventDispatcherForReactTag != null) {
                    eventDispatcherForReactTag.dispatchEvent(new PageScrollEvent(nestedScrollableHost.getId(), position, positionOffset));
                }
            }

            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(ThemedReactContext.this, nestedScrollableHost.getId());
                if (eventDispatcherForReactTag != null) {
                    eventDispatcherForReactTag.dispatchEvent(new PageSelectedEvent(nestedScrollableHost.getId(), position));
                }
            }

            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageScrollStateChanged(int state) {
                String str;
                super.onPageScrollStateChanged(state);
                if (state == 0) {
                    str = "idle";
                } else if (state == 1) {
                    str = "dragging";
                } else if (state == 2) {
                    str = "settling";
                } else {
                    throw new IllegalStateException("Unsupported pageScrollState");
                }
                EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(ThemedReactContext.this, nestedScrollableHost.getId());
                if (eventDispatcherForReactTag != null) {
                    eventDispatcherForReactTag.dispatchEvent(new PageScrollStateChangedEvent(nestedScrollableHost.getId(), str));
                }
            }
        });
        EventDispatcher eventDispatcherForReactTag = UIManagerHelper.getEventDispatcherForReactTag(themedReactContext, nestedScrollableHost.getId());
        if (eventDispatcherForReactTag != null) {
            eventDispatcherForReactTag.dispatchEvent(new PageSelectedEvent(nestedScrollableHost.getId(), viewPager2.getCurrentItem()));
        }
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public void addView(NestedScrollableHost host, View child, int index) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(child, "child");
        PagerViewViewManagerImpl.INSTANCE.addView(host, child, index);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public int getChildCount(NestedScrollableHost parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        return PagerViewViewManagerImpl.INSTANCE.getChildCount(parent);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public View getChildAt(NestedScrollableHost parent, int index) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        return PagerViewViewManagerImpl.INSTANCE.getChildAt(parent, index);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public void removeView(NestedScrollableHost parent, View view) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        Intrinsics.checkNotNullParameter(view, "view");
        PagerViewViewManagerImpl.INSTANCE.removeView(parent, view);
    }

    @Override // com.facebook.react.uimanager.IViewGroupManager
    public void removeAllViews(NestedScrollableHost parent) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        PagerViewViewManagerImpl.INSTANCE.removeAllViews(parent);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public void removeViewAt(NestedScrollableHost parent, int index) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        PagerViewViewManagerImpl.INSTANCE.removeViewAt(parent, index);
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager, com.facebook.react.uimanager.IViewManagerWithChildren
    public boolean needsCustomLayoutForChildren() {
        return PagerViewViewManagerImpl.INSTANCE.needsCustomLayoutForChildren();
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(defaultBoolean = true, name = "scrollEnabled")
    public void setScrollEnabled(NestedScrollableHost view, boolean value) {
        if (view != null) {
            PagerViewViewManagerImpl.INSTANCE.setScrollEnabled(view, value);
        }
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(name = ViewProps.LAYOUT_DIRECTION)
    public void setLayoutDirection(NestedScrollableHost view, String value) {
        if (view == null || value == null) {
            return;
        }
        PagerViewViewManagerImpl.INSTANCE.setLayoutDirection(view, value);
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(defaultInt = 0, name = "initialPage")
    public void setInitialPage(NestedScrollableHost view, int value) {
        if (view != null) {
            PagerViewViewManagerImpl.INSTANCE.setInitialPage(view, value);
        }
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(name = "orientation")
    public void setOrientation(NestedScrollableHost view, String value) {
        if (view == null || value == null) {
            return;
        }
        PagerViewViewManagerImpl.INSTANCE.setOrientation(view, value);
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(defaultInt = -1, name = "offscreenPageLimit")
    public void setOffscreenPageLimit(NestedScrollableHost view, int value) {
        if (view != null) {
            PagerViewViewManagerImpl.INSTANCE.setOffscreenPageLimit(view, value);
        }
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(defaultInt = 0, name = "pageMargin")
    public void setPageMargin(NestedScrollableHost view, int value) {
        if (view != null) {
            PagerViewViewManagerImpl.INSTANCE.setPageMargin(view, value);
        }
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    @ReactProp(name = "overScrollMode")
    public void setOverScrollMode(NestedScrollableHost view, String value) {
        if (view == null || value == null) {
            return;
        }
        PagerViewViewManagerImpl.INSTANCE.setOverScrollMode(view, value);
    }

    public final void goTo(NestedScrollableHost root, int selectedPage, boolean scrollWithAnimation) {
        if (root == null) {
            return;
        }
        ViewPager2 viewPager = PagerViewViewManagerImpl.INSTANCE.getViewPager(root);
        Assertions.assertNotNull(viewPager);
        RecyclerView.Adapter adapter = viewPager.getAdapter();
        Integer valueOf = adapter != null ? Integer.valueOf(adapter.getItemCount()) : null;
        if (valueOf == null || valueOf.intValue() <= 0 || selectedPage < 0 || selectedPage >= valueOf.intValue()) {
            return;
        }
        PagerViewViewManagerImpl.INSTANCE.setCurrentItem(viewPager, selectedPage, scrollWithAnimation);
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    public void setPage(NestedScrollableHost view, int selectedPage) {
        goTo(view, selectedPage, true);
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    public void setPageWithoutAnimation(NestedScrollableHost view, int selectedPage) {
        goTo(view, selectedPage, false);
    }

    @Override // com.facebook.react.viewmanagers.RNCViewPagerManagerInterface
    public void setScrollEnabledImperatively(NestedScrollableHost view, boolean scrollEnabled) {
        if (view != null) {
            PagerViewViewManagerImpl.INSTANCE.setScrollEnabled(view, scrollEnabled);
        }
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Map<String, String>> getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> m174of = MapBuilder.m174of(PageScrollEvent.EVENT_NAME, MapBuilder.m172of("registrationName", "onPageScroll"), PageScrollStateChangedEvent.EVENT_NAME, MapBuilder.m172of("registrationName", "onPageScrollStateChanged"), PageSelectedEvent.EVENT_NAME, MapBuilder.m172of("registrationName", "onPageSelected"));
        Intrinsics.checkNotNullExpressionValue(m174of, "of(...)");
        return m174of;
    }
}
