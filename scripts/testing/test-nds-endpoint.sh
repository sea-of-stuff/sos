#!/bin/bash
set -e

command -v parallel >/dev/null 2>&1 || { echo >&2 "I require parallel but it's not installed. (Run """brew install parallel""" if you are a mac user) Aborting."; exit 1; }

time=$(date)
mkdir -p logs-test-nds-endpoint/"$time"

seq 10000 | parallel -n0 "curl -X GET "http://cs-wifi-174.cs.st-andrews.ac.uk:8080/info"" > logs-test-nds/"$time"/test-nds-endpoint.log
