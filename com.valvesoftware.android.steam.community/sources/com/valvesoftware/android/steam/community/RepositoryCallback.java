package com.valvesoftware.android.steam.community;

/* loaded from: classes.dex */
public interface RepositoryCallback<T> {
    void dataAvailable(T t);

    void end();
}
