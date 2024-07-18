package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.util.Log;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.jsontranslators.GroupTranslator;
import com.valvesoftware.android.steam.community.jsontranslators.SearchResultsTranslator;
import com.valvesoftware.android.steam.community.model.Group;
import com.valvesoftware.android.steam.community.model.SearchResults;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class GroupSearchFragment extends BaseSearchListFragment {
    private List<Group> searchResults = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    public void display() {
        if (this.searchResults != null && ActivityHelper.fragmentOrActivityIsActive(getActivity())) {
            this.adapter = new GroupsListAdapter(this.searchResults, getActivity(), false);
            setListAdapter(this.adapter);
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void query(String str) {
        RequestBuilder groupsSearchRequestBuilder = Endpoints.getGroupsSearchRequestBuilder(str, this.queryOffset, 50);
        groupsSearchRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupSearchFragment.1
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                SearchResults translate = SearchResultsTranslator.translate(jSONObject);
                GroupSearchFragment.this.setNumTotalResults(translate.total);
                GroupSearchFragment.this.setNumCurrentResults(translate.getCurrentCount());
                GroupSearchFragment.this.getDetailedGroupInfo(translate.getResultIds());
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                Log.e("error", "error processing data");
            }
        });
        sendRequest(groupsSearchRequestBuilder);
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void setTitleBarText(String str) {
        MainActivity baseActivity = getBaseActivity();
        if (baseActivity != null && ActivityHelper.fragmentIsActive(this)) {
            baseActivity.setTitle(getResources().getString(R.string.Search_Groups_Results).replace("#", str));
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void startSearch() {
        String string;
        displayInProgress();
        Bundle arguments = getArguments();
        if (arguments == null || (string = arguments.getString("groups")) == null) {
            return;
        }
        this.queryString = string;
        setTitleBarText(this.queryString);
        query(this.queryString);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDetailedGroupInfo(Collection<String> collection) {
        List<RequestBuilder> groupSummariesRequestBuilder = Endpoints.getGroupSummariesRequestBuilder(collection);
        this.searchResults.clear();
        for (RequestBuilder requestBuilder : groupSummariesRequestBuilder) {
            requestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.GroupSearchFragment.2
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    GroupSearchFragment.this.searchResults.addAll(GroupTranslator.translateList(jSONObject));
                    GroupSearchFragment.this.searchComplete();
                    GroupSearchFragment.this.display();
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    requestErrorInfo.toString();
                    GroupSearchFragment.this.searchComplete();
                }
            });
            sendRequest(requestBuilder);
        }
    }
}
