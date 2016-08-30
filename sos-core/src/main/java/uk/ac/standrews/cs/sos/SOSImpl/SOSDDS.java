package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsManager;
import uk.ac.standrews.cs.sos.interfaces.policy.PolicyManager;
import uk.ac.standrews.cs.sos.interfaces.sos.DDS;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    protected ManifestsManager manifestsManager;

    public SOSDDS(ManifestsManager manifestsManager) {

        this.manifestsManager = manifestsManager;
    }

    @Override
    public PolicyManager getPolicyManager() {
        return null;
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsManager.addManifest(manifest);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest manifest = manifestsManager.findManifest(guid);
        // TODO - contact other DDS nodes!

        return manifest;
    }

    @Override
    public Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException {
        return manifestsManager.findManifestsByType(type);
    }

    @Override
    public Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException {
        return manifestsManager.findManifestsThatMatchLabel(label);
    }

    @Override
    public Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException {
        return manifestsManager.findVersions(invariant);
    }

}
