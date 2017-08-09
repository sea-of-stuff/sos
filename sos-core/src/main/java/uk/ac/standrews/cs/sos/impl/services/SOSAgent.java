package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.*;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.*;

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

    public static void destroy() {
        instance = null;
    }

    @Override
    public Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException {

        return storage.addAtom(atomBuilder);
    }

    @Override
    public SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException {

        return storage.addSecureAtom(atomBuilder);
    }

    @Override
    public Compound addCompound(CompoundBuilder compoundBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException {

        CompoundType type = compoundBuilder.getType();
        Set<Content> contents = compoundBuilder.getContents();

        Role role = usersRolesService.getRole(compoundBuilder);

        Compound compound = ManifestFactory.createCompoundManifest(type, contents, role);
        addManifest(compound);

        return compound;
    }

    @Override
    public SecureCompound addSecureCompound(CompoundBuilder compoundBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException {

        CompoundType type = compoundBuilder.getType();
        Set<Content> contents = compoundBuilder.getContents();

        Role role = usersRolesService.getRole(compoundBuilder);

        SecureCompound compound = ManifestFactory.createSecureCompoundManifest(type, contents, role);
        addManifest(compound);

        return compound;
    }

    @Override
    public Version addVersion(VersionBuilder versionBuilder) throws ManifestNotMadeException, ManifestPersistException, RoleNotFoundException {

        IGUID content = versionBuilder.getContent();
        IGUID invariant = versionBuilder.getInvariant();
        Set<IGUID> prevs = versionBuilder.getPreviousCollection();
        IGUID metadata = versionBuilder.getMetadataCollection();

        Role role = usersRolesService.getRole(versionBuilder);

        Version manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, role);
        addManifest(manifest);

        // NOTE:
        // Make the added manifest the HEAD by default
        dataDiscoveryService.setHead(manifest);

        return manifest;
    }

    @Override
    public Version addData(VersionBuilder versionBuilder) {

        try {

            Atom atom = addAtom(versionBuilder.getAtomBuilder());
            versionBuilder.setContent(atom.guid());

            return addVersion(versionBuilder);
        } catch (DataStorageException | ManifestPersistException | ManifestNotMadeException | RoleNotFoundException e) {
            e.printStackTrace();
            // TODO - throw proper exception
        }

        return null;
    }

    @Override
    public Version addCollection(VersionBuilder versionBuilder) {

        try {
            Compound compound = addCompound(versionBuilder.getCompoundBuilder());
            versionBuilder.setContent(compound.guid());

            return addVersion(versionBuilder);
        } catch (ManifestNotMadeException | ManifestPersistException | RoleNotFoundException e) {
            e.printStackTrace();
            // TODO - throw proper exception
        }

        return null;
    }

    @Override
    public Data getData(Version version) throws AtomNotFoundException {

        IGUID content = version.getContentGUID();
        return storage.getAtomContent(content);
    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return Input Stream of the data for the given atom
     */
    @Override
    public Data getAtomContent(Atom atom) throws AtomNotFoundException {
        return storage.getAtomContent(atom);
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ManifestNotFoundException {
        return dataDiscoveryService.getManifest(guid);
    }

    @Override
    public boolean verifyManifestSignature(Role role, Manifest manifest) throws SignatureException {

        return manifest.verifySignature(role);
    }

    @Override
    public boolean verifyManifestIntegrity(Manifest manifest) throws ManifestVerificationException {

        return manifest.verifyIntegrity();
    }

    @Override
    public Object getMetaProperty(IGUID guid, String property) throws ManifestNotFoundException, MetadataNotFoundException {
        Version version = (Version) getManifest(guid);

        Metadata metadata = getMetadata(version.getMetadata());
        return metadata.getProperty(property);
    }

    @Override
    public Metadata addMetadata(Data data) throws MetadataException {

        Metadata metadata = metadataService.processMetadata(data);
        metadataService.addMetadata(metadata);

        return metadata;
    }

    // TODO - add secure metadata method

    @Override
    public Metadata getMetadata(IGUID guid) throws MetadataNotFoundException {

        return metadataService.getMetadata(guid);
    }

    private void addManifest(Manifest manifest) throws ManifestPersistException {
        dataDiscoveryService.addManifest(manifest);
    }

}
