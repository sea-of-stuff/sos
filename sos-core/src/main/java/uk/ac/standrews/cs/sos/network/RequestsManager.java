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
package uk.ac.standrews.cs.sos.network;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Options;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.security.PrivateKey;

/**
 * Singleton Class
 *
 * If HTTPS requests do not work:
 * https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6g1/index.html (THIS DID NOT WORK)
 * https://stackoverflow.com/questions/25084104/https-certificate-validation-fails-when-using-a-truststore (Should use already existing cacerts)
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RequestsManager {

    private PrivateKey d_privateKey;
    private static RequestsManager lazyInstance;

    private RequestsManager() {}

    // Ensure that this class cannot be instantiated by other classes by making the constructor private
    private RequestsManager(PrivateKey d_privateKey) {
        this.d_privateKey = d_privateKey;
    }

    public static RequestsManager init(PrivateKey d_privateKey) {
        if(lazyInstance == null){
            Options.refresh(); // Make sure that Unirest instance is restarted
            // Unirest.setTimeouts(10000, 120000); // NOTE - Make these params configurable
            lazyInstance = new RequestsManager(d_privateKey);
        }
        return lazyInstance;
    }

    public static RequestsManager getInstance() {
        if(lazyInstance == null){
            Options.refresh(); // Make sure that Unirest instance is restarted
            lazyInstance = new RequestsManager();
        }
        return lazyInstance;
    }

    /**
     * Synchronous request, which is signed by default.
     *
     * @param request to make
     * @return response to the request
     * @throws IOException if request failed to be executed
     */
    public Response playSyncRequest(Request request) throws IOException {
        return playSyncRequest(request, false);
    }

    /**
     * Synchronous request
     *
     * @param request to make
     * @param sign set to true to sign the outgoing request, false otherwise
     * @return response to the request
     * @throws IOException if request failed to be executed
     */
    public Response playSyncRequest(Request request, boolean sign) throws IOException {

        if (sign) {
            request = request.setSigningPrivateKey(d_privateKey);
        }
        return request.play();
    }

    public void shutdown() {

        try {
            lazyInstance = null;
            Unirest.shutdown();
        } catch (IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to shutdown Requests Manager");
        }
    }

}
