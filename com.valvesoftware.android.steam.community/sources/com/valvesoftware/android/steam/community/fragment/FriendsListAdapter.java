package com.valvesoftware.android.steam.community.fragment;

import android.content.Context;
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
import com.android.volley.toolbox.NetworkImageView;
import com.valvesoftware.android.steam.community.AndroidUtils;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamAppIntents;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.PersonaRelationship;
import com.valvesoftware.android.steam.community.model.PersonaState;
import com.valvesoftware.android.steam.community.model.PersonaStateCategoryInList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class FriendsListAdapter extends BaseAdapter implements FilterableAdapter<Persona> {
    private static Comparator<Persona> personaComparator = new Comparator<Persona>() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListAdapter.1
        @Override // java.util.Comparator
        public int compare(Persona persona, Persona persona2) {
            if (persona.getDisplayCategory() != persona2.getDisplayCategory()) {
                return persona.getDisplayCategory().ordinal() < persona2.getDisplayCategory().ordinal() ? -1 : 1;
            }
            if (persona.hasSentUnreadMessage() != persona2.hasSentUnreadMessage()) {
                return persona.hasSentUnreadMessage() ? -1 : 1;
            }
            if (persona.getDisplayCategory() != PersonaStateCategoryInList.CHATS || persona.getLastMessageTime() <= 0 || persona2.getLastMessageTime() <= 0) {
                return persona.personaName.compareToIgnoreCase(persona2.personaName);
            }
            return persona.getLastMessageTime() > persona2.getLastMessageTime() ? -1 : 1;
        }
    };
    private final Context context;
    private View.OnClickListener friendChatClickListener;
    private GenericFilter friendInfoFilter;
    private View.OnClickListener friendProfileClickListener;
    private List<Persona> friendsList;
    private final boolean groupAndLabelByStatus;
    private ImageLoader imageLoader;
    private LayoutInflater layoutInflater;
    private final Persona searchItemInfo;
    private String searchString;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public FriendsListAdapter(Collection<Persona> collection, FragmentActivity fragmentActivity) {
        this(collection, fragmentActivity, true);
    }

    public FriendsListAdapter(Collection<Persona> collection, final FragmentActivity fragmentActivity, boolean z) {
        this.friendsList = new ArrayList(collection);
        Collections.sort(this.friendsList, personaComparator);
        this.context = fragmentActivity;
        this.imageLoader = SteamCommunityApplication.GetInstance().imageLoader;
        this.layoutInflater = (LayoutInflater) fragmentActivity.getApplicationContext().getSystemService("layout_inflater");
        this.groupAndLabelByStatus = z;
        this.friendProfileClickListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Object tag = view.getTag();
                String str = tag instanceof String ? (String) tag : null;
                if (tag instanceof FriendListViewHolder) {
                    str = ((FriendListViewHolder) tag).steamId;
                }
                if (str == null) {
                    return;
                }
                if (str.equals("0")) {
                    Intent searchFriendIntent = SteamAppIntents.searchFriendIntent(fragmentActivity, FriendsListAdapter.this.getSearchString());
                    FriendsListAdapter.this.setSearchString("");
                    fragmentActivity.startActivity(searchFriendIntent);
                    return;
                }
                fragmentActivity.startActivity(SteamAppIntents.visitProfileIntent(fragmentActivity, str));
            }
        };
        this.friendChatClickListener = new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FriendListViewHolder friendListViewHolder = (FriendListViewHolder) view.getTag();
                if (friendListViewHolder != null) {
                    fragmentActivity.startActivity(SteamAppIntents.chatIntent(fragmentActivity, friendListViewHolder.steamId, friendListViewHolder.personaName, friendListViewHolder.avatarUrl));
                }
            }
        };
        this.searchItemInfo = createSearchItem();
    }

    @Override // android.widget.BaseAdapter, com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void notifyDataSetChanged() {
        Collections.sort(this.friendsList, personaComparator);
        super.notifyDataSetChanged();
    }

    @Override // com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void add(Persona persona) {
        if (persona == null) {
            return;
        }
        if (this.friendsList == null) {
            this.friendsList = new ArrayList();
        }
        this.friendsList.add(persona);
    }

    public void add(List<Persona> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (this.friendsList == null) {
            this.friendsList = new ArrayList();
        }
        this.friendsList.addAll(list);
    }

    public void remove(List<Persona> list) {
        List<Persona> list2;
        if (list == null || list.size() == 0 || (list2 = this.friendsList) == null || list2.size() == 0) {
            return;
        }
        HashSet hashSet = new HashSet();
        Iterator<Persona> it = list.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next().steamId);
        }
        Iterator<Persona> it2 = this.friendsList.iterator();
        while (it2.hasNext()) {
            if (hashSet.contains(it2.next().steamId)) {
                it2.remove();
            }
        }
        notifyDataSetChanged();
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String str) {
        if (str != null && str.length() > 0 && this.friendInfoFilter == null) {
            this.friendInfoFilter = new GenericFilter(this.friendsList, this, this.searchItemInfo);
        }
        if (this.friendInfoFilter == null) {
            return;
        }
        if (str != null && str.length() > 0) {
            this.friendInfoFilter.removeFromOriginal(this.searchItemInfo);
            this.friendInfoFilter.addToOriginal(this.searchItemInfo);
        } else {
            notifyDataSetChanged();
            this.friendInfoFilter.removeFromOriginal(this.searchItemInfo);
        }
        this.searchString = str;
        this.friendInfoFilter.filter(this.searchString);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<Persona> list = this.friendsList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override // android.widget.Adapter
    public Persona getItem(int i) {
        if (i >= getCount()) {
            return null;
        }
        return this.friendsList.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        Persona persona;
        Persona persona2;
        FriendListViewHolder friendListViewHolder;
        if (i < getCount()) {
            persona = getItem(i);
            persona2 = i > 0 ? getItem(i - 1) : null;
        } else {
            persona = null;
            persona2 = null;
        }
        if (persona == null) {
            return view;
        }
        if (view == null) {
            view = this.layoutInflater.inflate(R.layout.friend_list_item, (ViewGroup) null);
            friendListViewHolder = new FriendListViewHolder();
            friendListViewHolder.labelView = (TextView) view.findViewById(R.id.label);
            friendListViewHolder.nameView = (TextView) view.findViewById(R.id.name);
            friendListViewHolder.statusView = (TextView) view.findViewById(R.id.status);
            friendListViewHolder.avatarView = (NetworkImageView) view.findViewById(R.id.avatar);
            friendListViewHolder.avatarViewFrame = (ImageView) view.findViewById(R.id.avatar_frame);
            friendListViewHolder.chevronView = (ImageView) view.findViewById(R.id.imageChevron);
            friendListViewHolder.chatBtn = (ImageView) view.findViewById(R.id.chatButton);
            friendListViewHolder.unreadMessageTextView = (TextView) view.findViewById(R.id.unreadMessageCount);
            friendListViewHolder.vAreaAroundChatBtn = view.findViewById(R.id.friendItemAreaAroundChatButton);
            friendListViewHolder.mobileIcon = view.findViewById(R.id.mobileIcon);
            friendListViewHolder.nameAndStatusContainer = view.findViewById(R.id.nameAndStatusContainer);
            view.setTag(friendListViewHolder);
        } else {
            friendListViewHolder = (FriendListViewHolder) view.getTag();
        }
        friendListViewHolder.steamId = persona.steamId;
        friendListViewHolder.personaName = persona.personaName;
        friendListViewHolder.avatarUrl = persona.mediumAvatarUrl;
        friendListViewHolder.vAreaAroundChatBtn.setOnClickListener(null);
        friendListViewHolder.chatBtn.setOnClickListener(null);
        view.setClickable(true);
        if (persona.isFriend()) {
            view.setOnClickListener(this.friendChatClickListener);
        } else if (persona == this.searchItemInfo) {
            friendListViewHolder.vAreaAroundChatBtn.setOnClickListener(this.friendProfileClickListener);
            friendListViewHolder.nameAndStatusContainer.setOnClickListener(this.friendProfileClickListener);
            friendListViewHolder.nameAndStatusContainer.setTag(friendListViewHolder.steamId);
            view.setOnClickListener(this.friendProfileClickListener);
        } else {
            view.setOnClickListener(this.friendProfileClickListener);
        }
        friendListViewHolder.avatarView.setTag(friendListViewHolder.steamId);
        friendListViewHolder.avatarView.setOnClickListener(this.friendProfileClickListener);
        if (this.groupAndLabelByStatus && (i == 0 || persona2 == null || persona.getDisplayCategory() != persona2.getDisplayCategory())) {
            friendListViewHolder.labelView.setText(getContext().getString(persona.getDisplayCategory().GetDisplayString()).toUpperCase(Locale.getDefault()));
            friendListViewHolder.labelView.setVisibility(0);
        } else {
            friendListViewHolder.labelView.setVisibility(8);
        }
        AndroidUtils.setTextViewText(friendListViewHolder.nameView, persona.personaName);
        determineChatElementsState(persona, friendListViewHolder);
        determineStatusAndAvatar(persona, friendListViewHolder, getContext());
        return view;
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return this.friendInfoFilter;
    }

    @Override // com.valvesoftware.android.steam.community.fragment.FilterableAdapter
    public void clear() {
        List<Persona> list = this.friendsList;
        if (list == null) {
            return;
        }
        list.clear();
    }

    private Context getContext() {
        return this.context;
    }

    private Persona createSearchItem() {
        Persona persona = new Persona();
        persona.steamId = "0";
        persona.personaState = PersonaState.OFFLINE;
        persona.relationship = PersonaRelationship.none;
        String string = SteamCommunityApplication.GetInstance().getResources().getString(R.string.Friend_Search_All);
        persona.realName = string;
        persona.personaName = string;
        persona.setDisplayCategoryForSearch();
        persona.isOnMobile = false;
        persona.isOnTenFoot = false;
        persona.isOnWeb = false;
        return persona;
    }

    private void determineStatusAndAvatar(Persona persona, FriendListViewHolder friendListViewHolder, Context context) {
        friendListViewHolder.mobileIcon.setVisibility(8);
        if (persona.isPlaying()) {
            friendListViewHolder.avatarViewFrame.setImageResource(R.drawable.avatar_frame_ingame);
            friendListViewHolder.nameView.setTextColor(context.getResources().getColor(R.color.ingame));
            friendListViewHolder.statusView.setTextColor(context.getResources().getColor(R.color.ingame));
            String string = context.getResources().getString(R.string.Playing);
            TextView textView = friendListViewHolder.statusView;
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append(" ");
            sb.append(persona.currentGameString != null ? persona.currentGameString : "");
            textView.setText(sb.toString());
        } else if (persona.isOnline()) {
            friendListViewHolder.avatarViewFrame.setImageResource(R.drawable.avatar_frame_online);
            friendListViewHolder.nameView.setTextColor(context.getResources().getColor(R.color.online));
            friendListViewHolder.statusView.setTextColor(context.getResources().getColor(R.color.online));
            friendListViewHolder.statusView.setText(persona.personaState.GetDisplayString());
            if (persona.isOnMobile) {
                friendListViewHolder.mobileIcon.setVisibility(0);
            }
        } else {
            friendListViewHolder.avatarViewFrame.setImageResource(R.drawable.avatar_frame_offline);
            friendListViewHolder.nameView.setTextColor(context.getResources().getColor(R.color.offline));
            friendListViewHolder.statusView.setTextColor(context.getResources().getColor(R.color.offline));
            friendListViewHolder.statusView.setText(getLastOnlineString(persona.lastOnlineTime));
        }
        if (persona == this.searchItemInfo) {
            friendListViewHolder.avatarView.setVisibility(4);
            friendListViewHolder.avatarViewFrame.setImageResource(R.drawable.icon_search);
            friendListViewHolder.statusView.setText(this.searchString);
        } else {
            friendListViewHolder.avatarView.setVisibility(0);
            friendListViewHolder.avatarView.setImageUrl(persona.mediumAvatarUrl, this.imageLoader);
            friendListViewHolder.avatarUrl = persona.mediumAvatarUrl;
            friendListViewHolder.avatarViewFrame.setVisibility(0);
        }
    }

    private void determineChatElementsState(Persona persona, FriendListViewHolder friendListViewHolder) {
        if (persona.isFriend()) {
            turnOnChatElements(persona, friendListViewHolder);
        } else {
            turnOffChatElements(persona, friendListViewHolder);
        }
    }

    private void turnOffChatElements(Persona persona, FriendListViewHolder friendListViewHolder) {
        friendListViewHolder.vAreaAroundChatBtn.setOnClickListener(null);
        friendListViewHolder.chatBtn.setOnClickListener(null);
        friendListViewHolder.vAreaAroundChatBtn.setVisibility(8);
        friendListViewHolder.chatBtn.setVisibility(8);
        friendListViewHolder.unreadMessageTextView.setVisibility(8);
        if (persona == this.searchItemInfo) {
            friendListViewHolder.chevronView.setVisibility(8);
        } else {
            friendListViewHolder.chevronView.setVisibility(0);
        }
    }

    private void turnOnChatElements(Persona persona, FriendListViewHolder friendListViewHolder) {
        friendListViewHolder.chevronView.setVisibility(8);
        if (persona.hasSentUnreadMessage()) {
            friendListViewHolder.chatBtn.setBackgroundResource(R.drawable.ic_unread_message_chat);
            friendListViewHolder.unreadMessageTextView.setText(String.valueOf(persona.getUnreadMessageCount()));
            friendListViewHolder.unreadMessageTextView.setVisibility(0);
            friendListViewHolder.unreadMessageTextView.setTextColor(getContext().getResources().getColor(R.color.primary_background));
        } else if (!persona.isOnline()) {
            friendListViewHolder.unreadMessageTextView.setVisibility(8);
            friendListViewHolder.chatBtn.setBackgroundResource(R.drawable.chat_button_available);
        } else {
            friendListViewHolder.unreadMessageTextView.setVisibility(8);
            friendListViewHolder.chatBtn.setBackgroundResource(R.drawable.ic_chat_back_forth);
        }
        friendListViewHolder.chatBtn.setVisibility(0);
        friendListViewHolder.vAreaAroundChatBtn.setVisibility(0);
        friendListViewHolder.vAreaAroundChatBtn.setTag(friendListViewHolder);
        friendListViewHolder.chatBtn.setTag(friendListViewHolder);
        friendListViewHolder.vAreaAroundChatBtn.setOnClickListener(this.friendChatClickListener);
        friendListViewHolder.chatBtn.setOnClickListener(this.friendChatClickListener);
    }

    private String getLastOnlineString(long j) {
        if (j <= 0) {
            return "";
        }
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        if (currentTimeMillis == 0 || j == 0) {
            return "";
        }
        long j2 = currentTimeMillis - j;
        long j3 = j2 >= 10 ? j2 : 10L;
        if (j3 < 60) {
            return SteamCommunityApplication.GetInstance().getResources().getString(R.string.LastOnline_SecondsAgo).replace("#", String.valueOf(j3));
        }
        long j4 = (j3 / 60) + 1;
        if (j4 < 60) {
            return SteamCommunityApplication.GetInstance().getResources().getString(R.string.LastOnline_MinutesAgo).replace("#", String.valueOf(j4));
        }
        long j5 = (j4 / 60) + 1;
        if (j5 < 48) {
            return SteamCommunityApplication.GetInstance().getResources().getString(R.string.LastOnline_HoursAgo).replace("#", String.valueOf(j5));
        }
        long j6 = j5 / 24;
        if (j6 < 365) {
            return SteamCommunityApplication.GetInstance().getResources().getString(R.string.LastOnline_DaysAgo).replace("#", String.valueOf(j6));
        }
        return SteamCommunityApplication.GetInstance().getResources().getString(R.string.LastOnline_YearOrMore);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FriendListViewHolder {
        public String avatarUrl;
        public NetworkImageView avatarView;
        public ImageView avatarViewFrame;
        public ImageView chatBtn;
        public ImageView chevronView;
        public TextView labelView;
        public View mobileIcon;
        public View nameAndStatusContainer;
        public TextView nameView;
        public String personaName;
        public TextView statusView;
        public String steamId;
        public TextView unreadMessageTextView;
        public View vAreaAroundChatBtn;

        private FriendListViewHolder() {
        }
    }
}
