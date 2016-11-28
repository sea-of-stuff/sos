package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;

import java.util.Set;

/**
 * A compound serves as an aggregator of atoms, compounds and versions.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Compound extends Manifest {

    /**
     * Get the contents of this compound.
     *
     * @return the contents of this compound
     */
    Set<Content> getContents();

    /**
     * Get the type of compound.
     *
     * @return the compound type
     */
    CompoundType getType();
}
