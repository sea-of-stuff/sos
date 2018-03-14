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

import com.mashape.unirest.http.HttpResponse;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper around the okhttp3 response class
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ResponseImpl implements Response {

    private HttpResponse<?> response;

    public ResponseImpl(HttpResponse<?> response) {
        this.response = response;
    }

    @Override
    public int getCode() {
        return response.getStatus();
    }

    @Override
    public InputStream getBody() {
        return response.getRawBody();
    }

    @Override
    public com.fasterxml.jackson.databind.JsonNode getJSON() {

        try {
            return JSONHelper.jsonObjMapper().readTree(response.getBody().toString());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getStringBody() {

        try {
            return IO.InputStreamToString(getBody());
        } catch (IOException e) {
            return "";
        }
    }

    public int getContentLength() {

        return Integer.parseInt(response.getHeaders().getFirst("Content-Length"));
    }

    @Override
    public void consumeResponse() throws IOException {

        try(InputStream ignored = getBody()) {} // Ensure that connection is closed properly.
    }
}
