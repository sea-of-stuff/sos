package uk.ac.standrews.cs.sos.rest.HTTP;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HTTPResponses {

    public static Response INTERNAL_SERVER() {
        return Response.status(HTTPStatus.INTERNAL_SERVER)
                .type(MediaType.TEXT_PLAIN)
                .entity("Something went wrong on our side. Sorry")
                .build();
    }

    public static Response BAD_REQUEST(String message) {
        return Response.status(HTTPStatus.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    public static Response NOT_FOUND(String message) {
        return Response.status(HTTPStatus.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    public static Response CREATED(String message) {
        return Response.status(HTTPStatus.CREATED)
                .entity(message)
                .build();
    }

    public static Response OK(InputStream inputStream) {
        return Response.status(HTTPStatus.OK)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA) // Note - this is a general media-type. will not render on browser.
                .build();
    }

    public static Response OK(String message) {
        return Response.status(HTTPStatus.OK)
                .entity(message)
                .build();
    }

    public static Response OK() {
        return Response.status(HTTPStatus.OK)
                .build();
    }

}
