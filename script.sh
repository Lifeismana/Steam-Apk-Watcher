#!/bin/bash

set -e

APPS="com.valvesoftware.android.steam.community com.valvesoftware.android.steam.friendsui com.valvesoftware.steamlink"

ProcessApp()
{
    rm -rf $1
    mkdir -p $1
    if [ -n "$2" ]; then
        echo "Downloading $1 Version $2"
        apkeep -a $1@$2 -d apk-pure $1 $2
    else
        echo "Downloading $1"
        apkeep -a $1 -d apk-pure $1
    fi
    if [ -f $1/$1.xapk ]; then
        unzip -o $1/$1.xapk -d $1
    fi
    echo "Unzipping $1.apk"
    jadx --deobf --show-bad-code -d $1 $1/$1.apk
    find $1 -type f -exec md5sum {} >> $1/$1.apk.jadx.txt \;
    for FILE in $1/*.apk
    do
        unzip -lv "$FILE" | head -n -2 | tail -n+4 | awk '{print $1,$(NF-1),$NF}' >> "$FILE.txt.tmp"
        sort -k 3 -o "$FILE.txt.tmp"{,}
        column -t "$FILE.txt.tmp" > "$FILE.txt"
    done
    # decompile bundle
    if [ -f $1/resources/assets/index.android.bundle ]; then
        echo "Decompiling $1/resources/assets/index.android.bundle"
        hbc-decompiler $1/resources/assets/index.android.bundle $1/resources/assets/index.android.bundle.decompiled.js
        hbc-file-parser $1/resources/assets/index.android.bundle >> $1/resources/assets/index.android.bundle.header.txt
    fi
}

if [ -n "$APP_TO_PROCESS" ]; then
    ProcessApp $APP_TO_PROCESS $APP_VERSION
else
    for APP in $APPS;
    do
        ProcessApp $APP
    done
fi