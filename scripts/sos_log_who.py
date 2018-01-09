# TODO - docs
import subprocess
import time

with open("sos_who.log", "a") as sos_who_log:
    while True:
        # Log time
        sos_who_log.write(subprocess.check_output("who"))
        sos_who_log.write("\n")
        time.sleep(5)
        sos_who_log.flush()
