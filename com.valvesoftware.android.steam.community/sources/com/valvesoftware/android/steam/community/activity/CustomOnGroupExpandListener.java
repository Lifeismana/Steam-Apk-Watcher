package com.valvesoftware.android.steam.community.activity;

import android.widget.ExpandableListView;

/* compiled from: BaseActivity.java */
/* loaded from: classes.dex */
class CustomOnGroupExpandListener implements ExpandableListView.OnGroupExpandListener {
    private static int prevExpandedGroup = -1;
    private final ExpandableListView expandableListView;

    public CustomOnGroupExpandListener(ExpandableListView expandableListView) {
        this.expandableListView = expandableListView;
    }

    @Override // android.widget.ExpandableListView.OnGroupExpandListener
    public void onGroupExpand(int i) {
        ExpandableListView expandableListView;
        int i2 = prevExpandedGroup;
        if (i2 != -1 && i2 != i && (expandableListView = this.expandableListView) != null) {
            expandableListView.collapseGroup(i2);
        }
        prevExpandedGroup = i;
    }
}
