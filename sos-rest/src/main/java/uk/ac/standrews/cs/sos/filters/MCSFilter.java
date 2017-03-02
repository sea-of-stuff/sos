package uk.ac.standrews.cs.sos.filters;

import uk.ac.standrews.cs.sos.HTTP.HTTPResponses;
import uk.ac.standrews.cs.sos.RESTConfig;
import uk.ac.standrews.cs.sos.bindings.MCSNode;

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
@MCSNode
public class MCSFilter implements ContainerRequestFilter {

 @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!RESTConfig.sos.isMMS()) {
            Response response = HTTPResponses.BAD_REQUEST("I am not an NDS node");
            throw new WebApplicationException(response);
        }

    }
}
