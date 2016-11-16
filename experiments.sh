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
    echo DONE
}

for (( i=1; i<=$#; i++ )); do
    config="${!i}"

    echo "Running SOS instance with config: $config"

    echo "---------------------------" >> node-output-${i}.txt
    echo "OUTPUT LOG -- Time: $(date)" >> node-output-${i}.txt
    echo "---------------------------" >> node-output-${i}.txt

    echo "---------------------------" >> node-error-${i}.txt
    echo "ERROR LOG -- Time: $(date)" >> node-error-${i}.txt
	echo "---------------------------" >> node-error-${i}.txt

	java -jar sos.jar -c $config -fs -j >> node-output-${i}.txt 2>> node-error-${i}.txt &
	echo "Java process run at id: $!"
done

echo 'All instances are running. Check logs for more info.'
echo 'CTRL-C to stop all nodes.'

cat # wait forver
