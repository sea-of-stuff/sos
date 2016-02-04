package uk.ac.standrews.cs.sos.model.interfaces;

import uk.ac.standrews.cs.sos.exceptions.UnknownGUIDException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestSaveException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.locations.OldLocation;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;
import uk.ac.standrews.cs.sos.model.interfaces.components.Metadata;
import uk.ac.standrews.cs.sos.model.interfaces.identity.Identity;

import java.io.InputStream;
import java.util.Collection;

/**
 * This interface describes the set of allowed operations in the Sea of Stuff.
 * <br>
 * Client applications interact with the Sea of Stuff through this interface.
 * This interface abstracts the complexity of the data management in the Sea of Stuff,
 * such as data syncing, data retrieval from remote, de-duplication or data
 * redundancy.
 * <br>
 * The sea of stuff is a large collection of assets, compounds and atoms stored
 * across a collection of storage repositories.
 * The sea of stuff supports locatable persistent data via asset, compound and
 * atom GUIDs.
 *
 * <p>
 * The Sea of Stuff is divided into two logical sub-spaces: data and manifest space.
 * All manifests reside in the manifest space, while all other data is stored in the data space.
 * In reality, however, all data is stored in the data space.
 * </p>
 *
 * <p>
 * The sea of stuff is made of few entities that represent data (atoms), aggregations
 * of data (compounds), and allows these to be versioned and linked to other
 * entities - possibly metadata (assets). Atoms do exist in the sea of stuff
 * as sequence of bytes and are represented by an AtomManifest. Compounds and
 * assets, instead, are metadata information about aggregations, versions and
 * links. Therefore, compounds and assets exist only in the form of manifests -
 * CompoundManifest and AssetManifest.
 * </p>
 *
 * <p>
 * The operations defines in this interface are of 3 categories: <br>
 * 1. Manipulation of the sea of stuff - the operations change the state of the sea of stuff <br>
 * 2. Behaviour - describe how the operations in (1) behave
 * 3. XXX Local behaviours? session???
 * </p>
 *
 * <p>
 * Policies:
 * </p>
 *
 * <p>
 * Metadata:
 * </p>
 *
 * <p>
 *     Assumptions:
 *     - manifests can be inspected easily and quickly
 *     - manifests can be stored efficiently
 *     - manifests can be represented in JSON.
 *     We use JSON for simplicity, but it could be any other format in theory.
 *
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaOfStuff {

    // NOTE on identities and implicit contexts
    // identity such as simone's work, simone's home, etc
    // should be able to set multiple identities active (say I am at home and working)

    /**
     * Adds an atom to the Sea of Stuff.
     * The locations of the atom are used to generate a manifest.
     *
     * @param locations of the atom.
     * @return AtomManifest for the added atom.
     * @throws ManifestNotMadeException
     *
     * @see Manifest
     */
    AtomManifest addAtom(Collection<OldLocation> locations)
            throws ManifestNotMadeException, ManifestSaveException, DataStorageException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atomManifest describing the atom to retrieve.
     * @return atom to retrieve in bytes.
     */
    InputStream getAtomContent(AtomManifest atomManifest);

    /**
     * Adds a CompoundManifest to the Sea of Stuff.
     *
     * @param contents of this compound.
     * @return CompoundManifest for the added compound.
     * @throws ManifestNotMadeException
     *
     * @see Manifest
     */
    CompoundManifest addCompound(Collection<Content> contents)
            throws ManifestNotMadeException, ManifestSaveException;

    /**
     * Adds an asset to the Sea of Stuff.
     *
     * @param content of this asset.
     * @param invariant guid of this asset
     * @param prevs version of this asset.
     * @param metadata of this asset.
     * @return AssetManifest for the added asset.
     * @throws ManifestNotMadeException
     *
     */
    AssetManifest addAsset(Content content, GUID invariant,
                           Collection<GUID> prevs, Collection<GUID> metadata)
            throws ManifestNotMadeException, ManifestSaveException;

    /**
     * Get the manifest that matches a given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws UnknownGUIDException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     *
     * @see Manifest
     * @see GUID
     */
    Manifest getManifest(GUID guid) throws UnknownGUIDException;

    Identity getIdentity();

    /**
     * Hash-based verification ensures that a file has not been corrupted by
     * comparing the data's hash value to a previously calculated value.
     * If these values match, the data is presumed to be unmodified.
     * Due to the nature of hash functions, hash collisions may result
     * in false positives, but the likelihood of collisions is
     * often negligible with random corruption. (https://en.wikipedia.org/wiki/File_verification)
     *
     * <p>
     * verifyManifest checks the integrity of the manifest's GUID against the
     * content of the manifest.
     * </p>
     *
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     * @throws ManifestVerificationFailedException
     *
     * @see Manifest
     * @see GUID
     */
    boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException;


    Collection<GUID> findManifestByType(String type);

    Collection<GUID> findManifestByLabel(String label);

    Collection<GUID> findVersions(GUID invariant);

    /**
     * Search the sea of stuff for manifests that match the specified metadata.
     *
     * @param metadata used for querying the sea of stuff.
     * @return a stream of manifests that match the query.
     */
    void findManifests(Metadata metadata);

}
