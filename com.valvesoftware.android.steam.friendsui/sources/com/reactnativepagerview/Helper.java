package com.reactnativepagerview;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import com.facebook.react.bridge.ReactContext;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Helper.kt */
@Metadata(m693d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"}, m694d2 = {"Lcom/reactnativepagerview/Helper;", "", "<init>", "()V", "Companion", "react-native-pager-view_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
/* loaded from: classes2.dex */
public final class Helper {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);

    /* compiled from: Helper.kt */
    @Metadata(m693d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"}, m694d2 = {"Lcom/reactnativepagerview/Helper$Companion;", "", "<init>", "()V", "getReactContext", "Lcom/facebook/react/bridge/ReactContext;", "view", "Landroid/view/View;", "react-native-pager-view_release"}, m695k = 1, m696mv = {2, 0, 0}, m698xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ReactContext getReactContext(View view) {
            Intrinsics.checkNotNullParameter(view, "view");
            Context context = view.getContext();
            Intrinsics.checkNotNullExpressionValue(context, "getContext(...)");
            if (!(context instanceof ReactContext) && (context instanceof ContextWrapper)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            if (context instanceof ReactContext) {
                return (ReactContext) context;
            }
            return null;
        }
    }
}
