package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: SDLActivity.java */
/* loaded from: classes.dex */
public class DummyEdit extends View implements View.OnKeyListener {

    /* renamed from: ic */
    InputConnection f0ic;

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        return true;
    }

    public DummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return SDLActivity.handleKeyEvent(view, i, keyEvent, this.f0ic);
    }

    @Override // android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && i == 4 && SDLActivity.mTextEdit != null && SDLActivity.mTextEdit.getVisibility() == 0) {
            SDLActivity.onNativeKeyboardFocusLost();
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        this.f0ic = new SDLInputConnection(this, true);
        editorInfo.inputType = 131073;
        editorInfo.imeOptions = 301989888;
        return this.f0ic;
    }
}
