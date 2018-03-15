/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
import uk.ac.standrews.cs.sos.impl.datamodel.ContentImpl;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestFactory;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.impl.metadata.Property;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // NOTE: This is WIP and should be tested
    public Compound addChunkedData(CompoundBuilder compoundBuilder) throws ServiceException {

        if (compoundBuilder.getType() != CompoundType.DATA) throw new ServiceException(ServiceException.SERVICE.AGENT, "CompoundBuilder must be set with Type: DATA");

        try {
            List<Atom> atoms = storageService.addAtom(compoundBuilder);

            Set<Content> contents = atoms.stream() // return list of GUIDs for chunks in order
                    .map(a -> new ContentImpl(a.guid()))
                    .collect(Collectors.toSet());
            compoundBuilder.setContents(contents);

            return addCompound(compoundBuilder);
        } catch (DataStorageException | ManifestPersistException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, "Unable to add chunked data");
        }
    }

    @Override
    public Compound addCompound(CompoundBuilder compoundBuilder) throws ServiceException {

        try {
            CompoundType type = compoundBuilder.getType();
            Set<Content> contents = compoundBuilder.getContents();

            Role role = usersRolesService.getRole(compoundBuilder);

            Compound compound;
            if (compoundBuilder.isProtect()) {
                compound = ManifestFactory.createSecureCompoundManifest(type, contents, role);
            } else {
                compound = ManifestFactory.createCompoundManifest(type, contents, role);
            }

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

            // Make the added manifest the HEAD by default
            manifestsDataService.setHead(manifest);

            return manifest;
        } catch (RoleNotFoundException | ManifestPersistException | ManifestNotMadeException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Version addData(VersionBuilder versionBuilder) throws ServiceException {

        AtomBuilder atomBuilder = versionBuilder.getAtomBuilder();
        Atom atom = addAtom(atomBuilder);
        versionBuilder.setContent(atom.guid());

        if (versionBuilder.hasMetadataBuilder()) {
            Metadata metadata = addMetadata(versionBuilder.getMetadataBuilder());
            versionBuilder.setMetadata(metadata);
        }

        return addVersion(versionBuilder);
    }

    @Override
    public Version addCollection(VersionBuilder versionBuilder) throws ServiceException {

        CompoundBuilder compoundBuilder = versionBuilder.getCompoundBuilder();
        Compound compound = addCompound(compoundBuilder);
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
            IGUID contentGUID = guid;

            Manifest manifest = manifestsDataService.getManifest(guid);
            if (manifest.getType().equals(ManifestType.VERSION)) {
                contentGUID = ((Version) manifest).content();

                Manifest contentManifest = manifestsDataService.getManifest(contentGUID);
                if (!contentManifest.getType().equals(ManifestType.ATOM)) {
                    throw new ServiceException(ServiceException.SERVICE.AGENT, "Unable to find atom data");
                }
            }

            return storageService.getAtomContent(contentGUID);

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
            return manifestsDataService.getManifest(nodesCollection, NodeType.MDS, guid);
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
    public boolean verifyManifestIntegrity(Manifest manifest) {

        return manifest.verifyIntegrity();
    }

    @Override
    public Property getMetaProperty(IGUID guid, String property) throws ServiceException {

        Version version = (Version) getManifest(guid);

        Metadata metadata = getMetadata(version.getMetadata());
        return metadata.getProperty(property);
    }

    @Override
    public Metadata addMetadata(MetadataBuilder metadataBuilder) throws ServiceException {

        try {
            Metadata metadata = metadataService.processMetadata(metadataBuilder);
            metadataService.addMetadata(metadata);

            return metadata;
        } catch (MetadataException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public Metadata getMetadata(Version version) throws ServiceException {

        IGUID meta = version.getMetadata();
        if (meta != null && !meta.isInvalid()) {
            return getMetadata(meta);
        } else {
            throw new ServiceException(ServiceException.SERVICE.AGENT, "Unable to find metadata for version");
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

    @Override
    public Role getRole(IGUID guid) throws RoleNotFoundException {

        return usersRolesService.getRole(guid);
    }

    @Override
    public void delete(IGUID guid) throws ServiceException {
        // TODO
    }

    private void addManifest(Manifest manifest) throws ManifestPersistException {
        manifestsDataService.addManifest(manifest);
    }

    private Metadata getMetadata(IGUID guid) throws ServiceException {

        try {
            return metadataService.getMetadata(guid);
        } catch (MetadataNotFoundException e) {
            throw new ServiceException(ServiceException.SERVICE.AGENT, e);
        }
    }

    @Override
    public void flush() {
        // DO NOTHING
    }

    @Override
    public void shutdown() {
        // DO NOTHING
    }
}
