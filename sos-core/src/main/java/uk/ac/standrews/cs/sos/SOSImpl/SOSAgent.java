package uk.ac.standrews.cs.sos.SOSImpl;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.metadata.SOSMetadataException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.*;
import uk.ac.standrews.cs.sos.interfaces.metadata.MetadataDirectory;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.interfaces.policy.ReplicationPolicy;
import uk.ac.standrews.cs.sos.interfaces.sos.*;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.manifests.atom.AtomStorage;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.node.NodesDirectory;
import uk.ac.standrews.cs.sos.storage.LocalStorage;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAgent implements Agent {

    private Identity identity;
    private ManifestsDirectory manifestsDirectory;
    private MetadataDirectory metadataDirectory;

    private AtomStorage atomStorage;

    private Storage storage;
    private NDS nds;
    private DDS dds;
    private MCS mcs;

    public SOSAgent(Storage storage, NDS nds, DDS dds, MCS mcs) {
        this.storage = storage;
        this.nds = nds;
        this.dds = dds;
        this.mcs = mcs;
    }

    public SOSAgent(Node node, NodesDirectory nodesDirectory, LocalStorage storage, ManifestsDirectory manifestsDirectory,
                    Identity identity, ReplicationPolicy replicationPolicy, MetadataDirectory metadataDirectory) {

        this.manifestsDirectory = manifestsDirectory;
        this.identity = identity;
        this.metadataDirectory = metadataDirectory;

        atomStorage = new AtomStorage(node.getNodeGUID(), storage);
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws StorageException, ManifestPersistException {
        Atom manifest = storage.addAtom(atomBuilder, false);
        return manifest;
    }

    @Override
    public Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(type, contents, identity);
        dds.addManifest(manifest, false);

        return manifest;
    }

    @Override
    public Version addVersion(VersionBuilder versionBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Collection<IGUID> prevs = versionBuilder.getPreviousCollection();
        Collection<IGUID> metadata = versionBuilder.getMetadataCollection();

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, identity);
        dds.addManifest(manifest, false);

        return manifest;
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return
     */
    @Override
    public InputStream getAtomContent(Atom atom) {
        InputStream dataStream = storage.getAtomContent(atom);
        return dataStream;
    }

    @Override
    public void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException {
        dds.addManifest(manifest, recursive);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return dds.getManifest(guid);
    }

    @Override
    public Version getHEAD(IGUID invariant) throws HEADNotFoundException {
        return manifestsDirectory.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        manifestsDirectory.setHEAD(version);
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationException {
        boolean success = manifest.verify(identity);
        return success;
    }

    @Override
    public SOSMetadata addMetadata(Atom atom) throws SOSMetadataException {
        SOS_LOG.log(LEVEL.INFO, "Processing and Adding metadata");

        InputStream data = atom.getData();
        SOSMetadata metadata = metadataDirectory.processMetadata(data);
        metadataDirectory.addMetadata(metadata);

        return metadata;
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) {
        SOS_LOG.log(LEVEL.INFO, "Getting metadata for guid: " + guid.toString());

        return metadataDirectory.getMetadata(guid);
    }

    // FIXME - do not use this weird pattern anymore
    protected IGUID store(Location location, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.cacheAtomAndUpdateLocationBundles(location, bundles);
    }

    protected IGUID store(InputStream inputStream, Collection<LocationBundle> bundles) throws StorageException {
        return atomStorage.cacheAtomAndUpdateLocationBundles(inputStream, bundles);
    }


}
