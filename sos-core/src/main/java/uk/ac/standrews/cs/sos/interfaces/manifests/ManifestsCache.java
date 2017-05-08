package uk.ac.standrews.cs.sos.interfaces.manifests;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestsCacheMissException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Version;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsCache {

    void addManifest(Manifest manifest);

    Manifest getManifest(IGUID guid) throws ManifestsCacheMissException;

    void persist(IFile file) throws IOException;

    ConcurrentLinkedQueue<IGUID> getLRU();

    List<Version> getAllAsset();
}
