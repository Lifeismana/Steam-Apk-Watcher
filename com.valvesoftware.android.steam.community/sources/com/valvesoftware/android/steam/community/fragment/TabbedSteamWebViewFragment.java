package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ToggleButton;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SettingInfoDB;
import com.valvesoftware.android.steam.community.SteamAppUri;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.jsontranslators.UrlCategoryTranslator;
import com.valvesoftware.android.steam.community.model.UrlCategory;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.RequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.RequestErrorInfo;
import com.valvesoftware.android.steam.community.webrequests.ResponseListener;
import java.util.List;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TabbedSteamWebViewFragment extends WebViewFragment {
    private static final int[] m_categoryButtons = {R.id.web_select_1, R.id.web_select_2, R.id.web_select_3, R.id.web_select_4, R.id.web_select_5, R.id.web_select_6, R.id.web_select_7, R.id.web_select_8};
    private LinearLayout btnLayout;
    private int m_selectedTab = 0;
    private final RadioGroup.OnCheckedChangeListener m_toggleListener = new RadioGroup.OnCheckedChangeListener() { // from class: com.valvesoftware.android.steam.community.fragment.TabbedSteamWebViewFragment.1
        @Override // android.widget.RadioGroup.OnCheckedChangeListener
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            for (int i2 = 0; i2 < radioGroup.getChildCount(); i2++) {
                ToggleButton toggleButton = (ToggleButton) radioGroup.getChildAt(i2);
                toggleButton.setChecked(toggleButton.getId() == i);
                if (toggleButton.getId() == i) {
                    TabbedSteamWebViewFragment.this.m_selectedTab = i2;
                }
            }
        }
    };

    @Override // com.valvesoftware.android.steam.community.fragment.WebViewFragment, android.support.v4.app.Fragment
    public void onResume() {
        String str;
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments == null || (str = arguments.getString("category")) == null) {
            str = null;
        }
        getCategoryUrlInfo(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCategories(List<UrlCategory> list) {
        if (ActivityHelper.fragmentIsActive(this) && list != null && list.size() > 0) {
            if (this.m_selectedTab > list.size() - 1) {
                this.m_selectedTab = list.size() - 1;
            }
            this.btnLayout.setWeightSum(list.size());
            this.btnLayout.setVisibility(list.size() > 1 ? 0 : 8);
            int i = 0;
            while (i < list.size()) {
                ToggleButton toggleButton = (ToggleButton) getActivity().findViewById(m_categoryButtons[i]);
                toggleButton.setVisibility(0);
                toggleButton.setText(list.get(i).title);
                toggleButton.setTextOn(list.get(i).title);
                toggleButton.setTextOff(list.get(i).title);
                toggleButton.setOnClickListener(new ToggleOnClickListener(list.get(i).url));
                toggleButton.setChecked(i == this.m_selectedTab);
                i++;
            }
            while (i < m_categoryButtons.length) {
                ((ToggleButton) getActivity().findViewById(m_categoryButtons[i])).setVisibility(8);
                i++;
            }
            UpdateToggleButtonStyles();
            this.m_webView.loadUrl(list.get(this.m_selectedTab).url);
        }
    }

    @Override // com.valvesoftware.android.steam.community.fragment.WebViewFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        ((RadioGroup) onCreateView.findViewById(R.id.toggleGroup)).setOnCheckedChangeListener(this.m_toggleListener);
        this.btnLayout = (LinearLayout) onCreateView.findViewById(R.id.web_select_layout);
        return onCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCategoriesFailed() {
        this.m_webView.setViewContentsShowFailure(SteamAppUri.catalog().toString(), SteamCommunityApplication.GetInstance().getString(R.string.Web_Error_Reload));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void UpdateToggleButtonStyles() {
        for (int i : m_categoryButtons) {
            ToggleButton toggleButton = (ToggleButton) getActivity().findViewById(i);
            if (toggleButton != null) {
                ActivityHelper.UpdateToggleButtonStyles(toggleButton);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ToggleOnClickListener implements View.OnClickListener {
        String m_clickUrl;

        ToggleOnClickListener(String str) {
            this.m_clickUrl = str;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            ToggleButton toggleButton = (ToggleButton) view;
            RadioGroup radioGroup = (RadioGroup) view.getParent();
            boolean z = radioGroup.getCheckedRadioButtonId() == view.getId();
            radioGroup.check(view.getId());
            String str = this.m_clickUrl;
            if (z) {
                toggleButton.setChecked(true);
            } else {
                TabbedSteamWebViewFragment.this.m_webView.loadUrl(str);
            }
            TabbedSteamWebViewFragment.this.UpdateToggleButtonStyles();
        }
    }

    private void getCategoryUrlInfo(String str) {
        String str2 = "Settings".equals(str) ? SettingInfoDB.URL_SETTINGS_CATEGORIES : null;
        if (str2 == null || str2.isEmpty()) {
            setCategoriesFailed();
            return;
        }
        RequestBuilder genericJsonGetRequestBuilder = Endpoints.getGenericJsonGetRequestBuilder(str2);
        genericJsonGetRequestBuilder.setResponseListener(new ResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.TabbedSteamWebViewFragment.2
            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onSuccess(JSONObject jSONObject) {
                if (!jSONObject.optBoolean("success", false)) {
                    TabbedSteamWebViewFragment.this.setCategoriesFailed();
                    return;
                }
                List<UrlCategory> translate = UrlCategoryTranslator.translate(jSONObject);
                if (translate.isEmpty()) {
                    TabbedSteamWebViewFragment.this.setCategoriesFailed();
                } else {
                    TabbedSteamWebViewFragment.this.setCategories(translate);
                }
            }

            @Override // com.valvesoftware.android.steam.community.webrequests.ResponseListener
            public void onError(RequestErrorInfo requestErrorInfo) {
                TabbedSteamWebViewFragment.this.setCategoriesFailed();
            }
        });
        SteamCommunityApplication.GetInstance().sendRequest(genericJsonGetRequestBuilder);
    }
}
