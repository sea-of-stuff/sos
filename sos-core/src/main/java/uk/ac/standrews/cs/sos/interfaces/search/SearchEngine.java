package uk.ac.standrews.cs.sos.interfaces.search;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;

import java.util.Collection;

/**
 * This interface allows us to expose the same search capabilities across
 * different node roles.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SearchEngine {

    // TODO - manifest stream?
    Collection<Manifest> searchManifest(SOSMetadata metadata);

    Manifest searchManifest(IGUID guid);
}
