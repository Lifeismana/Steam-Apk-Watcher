package com.reactnativepagerview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ViewPagerAdapter.kt */
@Metadata(m534d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0003J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\r\u001a\u00020\u000bH\u0016J\u0018\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016J\u0006\u0010\u0014\u001a\u00020\bJ\u000e\u0010\u0015\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006J\u000e\u0010\u0016\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"}, m535d2 = {"Lcom/reactnativepagerview/ViewPagerAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/reactnativepagerview/ViewPagerViewHolder;", "()V", "childrenViews", "Ljava/util/ArrayList;", "Landroid/view/View;", "addChild", "", "child", "index", "", "getChildAt", "getItemCount", "onBindViewHolder", "holder", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "removeAll", "removeChild", "removeChildAt", "react-native-pager-view_release"}, m536k = 1, m537mv = {1, 8, 0}, m539xi = 48)
/* loaded from: classes2.dex */
public final class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerViewHolder> {
    private final ArrayList<View> childrenViews = new ArrayList<>();

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewPagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        return ViewPagerViewHolder.INSTANCE.create(parent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewPagerViewHolder holder, int index) {
        Intrinsics.checkNotNullParameter(holder, "holder");
        FrameLayout container = holder.getContainer();
        View childAt = getChildAt(index);
        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }
        if (childAt.getParent() != null) {
            ViewParent parent = childAt.getParent();
            Intrinsics.checkNotNull(parent, "null cannot be cast to non-null type android.widget.FrameLayout");
            ((FrameLayout) parent).removeView(childAt);
        }
        container.addView(childAt);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.childrenViews.size();
    }

    public final void addChild(View child, int index) {
        Intrinsics.checkNotNullParameter(child, "child");
        this.childrenViews.add(index, child);
        notifyItemInserted(index);
    }

    public final View getChildAt(int index) {
        View view = this.childrenViews.get(index);
        Intrinsics.checkNotNullExpressionValue(view, "childrenViews[index]");
        return view;
    }

    public final void removeChild(View child) {
        Intrinsics.checkNotNullParameter(child, "child");
        int indexOf = this.childrenViews.indexOf(child);
        if (indexOf > -1) {
            removeChildAt(indexOf);
        }
    }

    public final void removeAll() {
        int size = this.childrenViews.size();
        int i = 1;
        if (1 <= size) {
            while (true) {
                View view = this.childrenViews.get(i - 1);
                Intrinsics.checkNotNullExpressionValue(view, "childrenViews[index-1]");
                View view2 = view;
                ViewParent parent = view2.getParent();
                if ((parent != null ? parent.getParent() : null) != null) {
                    ViewParent parent2 = view2.getParent().getParent();
                    Intrinsics.checkNotNull(parent2, "null cannot be cast to non-null type android.view.ViewGroup");
                    Object parent3 = view2.getParent();
                    Intrinsics.checkNotNull(parent3, "null cannot be cast to non-null type android.view.View");
                    ((ViewGroup) parent2).removeView((View) parent3);
                }
                if (i == size) {
                    break;
                } else {
                    i++;
                }
            }
        }
        int size2 = this.childrenViews.size();
        this.childrenViews.clear();
        notifyItemRangeRemoved(0, size2);
    }

    public final void removeChildAt(int index) {
        this.childrenViews.remove(index);
        notifyItemRemoved(index);
    }
}
