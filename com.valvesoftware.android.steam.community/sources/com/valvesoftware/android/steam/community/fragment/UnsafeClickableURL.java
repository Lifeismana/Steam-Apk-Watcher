package com.valvesoftware.android.steam.community.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.text.style.URLSpan;
import android.view.View;
import com.valvesoftware.android.steam.community.R;

/* loaded from: classes.dex */
public class UnsafeClickableURL extends URLSpan {
    private final FragmentActivity activity;
    private boolean m_bShowUnsafeWarning;

    public UnsafeClickableURL(URLSpan uRLSpan, boolean z, FragmentActivity fragmentActivity) {
        super(uRLSpan.getURL());
        this.m_bShowUnsafeWarning = false;
        this.m_bShowUnsafeWarning = z;
        this.activity = fragmentActivity;
    }

    public void HandleUserProcceedSelected(View view) {
        try {
            super.onClick(view);
        } catch (Exception unused) {
        }
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(final View view) {
        if (!this.m_bShowUnsafeWarning) {
            HandleUserProcceedSelected(view);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(R.string.nonsteam_link_title);
        builder.setMessage(this.activity.getString(R.string.nonsteam_link_text) + "\n\n" + getURL());
        builder.setPositiveButton(R.string.nonsteam_link_ok, new DialogInterface.OnClickListener() { // from class: com.valvesoftware.android.steam.community.fragment.UnsafeClickableURL.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                UnsafeClickableURL.this.HandleUserProcceedSelected(view);
            }
        });
        builder.setNegativeButton(R.string.Cancel, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }
}
