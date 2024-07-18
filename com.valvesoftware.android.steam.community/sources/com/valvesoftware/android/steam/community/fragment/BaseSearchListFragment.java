package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.activity.MainActivity;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;

/* loaded from: classes.dex */
public abstract class BaseSearchListFragment extends ListFragment {
    protected BaseAdapter adapter;
    protected TextView footerBtnNext;
    protected TextView footerBtnPrev;
    protected View footerButtons;
    protected int numCurrentResults;
    protected int numTotalResults;
    protected TextView progressLabel;
    protected int queryOffset = 0;
    protected String queryString;

    protected abstract void query(String str);

    protected abstract void setTitleBarText(String str);

    protected abstract void startSearch();

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.search_results_fragment, viewGroup, false);
        this.progressLabel = (TextView) inflate.findViewById(R.id.search_progress_label);
        this.footerButtons = inflate.findViewById(R.id.search_footer_buttons);
        this.footerButtons.setVisibility(8);
        this.footerBtnPrev = (TextView) inflate.findViewById(R.id.search_footer_button_prev);
        this.footerBtnNext = (TextView) inflate.findViewById(R.id.search_footer_button_next);
        this.footerBtnNext.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BaseSearchListFragment.this.queryOffset += 50;
                BaseSearchListFragment baseSearchListFragment = BaseSearchListFragment.this;
                baseSearchListFragment.query(baseSearchListFragment.queryString);
            }
        });
        this.footerBtnPrev.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.BaseSearchListFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BaseSearchListFragment.this.queryOffset = Math.max(0, r3.queryOffset - 50);
                BaseSearchListFragment baseSearchListFragment = BaseSearchListFragment.this;
                baseSearchListFragment.query(baseSearchListFragment.queryString);
            }
        });
        return inflate;
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        startSearch();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MainActivity getBaseActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity) {
            return (MainActivity) activity;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendRequest(RequestBuilder requestBuilder) {
        SteamCommunityApplication.GetInstance().sendRequest(requestBuilder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void searchComplete() {
        if (ActivityHelper.fragmentOrActivityIsActive(getActivity())) {
            hideInProgress();
            displayResultsSummary();
            displayPagingElementsIfNeeded();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNumTotalResults(int i) {
        this.numTotalResults = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setNumCurrentResults(int i) {
        this.numCurrentResults = i;
    }

    protected void displayResultsSummary() {
        int i;
        String str;
        int i2 = this.numTotalResults;
        if (i2 <= 0) {
            i = R.string.Search_Label_ResultsNone;
        } else {
            i = this.numCurrentResults == i2 ? R.string.Search_Label_ResultsAll : R.string.Search_Label_Results;
        }
        String string = getResources().getString(i);
        int i3 = this.numCurrentResults;
        int i4 = this.numTotalResults;
        if (i3 == i4) {
            str = String.valueOf(i4);
        } else {
            str = String.valueOf(this.queryOffset + 1) + "-" + String.valueOf(this.queryOffset + this.numCurrentResults);
        }
        this.progressLabel.setText(string.replace("#", str).replace("$", String.valueOf(this.numTotalResults)));
        this.progressLabel.setVisibility(0);
    }

    protected void hideInProgress() {
        this.progressLabel.setVisibility(8);
    }

    protected void displayPagingElementsIfNeeded() {
        this.footerButtons.setVisibility((hasNextPage() || hasPrevPage()) ? 0 : 8);
        this.footerBtnNext.setVisibility(hasNextPage() ? 0 : 4);
        this.footerBtnPrev.setVisibility(hasPrevPage() ? 0 : 4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void displayInProgress() {
        this.progressLabel.setText(R.string.Search_Label_Searching);
        this.progressLabel.setVisibility(0);
    }

    private boolean hasNextPage() {
        return this.queryOffset <= this.numTotalResults + (-50);
    }

    private boolean hasPrevPage() {
        return this.queryOffset > 0;
    }
}
