package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.Logger;

/**
 * This is a SOS LOG wrapper.
 * This wrapper makes sure that the nodeGUID is included in all log messages.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOS_LOG {

    private static uk.ac.standrews.cs.LOG log;
    private static IGUID nodeGUID;

    public SOS_LOG(IGUID guid) {
        nodeGUID = guid;
        log = Logger.LOG(Logger.LOG4J_LOGGER);
    }

    public static void log(LEVEL level, String message) {
        if (log != null) {
            log.log("sos", level, nodeGUID.toString(), message);
        } else {
            System.err.println("LOG has not been initialised");
        }
    }
}
