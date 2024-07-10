#!/bin/bash
git config --global --add safe.directory $GITHUB_WORKSPACE
cd $GITHUB_WORKSPACE && ./script.sh