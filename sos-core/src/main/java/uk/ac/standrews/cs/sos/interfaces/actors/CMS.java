package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Context;

/**
 * Context Management Service
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface CMS extends SeaOfStuff {

    Asset addContext(Context context) throws Exception;
}
