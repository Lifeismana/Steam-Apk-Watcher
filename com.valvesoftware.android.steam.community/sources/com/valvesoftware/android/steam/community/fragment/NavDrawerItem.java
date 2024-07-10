package com.valvesoftware.android.steam.community.fragment;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class NavDrawerItem {
    protected final List<NavDrawerItem> children = new ArrayList();
    private final DrawerLayout drawerLayout;
    private final int groupId;
    private final int iconId;
    protected View m_view;
    private int nameId;
    private final Intent onClickIntent;

    public boolean isHidden() {
        return false;
    }

    public NavDrawerItem(int i, int i2, int i3, Intent intent, DrawerLayout drawerLayout) {
        this.groupId = i;
        this.iconId = i2;
        this.nameId = i3;
        this.drawerLayout = drawerLayout;
        this.onClickIntent = intent;
        Intent intent2 = this.onClickIntent;
        if (intent2 != null) {
            intent2.addFlags(268435456);
        }
    }

    public void onClick() {
        if (this.onClickIntent == null) {
            return;
        }
        SteamCommunityApplication.GetInstance().startActivity(this.onClickIntent);
        DrawerLayout drawerLayout = this.drawerLayout;
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
    }

    public int getNameId() {
        return this.nameId;
    }

    public void add(NavDrawerItem navDrawerItem) {
        this.children.add(navDrawerItem);
    }

    public int getChildrenCount() {
        List<NavDrawerItem> list = this.children;
        int i = 0;
        if (list == null) {
            return 0;
        }
        Iterator<NavDrawerItem> it = list.iterator();
        while (it.hasNext()) {
            if (!it.next().isHidden()) {
                i++;
            }
        }
        return i;
    }

    public NavDrawerItem getChild(int i) {
        List<NavDrawerItem> list = this.children;
        if (list == null) {
            return null;
        }
        int i2 = 0;
        for (NavDrawerItem navDrawerItem : list) {
            if (!navDrawerItem.isHidden()) {
                if (i2 == i) {
                    return navDrawerItem;
                }
                i2++;
            }
        }
        return null;
    }

    public boolean hasChildren() {
        return getChildrenCount() > 0;
    }

    public void setView(View view) {
        this.m_view = view;
    }
}
