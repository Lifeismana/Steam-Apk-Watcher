#!/bin/bash

set -e

shopt -s extglob

APPS="com.valvesoftware.android.steam.community com.valvesoftware.android.steam.friendsui com.valvesoftware.steamlink"

Commit_message="Daily update"

VerifyRequirements()
{
    echo "Verifying requirements"
    command -v identify >/dev/null 2>&1 || { echo >&2 "identify missing.  Aborting."; exit 1; }
    command -v apkeep >/dev/null 2>&1 || { echo >&2 "apkeep missing.  Aborting."; exit 1; }
    command -v unzip >/dev/null 2>&1 || { echo >&2 "unzip missing.  Aborting."; exit 1; }
    command -v jadx >/dev/null 2>&1 || { echo >&2 "jadx missing.  Aborting."; exit 1; }
    command -v xpath >/dev/null 2>&1 || { echo >&2 "xpath missing.  Aborting."; exit 1; }
    command -v hbc-decompiler >/dev/null 2>&1 || { echo >&2 "hbc-decompiler missing.  Aborting."; exit 1; }
    command -v hbc-file-parser >/dev/null 2>&1 || { echo >&2 "hbc-file-parser missing.  Aborting."; exit 1; }
    command -v git >/dev/null 2>&1 || { echo >&2 "git missing.  Aborting."; exit 1; }
}

MergeDPIPNG()
{
    echo "Merging DPI PNG"
    while IFS= read -r FILE
        do
        echo "Checking $FILE"
        if [[ ! $(dirname "$FILE") == *-@(ldpi|mdpi|hdpi|xhdpi|xxhdpi|xxxhdpi) ]]; then
            continue
        fi
        if [[ -f "$FILE" ]]; then
            echo "Merging $FILE"
            filename=$(basename "$FILE")
            folder=$(basename $(dirname "$FILE"))
            # test if folder exists in the parent folder if not create it
            foldernodpi=$(dirname $(dirname "$FILE"))/${folder%-*}
            [ -d $foldernodpi ] || ( mkdir -p "$foldernodpi" && touch "$foldernodpi/.folderisnotreal" )
            # if the file already exists in that folder compare pixel count and keep the bigger one
            if [[ -f "$foldernodpi/$filename"    ]]; then
                new=($(identify -format "%w %h" "$FILE"))
                old=($(identify -format "%w %h" "$foldernodpi/$filename"))
                if [[ $(expr ${new[0]} \* ${new[1]}) -gt $(expr ${old[0]} \* ${old[1]}) ]]; then
                    mv -f "$FILE" "$foldernodpi/$filename"
                else
                    rm -f "$FILE"
                fi
            else
                mv -f "$FILE" "$foldernodpi/$filename"
            fi
        fi
    done < <(find $1 -type f -name "*.png")
}

