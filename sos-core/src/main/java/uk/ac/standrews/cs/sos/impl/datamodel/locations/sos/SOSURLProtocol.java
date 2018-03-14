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
package uk.ac.standrews.cs.sos.impl.datamodel.locations.sos;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.services.NodeDiscoveryService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.net.URL;

/**
 * Singleton class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSURLProtocol {

    private static SOSURLProtocol instance;
    private SOSURLStreamHandlerFactory urlStreamHandlerFactory;

    private SOSURLProtocol() {}

    public static SOSURLProtocol getInstance() {
        if (instance == null) {
            instance = new SOSURLProtocol();
        }
        return instance;
    }

    public void register(LocalStorage localStorage, NodeDiscoveryService nodeDiscoveryService) throws SOSProtocolException {
        SOS_LOG.log(LEVEL.INFO, "Registering the SOS Protocol");
        try {

            // Solution suggested here: https://stackoverflow.com/a/30524545/2467938
            if (System.getProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled") == null ||
                    System.getProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled").equalsIgnoreCase("false")) {

                urlStreamHandlerFactory = new SOSURLStreamHandlerFactory(localStorage);

                URL.setURLStreamHandlerFactory(urlStreamHandlerFactory);
                System.setProperty("uk.ac.standrews.cs.sos.streamHandlerFactoryInstalled", "true");
            }

        } catch (final Error e) {
            SOS_LOG.log(LEVEL.WARN, "SOS Protocol registration failed: " + e.getMessage());
            throw new SOSProtocolException(e);
        }

        if (urlStreamHandlerFactory != null) {
            SOSURLStreamHandler handler = urlStreamHandlerFactory.getSOSURLStreamHandler();
            handler.setNodeDiscoveryService(nodeDiscoveryService);
        }
    }

}
