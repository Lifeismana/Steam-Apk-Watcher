#!/bin/bash
[ -n "$GIT_NAME" ] && git config --global user.name "$GIT_NAME"
[ -n "$GIT_EMAIL" ] && git config --global user.email "$GIT_EMAIL"
git config --global --add safe.directory $GITHUB_WORKSPACE
cd $GITHUB_WORKSPACE && ./script.sh