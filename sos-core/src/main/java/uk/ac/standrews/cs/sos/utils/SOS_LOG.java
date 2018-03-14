/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
        log.disable("org.apache.http");
        log.disable("org.apache.http.wire");
        log.disable("org.eclipse.jetty");
        log.disable("o.a.h");
        log.disable("spark");
    }

    public static void log(LEVEL level, String message) {
        if (log != null) {
            log.log("sos", level, nodeGUID.toShortString(), message);
        } else {
            System.err.println("LOG has not been initialised");
        }
    }
}
