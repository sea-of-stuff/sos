package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.actors.protocol.DDSNotificationInfo;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.actors.DDS;
import uk.ac.standrews.cs.sos.interfaces.actors.MCS;
import uk.ac.standrews.cs.sos.interfaces.actors.Storage;
import uk.ac.standrews.cs.sos.interfaces.context.Context;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.manifests.*;
import uk.ac.standrews.cs.sos.model.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Set;

/**
 * Implementation class for the SeaOfStuff interface.
 * The purpose of this class is to delegate jobs to the appropriate manifests
 * of the sea of stuff.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSAgent implements Agent {

    private Identity identity;

    private Storage storage;
    private DDS dds;
    private MCS mcs;

    public SOSAgent(Storage storage, DDS dds, MCS mcs, Identity identity) {
        this.storage = storage;
        this.dds = dds;
        this.mcs = mcs;

        this.identity = identity;
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws StorageException, ManifestPersistException {
        Atom manifest = storage.addAtom(atomBuilder, false, new DDSNotificationInfo().setNotifyDDSNodes(true)).x;
        return manifest;
    }

    @Override
    public Compound addCompound(CompoundType type, Set<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundManifest manifest = ManifestFactory.createCompoundManifest(type, contents, identity);
        addManifest(manifest, false);

        return manifest;
    }

    @Override
    public Asset addVersion(VersionBuilder versionBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Set<IGUID> prevs = versionBuilder.getPreviousCollection();
        Set<IGUID> metadata = versionBuilder.getMetadataCollection(); // Metadata was already calculated

        AssetManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, identity);
        addManifest(manifest, false);

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
    public InputStream getAtomContent(Atom atom) throws AtomNotFoundException {
        return storage.getAtomContent(atom);
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
    public Asset getHEAD(IGUID invariant) throws HEADNotFoundException {
        return dds.getHEAD(invariant);
    }

    @Override
    public void setHEAD(IGUID version) throws HEADNotSetException {
        dds.setHEAD(version);
    }

    @Override
    public boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationException {
        boolean success = manifest.verify(identity);
        return success;
    }

    @Override
    public SOSMetadata addMetadata(Atom atom) throws MetadataException {

        InputStream data = atom.getData();
        SOSMetadata metadata = mcs.processMetadata(data);
        dds.addMetadata(metadata);

        return metadata;
    }

    @Override
    public SOSMetadata getMetadata(IGUID guid) throws MetadataNotFoundException {
        SOSMetadata metadata = dds.getMetadata(guid);
        return metadata;
    }

    public void addContext(Context context) {

    }

}
