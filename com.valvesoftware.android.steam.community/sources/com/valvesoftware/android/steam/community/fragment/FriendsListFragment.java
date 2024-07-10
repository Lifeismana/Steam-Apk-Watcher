package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.valvesoftware.android.steam.community.ChatStateListener;
import com.valvesoftware.android.steam.community.LocalDb;
import com.valvesoftware.android.steam.community.LoggedInStatusChangedListener;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.NotificationSender;
import com.valvesoftware.android.steam.community.PersonaRepository;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.RepositoryCallback;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.UmqCommunicator;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.jsontranslators.PersonaTranslator;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.PersonaRelationship;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FriendsListFragment extends ListFragment {
    private FriendsListAdapter adapter;
    private View chatIsOfflineNotice;
    private LocalDb localDb;
    private MainActivity mainActivity;
    private String searchString;
    private final Map<String, Persona> steamIdToFriendsMap = new HashMap();
    private final Handler uiThreadHandler = new Handler();
    private UmqCommunicator umqCommunicator;

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.friends_groups_list_fragment, viewGroup, false);
        this.chatIsOfflineNotice = inflate.findViewById(R.id.chat_is_offline_notice);
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        this.mainActivity = getBaseActivity();
        ActivityHelper.hideKeyboard(getActivity());
        setupEventListeners();
        setTitleText();
        this.localDb = SteamCommunityApplication.GetInstance().getLocalDb();
        this.umqCommunicator = UmqCommunicator.getInstance();
        this.umqCommunicator.setChatStateListener(new ChatStateListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.1
            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void isTypingMessageReceived(List<UmqMessage> list) {
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void messageSendFailed(UmqMessage umqMessage) {
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void messageSent(UmqMessage umqMessage) {
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public boolean listenerWillHandleAllVisualChatNotifications() {
                return ActivityHelper.fragmentIsActive(FriendsListFragment.this);
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public boolean listenerWillHandleVisualChatNotificationForSteamId(String str) {
                return listenerWillHandleAllVisualChatNotifications();
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void newMessagesSaved(List<UmqMessage> list) {
                if (!ActivityHelper.fragmentIsActive(FriendsListFragment.this) || list == null || list.size() == 0) {
                    return;
                }
                FriendsListFragment friendsListFragment = FriendsListFragment.this;
                friendsListFragment.updateUnreadMessageCountsForEachFriend(friendsListFragment.steamIdToFriendsMap);
                FriendsListFragment.this.updateFriendsWithLastMessageTimes();
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void personaStateChanged(List<String> list) {
                if (ActivityHelper.fragmentIsActive(FriendsListFragment.this)) {
                    FriendsListFragment.this.getDetailedFriendInfo(list);
                }
            }

            @Override // com.valvesoftware.android.steam.community.ChatStateListener
            public void relationshipStateChanged(List<String> list) {
                if (ActivityHelper.fragmentIsActive(FriendsListFragment.this)) {
                    FriendsListFragment.this.getFriendsList();
                }
            }
        });
        this.umqCommunicator.setChatLoggedInStatusChangedListener(new LoggedInStatusChangedListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.2
            @Override // com.valvesoftware.android.steam.community.LoggedInStatusChangedListener
            public void loggedOff() {
                if (ActivityHelper.fragmentIsActive(FriendsListFragment.this) && FriendsListFragment.this.chatIsOfflineNotice != null) {
                    FriendsListFragment.this.chatIsOfflineNotice.setVisibility(0);
                }
            }

            @Override // com.valvesoftware.android.steam.community.LoggedInStatusChangedListener
            public void loggedIn() {
                if (ActivityHelper.fragmentIsActive(FriendsListFragment.this) && FriendsListFragment.this.chatIsOfflineNotice != null) {
                    FriendsListFragment.this.chatIsOfflineNotice.setVisibility(8);
                }
            }
        });
        showProgressDialog();
        loadCachedFriendsList();
        getFriendsList();
        this.umqCommunicator.updateOfflineChats();
        if (!LoggedInUserAccountInfo.dontLoginToChat()) {
            this.chatIsOfflineNotice.setVisibility(8);
        } else {
            this.chatIsOfflineNotice.setVisibility(0);
        }
        NotificationSender.getInstance().clearRecentNotificationsTracking();
    }

    @Override // android.support.v4.app.Fragment
    public void onStop() {
        super.onStop();
    }

    protected MainActivity getBaseActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            return (MainActivity) activity;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyItemContentsChanged() {
        this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.3
            @Override // java.lang.Runnable
            public void run() {
                if (ActivityHelper.fragmentIsActive(FriendsListFragment.this) && FriendsListFragment.this.adapter != null) {
                    FriendsListFragment.this.adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void showProgressDialog() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity != null) {
            mainActivity.showProgressIndicator();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgressDialog() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity != null) {
            mainActivity.hideProgressIndicator();
        }
    }

    private void setTitleText() {
        if (this.mainActivity != null && ActivityHelper.fragmentIsActive(this)) {
            this.mainActivity.setTitle(R.string.Friends);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearchText(String str) {
        this.searchString = str;
        FriendsListAdapter friendsListAdapter = this.adapter;
        if (friendsListAdapter == null) {
            return;
        }
        friendsListAdapter.setSearchString(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void display(Collection<Persona> collection, List<Persona> list, List<Persona> list2) {
        if (collection == null) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (ActivityHelper.fragmentIsActive(this)) {
            if (this.adapter == null) {
                this.adapter = new FriendsListAdapter(collection, activity);
                this.adapter.setSearchString(this.searchString);
                setListAdapter(this.adapter);
                return;
            }
            if (list != null && list.size() > 0) {
                this.adapter.add(list);
            }
            if (list2 != null && list2.size() > 0) {
                this.adapter.remove(list2);
            }
            notifyItemContentsChanged();
        }
    }

    private void setupEventListeners() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity == null) {
            return;
        }
        mainActivity.setRefreshButtonClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FriendsListFragment.this.getFriendsList();
            }
        });
        this.mainActivity.setSearchTextListener(new TextWatcher() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.5
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                FriendsListFragment.this.setSearchText(charSequence.toString());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getFriendsList() {
        showProgressDialog();
        RequestBuilder friendListRequestBuilder = Endpoints.getFriendListRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID(), "friend");
        friendListRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.6
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                List<Persona> translateList = PersonaTranslator.translateList(jSONObject);
                HashSet hashSet = new HashSet();
                if (translateList.size() == 0) {
                    FriendsListFragment.this.hideProgressDialog();
                    return;
                }
                ArrayList arrayList = new ArrayList();
                Iterator<Persona> it = translateList.iterator();
                while (it.hasNext()) {
                    Persona next = it.next();
                    if (next.relationship != PersonaRelationship.blocked && next.relationship != PersonaRelationship.ignored && next.relationship != PersonaRelationship.requestinitiator) {
                        if (!FriendsListFragment.this.steamIdToFriendsMap.containsKey(next.steamId)) {
                            FriendsListFragment.this.steamIdToFriendsMap.put(next.steamId, next);
                            arrayList.add(next);
                        } else {
                            Persona persona = (Persona) FriendsListFragment.this.steamIdToFriendsMap.get(next.steamId);
                            persona.relationship = next.relationship;
                            persona.determineDisplayCategory();
                        }
                        hashSet.add(next.steamId);
                    } else {
                        it.remove();
                    }
                }
                ArrayList arrayList2 = new ArrayList();
                Iterator it2 = FriendsListFragment.this.steamIdToFriendsMap.entrySet().iterator();
                while (it2.hasNext()) {
                    Map.Entry entry = (Map.Entry) it2.next();
                    if (!hashSet.contains(entry.getKey())) {
                        arrayList2.add(entry.getValue());
                        it2.remove();
                    }
                }
                FriendsListFragment.this.display(translateList, arrayList, arrayList2);
                FriendsListFragment friendsListFragment = FriendsListFragment.this;
                friendsListFragment.updateUnreadMessageCountsForEachFriend(friendsListFragment.steamIdToFriendsMap);
                FriendsListFragment.this.updateFriendsWithLastMessageTimes();
                FriendsListFragment friendsListFragment2 = FriendsListFragment.this;
                friendsListFragment2.getDetailedFriendInfo(friendsListFragment2.steamIdToFriendsMap.keySet());
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                FriendsListFragment.this.hideProgressDialog();
            }
        });
        sendRequest(friendListRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDetailedFriendInfo(Collection<String> collection) {
        showProgressDialog();
        PersonaRepository.getDetailedPersonaInfo(collection, new RepositoryCallback<List<Persona>>() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.7
            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void dataAvailable(List<Persona> list) {
                for (Persona persona : list) {
                    Persona persona2 = (Persona) FriendsListFragment.this.steamIdToFriendsMap.get(persona.steamId);
                    if (persona2 != null) {
                        persona2.overwriteOrMergeWith(persona);
                    }
                }
            }

            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void end() {
                FriendsListFragment.this.cacheFriends();
                FriendsListFragment.this.hideProgressDialog();
                FriendsListFragment.this.notifyItemContentsChanged();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cacheFriends() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity != null && mainActivity.steamData != null) {
            this.mainActivity.steamData.saveFriends(this.steamIdToFriendsMap);
        }
        SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.8
            @Override // java.lang.Runnable
            public void run() {
                try {
                    LocalDb localDb = SteamCommunityApplication.GetInstance().getLocalDb();
                    localDb.clearPersonaInfo();
                    localDb.replaceStoredFriendsList(FriendsListFragment.this.steamIdToFriendsMap.values(), LoggedInUserAccountInfo.getLoginSteamID());
                } catch (Exception unused) {
                }
            }
        });
    }

    private void loadCachedFriendsList() {
        MainActivity mainActivity;
        Map<String, Persona> map = this.steamIdToFriendsMap;
        if ((map == null || map.size() == 0) && (mainActivity = this.mainActivity) != null && mainActivity.steamData != null) {
            this.steamIdToFriendsMap.putAll(this.mainActivity.steamData.getSteamIdToFriendsMap());
        }
        Map<String, Persona> map2 = this.steamIdToFriendsMap;
        if (map2 != null) {
            display(map2.values(), null, null);
            getDetailedFriendInfo(this.steamIdToFriendsMap.keySet());
        }
    }

    private void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFriendsWithLastMessageTimes() {
        SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.9
            @Override // java.lang.Runnable
            public void run() {
                final HashMap<String, Long> latestMessagesFromAllUsers = FriendsListFragment.this.localDb.getLatestMessagesFromAllUsers(LoggedInUserAccountInfo.getLoginSteamID());
                FriendsListFragment.this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.9.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (latestMessagesFromAllUsers == null || FriendsListFragment.this.steamIdToFriendsMap == null || FriendsListFragment.this.steamIdToFriendsMap.size() == 0) {
                            return;
                        }
                        for (Persona persona : FriendsListFragment.this.steamIdToFriendsMap.values()) {
                            if (latestMessagesFromAllUsers.containsKey(persona.steamId)) {
                                persona.setLastMessageTime(((Long) latestMessagesFromAllUsers.get(persona.steamId)).longValue());
                            } else {
                                persona.setLastMessageTime(0L);
                            }
                            persona.determineDisplayCategory();
                        }
                        FriendsListFragment.this.notifyItemContentsChanged();
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUnreadMessageCountsForEachFriend(final Map<String, Persona> map) {
        if (map == null || map.size() == 0) {
            return;
        }
        SteamCommunityApplication.GetInstance().runOnBackgroundThread(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.10
            @Override // java.lang.Runnable
            public void run() {
                final List<UmqMessage> allUnreadMessages = FriendsListFragment.this.localDb.getAllUnreadMessages(LoggedInUserAccountInfo.getLoginSteamID());
                FriendsListFragment.this.uiThreadHandler.post(new Runnable() { // from class: com.valvesoftware.android.steam.community.fragment.FriendsListFragment.10.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Iterator it = map.values().iterator();
                        while (it.hasNext()) {
                            ((Persona) it.next()).clearUnreadMessageCount();
                        }
                        List list = allUnreadMessages;
                        if (list != null) {
                            Iterator it2 = list.iterator();
                            while (it2.hasNext()) {
                                Persona persona = (Persona) map.get(((UmqMessage) it2.next()).chatPartnerSteamId);
                                if (persona != null) {
                                    persona.incrementUnreadMessageCount();
                                }
                            }
                        }
                        FriendsListFragment.this.notifyItemContentsChanged();
                    }
                });
            }
        });
    }
}
