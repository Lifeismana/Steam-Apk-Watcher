package com.valvesoftware.android.steam.community.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;
import java.util.List;

/* loaded from: classes.dex */
public class NavDrawerListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<NavDrawerItem> navigationItems;

    @Override // android.widget.ExpandableListAdapter
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override // android.widget.ExpandableListAdapter
    public long getGroupId(int i) {
        return i;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return false;
    }

    @Override // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public NavDrawerListAdapter(Context context, List<NavDrawerItem> list) {
        this.context = context;
        this.navigationItems = list;
        this.layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
    }

    @Override // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.navigationItems.size();
    }

    @Override // android.widget.ExpandableListAdapter
    public int getChildrenCount(int i) {
        if (i >= this.navigationItems.size()) {
            return 0;
        }
        return this.navigationItems.get(i).getChildrenCount();
    }

    @Override // android.widget.ExpandableListAdapter
    public NavDrawerItem getGroup(int i) {
        if (i >= this.navigationItems.size()) {
            return null;
        }
        return this.navigationItems.get(i);
    }

    @Override // android.widget.ExpandableListAdapter
    public NavDrawerItem getChild(int i, int i2) {
        NavDrawerItem navDrawerItem;
        if (i < this.navigationItems.size() && (navDrawerItem = this.navigationItems.get(i)) != null && i2 < navDrawerItem.getChildrenCount()) {
            return navDrawerItem.getChild(i2);
        }
        return null;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getGroupView(final int i, boolean z, View view, ViewGroup viewGroup) {
        final NavDrawerItem group = getGroup(i);
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.nav_group_list_item, (ViewGroup) null);
        } else {
            NavDrawerItem navDrawerItem = (NavDrawerItem) view.getTag();
            if (navDrawerItem != null) {
                navDrawerItem.setView(null);
            }
            View findViewById = view.findViewById(R.id.nav_item_notification_count_ctn);
            if (findViewById != null) {
                findViewById.setVisibility(8);
            }
        }
        view.setTag(group);
        group.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.nav_item_name);
        ImageView imageView = (ImageView) view.findViewById(R.id.group_expand_collapse_icon);
        if (!group.hasChildren()) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.NavDrawerListAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    String str = String.format("position: %d ", Integer.valueOf(i)) + NavDrawerListAdapter.this.context.getResources().getString(group.getNameId());
                    group.onClick();
                }
            });
        } else {
            view.setClickable(false);
        }
        if (!group.hasChildren()) {
            imageView.setVisibility(8);
        } else {
            if (z) {
                imageView.setImageResource(R.drawable.ic_action_expand_less);
            } else {
                imageView.setImageResource(R.drawable.ic_action_expand_more);
            }
            imageView.setVisibility(0);
        }
        textView.setText(group.getNameId());
        return view;
    }

    @Override // android.widget.ExpandableListAdapter
    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        final NavDrawerItem child = getChild(i, i2);
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.nav_list_item, (ViewGroup) null);
        } else {
            NavDrawerItem navDrawerItem = (NavDrawerItem) view.getTag();
            if (navDrawerItem != null) {
                navDrawerItem.setView(null);
            }
        }
        view.setTag(child);
        child.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.nav_item_name);
        view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.NavDrawerListAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                child.onClick();
            }
        });
        if (child.isHidden()) {
            view.setVisibility(8);
        }
        textView.setText(child.getNameId());
        if (child instanceof NavDrawerNotificationItem) {
            textView.append(" ");
            textView.append(Integer.valueOf(((NavDrawerNotificationItem) child).getNotificationCount()).toString());
        }
        return view;
    }
}
