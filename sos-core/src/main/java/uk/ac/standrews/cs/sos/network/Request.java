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


import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class Request {

    public static final String SOS_NODE_CHALLENGE_HEADER = "sos-node-challenge";
    private static final int NODE_CHALLENGE_LENGTH_BITS = 1024;
    private static final int NODE_CHALLENGE_BASE = 32;

    protected HTTPMethod method;
    protected URL url;
    protected InputStream inputStream;

    String json_body;
    PublicKey d_publicKey;
    PrivateKey d_privateKey; // TODO - initialise it!
    String nodeChallenge;

    /**
     * Request constructor to the given end-point.
     *
     * This request is not signed.
     * @param method of the request
     * @param url for the request
     */
    public Request(HTTPMethod method, URL url) {
        this.method = method;
        this.url = url;
    }

    /**
     * Request constructor to the given end-point.
     *
     * This request is signed and so will be its response.
     * @param method of the request
     * @param url for the request
     */
    public Request(PublicKey d_publicKey, HTTPMethod method, URL url) {
        this.method = method;
        this.url = url;

        this.d_publicKey = d_publicKey;
        SecureRandom random = new SecureRandom();
        this.nodeChallenge = new BigInteger(NODE_CHALLENGE_LENGTH_BITS, random).toString(NODE_CHALLENGE_BASE);
    }

    public Request setJSONBody(String json_body) {
        this.json_body = json_body;
        this.inputStream = null;
        return this;
    }

    public Request setBody(InputStream inputStream) {
        this.inputStream = inputStream;
        this.json_body = null;
        return this;
    }

    @Override
    public String toString() {
        return method.toString() + " " + url.toString();
    }

}
