package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Compound extends Manifest {

    Collection<Content> getContents();

    CompoundType getType();
}
