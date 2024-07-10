package com.valvesoftware.android.steam.community.fragment;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;

/* loaded from: classes.dex */
public class NavDrawerNotificationItem extends NavDrawerItem {
    protected boolean m_hideWhenCountIsZero;
    protected int m_notificationCount;

    public NavDrawerNotificationItem(int i, int i2, Intent intent, DrawerLayout drawerLayout, boolean z) {
        super(i, 0, i2, intent, drawerLayout);
        this.m_hideWhenCountIsZero = z;
    }

    public void setNotificationCount(int i) {
        this.m_notificationCount = i;
        if (this.m_view != null) {
            TextView textView = (TextView) this.m_view.findViewById(R.id.nav_item_name);
            textView.setText(getNameId());
            textView.append(" ");
            textView.append(Integer.valueOf(getNotificationCount()).toString());
        }
    }

    public int getNotificationCount() {
        return this.m_notificationCount;
    }

    @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerItem
    public boolean isHidden() {
        return this.m_hideWhenCountIsZero && this.m_notificationCount == 0;
    }

    /* loaded from: classes.dex */
    public static class NavDrawerGroupItem extends NavDrawerNotificationItem {
        public NavDrawerGroupItem(int i, int i2, DrawerLayout drawerLayout) {
            super(i, i2, null, drawerLayout, false);
        }

        @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerItem
        public void setView(View view) {
            super.setView(view);
            setNotificationCount(this.m_notificationCount);
        }

        @Override // com.valvesoftware.android.steam.community.fragment.NavDrawerNotificationItem
        public void setNotificationCount(int i) {
            this.m_notificationCount = i;
            if (this.m_view != null) {
                TextView textView = (TextView) this.m_view.findViewById(R.id.nav_item_notification_count);
                View findViewById = this.m_view.findViewById(R.id.nav_item_notification_count_ctn);
                if (textView == null || findViewById == null) {
                    return;
                }
                textView.setText(Integer.valueOf(getNotificationCount()).toString());
                if (this.m_notificationCount > 0) {
                    findViewById.setVisibility(0);
                } else {
                    findViewById.setVisibility(8);
                }
            }
        }
    }
}
