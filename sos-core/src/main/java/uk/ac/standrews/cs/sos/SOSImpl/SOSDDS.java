package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.sos.DDS;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    protected ManifestsDirectory manifestsDirectory;

    public SOSDDS(ManifestsDirectory manifestsDirectory) {
        this.manifestsDirectory = manifestsDirectory;
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsDirectory.addManifest(manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest manifest = manifestsDirectory.findManifest(guid);
        // TODO - contact other DDS nodes!

        return manifest;
    }

}
