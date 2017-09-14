package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsCache extends ManifestsDirectory {

    ConcurrentLinkedQueue<IGUID> getLRU();

    Set<IGUID> getAllAssets();

    Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException ;

    IGUID getHead(IGUID invariant) throws HEADNotFoundException;

    void setHead(Version version);

    void advanceTip(Version version);
}
