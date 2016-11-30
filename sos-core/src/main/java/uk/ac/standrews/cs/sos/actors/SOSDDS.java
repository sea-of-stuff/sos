package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotSetException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.node.directory.DDSIndex;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    private ManifestsDirectory manifestsDirectory;
    private DDSIndex ddsIndex;

    public SOSDDS(ManifestsDirectory manifestsDirectory) {
        this.manifestsDirectory = manifestsDirectory;
        ddsIndex = new DDSIndex();
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        manifestsDirectory.addManifest(manifest);

        // TODO - replicate if necessary
    }

    @Override
    public void addManifestDDSAssociation(IGUID manifest, IGUID ddsNode) {
        ddsIndex.addEntry(manifest, ddsNode);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {

        Manifest manifest = manifestsDirectory.findManifest(guid);

        if (manifest == null) {
            Set<IGUID> ddsNodes = ddsIndex.getDDSRefs(guid);

            // TODO - contact dds nodes for manifest
        }

        return manifest;
    }

    @Override
    public Asset getHEAD(IGUID invariant) throws HEADNotFoundException {
        return manifestsDirectory.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        manifestsDirectory.setHEAD(version);
    }

}
