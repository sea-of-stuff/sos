#!/bin/bash
set -e # use -x option for debugging

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
    mkdir logs-archive/
    mkdir logs-archive/"$time"
    cp logs/*.log logs-archive/"$time"

    rm -rf logs/*.log
    rm -rf logs
    echo "Archiving logs finished"
    
    echo DONE
}

mkdir logs

for (( i=1; i<=$#; i++ )); do
    config="${!i}"

    echo "Running SOS instance with config: $config"
    output=logs/node-output-${i}.log
    error=logs/node-error-${i}.log

    echo "------------------------------------------------------" >> $output
    echo "OUTPUT LOG -- Time: $(date)" >> $output
    echo "------------------------------------------------------" >> $output

    echo "------------------------------------------------------" >> $error
    echo "ERROR LOG -- Time: $(date)" >> $error
	echo "------------------------------------------------------" >> $error

	java -jar sos.jar -c $config -fs -j >> $output 2>> $error &
	echo "Java process run at id: $!"
done

echo 'All instances are running. Check logs for more info.'
echo 'CTRL-C to stop all nodes.'

cat # wait forver
