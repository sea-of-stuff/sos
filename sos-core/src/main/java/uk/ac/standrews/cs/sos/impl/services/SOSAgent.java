package uk.ac.standrews.cs.sos.impl.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
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

    private StorageService storageService;
    private ManifestsDataService manifestsDataService;
    private MetadataService metadataService;
    private UsersRolesService usersRolesService;

    private SOSAgent(StorageService storageService, ManifestsDataService manifestsDataService, MetadataService metadataService, UsersRolesService usersRolesService) {

        this.storageService = storageService;
        this.manifestsDataService = manifestsDataService;
        this.metadataService = metadataService;
        this.usersRolesService = usersRolesService;
    }

    private static SOSAgent instance;
    public static SOSAgent instance(StorageService storageService, ManifestsDataService manifestsDataService, MetadataService metadataService, UsersRolesService usersRolesService) {
        if (instance == null) {
            instance = new SOSAgent(storageService, manifestsDataService, metadataService, usersRolesService);
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
    public Atom addAtom(AtomBuilder atomBuilder) throws ServiceException {

        try {
            return storageService.addAtom(atomBuilder);
        } catch (DataStorageException | ManifestPersistException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ServiceException {

        try {
            return storageService.addSecureAtom(atomBuilder);
        } catch (ManifestPersistException | ManifestNotMadeException | DataStorageException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Compound addCompound(CompoundBuilder compoundBuilder) throws ServiceException {

        try {
            CompoundType type = compoundBuilder.getType();
            Set<Content> contents = compoundBuilder.getContents();

            Role role = usersRolesService.getRole(compoundBuilder);

            Compound compound = ManifestFactory.createCompoundManifest(type, contents, role);
            addManifest(compound);

            return compound;
        } catch (RoleNotFoundException | ManifestPersistException | ManifestNotMadeException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public SecureCompound addSecureCompound(CompoundBuilder compoundBuilder) throws ServiceException {

        try {
            CompoundType type = compoundBuilder.getType();
            Set<Content> contents = compoundBuilder.getContents();

            Role role = usersRolesService.getRole(compoundBuilder);

            SecureCompound compound = ManifestFactory.createSecureCompoundManifest(type, contents, role);
            addManifest(compound);

            return compound;
        } catch (RoleNotFoundException | ManifestPersistException | ManifestNotMadeException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Version addVersion(VersionBuilder versionBuilder) throws ServiceException {

        try {
            IGUID content = versionBuilder.getContent();
            IGUID invariant = versionBuilder.getInvariant();
            Set<IGUID> prevs = versionBuilder.getPreviousCollection();
            IGUID metadata = versionBuilder.getMetadataCollection();

            Role role = usersRolesService.getRole(versionBuilder);

            Version manifest = ManifestFactory.createVersionManifest(content, invariant, prevs, metadata, role);
            addManifest(manifest);

            // NOTE:
            // Make the added manifest the HEAD by default
            manifestsDataService.setHead(manifest);

            return manifest;
        } catch (RoleNotFoundException | ManifestPersistException | ManifestNotMadeException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Version addData(VersionBuilder versionBuilder) throws ServiceException {

        Metadata metadata = addMetadata(versionBuilder.getAtomBuilder().getData());

        Atom atom;
        if(versionBuilder.getAtomBuilder().getRole() != null) {
            atom = addSecureAtom(versionBuilder.getAtomBuilder());
        } else {
            atom = addAtom(versionBuilder.getAtomBuilder());
        }

        versionBuilder.setContent(atom.guid());
        versionBuilder.setMetadata(metadata);

        return addVersion(versionBuilder);
    }

    @Override
    public Version addCollection(VersionBuilder versionBuilder) throws ServiceException {

        Compound compound;
        if(versionBuilder.getCompoundBuilder().getRole() != null) {
            compound = addSecureCompound(versionBuilder.getCompoundBuilder());
        } else {
            compound = addCompound(versionBuilder.getCompoundBuilder());
        }

        versionBuilder.setContent(compound.guid());

        return addVersion(versionBuilder);
    }

    @Override
    public Data getData(Version version) throws ServiceException {

        try {
            IGUID content = version.content();
            return storageService.getAtomContent(content);
        } catch (AtomNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Data getData(IGUID guid) throws ServiceException {

        try {
            IGUID atomGUID = guid;

            Manifest manifest = manifestsDataService.getManifest(guid);
            if (manifest.getType().equals(ManifestType.VERSION)) {
                atomGUID = ((Version) manifest).content();
                if (!manifestsDataService.getManifest(atomGUID).getType().equals(ManifestType.ATOM)) {
                    throw new ServiceException(ServiceException.SERVICE.AGENT, "Unable to find atom data");
                }
            }

            return storageService.getAtomContent(atomGUID);

        } catch (AtomNotFoundException | ManifestNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, "Unable to find manifest for data", e);
        }

    }

    /**
     * Return an InputStream for the given Atom.
     * The caller should ensure that the stream is closed.
     *
     * @param atom describing the atom to retrieve.
     * @return Input Stream of the data for the given atom
     */
    @Override
    public Data getAtomContent(Atom atom) throws ServiceException {

        try {
            return storageService.getAtomContent(atom);
        } catch (AtomNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Manifest getManifest(IGUID guid) throws ServiceException {
        try {
            return manifestsDataService.getManifest(guid);
        } catch (ManifestNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Manifest getManifest(NodesCollection nodesCollection, IGUID guid) throws ServiceException {
        try {
            return manifestsDataService.getManifest(nodesCollection, guid);
        } catch (ManifestNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public boolean verifyManifestSignature(Role role, Manifest manifest) throws ServiceException {

        try {
            if (manifest instanceof SignedManifest) {
                return ((SignedManifest) manifest).verifySignature(role);
            } else {
                return false;
            }

        } catch (SignatureException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public boolean verifyManifestIntegrity(Manifest manifest) throws ServiceException {

        return manifest.verifyIntegrity();
    }

    @Override
    public Object getMetaProperty(IGUID guid, String property) throws ServiceException {

        Version version = (Version) getManifest(guid);

        Metadata metadata = getMetadata(version.getMetadata());
        return metadata.getProperty(property);
    }

    @Override
    public Metadata addMetadata(Data data) throws ServiceException {

        try {
            Metadata metadata = metadataService.processMetadata(data);
            metadataService.addMetadata(metadata);

            return metadata;
        } catch (MetadataException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Metadata getMetadata(IGUID guid) throws ServiceException {

        try {
            return metadataService.getMetadata(guid);
        } catch (MetadataNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Metadata getMetadata(NodesCollection nodesCollection, IGUID guid) throws ServiceException {

        try {
            return metadataService.getMetadata(nodesCollection, guid);
        } catch (MetadataNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    private void addManifest(Manifest manifest) throws ManifestPersistException {
        manifestsDataService.addManifest(manifest);
    }

}
