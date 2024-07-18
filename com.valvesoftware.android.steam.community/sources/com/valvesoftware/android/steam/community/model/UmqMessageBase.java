package com.valvesoftware.android.steam.community.model;

/* loaded from: classes.dex */
public class UmqMessageBase {
    public boolean isIncoming;
    public String secureMessageId;
    public UmqMessageType type;
    public long utcTimeStamp;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UmqMessageBase)) {
            return false;
        }
        UmqMessageBase umqMessageBase = (UmqMessageBase) obj;
        return this.utcTimeStamp == umqMessageBase.utcTimeStamp && this.isIncoming == umqMessageBase.isIncoming;
    }

    public int hashCode() {
        return (((int) this.utcTimeStamp) * 31) + (this.isIncoming ? 1 : 0);
    }
}
