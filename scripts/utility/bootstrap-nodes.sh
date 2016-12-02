#!/bin/bash
set -e # use -x option for debugging

echo 'This script will start sos-nodes with the specified configurations'
echo 'Run this script as """sh experiment.sh [CONFIG_PATHS]""".'

if [ ! -f sos-app.jar ]; then
    echo "sos-app.jar not found!"
    echo "Aborting"
    exit 1
fi

# Solution from SO:
# http://unix.stackexchange.com/questions/55558/how-can-i-kill-and-wait-for-background-processes-to-finish-in-a-shell-script-whe
trap 'killall' INT

killall() {
    trap '' INT TERM     # ignore INT and TERM while shutting down
    echo "**** Shutting down... ****"
    kill -TERM 0         # fixed order, send TERM not INT
    wait
    echo "All processes are killed"

    echo "Archiving logs"
    time=$(date)
    mkdir -p logs-archive/"$time"
    cp logs/*.log logs-archive/"$time"

    echo "Deleting temp logs"
    rm -rf logs/*.log
    rm -rf logs
    echo "Archiving logs finished"

    echo DONE
}


mkdir -p logs

for (( i=1; i<=$#; i++ )); do
    config="${!i}"

    echo "Running SOS instance with config: $config"
    configname=${config%.*}

    output=logs/${configname}-output-${i}.log
    error=logs/${configname}-error-${i}.log

    echo "------------------------------------------------------" >> $output
    echo "OUTPUT LOG -- Time: $(date)" >> $output
    echo "------------------------------------------------------" >> $output

    echo "------------------------------------------------------" >> $error
    echo "ERROR LOG -- Time: $(date)" >> $error
	echo "------------------------------------------------------" >> $error

	java -jar sos-app.jar -c $config -fs -j >> $output 2>> $error &
	echo "Java process run at id: $!"
done

echo 'All instances are running. Check logs for more info.'
echo 'CTRL-C to stop all nodes.'
cat # wait forever
