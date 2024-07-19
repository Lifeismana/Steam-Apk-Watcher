package com.reactnativepagerview.event;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PageScrollStateChangedEvent.kt */
@Metadata(m534d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u000e2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u000eB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\u0005H\u0016J\b\u0010\f\u001a\u00020\rH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, m535d2 = {"Lcom/reactnativepagerview/event/PageScrollStateChangedEvent;", "Lcom/facebook/react/uimanager/events/Event;", "viewTag", "", "mPageScrollState", "", "(ILjava/lang/String;)V", "dispatch", "", "rctEventEmitter", "Lcom/facebook/react/uimanager/events/RCTEventEmitter;", "getEventName", "serializeEventData", "Lcom/facebook/react/bridge/WritableMap;", "Companion", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
/* loaded from: classes2.dex */
public final class PageScrollStateChangedEvent extends Event<PageScrollStateChangedEvent> {
    public static final String EVENT_NAME = "topPageScrollStateChanged";
    private final String mPageScrollState;

    @Override // com.facebook.react.uimanager.events.Event
    public String getEventName() {
        return EVENT_NAME;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public PageScrollStateChangedEvent(int i, String mPageScrollState) {
        super(i);
        Intrinsics.checkNotNullParameter(mPageScrollState, "mPageScrollState");
        this.mPageScrollState = mPageScrollState;
    }

    @Override // com.facebook.react.uimanager.events.Event
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        Intrinsics.checkNotNullParameter(rctEventEmitter, "rctEventEmitter");
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    private final WritableMap serializeEventData() {
        WritableMap eventData = Arguments.createMap();
        eventData.putString("pageScrollState", this.mPageScrollState);
        Intrinsics.checkNotNullExpressionValue(eventData, "eventData");
        return eventData;
    }
}
