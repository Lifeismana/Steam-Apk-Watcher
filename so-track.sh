#!/bin/bash

ROOT_DIR="$(dirname "$(realpath -s "${BASH_SOURCE[0]}")")"
DUMP_STRINGS_PATH="$ROOT_DIR/tools/DumpStrings/DumpStrings"

set -e

BuildReqs()
{
    cd tools/DumpStrings
    go build
    cd ../..

    command -v unzip >/dev/null 2>&1 || { echo >&2 "unzip missing.  Aborting."; exit 1; }
}

ProcessApp()
{
    rm -rf $1
    mkdir -p $1

    set +e
    unzip $2 */libmain.so */libvrlink_scene.so -d $1
    set -e

    while IFS= read -r -d '' file
	do
		echo " > $file"

		"$DUMP_STRINGS_PATH" -binary "$file" -target "elf" | sort --unique > "$(echo "$file" | sed -e "s/\.so$/_strings.txt/g")"
	done <   <(find $1 -type f -name "*.so" -print0)

    message="$(git status --porcelain | wc -l) files | $(git status --porcelain | sed '{:q;N;s/\n/, /g;t q}' | sed 's/^ *//g' | cut -c 1-1024)"
    git add $1
	git commit -a -m "$message"
}

BuildReqs

for APP in ./.storage/*;
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

git push