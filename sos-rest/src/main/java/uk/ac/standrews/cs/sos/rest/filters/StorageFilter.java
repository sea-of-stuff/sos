package uk.ac.standrews.cs.sos.rest.filters;

import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.StorageNode;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

@Provider
@StorageNode
public class StorageFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (RESTConfig.sos.isRestEnabled() && !RESTConfig.sos.isStorage()) {
            Response response = HTTPResponses.BAD_REQUEST(RESTConfig.sos, null, "I am not a storage node");
            throw new WebApplicationException(response);
        }

    }
}
