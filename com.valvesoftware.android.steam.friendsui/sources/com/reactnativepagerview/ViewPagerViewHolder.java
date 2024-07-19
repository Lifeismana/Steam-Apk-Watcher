package com.reactnativepagerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ViewPagerViewHolder.kt */
@Metadata(m534d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006¨\u0006\b"}, m535d2 = {"Lcom/reactnativepagerview/ViewPagerViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", TtmlNode.RUBY_CONTAINER, "Landroid/widget/FrameLayout;", "(Landroid/widget/FrameLayout;)V", "getContainer", "()Landroid/widget/FrameLayout;", "Companion", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
/* loaded from: classes2.dex */
public final class ViewPagerViewHolder extends RecyclerView.ViewHolder {

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);

    public /* synthetic */ ViewPagerViewHolder(FrameLayout frameLayout, DefaultConstructorMarker defaultConstructorMarker) {
        this(frameLayout);
    }

    private ViewPagerViewHolder(FrameLayout frameLayout) {
        super(frameLayout);
    }

    public final FrameLayout getContainer() {
        View view = this.itemView;
        Intrinsics.checkNotNull(view, "null cannot be cast to non-null type android.widget.FrameLayout");
        return (FrameLayout) view;
    }

    /* compiled from: ViewPagerViewHolder.kt */
    @Metadata(m534d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, m535d2 = {"Lcom/reactnativepagerview/ViewPagerViewHolder$Companion;", "", "()V", "create", "Lcom/reactnativepagerview/ViewPagerViewHolder;", "parent", "Landroid/view/ViewGroup;", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
    /* loaded from: classes2.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ViewPagerViewHolder create(ViewGroup parent) {
            Intrinsics.checkNotNullParameter(parent, "parent");
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            frameLayout.setSaveEnabled(false);
            return new ViewPagerViewHolder(frameLayout, null);
        }
    }
}
