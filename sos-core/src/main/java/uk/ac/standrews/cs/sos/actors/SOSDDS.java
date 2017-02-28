package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataPersistException;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.NDS;
import uk.ac.standrews.cs.sos.interfaces.context.ContextDirectory;
import uk.ac.standrews.cs.sos.interfaces.manifests.ManifestsDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataEngine;
import uk.ac.standrews.cs.sos.interfaces.model.Context;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.policy.ManifestPolicy;
import uk.ac.standrews.cs.sos.interfaces.policy.MetadataPolicy;
import uk.ac.standrews.cs.sos.model.context.ContextDirectoryImpl;
import uk.ac.standrews.cs.sos.model.manifests.directory.ManifestsDirectoryImpl;
import uk.ac.standrews.cs.sos.model.metadata.MetadataDirectoryImpl;
import uk.ac.standrews.cs.sos.storage.LocalStorage;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDDS implements DDS {

    private ManifestsDirectory manifestsDirectory;
    private MetadataDirectory metadataDirectory;
    private ContextDirectory contextDirectory;

    public SOSDDS(LocalStorage localStorage, MetadataEngine metadataEngine, ManifestPolicy manifestPolicy, MetadataPolicy metadataPolicy, NDS nds) {
        manifestsDirectory = new ManifestsDirectoryImpl(manifestPolicy, localStorage, nds, this);
        metadataDirectory = new MetadataDirectoryImpl(localStorage, metadataEngine, metadataPolicy, nds);
        contextDirectory = new ContextDirectoryImpl();
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
    public void addMetadata(SOSMetadata metadata) throws MetadataPersistException {
        metadataDirectory.addMetadata(metadata);
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) throws MetadataNotFoundException {
        return metadataDirectory.getMetadata(guid);
    }

    @Override
    public void addContext(Context context) {
        contextDirectory.addContext(context);
    }

    @Override
    public void flush() {
        manifestsDirectory.flush();
    }

}
