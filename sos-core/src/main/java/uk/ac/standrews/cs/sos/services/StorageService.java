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
package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.CompoundBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.*;

import java.util.List;
import java.util.Queue;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 * Atom manifests are stored via the MDS (@see DataDiscoveryService)
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
 * TODO - have a deprecate location? could downgrade to cache location, which can be removed...
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface StorageService extends Service {

    /**
     * Adds data to the Sea of Stuff as an atom.
     * The atom manifest is added to the MDS.
     *
     * @param atomBuilder defines the sources for the atom to be added
     * @return The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *
     * @throws DataStorageException if the data could not be added
     * @throws ManifestPersistException if the atom manifest could not be created
     */
    Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException;

    /**
     * Add chunked data
     *
     * @param compoundBuilder
     * @return
     * @throws DataStorageException
     * @throws ManifestPersistException
     */
    List<Atom> addAtom(CompoundBuilder compoundBuilder) throws DataStorageException, ManifestPersistException;

    /**
     * Let granterRole grant access to granteeRole to the secure entity
     *
     * @param secureManifest in question
     * @param granterRole the role granting access
     * @param granteeRole the role receiving access
     * @return the new update secure manifest
     * @throws ProtectionException if access could not be granted
     */
    SecureManifest grantAccess(SecureManifest secureManifest, Role granterRole, Role granteeRole) throws ProtectionException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return Data
     * @throws AtomNotFoundException if the atom could not be found
     */
    Data getAtomContent(Atom atom) throws AtomNotFoundException;

    /**
     * Get the data of a secure atom using the given role
     *
     * @param atom for which we want to get the data
     * @param role used to decrypt the data
     * @return the data
     * @throws DataNotFoundException if data was not found or could not be decrypted
     */
    Data getSecureAtomContent(SecureAtom atom, Role role) throws DataNotFoundException;

    /**
     * Get the data for the atom with the specified GUID
     *
     * @param guid of the atom
     * @return the data of the atom
     * @throws AtomNotFoundException if the atom was not found
     */
    Data getAtomContent(IGUID guid) throws AtomNotFoundException;

    /**
     * Get the data for the specified atom from a set of nodes.
     *
     * @param nodesCollection from which to get the data
     * @param guid of the atom
     * @return the data
     * @throws AtomNotFoundException if the data is not found
     */
    Data getAtomContent(NodesCollection nodesCollection, IGUID guid) throws AtomNotFoundException;

    /**
     * Add a new location for the atom matching that guid
     *
     * @param guid the guid of the atom
     * @param locationBundle the new location
     */
    void addLocation(IGUID guid, LocationBundle locationBundle);

    /**
     * Find all locations for a given atom
     *
     * @param atom for which locations should be found
     * @return locations
     */
    Queue<LocationBundle> findLocations(Atom atom);

    /**
     * Find all locations for a given atom
     *
     * @param guid of the atom
     * @return locations
     */
    Queue<LocationBundle> findLocations(IGUID guid);

    /**
     * Challenge the storage for the atom matching the given guid.
     *
     * The storage MUST return the GUID generated by hashing the content of the atom followed by the challenge string.
     *
     * @param guid of the atom to be challenged
     * @param challenge string used to challenge the atom
     * @return the guid of the atom+challenge
     */
    IGUID challenge(IGUID guid, String challenge);

    /**
     * Delete the data for the atom matching the guid from the local node.
     *
     * @param guid of the atom
     * @throws AtomNotFoundException if no atom data was found
     */
    void deleteAtom(IGUID guid) throws AtomNotFoundException;

    /**
     * Flush all indexes and caches managed by the storage actor
     */
    void flush();

    /**
     * The settings for the storage service
     *
     * @return the settings
     */
    SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings getStorageSettings();

}
