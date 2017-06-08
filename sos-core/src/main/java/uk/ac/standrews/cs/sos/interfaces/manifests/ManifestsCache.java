package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsCache extends ManifestsDirectory {

    ConcurrentLinkedQueue<IGUID> getLRU();

    Set<IGUID> getAllAssets();
}
