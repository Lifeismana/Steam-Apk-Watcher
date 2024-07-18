package com.valvesoftware.android.steam.community.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.valvesoftware.android.steam.community.LoggedInUserAccountInfo;
import com.valvesoftware.android.steam.community.PersonaRepository;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.RepositoryCallback;
import com.valvesoftware.android.steam.community.SettingInfo;
import com.valvesoftware.android.steam.community.SettingInfoDB;
import com.valvesoftware.android.steam.community.SteamAppIntents;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;
import com.valvesoftware.android.steam.community.activity.ActivityHelper;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.webrequests.Endpoints;
import com.valvesoftware.android.steam.community.webrequests.ImageRequestBuilder;
import com.valvesoftware.android.steam.community.webrequests.ImageResponseListener;
import java.util.ArrayList;
import java.util.Calendar;

/* loaded from: classes.dex */
public class SettingsFragment extends ListFragment {
    private Activity m_owner = null;
    private ArrayList<SettingInfo> m_settingsInfoArray = new ArrayList<>();
    private ListView m_listView = null;
    private SettingsListAdapter m_SettingsAdapter = null;
    private View m_viewProfile = null;
    private boolean m_bLoggedOnPresentation = false;

    @Override // android.support.v4.app.ListFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.settings_fragment, viewGroup, false);
    }

    @Override // android.support.v4.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.m_owner = getActivity();
        if (this.m_SettingsAdapter == null) {
            this.m_SettingsAdapter = new SettingsListAdapter(this.m_owner, R.layout.settings_list_item_info, this.m_settingsInfoArray);
        }
        if (this.m_listView == null) {
            this.m_listView = getListView();
        }
        setListAdapter(this.m_SettingsAdapter);
        if (this.m_owner == null || !ActivityHelper.fragmentIsActive(this)) {
            return;
        }
        this.m_owner.setTitle(R.string.Settings);
    }

    @Override // android.support.v4.app.Fragment
    public void onResume() {
        super.onResume();
        if (getActivity() == null) {
            return;
        }
        refreshListView();
    }

    private void setupUserAccountView(View view) {
        View view2 = getView();
        if (view2 == null) {
            return;
        }
        final TextView textView = (TextView) view2.findViewById(R.id.name);
        final ImageView imageView = (ImageView) view2.findViewById(R.id.avatar);
        ImageView imageView2 = (ImageView) view2.findViewById(R.id.avatar_frame);
        View findViewById = view2.findViewById(R.id.avatar_name_container);
        imageView.setImageResource(R.drawable.placeholder_contact);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view3) {
                SettingsFragment.this.getActivity().startActivity(SteamAppIntents.visitProfileIntent(SettingsFragment.this.getActivity(), LoggedInUserAccountInfo.getLoginSteamID()));
            }
        });
        PersonaRepository.getDetailedPersonaInfo(LoggedInUserAccountInfo.getLoginSteamID(), new RepositoryCallback<Persona>() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.2
            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void end() {
            }

            @Override // com.valvesoftware.android.steam.community.RepositoryCallback
            public void dataAvailable(Persona persona) {
                if (persona == null) {
                    return;
                }
                textView.setText(persona.personaName);
                SettingsFragment.this.getAvatar(persona, imageView);
            }
        });
        imageView2.setImageResource(R.drawable.avatar_frame_offline);
        textView.setTextColor(getActivity().getResources().getColor(R.color.offline));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getAvatar(Persona persona, final ImageView imageView) {
        ImageRequestBuilder imageUrlRequestBuilder = Endpoints.getImageUrlRequestBuilder(persona.fullAvatarUrl);
        imageUrlRequestBuilder.setResponseListener(new ImageResponseListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.3
            @Override // com.valvesoftware.android.steam.community.webrequests.ImageResponseListener
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        SteamCommunityApplication.GetInstance().sendRequest(imageUrlRequestBuilder);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:14:0x0069. Please report as an issue. */
    public void refreshListView() {
        this.m_bLoggedOnPresentation = LoggedInUserAccountInfo.isLoggedIn();
        this.m_settingsInfoArray.clear();
        setupUserAccountView(this.m_viewProfile);
        SettingInfoDB GetSettingInfoDB = SteamCommunityApplication.GetInstance().GetSettingInfoDB();
        this.m_settingsInfoArray.addAll(GetSettingInfoDB.GetSettingsList());
        int size = this.m_settingsInfoArray.size();
        while (true) {
            int i = size - 1;
            if (size > 0) {
                SettingInfo settingInfo = this.m_settingsInfoArray.get(i);
                boolean z = true;
                switch (settingInfo.m_access) {
                    case VALID_ACCOUNT:
                        z = this.m_bLoggedOnPresentation;
                        break;
                    case CODE:
                        if (settingInfo == GetSettingInfoDB.m_settingSslUntrustedPrompt) {
                            if (settingInfo.getRadioSelectorItemValue(this.m_owner.getApplicationContext()).value != -1) {
                                z = false;
                                break;
                            }
                        }
                        break;
                }
                z = false;
                if (z) {
                    switch (settingInfo.m_type) {
                        case INFO:
                        case CHECK:
                        case DATE:
                        case URI:
                        case MARKET:
                        case RADIOSELECTOR:
                        case RINGTONESELECTOR:
                        case UNREADMSG:
                            break;
                        default:
                            z = false;
                            break;
                    }
                }
                if (!z) {
                    this.m_settingsInfoArray.remove(i);
                }
                size = i;
            } else {
                this.m_SettingsAdapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SettingsListAdapter extends ArrayAdapter<SettingInfo> {
        private ArrayList<SettingInfo> items;

        public SettingsListAdapter(Context context, int i, ArrayList<SettingInfo> arrayList) {
            super(context, i, arrayList);
            this.items = arrayList;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            final SettingInfo settingInfo = this.items.get(i);
            if (settingInfo == null) {
                return view;
            }
            if (view == null) {
                view = ((LayoutInflater) SettingsFragment.this.m_owner.getSystemService("layout_inflater")).inflate(R.layout.settings_list_item_info, (ViewGroup) null);
                view.setClickable(true);
            }
            view.setOnClickListener(null);
            TextView textView = (TextView) view.findViewById(R.id.label);
            textView.setText(settingInfo.m_resid);
            final TextView textView2 = (TextView) view.findViewById(R.id.info);
            textView2.setText("");
            if (settingInfo.m_resid_detailed != 0) {
                textView2.setText(settingInfo.m_resid_detailed);
            }
            final ImageView imageView = (ImageView) view.findViewById(R.id.imageChevron);
            imageView.setVisibility(8);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.setting_checkbox);
            checkBox.setVisibility(8);
            switch (settingInfo.m_type) {
                case INFO:
                    textView2.setText(settingInfo.m_defaultValue);
                    break;
                case CHECK:
                    boolean booleanValue = settingInfo.getBooleanValue(SettingsFragment.this.m_owner.getApplicationContext());
                    checkBox.setVisibility(0);
                    checkBox.setChecked(booleanValue);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.3
                        @Override // android.widget.CompoundButton.OnCheckedChangeListener
                        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                            settingInfo.setValueAndCommit(SettingsFragment.this.m_owner.getApplicationContext(), z ? "1" : "");
                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            checkBox.setChecked(!r2.isChecked());
                        }
                    });
                    break;
                case DATE:
                    imageView.setVisibility(0);
                    String value = settingInfo.getValue(SettingsFragment.this.m_owner.getApplicationContext());
                    final Calendar makeCalendar = SettingInfo.DateConverter.makeCalendar(value);
                    if (value != null && !value.equals("")) {
                        textView2.setText(SettingInfo.DateConverter.formatDate(value));
                    } else {
                        textView2.setText(R.string.date_not_set);
                    }
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.5
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            Context context = SettingsFragment.this.m_owner;
                            if (Build.VERSION.SDK_INT > 10) {
                                context = new ContextThemeWrapper(context, android.R.style.Theme.Holo.Light.Dialog);
                            }
                            new SettingInfo.CustomDatePickerDialog(context, new DatePickerDialog.OnDateSetListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.5.1
                                @Override // android.app.DatePickerDialog.OnDateSetListener
                                public void onDateSet(DatePicker datePicker, int i2, int i3, int i4) {
                                    makeCalendar.set(i2, i3, i4);
                                    String makeValue = SettingInfo.DateConverter.makeValue(i2, i3, i4);
                                    if (makeValue != null && !makeValue.equals("")) {
                                        textView2.setText(SettingInfo.DateConverter.formatDate(makeValue));
                                    }
                                    settingInfo.setValueAndCommit(SettingsFragment.this.m_owner.getApplicationContext(), makeValue);
                                }
                            }, makeCalendar, R.string.settings_personal_dob_instr).show();
                        }
                    });
                    break;
                case URI:
                    imageView.setVisibility(0);
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.6
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            SettingsFragment.this.startActivity(SteamAppIntents.mainActivityIntent(SettingsFragment.this.getActivity(), Uri.parse(settingInfo.m_defaultValue)));
                        }
                    });
                    break;
                case MARKET:
                    imageView.setVisibility(0);
                    textView2.setText(settingInfo.m_defaultValue);
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.1
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            try {
                                SettingsFragment.this.getActivity().startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse("market://details?id=com.valvesoftware.android.steam.community")));
                            } catch (Exception unused) {
                                textView2.setText(settingInfo.m_defaultValue + " / " + SettingsFragment.this.getActivity().getString(R.string.Market_Unavailable));
                                try {
                                    SettingsFragment.this.getActivity().startActivity(new Intent("android.intent.action.VIEW").setData(Uri.parse("https://store.steampowered.com/mobile")));
                                } catch (Exception unused2) {
                                    imageView.setVisibility(8);
                                }
                            }
                        }
                    });
                    break;
                case RADIOSELECTOR:
                    imageView.setVisibility(0);
                    textView2.setText(settingInfo.getRadioSelectorItemValue(SettingsFragment.this.m_owner.getApplicationContext()).resid_text);
                    view.setOnClickListener(new RadioSelectorItemOnClickListener(SettingsFragment.this.getActivity(), settingInfo, textView2));
                    break;
                case RINGTONESELECTOR:
                    imageView.setVisibility(0);
                    try {
                        String value2 = settingInfo.getValue(SettingsFragment.this.m_owner.getApplicationContext());
                        if (value2 != null && settingInfo.m_defaultValue.equals(value2)) {
                            textView2.setText(R.string.settings_notifications_ring_steam);
                        } else {
                            textView2.setText(RingtoneManager.getRingtone(SettingsFragment.this.getActivity(), Uri.parse(value2)).getTitle(SettingsFragment.this.getActivity()));
                        }
                    } catch (Exception unused) {
                        textView2.setText(R.string.settings_notifications_ring_default);
                    }
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.7
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            try {
                                Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
                                intent.putExtra("android.intent.extra.ringtone.TYPE", 2);
                                intent.putExtra("android.intent.extra.ringtone.TITLE", SettingsFragment.this.getActivity().getString(settingInfo.m_resid));
                                try {
                                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", Uri.parse(settingInfo.getValue(SettingsFragment.this.m_owner.getApplicationContext())));
                                } catch (Exception unused2) {
                                    intent.putExtra("android.intent.extra.ringtone.EXISTING_URI", (Uri) null);
                                }
                                intent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", Uri.parse(settingInfo.m_defaultValue));
                                intent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                                intent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", false);
                                SettingsFragment.this.getActivity().startActivityForResult(intent, SettingInfoDB.ringToneSelectorRequestCode);
                            } catch (Exception unused3) {
                            }
                        }
                    });
                    break;
                case UNREADMSG:
                    imageView.setVisibility(0);
                    LoggedInUserAccountInfo.isLoggedIn();
                    textView.setText(SettingsFragment.this.getActivity().getString(settingInfo.m_resid).replace("#", String.valueOf(0)));
                    view.setOnClickListener(new View.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.SettingsListAdapter.2
                        @Override // android.view.View.OnClickListener
                        public void onClick(View view2) {
                            SettingsFragment.this.refreshListView();
                        }
                    });
                    break;
            }
            return view;
        }
    }

    /* loaded from: classes.dex */
    public static class RadioSelectorItemOnClickListener implements View.OnClickListener {
        Activity activity;
        AlertDialog alert;
        DialogInterface.OnClickListener m_onRadioButtonSelected = new DialogInterface.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.SettingsFragment.RadioSelectorItemOnClickListener.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SettingInfo.RadioSelectorItem[] radioSelectorItemArr = (SettingInfo.RadioSelectorItem[]) RadioSelectorItemOnClickListener.this.settingInfo.m_extraData;
                if (i < 0 || i >= radioSelectorItemArr.length) {
                    return;
                }
                if (RadioSelectorItemOnClickListener.this.valueView != null) {
                    RadioSelectorItemOnClickListener.this.valueView.setText(radioSelectorItemArr[i].resid_text);
                }
                RadioSelectorItemOnClickListener.this.settingInfo.setValueAndCommit(RadioSelectorItemOnClickListener.this.activity.getApplicationContext(), String.valueOf(radioSelectorItemArr[i].value));
                RadioSelectorItemOnClickListener.this.alert.dismiss();
                RadioSelectorItemOnClickListener.this.onSettingChanged(radioSelectorItemArr[i]);
            }
        };
        SettingInfo settingInfo;
        TextView valueView;

        public void onSettingChanged(SettingInfo.RadioSelectorItem radioSelectorItem) {
        }

        public RadioSelectorItemOnClickListener(Activity activity, SettingInfo settingInfo, TextView textView) {
            this.activity = activity;
            this.settingInfo = settingInfo;
            this.valueView = textView;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle(this.settingInfo.m_resid);
            SettingInfo.RadioSelectorItem[] radioSelectorItemArr = (SettingInfo.RadioSelectorItem[]) this.settingInfo.m_extraData;
            CharSequence[] charSequenceArr = new CharSequence[radioSelectorItemArr.length];
            SettingInfo.RadioSelectorItem radioSelectorItemValue = this.settingInfo.getRadioSelectorItemValue(this.activity.getApplicationContext());
            int i = -1;
            for (int i2 = 0; i2 < radioSelectorItemArr.length; i2++) {
                charSequenceArr[i2] = this.activity.getString(radioSelectorItemArr[i2].resid_text);
                if (radioSelectorItemValue == radioSelectorItemArr[i2]) {
                    i = i2;
                }
            }
            builder.setSingleChoiceItems(charSequenceArr, i, this.m_onRadioButtonSelected);
            this.alert = builder.create();
            this.alert.show();
        }
    }
}
