package org.libsdl.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;

/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
class SDLClipboardHandler implements ClipboardManager.OnPrimaryClipChangedListener {
    protected ClipboardManager mClipMgr;

    SDLClipboardHandler() {
        ClipboardManager clipboardManager = (ClipboardManager) SDL.getContext().getSystemService("clipboard");
        this.mClipMgr = clipboardManager;
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    public boolean clipboardHasText() {
        if (Build.VERSION.SDK_INT >= 28) {
            return this.mClipMgr.hasPrimaryClip();
        }
        return this.mClipMgr.hasText();
    }

    public String clipboardGetText() {
        ClipData.Item itemAt;
        CharSequence text;
        ClipData primaryClip = this.mClipMgr.getPrimaryClip();
        if (primaryClip == null || (itemAt = primaryClip.getItemAt(0)) == null || (text = itemAt.getText()) == null) {
            return null;
        }
        return text.toString();
    }

    public void clipboardSetText(String str) {
        this.mClipMgr.removePrimaryClipChangedListener(this);
        if (str.isEmpty()) {
            if (Build.VERSION.SDK_INT >= 28) {
                this.mClipMgr.clearPrimaryClip();
            } else {
                this.mClipMgr.setPrimaryClip(ClipData.newPlainText(null, ""));
            }
        } else {
            this.mClipMgr.setPrimaryClip(ClipData.newPlainText(null, str));
        }
        this.mClipMgr.addPrimaryClipChangedListener(this);
    }

    @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
    public void onPrimaryClipChanged() {
        SDLActivity.onNativeClipboardChanged();
    }
}
