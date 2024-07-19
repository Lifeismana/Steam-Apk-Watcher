package com.reactnativepagerview;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.reactnativepagerview.event.PageScrollEvent;
import com.reactnativepagerview.event.PageScrollStateChangedEvent;
import com.reactnativepagerview.event.PageSelectedEvent;
import expo.modules.updates.codesigning.CodeSigningAlgorithmKt;
import java.util.Arrays;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;

/* compiled from: PagerViewViewManager.kt */
@Metadata(m534d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000 ,2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001,B\u0005¢\u0006\u0002\u0010\u0003J\"\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000fH\u0014J\u0018\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0010\u0010\u0012\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0002H\u0016J \u0010\u0013\u001a\u001a\u0012\u0004\u0012\u00020\u0015\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00150\u00160\u0014H\u0016J\b\u0010\u0017\u001a\u00020\u0015H\u0016J\b\u0010\u0018\u001a\u00020\u0019H\u0016J$\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u00022\b\u0010\u001c\u001a\u0004\u0018\u00010\u00152\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0016J\u0010\u0010\u001f\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0002H\u0016J\u0018\u0010 \u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010!\u001a\u00020\nH\u0016J\u0018\u0010\"\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0019\u0010#\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\fH\u0087\u0002J\u0018\u0010%\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\fH\u0007J\u0018\u0010&\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0015H\u0007J\u0018\u0010'\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0015H\u0007J\u0018\u0010(\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0015H\u0007J\u0018\u0010)\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010*\u001a\u00020\fH\u0007J\u0018\u0010+\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0019H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006-"}, m535d2 = {"Lcom/reactnativepagerview/PagerViewViewManager;", "Lcom/facebook/react/uimanager/ViewGroupManager;", "Lcom/reactnativepagerview/NestedScrollableHost;", "()V", "eventDispatcher", "Lcom/facebook/react/uimanager/events/EventDispatcher;", "addView", "", "host", "child", "Landroid/view/View;", "index", "", "createViewInstance", "reactContext", "Lcom/facebook/react/uimanager/ThemedReactContext;", "getChildAt", "parent", "getChildCount", "getExportedCustomDirectEventTypeConstants", "", "", "", "getName", "needsCustomLayoutForChildren", "", "receiveCommand", CodeSigningAlgorithmKt.CODE_SIGNING_METADATA_DEFAULT_KEY_ID, "commandId", "args", "Lcom/facebook/react/bridge/ReadableArray;", "removeAllViews", "removeView", "view", "removeViewAt", "set", "value", "setInitialPage", "setLayoutDirection", "setOrientation", "setOverScrollMode", "setPageMargin", ViewProps.MARGIN, "setScrollEnabled", "Companion", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
/* loaded from: classes2.dex */
public final class PagerViewViewManager extends ViewGroupManager<NestedScrollableHost> {
    private static final String COMMAND_SET_PAGE = "setPage";
    private static final String COMMAND_SET_PAGE_WITHOUT_ANIMATION = "setPageWithoutAnimation";
    private static final String COMMAND_SET_SCROLL_ENABLED = "setScrollEnabledImperatively";
    private EventDispatcher eventDispatcher;

    @Override // com.facebook.react.uimanager.ViewManager, com.facebook.react.bridge.NativeModule
    public String getName() {
        return PagerViewViewManagerImpl.NAME;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.react.uimanager.ViewManager
    public NestedScrollableHost createViewInstance(ThemedReactContext reactContext) {
        Intrinsics.checkNotNullParameter(reactContext, "reactContext");
        ThemedReactContext themedReactContext = reactContext;
        final NestedScrollableHost nestedScrollableHost = new NestedScrollableHost(themedReactContext);
        nestedScrollableHost.setId(View.generateViewId());
        nestedScrollableHost.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        nestedScrollableHost.setSaveEnabled(false);
        final ViewPager2 viewPager2 = new ViewPager2(themedReactContext);
        viewPager2.setAdapter(new ViewPagerAdapter());
        viewPager2.setSaveEnabled(false);
        NativeModule nativeModule = reactContext.getNativeModule(UIManagerModule.class);
        Intrinsics.checkNotNull(nativeModule);
        EventDispatcher eventDispatcher = ((UIManagerModule) nativeModule).getEventDispatcher();
        Intrinsics.checkNotNullExpressionValue(eventDispatcher, "reactContext.getNativeMo…s.java)!!.eventDispatcher");
        this.eventDispatcher = eventDispatcher;
        viewPager2.post(new Runnable() { // from class: com.reactnativepagerview.PagerViewViewManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PagerViewViewManager.createViewInstance$lambda$0(ViewPager2.this, this, nestedScrollableHost);
            }
        });
        nestedScrollableHost.addView(viewPager2);
        return nestedScrollableHost;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void createViewInstance$lambda$0(ViewPager2 vp, final PagerViewViewManager this$0, final NestedScrollableHost host) {
        Intrinsics.checkNotNullParameter(vp, "$vp");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(host, "$host");
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.reactnativepagerview.PagerViewViewManager$createViewInstance$1$1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                EventDispatcher eventDispatcher;
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                eventDispatcher = PagerViewViewManager.this.eventDispatcher;
                if (eventDispatcher == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("eventDispatcher");
                    eventDispatcher = null;
                }
                eventDispatcher.dispatchEvent(new PageScrollEvent(host.getId(), position, positionOffset));
            }

            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int position) {
                EventDispatcher eventDispatcher;
                super.onPageSelected(position);
                eventDispatcher = PagerViewViewManager.this.eventDispatcher;
                if (eventDispatcher == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("eventDispatcher");
                    eventDispatcher = null;
                }
                eventDispatcher.dispatchEvent(new PageSelectedEvent(host.getId(), position));
            }

            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageScrollStateChanged(int state) {
                String str;
                EventDispatcher eventDispatcher;
                super.onPageScrollStateChanged(state);
                if (state == 0) {
                    str = "idle";
                } else if (state == 1) {
                    str = "dragging";
                } else {
                    if (state != 2) {
                        throw new IllegalStateException("Unsupported pageScrollState");
                    }
                    str = "settling";
                }
                eventDispatcher = PagerViewViewManager.this.eventDispatcher;
                if (eventDispatcher == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("eventDispatcher");
                    eventDispatcher = null;
                }
                eventDispatcher.dispatchEvent(new PageScrollStateChangedEvent(host.getId(), str));
            }
        });
        EventDispatcher eventDispatcher = this$0.eventDispatcher;
        if (eventDispatcher == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eventDispatcher");
            eventDispatcher = null;
        }
        eventDispatcher.dispatchEvent(new PageSelectedEvent(host.getId(), vp.getCurrentItem()));
    }

    @Override // com.facebook.react.uimanager.ViewGroupManager
    public void addView(NestedScrollableHost host, View child, int index) {
        Intrinsics.checkNotNullParameter(host, "host");
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

    @Override // com.facebook.react.uimanager.ViewGroupManager
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

    @ReactProp(defaultBoolean = true, name = "scrollEnabled")
    public final void setScrollEnabled(NestedScrollableHost host, boolean value) {
        Intrinsics.checkNotNullParameter(host, "host");
        PagerViewViewManagerImpl.INSTANCE.setScrollEnabled(host, value);
    }

    @ReactProp(defaultInt = 0, name = "initialPage")
    public final void setInitialPage(NestedScrollableHost host, int value) {
        Intrinsics.checkNotNullParameter(host, "host");
        PagerViewViewManagerImpl.INSTANCE.setInitialPage(host, value);
    }

    @ReactProp(name = "orientation")
    public final void setOrientation(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        PagerViewViewManagerImpl.INSTANCE.setOrientation(host, value);
    }

    @ReactProp(defaultInt = -1, name = "offscreenPageLimit")
    public final void set(NestedScrollableHost host, int value) {
        Intrinsics.checkNotNullParameter(host, "host");
        PagerViewViewManagerImpl.INSTANCE.setOffscreenPageLimit(host, value);
    }

    @ReactProp(name = "overScrollMode")
    public final void setOverScrollMode(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        PagerViewViewManagerImpl.INSTANCE.setOverScrollMode(host, value);
    }

    @ReactProp(name = ViewProps.LAYOUT_DIRECTION)
    public final void setLayoutDirection(NestedScrollableHost host, String value) {
        Intrinsics.checkNotNullParameter(host, "host");
        Intrinsics.checkNotNullParameter(value, "value");
        PagerViewViewManagerImpl.INSTANCE.setLayoutDirection(host, value);
    }

    @Override // com.facebook.react.uimanager.BaseViewManager, com.facebook.react.uimanager.ViewManager
    public Map<String, Map<String, String>> getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> m164of = MapBuilder.m164of(PageScrollEvent.EVENT_NAME, MapBuilder.m162of("registrationName", "onPageScroll"), PageScrollStateChangedEvent.EVENT_NAME, MapBuilder.m162of("registrationName", "onPageScrollStateChanged"), PageSelectedEvent.EVENT_NAME, MapBuilder.m162of("registrationName", "onPageSelected"));
        Intrinsics.checkNotNullExpressionValue(m164of, "of(\n                Page…Name\", \"onPageSelected\"))");
        return m164of;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0045, code lost:
    
        if (r10.equals(com.reactnativepagerview.PagerViewViewManager.COMMAND_SET_PAGE) != false) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0063, code lost:
    
        kotlin.jvm.internal.Intrinsics.checkNotNull(r11);
        r11 = r11.getInt(0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x006a, code lost:
    
        if (r1 == null) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0070, code lost:
    
        if (r1.intValue() <= 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0072, code lost:
    
        if (r11 < 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0078, code lost:
    
        if (r11 >= r1.intValue()) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x007c, code lost:
    
        if (r3 == false) goto L41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x007e, code lost:
    
        com.reactnativepagerview.PagerViewViewManagerImpl.INSTANCE.setCurrentItem(r0, r11, kotlin.jvm.internal.Intrinsics.areEqual(r10, com.reactnativepagerview.PagerViewViewManager.COMMAND_SET_PAGE));
        r10 = r8.eventDispatcher;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0089, code lost:
    
        if (r10 != null) goto L35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x008b, code lost:
    
        kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException("eventDispatcher");
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0092, code lost:
    
        r2.dispatchEvent(new com.reactnativepagerview.event.PageSelectedEvent(r9.getId(), r11));
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x00a0, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0091, code lost:
    
        r2 = r10;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x007b, code lost:
    
        r3 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0061, code lost:
    
        if (r10.equals(com.reactnativepagerview.PagerViewViewManager.COMMAND_SET_PAGE_WITHOUT_ANIMATION) != false) goto L22;
     */
    @Override // com.facebook.react.uimanager.ViewManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void receiveCommand(NestedScrollableHost root, String commandId, ReadableArray args) {
        Intrinsics.checkNotNullParameter(root, "root");
        super.receiveCommand((PagerViewViewManager) root, commandId, args);
        ViewPager2 viewPager = PagerViewViewManagerImpl.INSTANCE.getViewPager(root);
        Assertions.assertNotNull(viewPager);
        Assertions.assertNotNull(args);
        RecyclerView.Adapter adapter = viewPager.getAdapter();
        EventDispatcher eventDispatcher = null;
        Integer valueOf = adapter != null ? Integer.valueOf(adapter.getItemCount()) : null;
        boolean z = true;
        if (commandId != null) {
            int hashCode = commandId.hashCode();
            if (hashCode != -445763635) {
                if (hashCode != 1747675147) {
                    if (hashCode == 1984860689) {
                    }
                } else if (commandId.equals(COMMAND_SET_SCROLL_ENABLED)) {
                    Intrinsics.checkNotNull(args);
                    viewPager.setUserInputEnabled(args.getBoolean(0));
                    return;
                }
            }
        }
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String format = String.format("Unsupported command %d received by %s.", Arrays.copyOf(new Object[]{commandId, getClass().getSimpleName()}, 2));
        Intrinsics.checkNotNullExpressionValue(format, "format(format, *args)");
        throw new IllegalArgumentException(format);
    }

    @ReactProp(defaultInt = 0, name = "pageMargin")
    public final void setPageMargin(NestedScrollableHost host, int margin) {
        Intrinsics.checkNotNullParameter(host, "host");
        PagerViewViewManagerImpl.INSTANCE.setPageMargin(host, margin);
    }
}
