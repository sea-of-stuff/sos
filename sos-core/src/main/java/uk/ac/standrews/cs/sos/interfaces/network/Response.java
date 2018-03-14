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
package uk.ac.standrews.cs.sos.interfaces.network;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP Response wrapper
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Response {

    /**
     *
     * @return the HTTP response code
     */
    int getCode();

    /**
     *
     * @return the body of the response as an input stream
     */
    InputStream getBody();

    /**
     *
     * @return the body of the response as a JSON object
     */
    JsonNode getJSON();

    /**
     *
     * @return the body of the response as a string
     */
    String getStringBody();

    /**
     * Content Lenght in bytes
     * @return length of the response body
     */
    int getContentLength();

    /**
     * Consume resources used by the response.
     * @throws IOException if unable to consume response.
     */
    void consumeResponse() throws IOException;
}
