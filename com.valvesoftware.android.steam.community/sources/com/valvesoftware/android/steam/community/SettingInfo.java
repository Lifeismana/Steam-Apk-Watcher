package com.valvesoftware.android.steam.community;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.util.Calendar;

/* loaded from: classes.dex */
public class SettingInfo {
    public int m_resid = 0;
    public int m_resid_detailed = 0;
    public String m_key = null;
    public SettingType m_type = SettingType.SECTION;
    public AccessRight m_access = AccessRight.NONE;
    public String m_defaultValue = null;
    public Object m_extraData = null;
    public UpdateListener m_pUpdateListener = null;

    /* loaded from: classes.dex */
    public enum AccessRight {
        NONE,
        VALID_ACCOUNT,
        USER,
        CODE
    }

    /* loaded from: classes.dex */
    public static class RadioSelectorItem {
        public int resid_text;
        public int value;
    }

    /* loaded from: classes.dex */
    public enum SettingType {
        SECTION,
        INFO,
        CHECK,
        DATE,
        URI,
        MARKET,
        UNREADMSG,
        RADIOSELECTOR,
        RINGTONESELECTOR
    }

    /* loaded from: classes.dex */
    public interface UpdateListener {
        void OnSettingInfoValueUpdate(SettingInfo settingInfo, String str, Transaction transaction);
    }

    public String getValue(Context context) {
        String loginSteamID = LoggedInUserAccountInfo.getLoginSteamID();
        if (loginSteamID == null || loginSteamID.equals("")) {
            return this.m_defaultValue;
        }
        return context.getSharedPreferences("steam.settings." + loginSteamID, 0).getString(this.m_key, this.m_defaultValue);
    }

    public boolean getBooleanValue(Context context) {
        String value = getValue(context);
        return (value == null || value.equals("")) ? false : true;
    }

    public int getIntegerValue(Context context) {
        try {
            return Integer.parseInt(getValue(context));
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    public RadioSelectorItem getRadioSelectorItemValue(Context context) {
        RadioSelectorItem[] radioSelectorItemArr;
        try {
            radioSelectorItemArr = (RadioSelectorItem[]) this.m_extraData;
        } catch (Exception unused) {
            radioSelectorItemArr = null;
        }
        if (radioSelectorItemArr == null) {
            return null;
        }
        try {
            RadioSelectorItem findRadioSelectorItemByValue = findRadioSelectorItemByValue(Integer.valueOf(getValue(context)).intValue(), radioSelectorItemArr);
            if (findRadioSelectorItemByValue != null) {
                return findRadioSelectorItemByValue;
            }
        } catch (Exception unused2) {
        }
        try {
            RadioSelectorItem findRadioSelectorItemByValue2 = findRadioSelectorItemByValue(Integer.valueOf(this.m_defaultValue).intValue(), radioSelectorItemArr);
            if (findRadioSelectorItemByValue2 != null) {
                return findRadioSelectorItemByValue2;
            }
        } catch (Exception unused3) {
        }
        try {
            return radioSelectorItemArr[0];
        } catch (Exception unused4) {
            return null;
        }
    }

    public void setValueAndCommit(Context context, String str) {
        Transaction transaction = new Transaction(context);
        transaction.setValue(this, str);
        transaction.commit();
    }

    /* loaded from: classes.dex */
    public static class Transaction {
        private boolean m_bCookiesMarkedForSync = false;
        private SharedPreferences.Editor m_editor;

        @SuppressLint({"CommitPrefEdits"})
        public Transaction(Context context) {
            this.m_editor = null;
            String loginSteamID = LoggedInUserAccountInfo.getLoginSteamID();
            if (loginSteamID == null || loginSteamID.equals("")) {
                return;
            }
            this.m_editor = context.getSharedPreferences("steam.settings." + loginSteamID, 0).edit();
        }

        public void setValue(SettingInfo settingInfo, String str) {
            SharedPreferences.Editor editor = this.m_editor;
            if (editor != null) {
                editor.putString(settingInfo.m_key, str);
            }
            if (settingInfo.m_pUpdateListener != null) {
                settingInfo.m_pUpdateListener.OnSettingInfoValueUpdate(settingInfo, str, this);
            }
        }

        public void commit() {
            if (this.m_bCookiesMarkedForSync) {
                LoggedInUserAccountInfo.syncAllCookies();
                this.m_bCookiesMarkedForSync = false;
            }
            SharedPreferences.Editor editor = this.m_editor;
            if (editor != null) {
                editor.commit();
            }
        }

        public void markCookiesForSync() {
            this.m_bCookiesMarkedForSync = true;
        }
    }

    /* loaded from: classes.dex */
    public static class DateConverter {
        public static Calendar makeCalendar(String str) {
            Calendar calendar = Calendar.getInstance();
            if (str != null && !str.equals("")) {
                int intValue = Integer.valueOf(str).intValue();
                int i = intValue / 10000;
                calendar.set(i, (intValue - (i * 10000)) / 100, intValue % 100);
            }
            return calendar;
        }

        public static String formatDate(String str) {
            return DateFormat.getDateInstance(0).format(makeCalendar(str).getTime());
        }

        public static String makeValue(int i, int i2, int i3) {
            return "" + ((i * 10000) + (i2 * 100) + i3);
        }

        public static String makeUnixTime(String str) {
            return "" + (makeCalendar(str).getTimeInMillis() / 1000);
        }
    }

    /* loaded from: classes.dex */
    public static class CustomDatePickerDialog extends DatePickerDialog {
        private boolean m_bAllowSetTitle;

        public CustomDatePickerDialog(Context context, DatePickerDialog.OnDateSetListener onDateSetListener, Calendar calendar, int i) {
            super(context, onDateSetListener, calendar.get(1), calendar.get(2), calendar.get(5));
            this.m_bAllowSetTitle = true;
            setTitle(i);
            this.m_bAllowSetTitle = false;
        }

        @Override // android.app.AlertDialog, android.app.Dialog
        public void setTitle(CharSequence charSequence) {
            if (this.m_bAllowSetTitle) {
                super.setTitle(charSequence);
            }
        }
    }

    public static RadioSelectorItem findRadioSelectorItemByValue(int i, RadioSelectorItem[] radioSelectorItemArr) {
        for (RadioSelectorItem radioSelectorItem : radioSelectorItemArr) {
            if (radioSelectorItem.value == i) {
                return radioSelectorItem;
            }
        }
        return null;
    }
}
