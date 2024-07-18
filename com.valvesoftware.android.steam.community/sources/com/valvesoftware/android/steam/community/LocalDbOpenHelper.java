package com.valvesoftware.android.steam.community;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/* loaded from: classes.dex */
public class LocalDbOpenHelper extends SQLiteOpenHelper {
    private static LocalDbOpenHelper instance;

    private LocalDbOpenHelper(Context context) {
        super(context, "SteamLocal.db", (SQLiteDatabase.CursorFactory) null, 11);
    }

    public static LocalDbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE Messages ( id integer primary key autoincrement, chatPartnerId text not null, deviceLoggedInSteamId text not null, time integer not null, utcTime integer not null, messageText text, isUnread integer not null, isIncoming integer not null,  UNIQUE (utcTime,messageText) )");
        sQLiteDatabase.execSQL("CREATE INDEX idxMessageFromSteamId ON Messages ( chatPartnerId,deviceLoggedInSteamId )");
        sQLiteDatabase.execSQL("CREATE INDEX idxMessageToSteamIdUnread ON Messages ( chatPartnerId,isUnread )");
        sQLiteDatabase.execSQL("CREATE TABLE Personas( steamId text not null, personaName text, avatarUrl text, friendOfSteamId text )");
        sQLiteDatabase.execSQL("CREATE INDEX idxPersonaSteamId ON Personas ( steamId )");
        sQLiteDatabase.execSQL("CREATE TABLE Notifications( fromPersona text, notificationMessage text)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.w("SteamLocal.db", "Database upgrade dropping tables: old ver = " + i + ", new ver = " + i2);
        sQLiteDatabase.execSQL("DROP TABLE if exists Messages");
        sQLiteDatabase.execSQL("DROP TABLE if exists Personas");
        onCreate(sQLiteDatabase);
    }
}
