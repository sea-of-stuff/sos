/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module rest.
 *
 * rest is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * rest is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with rest. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.rest.HTTP;

import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.utilities.crypto.CryptoException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static uk.ac.standrews.cs.sos.network.Request.SOS_NODE_CHALLENGE_HEADER;

/**
 * TODO - log responses
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HTTPResponses {

    public static Response INTERNAL_SERVER(SOSLocalNode localNode, String challenge) {
        Response.ResponseBuilder builder = Response.status(HTTPStatus.INTERNAL_SERVER)
                .type(MediaType.TEXT_PLAIN)
                .entity("Something went wrong on our side. Sorry");

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response BAD_REQUEST(SOSLocalNode localNode, String challenge, String message) {
        Response.ResponseBuilder builder = Response.status(HTTPStatus.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message);

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response NOT_FOUND(SOSLocalNode localNode, String challenge, String message) {
        Response.ResponseBuilder builder =  Response.status(HTTPStatus.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity(message);

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response CREATED(SOSLocalNode localNode, String challenge, String message) {
        Response.ResponseBuilder builder =  Response.status(HTTPStatus.CREATED)
                .entity(message);

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response OK(SOSLocalNode localNode, String challenge, InputStream inputStream) {
        Response.ResponseBuilder builder =  Response.status(HTTPStatus.OK)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA); // Note - this is a general media-type. will not render on browser.

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response OK(SOSLocalNode localNode, String challenge, Object message) {
        Response.ResponseBuilder builder =  Response.status(HTTPStatus.OK)
                .entity(message);

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    public static Response OK(SOSLocalNode localNode, String challenge) {
        Response.ResponseBuilder builder =  Response.status(HTTPStatus.OK);

        builder = signChallenge(builder, localNode, challenge);
        return builder.build();
    }

    private static Response.ResponseBuilder signChallenge(Response.ResponseBuilder builder, SOSLocalNode localNode, String challenge) {

        String signedChallenge = null;
        try {
            if (challenge != null && !challenge.isEmpty()) {
                signedChallenge = localNode.sign(challenge);
            }
        } catch (CryptoException e) { }

        if (signedChallenge != null) {
            SOS_LOG.log(LEVEL.DEBUG, "Signing challenge: " + challenge);
            builder = builder.header(SOS_NODE_CHALLENGE_HEADER, signedChallenge);
        }

        return builder;

    }

}
