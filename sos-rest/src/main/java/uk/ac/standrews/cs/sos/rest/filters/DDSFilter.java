package uk.ac.standrews.cs.sos.rest.filters;

import uk.ac.standrews.cs.sos.rest.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.rest.RESTConfig;
import uk.ac.standrews.cs.sos.rest.bindings.DDSNode;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
@Provider
@DDSNode
public class DDSFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!RESTConfig.sos.isDDS()) {
            Response response = HTTPResponses.BAD_REQUEST(RESTConfig.sos, null, "I am not a DDS node");
            throw new WebApplicationException(response);
        }

    }
}
