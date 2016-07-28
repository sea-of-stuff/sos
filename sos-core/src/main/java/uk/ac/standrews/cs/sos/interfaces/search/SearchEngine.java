package uk.ac.standrews.cs.sos.interfaces.search;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.Context;
import uk.ac.standrews.cs.sos.interfaces.metadata.Metadata;

import java.util.Collection;

/**
 * This interface allows us to expose the same search capabilities across
 * different node roles.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SearchEngine {

    // TODO - manifest stream?
    Collection<Manifest> searchManifest(Metadata metadata);

    // NOTE: context and metadata might be the same?
    // how about concatenation of contexts/metadata?
    Collection<Manifest> searchManifest(Context context);

    Manifest searchManifest(IGUID guid);
}
