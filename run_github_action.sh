#!/bin/bash
while IFS=, read -r col1 col2
do
    gh workflow run run.yml --ref main -f app_id=$col1 -f version=$col2 &
    sleep 300
done < app_versions.csv

