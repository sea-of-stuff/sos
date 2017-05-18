package uk.ac.standrews.cs.sos.impl.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.manifests.VersionManifest;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;

import java.io.IOException;
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

    private Storage storage;
    private DataDiscoveryService dataDiscoveryService;
    private MetadataService metadataService;
    private UsersRolesService usersRolesService;

    private SOSAgent(Storage storage, DataDiscoveryService dataDiscoveryService, MetadataService metadataService, UsersRolesService usersRolesService) {
        this.storage = storage;
        this.dataDiscoveryService = dataDiscoveryService;
        this.metadataService = metadataService;
        this.usersRolesService = usersRolesService;
    }

    private static SOSAgent instance;
    public static SOSAgent instance(Storage storage, DataDiscoveryService dataDiscoveryService, MetadataService metadataService, UsersRolesService usersRolesService) {
        if (instance == null) {
            instance = new SOSAgent(storage, dataDiscoveryService, metadataService, usersRolesService);
        }

        return instance;
    }

    public static SOSAgent instance() {
        return instance;
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws StorageException, ManifestPersistException {
        Atom manifest = storage.addAtom(atomBuilder, false, new DDSNotificationInfo().setNotifyDDSNodes(true));
        return manifest;
    }

    @Override
    public Compound addCompound(CompoundBuilder compoundBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        CompoundType type = compoundBuilder.getType();
        Set<Content> contents = compoundBuilder.getContents();
        CompoundManifest compound = ManifestFactory.createCompoundManifest(type, contents, usersRolesService.active());

        addManifest(compound);

        return compound;
    }

    @Override
    public Version addVersion(VersionBuilder versionBuilder)
            throws ManifestNotMadeException, ManifestPersistException {

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Set<IGUID> prevs = versionBuilder.getPreviousCollection();
        IGUID metadata = versionBuilder.getMetadataCollection();

        VersionManifest manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, usersRolesService.active());
        addManifest(manifest);

        return manifest;
    }

    @Override
    public Version addData(VersionBuilder versionBuilder) {

        // FIXME - we are consuming the inputstream before storing the data. Can we then reuse the stream?
        try (InputStream stream = versionBuilder.getAtomBuilder().getInputStream()) {

            Atom atom = addAtom(versionBuilder.getAtomBuilder());
            versionBuilder.setContent(atom.guid());

            Version manifest = addVersion(versionBuilder);

            return manifest;
        } catch (StorageException | ManifestPersistException | IOException | ManifestNotMadeException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Version addCollection(VersionBuilder versionBuilder) {

        try {
            Compound compound = addCompound(versionBuilder.getCompoundBuilder());
            versionBuilder.setContent(compound.guid());

            Version manifest = addVersion(versionBuilder);

            return manifest;
        } catch (ManifestNotMadeException | ManifestPersistException e) {
            e.printStackTrace();
            // TODO - throw proper exception
        }

        return null;
    }

    @Override
    public InputStream getData(Version version) throws AtomNotFoundException {

        IGUID content = version.getContentGUID();
        return storage.getAtomContent(content);
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
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return dataDiscoveryService.getManifest(guid);
    }

    @Override
    public boolean verifyManifest(Role role, Manifest manifest) throws ManifestVerificationException {
        boolean success = manifest.verifySignature(role);
        return success;
    }

    @Override
    public Metadata addMetadata(InputStream inputStream) throws MetadataException {

        Metadata metadata = metadataService.processMetadata(inputStream);
        metadataService.addMetadata(metadata);

        return metadata;
    }

    @Override
    public Metadata getMetadata(IGUID guid) throws MetadataNotFoundException {
        Metadata metadata = metadataService.getMetadata(guid);
        return metadata;
    }

    private void addManifest(Manifest manifest) throws ManifestPersistException {
        dataDiscoveryService.addManifest(manifest);
    }

}
