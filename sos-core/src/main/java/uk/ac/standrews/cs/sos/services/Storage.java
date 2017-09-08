package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.DataNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.SecureAtom;

import java.util.Iterator;

/**
 * The Storage roles defines an entry point in the SOS to store data.
 * Atom manifests are stored via the DDS (@see DataDiscoveryService)
 *
 * Data stored via a storage node is available to other nodes in the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Storage {

    /**
     * Adds data to the Sea of Stuff as an atom.
     * The atom manifest is added to the DDS.
     *
     * @param atomBuilder defines the sources for the atom to be added
     * @return The generated atom manifest. This will contain the locations known to this node prior to any replication.
     *
     * @throws DataStorageException if the data could not be added
     * @throws ManifestPersistException if the atom manifest could not be created
     */
    Atom addAtom(AtomBuilder atomBuilder) throws DataStorageException, ManifestPersistException;

    /**
     * Adds an atom to the SOS as a secure encrypted entity.
     * The Role used for encryption will be the one specified within the builder OR the current active role.
     *
     * @param atomBuilder used to build the secure atom
     * @return the secure atom manifest
     * @throws ManifestPersistException if the manifest could not be stored
     * @throws ManifestNotMadeException if the manifest could not be made
     * @throws DataStorageException if the data could not be stored
     */
    SecureAtom addSecureAtom(AtomBuilder atomBuilder) throws ManifestPersistException, ManifestNotMadeException, DataStorageException;

    /**
     * Let granterRole grant access to granteeRole to the secure atom
     *
     * @param secureAtom in question
     * @param granterRole the role granting access
     * @param granteeRole the role receiving access
     * @return the new update secure atom manifest
     * @throws ProtectionException if access could not be granted
     */
    SecureAtom grantAccess(SecureAtom secureAtom, Role granterRole, Role granteeRole) throws ProtectionException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return Data
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
     * Add a new location for the atom matching that guid
     *
     * @param guid the guid of the atom
     * @param locationBundle the new location
     */
    void addLocation(IGUID guid, LocationBundle locationBundle);

    // TODO - have a deprecate location? could downgrade to cache location, which can be removed...

    /**
     * Find all locations for a given atom
     *
     * @param guid of the atom
     * @return an iterator of locations
     */
    Iterator<LocationBundle> findLocations(IGUID guid);

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
     * Flush all indexes and caches managed by the storage actor
     */
    void shutdown();

    /**
     * The settings for the storage service
     *
     * @return the settings
     */
    SettingsConfiguration.Settings.AdvanceServicesSettings.StorageSettings getStorageSettings();

}
