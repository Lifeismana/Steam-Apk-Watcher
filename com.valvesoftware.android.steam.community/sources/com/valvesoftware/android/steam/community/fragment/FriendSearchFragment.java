package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.util.Log;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.jsontranslators.PersonaTranslator;
import com.valvesoftware.android.steam.community.jsontranslators.SearchResultsTranslator;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.SearchResults;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class FriendSearchFragment extends BaseSearchListFragment {
    private List<Persona> searchResults = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    public void display() {
        if (this.searchResults != null && ActivityHelper.fragmentOrActivityIsActive(getActivity())) {
            Collections.sort(this.searchResults, new Comparator<Persona>() { // from class: com.valvesoftware.android.steam.community.fragment.FriendSearchFragment.1
                @Override // java.util.Comparator
                public int compare(Persona persona, Persona persona2) {
                    if (persona.personaName != null && !persona.personaName.equals(persona2.personaName)) {
                        return persona.personaName.compareTo(persona2.personaName);
                    }
                    return persona.steamId.compareTo(persona2.steamId);
                }
            });
            this.adapter = new FriendsListAdapter(this.searchResults, getActivity(), false);
            setListAdapter(this.adapter);
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void setTitleBarText(String str) {
        MainActivity baseActivity = getBaseActivity();
        if (baseActivity != null && ActivityHelper.fragmentIsActive(this)) {
            baseActivity.setTitle(getResources().getString(R.string.Search_Players_Results).replace("#", str));
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void startSearch() {
        String string;
        displayInProgress();
        Bundle arguments = getArguments();
        if (arguments != null && (string = arguments.getString("friends")) != null) {
            this.queryString = string;
            setTitleBarText(this.queryString);
            query(this.queryString);
            return;
        }
        getActivity().onBackPressed();
    }

    @Override // com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment
    protected void query(String str) {
        RequestBuilder friendsSearchRequestBuilder = Endpoints.getFriendsSearchRequestBuilder(str, this.queryOffset, 50);
        friendsSearchRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendSearchFragment.2
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                SearchResults translate = SearchResultsTranslator.translate(jSONObject);
                FriendSearchFragment.this.setNumTotalResults(translate.total);
                FriendSearchFragment.this.setNumCurrentResults(translate.getCurrentCount());
                FriendSearchFragment.this.getDetailedPersonaInfo(translate.getResultIds());
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                Log.e("error", "error processing data");
            }
        });
        sendRequest(friendsSearchRequestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDetailedPersonaInfo(Collection<String> collection) {
        List<RequestBuilder> userSummariesRequestBuilder = Endpoints.getUserSummariesRequestBuilder(collection);
        this.searchResults.clear();
        for (RequestBuilder requestBuilder : userSummariesRequestBuilder) {
            requestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.FriendSearchFragment.3
                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onSuccess(JSONObject jSONObject) {
                    FriendSearchFragment.this.searchResults.addAll(PersonaTranslator.translateList(jSONObject));
                    FriendSearchFragment.this.display();
                    FriendSearchFragment.this.searchComplete();
                }

                @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
                public void onError(RequestErrorInfo requestErrorInfo) {
                    requestErrorInfo.toString();
                    FriendSearchFragment.this.searchComplete();
                }
            });
            sendRequest(requestBuilder);
        }
    }
}
