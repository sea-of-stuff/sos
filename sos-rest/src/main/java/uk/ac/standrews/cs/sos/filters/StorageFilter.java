package uk.ac.standrews.cs.sos.filters;

import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.ServerState;
import uk.ac.standrews.cs.sos.bindings.StorageNode;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

@Provider
@StorageNode
public class StorageFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!ServerState.sos.isStorage()) {
            Response response = HTTPResponses.BAD_REQUEST("I am not a storage node");
            throw new WebApplicationException(response);
        }

    }
}