package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import com.valvesoftware.android.steam.community.model.Persona;
import com.valvesoftware.android.steam.community.model.UmqMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public class LocalDb {

    /* renamed from: db */
    private SQLiteDatabase f11db;
    private LocalDbOpenHelper dbHelper;

    public LocalDb(Context context) {
        this.dbHelper = LocalDbOpenHelper.getInstance(context);
        this.f11db = this.dbHelper.getWritableDatabase();
    }

    public synchronized List<UmqMessage> getAllUnreadMessages(String str) {
        ArrayList arrayList = new ArrayList();
        if (!BIsValidSteamIdString(str)) {
            return arrayList;
        }
        Cursor cursor = null;
        try {
            cursor = this.f11db.query("Messages", null, String.format("%s = ? AND %s = ?", "deviceLoggedInSteamId", "isUnread"), new String[]{str, "1"}, null, null, null);
            return convertToMessageList(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized HashMap<String, Long> getLatestMessagesFromAllUsers(String str) {
        Cursor cursor = null;
        HashMap<String, Long> hashMap = new HashMap<>();
        if (str == null) {
            return hashMap;
        }
        try {
            cursor = this.f11db.rawQuery("SELECT chatPartnerId, MAX(utcTime) AS utcTime FROM Messages WHERE deviceLoggedInSteamId = ? AND messageText IS NOT NULL GROUP BY chatPartnerId", new String[]{str});
            return convertToMap(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x000b, code lost:
    
        if (r5.moveToFirst() != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000d, code lost:
    
        r0.put(r5.getString(r5.getColumnIndex("chatPartnerId")), java.lang.Long.valueOf(r5.getLong(r5.getColumnIndex("utcTime"))));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x002c, code lost:
    
        if (r5.moveToNext() != false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private HashMap<String, Long> convertToMap(Cursor cursor) {
        HashMap<String, Long> hashMap = new HashMap<>();
        if (cursor != null) {
        }
        return hashMap;
    }

    public synchronized List<UmqMessage> getMessages(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        if (BIsValidSteamIdString(str) && BIsValidSteamIdString(str2)) {
            deleteOldMessages(str, str2);
            Cursor cursor = null;
            try {
                cursor = this.f11db.query("Messages", null, String.format("%s = ? AND %s = ? AND messageText IS NOT NULL", "deviceLoggedInSteamId", "chatPartnerId"), new String[]{str, str2}, null, null, null, String.valueOf(200));
                return convertToMessageList(cursor);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return arrayList;
    }

    public synchronized void saveSentMessage(String str, long j, String str2, String str3) {
        if (str == null || j == 0 || str3 == null || str2 == null) {
            return;
        }
        executeInsert(getSaveMessagesStatement(this.f11db, str2, str3, System.currentTimeMillis(), j, str, false, false));
    }

    public synchronized void deleteMessages(String str, String str2) {
        if (BIsValidSteamIdString(str) && BIsValidSteamIdString(str2)) {
            String format = String.format("(SELECT id FROM Messages WHERE deviceLoggedInSteamId = %s AND chatPartnerId = %s ORDER BY utcTime DESC LIMIT 1)", str, str2);
            this.f11db.delete("Messages", String.format("%s = ? AND %s = ? AND id NOT IN " + format, "deviceLoggedInSteamId", "chatPartnerId"), new String[]{str, str2});
            execute(getClearMessageTextStatement(this.f11db, str, str2));
        }
    }

    public synchronized void markMessagesRead(String str, String str2) {
        if (str == null || str2 == null) {
            return;
        }
        if (str.length() != 0 && str2.length() != 0) {
            execute(getMarkMessagesReadStatement(this.f11db, str, str2));
        }
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public synchronized int saveMessages(List<UmqMessage> list, String str, boolean z) {
        int i = 0;
        if (list == null) {
            return 0;
        }
        if (str == null) {
            return 0;
        }
        try {
            this.f11db.beginTransaction();
            for (UmqMessage umqMessage : list) {
                if (executeInsert(getSaveMessagesStatement(this.f11db, str, umqMessage.chatPartnerSteamId, 0L, umqMessage.utcTimeStamp, umqMessage.text, umqMessage.isIncoming, z))) {
                    i++;
                }
            }
            this.f11db.setTransactionSuccessful();
            return i;
        } finally {
            this.f11db.endTransaction();
        }
    }

    public synchronized long getMostRecentDeletionTime(String str, String str2) {
        if (str == null || str2 == null) {
            return 0L;
        }
        Cursor cursor = null;
        try {
            cursor = this.f11db.rawQuery("SELECT utcTime FROM Messages WHERE messageText IS NULL AND deviceLoggedInSteamId = ? AND chatPartnerId = ? ORDER BY utcTime DESC LIMIT 1", new String[]{str, str2});
            if (!cursor.moveToFirst()) {
                return 0L;
            }
            long j = cursor.getLong(cursor.getColumnIndex("utcTime"));
            if (cursor != null) {
                cursor.close();
            }
            return j;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized String getPersonaNameForSteamId(String str) {
        if (str == null) {
            return "";
        }
        Cursor cursor = null;
        try {
            cursor = this.f11db.rawQuery("SELECT personaName FROM Personas WHERE steamId = ?", new String[]{str});
            if (!cursor.moveToFirst()) {
                return "";
            }
            String string = cursor.getString(cursor.getColumnIndex("personaName"));
            if (cursor != null) {
                cursor.close();
            }
            return string;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public synchronized String getSteamIdForPersonaName(String str) {
        if (str == null) {
            return "";
        }
        Cursor cursor = null;
        try {
            cursor = this.f11db.rawQuery("SELECT steamId FROM Personas WHERE personaName = ?", new String[]{str});
            if (!cursor.moveToFirst()) {
                return "";
            }
            if (cursor.getCount() > 1) {
                if (cursor != null) {
                    cursor.close();
                }
                return "";
            }
            String string = cursor.getString(cursor.getColumnIndex("steamId"));
            if (cursor != null) {
                cursor.close();
            }
            return string;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void clearPersonaInfo() {
        this.f11db.delete("Personas", null, null);
    }

    /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
    public synchronized void replaceStoredFriendsList(Collection<Persona> collection, String str) {
        try {
            SQLiteStatement compileStatement = this.f11db.compileStatement("INSERT INTO Personas( steamId, personaName, avatarUrl, friendOfSteamId )  VALUES (?,?,?,?);");
            try {
                this.f11db.beginTransaction();
                for (Persona persona : collection) {
                    if (persona.isFriend()) {
                        compileStatement.bindString(1, persona.steamId);
                        compileStatement.bindString(2, persona.personaName);
                        compileStatement.bindString(3, persona.mediumAvatarUrl);
                        compileStatement.bindString(4, str);
                        compileStatement.execute();
                    }
                }
                this.f11db.setTransactionSuccessful();
                this.f11db.endTransaction();
            } catch (Throwable th) {
                this.f11db.endTransaction();
                throw th;
            }
        } catch (Exception e) {
            Log.e("Sqlite error", e.toString());
        }
    }

    public synchronized void clearNotifications() {
        this.f11db.delete("Notifications", null, null);
    }

    public synchronized void saveChatNotification(ChatNotification chatNotification) {
        SQLiteStatement compileStatement = this.f11db.compileStatement("INSERT INTO Notifications ( fromPersona, notificationMessage )  VALUES (?,?);");
        compileStatement.bindString(1, chatNotification.from != null ? chatNotification.from : "");
        compileStatement.bindString(2, chatNotification.message);
        executeInsert(compileStatement);
    }

    public synchronized List<ChatNotification> getNotifications() {
        ArrayList arrayList = new ArrayList();
        Cursor cursor = null;
        try {
            cursor = this.f11db.rawQuery("SELECT fromPersona, notificationMessage FROM Notifications", null);
            if (!cursor.moveToFirst()) {
                return arrayList;
            }
            List<ChatNotification> convertToNotificationsList = convertToNotificationsList(cursor);
            if (cursor != null) {
                cursor.close();
            }
            return convertToNotificationsList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:4:0x000b, code lost:
    
        if (r4.moveToFirst() != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000d, code lost:
    
        r1 = new com.valvesoftware.android.steam.community.ChatNotification();
        r1.from = r4.getString(r4.getColumnIndex("fromPersona"));
        r1.message = r4.getString(r4.getColumnIndex("notificationMessage"));
        r0.add(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0031, code lost:
    
        if (r4.moveToNext() != false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private List<ChatNotification> convertToNotificationsList(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor != null) {
        }
        return arrayList;
    }

    private void deleteOldMessages(String str, String str2) {
        if (BIsValidSteamIdString(str) && BIsValidSteamIdString(str2)) {
            try {
                String format = String.format("(SELECT id FROM Messages WHERE deviceLoggedInSteamId = %s AND chatPartnerId = '%s' ORDER BY utcTime DESC LIMIT " + String.valueOf(200) + ")", str, str2);
                this.f11db.delete("Messages", String.format("%s = ? AND %s = ? AND id NOT IN " + format, "deviceLoggedInSteamId", "chatPartnerId"), new String[]{str, str2});
            } catch (Exception e) {
                Log.e("err", e.toString());
            }
        }
    }

    private SQLiteStatement getSaveMessagesStatement(SQLiteDatabase sQLiteDatabase, String str, String str2, long j, long j2, String str3, boolean z, boolean z2) {
        SQLiteStatement compileStatement = sQLiteDatabase.compileStatement("INSERT OR IGNORE INTO Messages( chatPartnerId,deviceLoggedInSteamId,time,utcTime,messageText,isUnread,isIncoming) VALUES (?,?,?,?,?,?,?)");
        compileStatement.bindString(1, str2);
        compileStatement.bindString(2, str);
        compileStatement.bindLong(3, j);
        compileStatement.bindLong(4, j2);
        compileStatement.bindString(5, str3);
        compileStatement.bindLong(6, z2 ? 1L : 0L);
        compileStatement.bindLong(7, z ? 1L : 0L);
        return compileStatement;
    }

    private SQLiteStatement getMarkMessagesReadStatement(SQLiteDatabase sQLiteDatabase, String str, String str2) {
        SQLiteStatement compileStatement = sQLiteDatabase.compileStatement("UPDATE Messages SET isUnread = 0 WHERE deviceLoggedInSteamId = ? AND chatPartnerId = ? ");
        compileStatement.bindString(1, str);
        compileStatement.bindString(2, str2);
        return compileStatement;
    }

    private SQLiteStatement getClearMessageTextStatement(SQLiteDatabase sQLiteDatabase, String str, String str2) {
        SQLiteStatement compileStatement = sQLiteDatabase.compileStatement("UPDATE Messages SET messageText = NULL WHERE deviceLoggedInSteamId = ? AND chatPartnerId = ? ");
        compileStatement.bindString(1, str);
        compileStatement.bindString(2, str2);
        return compileStatement;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x004c, code lost:
    
        r2 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x000b, code lost:
    
        if (r7.moveToFirst() != false) goto L6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x000d, code lost:
    
        r1 = new com.valvesoftware.android.steam.community.model.UmqMessage();
        r1.type = com.valvesoftware.android.steam.community.model.UmqMessageType.MESSAGE_TEXT;
        r1.chatPartnerSteamId = r7.getString(r7.getColumnIndex("chatPartnerId"));
        r1.text = r7.getString(r7.getColumnIndex("messageText"));
        r1.utcTimeStamp = r7.getLong(r7.getColumnIndex("utcTime"));
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0048, code lost:
    
        if (r7.getLong(r7.getColumnIndex("isIncoming")) != 1) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x004a, code lost:
    
        r2 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x004d, code lost:
    
        r1.isIncoming = r2;
        r0.add(r1);
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x0056, code lost:
    
        if (r7.moveToNext() != false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static List<UmqMessage> convertToMessageList(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor != null) {
        }
        return arrayList;
    }

    private static void execute(SQLiteStatement sQLiteStatement) {
        try {
            sQLiteStatement.execute();
        } finally {
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
            }
        }
    }

    private static boolean executeInsert(SQLiteStatement sQLiteStatement) {
        long j;
        try {
            j = sQLiteStatement.executeInsert();
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
            }
        } catch (Exception unused) {
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
            }
            j = -1;
        } catch (Throwable th) {
            if (sQLiteStatement != null) {
                sQLiteStatement.close();
            }
            throw th;
        }
        return j != -1;
    }

    static boolean BIsValidSteamIdString(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
