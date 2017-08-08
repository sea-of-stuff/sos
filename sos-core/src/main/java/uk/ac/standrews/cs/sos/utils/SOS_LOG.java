package uk.ac.standrews.cs.sos.utils;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.logger.Logger;

/**
 * This is a SOS LOG wrapper.
 * This wrapper makes sure that the nodeGUID is included in all log messages.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOS_LOG {

    private static uk.ac.standrews.cs.logger.LOG log;
    private static IGUID nodeGUID;

    public SOS_LOG(IGUID guid) {
        nodeGUID = guid;

        log = Logger.LOG(Logger.LOG4J_LOGGER, "logs/sos.log");
        log.disable("org.apache.http.wire");
        log.disable("o.a.h");
        log.disable("org.apache.http");
    }

    public static void log(LEVEL level, String message) {
        if (log != null) {
            log.log("sos", level, nodeGUID.toShortString(), message);
        } else {
            System.err.println("LOG has not been initialised");
        }
    }
}
