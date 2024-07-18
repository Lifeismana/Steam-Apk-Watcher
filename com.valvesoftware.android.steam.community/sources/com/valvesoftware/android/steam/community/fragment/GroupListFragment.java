package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.jsontranslators.GroupTranslator;
import com.valvesoftware.android.steam.community.model.Group;
import com.valvesoftware.android.steam.community.model.GroupRelationship;
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
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GroupListFragment extends ListFragment {
    private GroupsListAdapter adapter;
    private final HashSet<Group> groupsSet = new HashSet<>();
    private MainActivity mainActivity;

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.friends_groups_list_fragment, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        ActivityHelper.hideKeyboard(getActivity());
        setupEventListeners();
        setTitleText();
        this.mainActivity = getBaseActivity();
        getGroupsList();
    }

    private void setTitleText() {
        MainActivity baseActivity = getBaseActivity();
        if (baseActivity != null && ActivityHelper.fragmentIsActive(this)) {
            baseActivity.setTitle(R.string.Groups);
        }
    }

    protected MainActivity getBaseActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            return (MainActivity) activity;
        }
        return null;
    }

    private void showProgressIndicator() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity != null) {
            mainActivity.showProgressIndicator();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideProgressIndicator() {
        MainActivity mainActivity = this.mainActivity;
        if (mainActivity != null) {
            mainActivity.hideProgressIndicator();
        }
    }

    private void setupEventListeners() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.setRefreshButtonClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupListFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    GroupListFragment.this.getGroupsList();
                }
            });
            mainActivity.setSearchTextListener(new TextWatcher() { // from class: com.valvesoftware.android.steam.community.fragment.GroupListFragment.2
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    GroupListFragment.this.setSearchText(charSequence.toString());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearchText(String str) {
        GroupsListAdapter groupsListAdapter = this.adapter;
        if (groupsListAdapter == null) {
            return;
        }
        groupsListAdapter.setSearchString(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void display(List<Group> list) {
        if (list != null && ActivityHelper.fragmentIsActive(this)) {
            GroupsListAdapter groupsListAdapter = this.adapter;
            if (groupsListAdapter == null) {
                this.adapter = new GroupsListAdapter(list, getActivity());
                setListAdapter(this.adapter);
            } else {
                groupsListAdapter.clear();
                this.adapter.add(list);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDataSetChanged() {
        GroupsListAdapter groupsListAdapter;
        if (ActivityHelper.fragmentIsActive(this) && (groupsListAdapter = this.adapter) != null) {
            groupsListAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getGroupsList() {
        showProgressIndicator();
        RequestBuilder groupListRequestBuilder = Endpoints.getGroupListRequestBuilder(LoggedInUserAccountInfo.getLoginSteamID());
        groupListRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupListFragment.3
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                List<Group> translateList = GroupTranslator.translateList(jSONObject);
                Iterator<Group> it = translateList.iterator();
                while (it.hasNext()) {
                    Group next = it.next();
                    if (next.relationship != GroupRelationship.Member && next.relationship != GroupRelationship.Invited) {
                        it.remove();
                    }
                }
                HashSet hashSet = new HashSet(translateList);
                GroupListFragment.this.groupsSet.retainAll(hashSet);
                hashSet.removeAll(GroupListFragment.this.groupsSet);
                GroupListFragment.this.groupsSet.addAll(hashSet);
                GroupListFragment groupListFragment = GroupListFragment.this;
                groupListFragment.display(new ArrayList(groupListFragment.groupsSet));
                GroupListFragment.this.hideProgressIndicator();
                GroupListFragment groupListFragment2 = GroupListFragment.this;
                groupListFragment2.getDetailedGroupInfo(groupListFragment2.groupsSet);
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                GroupListFragment.this.hideProgressIndicator();
            }
        });
        sendRequest(groupListRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDetailedGroupInfo(Collection<Group> collection) {
        if (collection.size() == 0) {
            return;
        }
        final HashMap hashMap = new HashMap();
        for (Group group : collection) {
            hashMap.put(group.steamId, group);
        }
        List<RequestBuilder> groupSummariesRequestBuilder = Endpoints.getGroupSummariesRequestBuilder(hashMap.keySet());
        showProgressIndicator();
        final AtomicInteger atomicInteger = new AtomicInteger(groupSummariesRequestBuilder.size());
        for (RequestBuilder requestBuilder : groupSummariesRequestBuilder) {
            requestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupListFragment.4
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    for (Group group2 : GroupTranslator.translateList(jSONObject)) {
                        Group group3 = (Group) hashMap.get(group2.steamId);
                        if (group3 != null) {
                            group3.merge(group2);
                        }
                    }
                    if (atomicInteger.decrementAndGet() == 0) {
                        GroupListFragment.this.hideProgressIndicator();
                        GroupListFragment.this.notifyDataSetChanged();
                    }
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    if (atomicInteger.decrementAndGet() == 0) {
                        GroupListFragment.this.hideProgressIndicator();
                        GroupListFragment.this.notifyDataSetChanged();
                    }
                }
            });
            sendRequest(requestBuilder);
        }
    }

    private void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }
}