DownloadAPK()
{
    if [[ "$SOURCE" == "google" ]]; then
        apkeep -a $1 -d google-play -e $GOOGLE_MAIL -t $AAS_TOKEN $1
    elif [[ "$SOURCE" == "apk-pure" || "$SOURCE" == "" ]]; then
        if [[ -n "$2" ]]; then
            apkeep -a $1@$2 -d apk-pure $1
            for FILE in $1/*@*apk
            do
                echo "Renaming $FILE"
                extension="${FILE##*.}"
                filename="${FILE%@*}"
                mv $FILE $filename.$extension
            done
            Commit_message="$1 Version $2"
        else
            apkeep -a $1 -d apk-pure $1
        fi
        if [[ -f $1/$1.xapk ]]; then
            unzip -o $1/$1.xapk -d $1
        fi
    elif [[ "$SOURCE" == "manual" ]]; then
        cp $2 $1/$1.apk
    else
        echo "Unknown source"
        exit 1
    fi
}

ProcessApp()
{
    echo "Processing $1"
    prev_version_code=''
    if [[ -f $1/resources/AndroidManifest.xml ]]; then
        prev_version_code=$(xpath -q -e "string(/manifest/@android:versionCode)" $1/resources/AndroidManifest.xml)
        prev_version_name=$(xpath -q -e "string(/manifest/@android:versionName)" $1/resources/AndroidManifest.xml)
    fi
    echo "Previous version: $prev_version_name - $prev_version_code"
    rm -rf $1
    mkdir -p $1
    DownloadAPK $1 $2
    jadx --deobf --show-bad-code -d $1 $1/$1.apk
    find $1 -type f -exec md5sum {} >> $1/$1.apk.jadx.txt \;
    sort -k 2 -o "$1/$1.apk.jadx.txt"{,}
    for FILE in $1/*.apk
    do
        rm -f "/tmp/apkunzip.txt"
        unzip -lv "$FILE" | head -n -2 | tail -n+4 | awk '{print $1,$(NF-1),$NF}' >> "/tmp/apkunzip.txt"
        sort -k 3 -o "/tmp/apkunzip.txt"{,}
        column -t "/tmp/apkunzip.txt" > "$FILE.txt"
    done
    # decompile bundle
    if [[ -f $1/resources/assets/index.android.bundle ]]; then
        echo "Decompiling $1/resources/assets/index.android.bundle"
        hbc-decompiler $1/resources/assets/index.android.bundle $1/resources/assets/index.android.bundle.decompiled.js
        hbc-file-parser $1/resources/assets/index.android.bundle > /tmp/index.android.bundle.header.txt
        while read -r line; do
            # each case to handle:
            # => 1: '(keep what's inside here)'
            # => 0: '(keep what's inside here)'
            # => [Function #12891 (keep what's here if there's something) of 
            case $line in
                "=> 0:"*)
                    string=${line#"=> 0: "}
                    echo ${string:1:-1} >> $1/resources/assets/index.android.bundle.0.txt
                    ;;
                "=> 1:"*)
                    string=${line#"=> 1: "}
                    echo ${string:1:-1} >> $1/resources/assets/index.android.bundle.1.txt
                    ;;
                "=> [Function"*)
                    echo ${line% of *} | sed -r 's/^=> \[Function #[0-9]+ ?//' >> $1/resources/assets/index.android.bundle.function.txt
                    ;;
            esac
        done < /tmp/index.android.bundle.header.txt
        sort -uo $1/resources/assets/index.android.bundle.0.txt $1/resources/assets/index.android.bundle.0.txt
        sort -uo $1/resources/assets/index.android.bundle.1.txt $1/resources/assets/index.android.bundle.1.txt
        sort -uo $1/resources/assets/index.android.bundle.function.txt $1/resources/assets/index.android.bundle.function.txt
    fi
    MergeDPIPNG $1

    cur_version_code=''
    cur_version_name=''
    if [[ -f $1/resources/AndroidManifest.xml ]]; then
        cur_version_code=$(xpath -q -e "string(/manifest/@android:versionCode)" $1/resources/AndroidManifest.xml)
        cur_version_name=$(xpath -q -e "string(/manifest/@android:versionName)" $1/resources/AndroidManifest.xml)
    fi
    echo "Current version: $cur_version_name - $cur_version_code"
    if [ -n "$APP_TO_PROCESS" ] || [[ -z "$prev_version_code" ]] || [ "$cur_version_code" -gt "$prev_version_code" ] || [ -n "$FORCE" -a "$FORCE" = "true" ]; then
        git add $1

        if [[ -n "$prev_version_code" ]]; then
            Commit_message="$1 Version $cur_version_name - $prev_version_code -> $cur_version_code"
        else
            Commit_message="$1 Initial Version $cur_version_name - $cur_version_code"
        fi

        if ! git diff-index --cached --quiet HEAD; then
            git commit -m "$Commit_message | $(git status --porcelain | wc -l) files | $(git status --porcelain|awk '{print "basename " $2}'| sh | sed '{:q;N;s/\n/, /g;t q}')"
        fi
    else
        echo "Skipping staging changes: apk version didn't change"
    fi
}

VerifyRequirements

if [[ "$SOURCE" == "manual" ]]; then
    for APP in "./.storage/*";
    do
        # untested, future-proofing
        if [[ -n "$APP_TO_PROCESS" && "$APP" != *"$APP_TO_PROCESS"* ]]; then
            echo "Skipping $APP"
            continue
        fi

        declare -a APKS=($(find $APP -name "*.apk" | sort -V))

        for APK in ${APKS[@]};
        do
            if [[ -n "$APK_VERSION_TO_PROCESS" && "$APK" != *"$APK_VERSION_TO_PROCESS"* ]]; then
                echo "Skipping $APK"
                continue
            fi
            echo "Processing $APK"
            ProcessApp $(basename $APP) $APK
        done
    done
elif [[ -n "$APP_TO_PROCESS" ]]; then
    ProcessApp $APP_TO_PROCESS $APP_VERSION
else
    for APP in $APPS;
    do
        ProcessApp $APP
    done
fi

git push
