package com.valvesoftware.android.steam.community.model;

/* loaded from: classes.dex */
public class UmqMessage extends UmqMessageBase {
    public String chatPartnerSteamId;
    public boolean hadSendError;
    public PersonaState personaState;
    public String text;

    public long getUtcTimeStampInMilliseconds() {
        return this.utcTimeStamp * 1000;
    }

    public boolean isEmpty() {
        String str = this.text;
        return str == null || str.length() == 0;
    }

    @Override // com.valvesoftware.android.steam.community.model.UmqMessageBase
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass() || !super.equals(obj)) {
            return false;
        }
        UmqMessage umqMessage = (UmqMessage) obj;
        String str = this.chatPartnerSteamId;
        if (str == null ? umqMessage.chatPartnerSteamId != null : !str.equals(umqMessage.chatPartnerSteamId)) {
            return false;
        }
        String str2 = this.text;
        if (str2 != null) {
            if (str2.equals(umqMessage.text)) {
                return true;
            }
        } else if (umqMessage.text == null) {
            return true;
        }
        return false;
    }

    @Override // com.valvesoftware.android.steam.community.model.UmqMessageBase
    public int hashCode() {
        int hashCode = super.hashCode() * 31;
        String str = this.chatPartnerSteamId;
        int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.text;
        return hashCode2 + (str2 != null ? str2.hashCode() : 0);
    }
}
