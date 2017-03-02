package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.model.manifests.directory.ManifestsDirectoryImpl;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    private ManifestsDirectory manifestsDirectory;

    public SOSDDS(LocalStorage localStorage, ManifestPolicy manifestPolicy, NDS nds) {
        manifestsDirectory = new ManifestsDirectoryImpl(manifestPolicy, localStorage, nds, this);
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsDirectory.addManifest(manifest);
    }

    @Override
    public void addManifestDDSMapping(IGUID manifest, IGUID ddsNode) {
        manifestsDirectory.addManifestDDSMapping(manifest, ddsNode);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return manifestsDirectory.findManifest(guid);
    }

    @Override
    public void flush() {
        manifestsDirectory.flush();
    }

}
