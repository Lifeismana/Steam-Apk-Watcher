#!/bin/bash

set -e

APPS="com.valvesoftware.android.steam.community com.valvesoftware.android.steam.friendsui com.valvesoftware.steamlink"

for APP in $APPS;
do
    rm -rf $APP
    mkdir -p $APP
    apkeep -a $APP -d apk-pure $APP
    if [ -f $APP/$APP.xapk ]; then
        unzip -o $APP/$APP.xapk -d $APP
    fi
    echo "Unzipping $APP.apk"
    jadx --deobf --show-bad-code -d $APP $APP/$APP.apk
    # TODO: list all files extracted by jadx
    find $APP -type f -exec md5sum {} >> $APP/$APP.apk.jadx.txt \;
    for FILE in $APP/*.apk
    do
        unzip -lv "$FILE" | head -n -2 | tail -n+4 | awk '{print $1,$(NF-1),$NF}' >> "$FILE.txt.tmp"
        sort -k 3 -o "$FILE.txt.tmp"{,}
        column -t "$FILE.txt.tmp" > "$FILE.txt"
    done
    # decompile bundle
    if [ -f $APP/ressources/assets/index.android.bundle ]; then
        echo "Decompiling $APP/assets/index.android.bundle"
        hbc-decompiler $APP/assets/index.android.bundle $APP/assets/index.android.bundle.decompiled.js
    fi
done

# for bundle in ./**/index.android.bundle;
# do
#     echo "Unpacking $bundle"
#     file $bundle    
# done