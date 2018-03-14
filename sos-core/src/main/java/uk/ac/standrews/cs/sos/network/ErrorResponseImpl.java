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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.input.NullInputStream;
import uk.ac.standrews.cs.sos.interfaces.network.Response;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ErrorResponseImpl implements Response {
    @Override
    public int getCode() {
        return HTTPStatus.INTERNAL_SERVER;
    }

    @Override
    public InputStream getBody() {
        return new NullInputStream(0);
    }

    @Override
    public JsonNode getJSON() {
        return null;
    }

    @Override
    public String getStringBody() {
        return "";
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public void consumeResponse() { }
}
