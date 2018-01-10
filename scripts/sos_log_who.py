import logging
import subprocess
import time

logger = logging.getLogger("sos_log_who")
logger.setLevel(logging.INFO)
fh = logging.FileHandler("sos_who.log")
 
formatter = logging.Formatter('%(asctime)s \n%(message)s')
fh.setFormatter(formatter)
logger.addHandler(fh)

while True:
    logger.info(subprocess.check_output("who"))
    time.sleep(30)
