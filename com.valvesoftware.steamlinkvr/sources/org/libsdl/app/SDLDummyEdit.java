package org.libsdl.app;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/* loaded from: classes.dex */
public class SDLDummyEdit extends View implements View.OnKeyListener {

    /* renamed from: ic */
    InputConnection f4ic;
    int input_type;

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        return true;
    }

    SDLDummyEdit(Context context) {
        super(context);
        setFocusableInTouchMode(true);
        setFocusable(true);
        setOnKeyListener(this);
    }

    void setInputType(int i) {
        this.input_type = i;
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return SDLActivity.handleKeyEvent(view, i, keyEvent, this.f4ic);
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
        this.f4ic = new SDLInputConnection(this, true);
        editorInfo.inputType = this.input_type;
        editorInfo.imeOptions = 301989888;
        return this.f4ic;
    }
}
