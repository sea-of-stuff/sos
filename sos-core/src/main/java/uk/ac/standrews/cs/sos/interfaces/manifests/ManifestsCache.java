package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsCache extends ManifestsDirectory {

    LRU_GUID getLRU();

    void clear();
}
