package com.valvesoftware.android.steam.community.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.valvesoftware.android.steam.community.AndroidUtils;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamAppUri;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.model.Group;
import com.valvesoftware.android.steam.community.model.GroupCategoryInList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class GroupsListAdapter extends BaseAdapter implements FilterableAdapter<Group> {
    private final View.OnClickListener dummyClickListener;
    private final boolean groupAndLabelByStatus;
    private GenericFilter groupInfoFilter;
    private View.OnClickListener groupListClickListener;
    private List<Group> groupsList;
    private String groupsSearchString;
    private Map<String, Group> idsToGroupsMap;
    private final ImageLoader imageLoader;
    private final Group searchItemInfo;
    private String searchString;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public GroupsListAdapter(List<Group> list, FragmentActivity fragmentActivity) {
        this(list, fragmentActivity, true);
    }

    public GroupsListAdapter(List<Group> list, final FragmentActivity fragmentActivity, boolean z) {
        this.groupsSearchString = "";
        this.idsToGroupsMap = new HashMap();
        this.groupsList = new ArrayList(list);
        this.imageLoader = SteamCommunityApplication.GetInstance().imageLoader;
        this.groupsSearchString = fragmentActivity.getResources().getString(R.string.Group_Search_All);
        this.groupAndLabelByStatus = z;
        for (Group group : list) {
            this.idsToGroupsMap.put(group.steamId, group);
        }
        this.dummyClickListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupsListAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        };
        this.groupListClickListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupsListAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                GroupViewHolder groupViewHolder = (GroupViewHolder) view.getTag();
                if ((groupViewHolder.group != null ? groupViewHolder.group.steamId : "0").equals("0")) {
                    fragmentActivity.startActivity(new Intent().setClass(fragmentActivity, MainActivity.class).addFlags(536870912).addFlags(268435456).setData(SteamAppUri.createGroupsSearchUri(GroupsListAdapter.this.getSearchString())));
                    return;
                }
                Group group2 = groupViewHolder.group;
                if (group2 == null) {
                    return;
                }
                fragmentActivity.startActivity(new Intent().addFlags(402653184).setClass(fragmentActivity, MainActivity.class).setData(SteamAppUri.groupWebPage(group2.profileUrl)).setAction("android.intent.action.VIEW"));
            }
        };
        this.searchItemInfo = createSearchItem();
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String str) {
        if (str != null && str.length() > 0 && this.groupInfoFilter == null) {
            this.groupInfoFilter = new GenericFilter(this.groupsList, this, this.searchItemInfo);
        }
        if (this.groupInfoFilter == null) {
            return;
        }
        if (str != null && str.length() > 0) {
            this.groupInfoFilter.removeFromOriginal(this.searchItemInfo);
            this.groupInfoFilter.addToOriginal(this.searchItemInfo);
        } else {
            this.groupInfoFilter.removeFromOriginal(this.searchItemInfo);
        }
        this.searchString = str;
        this.groupInfoFilter.filter(this.searchString);
    }

    @Override // android.widget.BaseAdapter, com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void notifyDataSetChanged() {
        Collections.sort(this.groupsList, new Comparator<Group>() { // from class: com.valvesoftware.android.steam.community.fragment.GroupsListAdapter.3
            @Override // java.util.Comparator
            public int compare(Group group, Group group2) {
                if (group.categoryInList != group2.categoryInList) {
                    return group.categoryInList.ordinal() < group2.categoryInList.ordinal() ? -1 : 1;
                }
                return group.name.compareToIgnoreCase(group2.name);
            }
        });
        super.notifyDataSetChanged();
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return this.groupInfoFilter;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<Group> list = this.groupsList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.widget.Adapter
    public Group getItem(int i) {
        if (i >= getCount()) {
            return null;
        }
        return this.groupsList.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        Group item = getItem(i);
        if (item == null) {
            return view;
        }
        SteamCommunityApplication GetInstance = SteamCommunityApplication.GetInstance();
        if (view == null) {
            view = ((LayoutInflater) GetInstance.getSystemService("layout_inflater")).inflate(R.layout.group_list_item, (ViewGroup) null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.labelView = (TextView) view.findViewById(R.id.label);
            groupViewHolder.nameView = (TextView) view.findViewById(R.id.name);
            groupViewHolder.lblMembersTotal = (TextView) view.findViewById(R.id.groupMembersTotal);
            groupViewHolder.lblMembersOnline = (TextView) view.findViewById(R.id.groupMembersOnline);
            groupViewHolder.avatarView = (NetworkImageViewWithBackup) view.findViewById(R.id.avatar);
            groupViewHolder.avatarViewFrame = (ImageView) view.findViewById(R.id.avatar_frame);
            view.setClickable(true);
            view.setOnClickListener(this.groupListClickListener);
            view.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }
        groupViewHolder.group = item;
        if (this.groupAndLabelByStatus && (i == 0 || item.categoryInList != getItem(i - 1).categoryInList)) {
            groupViewHolder.labelView.setText(GetInstance.getResources().getString(item.categoryInList.getDisplayNumber()).toUpperCase(Locale.getDefault()));
            groupViewHolder.labelView.setVisibility(0);
        } else {
            groupViewHolder.labelView.setVisibility(8);
        }
        groupViewHolder.labelView.setOnClickListener(this.dummyClickListener);
        AndroidUtils.setTextViewText(groupViewHolder.nameView, item.name);
        if (item != this.searchItemInfo) {
            groupViewHolder.avatarView.setVisibility(0);
            groupViewHolder.avatarView.setImageUrl(item.mediumAvatarUrl, item.smallAvatarUrl, this.imageLoader);
            groupViewHolder.avatarView.forceLayout();
            groupViewHolder.avatarViewFrame.setImageResource(R.drawable.avatar_frame_offline);
            groupViewHolder.avatarViewFrame.setVisibility(0);
            groupViewHolder.lblMembersTotal.setText(Integer.toString(item.numUsersTotal) + " " + GetInstance.getResources().getString(R.string.Group_Num_Members_Total));
            groupViewHolder.lblMembersOnline.setText(Integer.toString(item.numUsersOnline) + " " + GetInstance.getResources().getString(R.string.Group_Num_Members_Online));
        } else {
            groupViewHolder.avatarViewFrame.setImageResource(R.drawable.icon_search);
            groupViewHolder.avatarView.setVisibility(4);
            AndroidUtils.setTextViewText(groupViewHolder.lblMembersTotal, getSearchString());
            groupViewHolder.lblMembersOnline.setText("");
        }
        return view;
    }

    @Override // com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void clear() {
        List<Group> list = this.groupsList;
        if (list == null) {
            return;
        }
        list.clear();
    }

    @Override // com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void add(Group group) {
        if (group == null) {
            return;
        }
        if (this.groupsList == null) {
            this.groupsList = new ArrayList();
        }
        this.groupsList.add(group);
    }

    public void add(List<Group> list) {
        this.groupsList = new ArrayList(list);
    }

    private Group createSearchItem() {
        Group group = new Group();
        group.steamId = "0";
        group.name = this.groupsSearchString;
        group.categoryInList = GroupCategoryInList.SEARCH_ALL;
        return group;
    }

    /* loaded from: classes.dex */
    private class GroupViewHolder {
        NetworkImageViewWithBackup avatarView;
        ImageView avatarViewFrame;
        Group group;
        TextView labelView;
        TextView lblMembersOnline;
        TextView lblMembersTotal;
        TextView nameView;

        private GroupViewHolder() {
        }
    }
}
