package uk.ac.standrews.cs.sos.rest.filters;

import uk.ac.standrews.cs.sos.rest.bindings.GeneralAPI;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */

@Provider
@GeneralAPI
public class GeneralFilter extends CommonFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        super.filter(requestContext);
    }
}
