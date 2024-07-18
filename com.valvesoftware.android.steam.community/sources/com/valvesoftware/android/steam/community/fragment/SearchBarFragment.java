package com.valvesoftware.android.steam.community.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;

/* loaded from: classes.dex */
public class SearchBarFragment extends Fragment {
    private EditText searchTextBox;
    private TextWatcher textWatcher;

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.search_bar_fragment, viewGroup, false);
        Button closeButton = getCloseButton(inflate);
        this.searchTextBox = getSearchTextBox(inflate);
        TextWatcher textWatcher = this.textWatcher;
        if (textWatcher != null) {
            this.searchTextBox.addTextChangedListener(textWatcher);
        }
        closeButton.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SearchBarFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SearchBarFragment.this.closeSearch();
            }
        });
        return inflate;
    }

    public void openSearch() {
        setVisibility(0);
        this.searchTextBox.requestFocus();
        ActivityHelper.showKeyboard(getActivity());
    }

    public void closeSearch() {
        this.searchTextBox.setText("");
        setVisibility(8);
        ActivityHelper.hideKeyboard(getActivity());
    }

    private void setVisibility(int i) {
        View findViewById = getView().findViewById(R.id.list_search_bar);
        if (findViewById != null) {
            findViewById.setVisibility(i);
        } else {
            getView().setVisibility(i);
        }
    }

    public void setSearchTextChangedListener(TextWatcher textWatcher) {
        if (textWatcher == null) {
            return;
        }
        this.textWatcher = textWatcher;
        EditText editText = this.searchTextBox;
        if (editText != null) {
            editText.addTextChangedListener(this.textWatcher);
        }
    }

    private EditText getSearchTextBox(View view) {
        return (EditText) view.findViewById(R.id.search_bar_text);
    }

    private Button getCloseButton(View view) {
        return (Button) view.findViewById(R.id.search_bar_close_button);
    }
}
