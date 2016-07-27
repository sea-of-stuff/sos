package uk.ac.standrews.cs.sos.HTTP;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class HTTPResponses {

    public static Response INTERNAL_SERVER() {
        return Response.status(HTTPState.INTERNAL_SERVER)
                .entity("Something went wrong on our side. Sorry")
                .build();
    }

    public static Response BAD_REQUEST(String message) {
        return Response.status(HTTPState.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(message)
                .build();
    }

    public static Response CREATED(String message) {
        return Response.status(HTTPState.CREATED)
                .entity(message)
                .build();
    }

    public static Response OK(InputStream inputStream) {
        return Response.status(HTTPState.OK)
                .entity(inputStream)
                .type(MediaType.MULTIPART_FORM_DATA) // Note - this is a general media-type. will not render on browser.
                .build();
    }
}
