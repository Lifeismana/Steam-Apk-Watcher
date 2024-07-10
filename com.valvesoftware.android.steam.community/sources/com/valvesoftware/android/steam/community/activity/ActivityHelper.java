package com.valvesoftware.android.steam.community.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ToggleButton;
import com.valvesoftware.android.steam.community.R;
import com.valvesoftware.android.steam.community.SteamCommunityApplication;

/* loaded from: classes.dex */
public class ActivityHelper {
    public static void UpdateToggleButtonStyles(ToggleButton toggleButton) {
        int i;
        int i2;
        int i3;
        if (toggleButton.isChecked()) {
            i = R.color.primary_foreground;
            i2 = R.color.primary_background;
            i3 = 1;
        } else {
            i = R.color.primary_foreground_dim;
            i2 = R.color.secondary_background;
            i3 = 0;
        }
        toggleButton.setTextColor(SteamCommunityApplication.GetInstance().getResources().getColor(i));
        toggleButton.setBackgroundColor(SteamCommunityApplication.GetInstance().getResources().getColor(i2));
        toggleButton.setTypeface(null, i3);
    }

    public static void hideKeyboard(Activity activity) {
        View currentFocus;
        if (activity == null || (currentFocus = activity.getCurrentFocus()) == null) {
            return;
        }
        ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 1);
    }

    public static void showKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        ((InputMethodManager) activity.getSystemService("input_method")).toggleSoftInput(1, 0);
    }

    public static boolean fragmentIsActive(Fragment fragment) {
        return (fragment == null || fragment.getActivity() == null || fragment.getActivity().isFinishing() || fragment.isRemoving() || fragment.isDetached() || !fragment.isAdded()) ? false : true;
    }

    @SuppressLint({"NewApi"})
    public static boolean fragmentOrActivityIsActive(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Activity)) {
            return (obj instanceof Fragment) && fragmentIsActive((Fragment) obj);
        }
        Activity activity = (Activity) obj;
        return !activity.isFinishing() && (Build.VERSION.SDK_INT <= 17 || !activity.isDestroyed());
    }
}
