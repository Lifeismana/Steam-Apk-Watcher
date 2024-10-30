package org.libsdl.app;

import android.os.Build;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.EditText;

/* loaded from: classes.dex */
public class SDLInputConnection extends BaseInputConnection {
    protected String mCommittedText;
    protected EditText mEditText;

    public static native void nativeCommitText(String str, int i);

    public static native void nativeGenerateScancodeForUnichar(char c);

    public SDLInputConnection(View view, boolean z) {
        super(view, z);
        this.mCommittedText = "";
        this.mEditText = new EditText(SDL.getContext());
    }

    @Override // android.view.inputmethod.BaseInputConnection
    public Editable getEditable() {
        return this.mEditText.getEditableText();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 66 && SDLActivity.onNativeSoftReturnKey()) {
            return true;
        }
        return super.sendKeyEvent(keyEvent);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence charSequence, int i) {
        if (!super.commitText(charSequence, i)) {
            return false;
        }
        updateText();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence charSequence, int i) {
        if (!super.setComposingText(charSequence, i)) {
            return false;
        }
        updateText();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int i, int i2) {
        if (Build.VERSION.SDK_INT > 29 || i <= 0 || i2 != 0) {
            if (!super.deleteSurroundingText(i, i2)) {
                return false;
            }
            updateText();
            return true;
        }
        while (true) {
            int i3 = i - 1;
            if (i <= 0) {
                return true;
            }
            nativeGenerateScancodeForUnichar('\b');
            i = i3;
        }
    }

    protected void updateText() {
        Editable editable = getEditable();
        if (editable == null) {
            return;
        }
        String obj = editable.toString();
        int min = Math.min(obj.length(), this.mCommittedText.length());
        int i = 0;
        while (i < min) {
            int codePointAt = this.mCommittedText.codePointAt(i);
            if (codePointAt != obj.codePointAt(i)) {
                break;
            } else {
                i += Character.charCount(codePointAt);
            }
        }
        int i2 = i;
        while (i2 < this.mCommittedText.length()) {
            int codePointAt2 = this.mCommittedText.codePointAt(i2);
            nativeGenerateScancodeForUnichar('\b');
            i2 += Character.charCount(codePointAt2);
        }
        if (i < obj.length()) {
            String charSequence = obj.subSequence(i, obj.length()).toString();
            int i3 = 0;
            while (i3 < charSequence.length()) {
                int codePointAt3 = charSequence.codePointAt(i3);
                if (codePointAt3 == 10 && SDLActivity.onNativeSoftReturnKey()) {
                    return;
                }
                if (codePointAt3 < 128) {
                    nativeGenerateScancodeForUnichar((char) codePointAt3);
                }
                i3 += Character.charCount(codePointAt3);
            }
            nativeCommitText(charSequence, 0);
        }
        this.mCommittedText = obj;
    }
}
