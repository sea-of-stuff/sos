package uk.ac.standrews.cs.sos.interfaces;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationFailedException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.storage.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;

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
 * links to metadata. Therefore, compounds and assets exist only in the form of manifests:
 * CompoundManifest and AssetManifest.
 * </p>
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaOfStuff {

    /**
     * Adds an atom to the Sea of Stuff.
     *
     * The locations of the atom are used to generate a manifest.
     *
     * TODO - mention caching, fact that atom comes from the location
     *
     * @param location of the data for the atom.
     * @return Atom for the added atom.
     * @throws DataStorageException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Atom addAtom(Location location)
            throws DataStorageException, ManifestPersistException;

    /**
     *
     * @param inputStream for this atom
     * @return Atom for the added atom
     * @throws DataStorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(InputStream inputStream)
            throws DataStorageException, ManifestPersistException;

    /**
     * Adds a CompoundManifest to the Sea of Stuff.
     *
     * @param contents of this compound.
     * @return Compound for the added compound.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Adds an asset to the Sea of Stuff.
     *
     * @param content of this asset.
     * @param invariant guid of this asset
     * @param prevs version of this asset.
     * @param metadata of this asset.
     * @return Asset for the added asset.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     */
    Asset addAsset(IGUID content, IGUID invariant,
                   Collection<IGUID> prevs, Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Add a manifest to the sea of stuff.
     * If {@code recursive} is true, then manifests referenced from the specified one will also be added,
     * assuming that such manifests are available and reachable.
     * This operation will be performed recursively.
     *
     * @param manifest to add to the SOS
     * @param recursive if true adds the references manifests and data recursively.
     * @return Manifest - the returned manifests might differ from the one passed to the sea of stuff {@code manifest}
     * @throws ManifestPersistException
     */
    Manifest addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException;

    /**
     * Get an atom's data given an AtomManifest.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     */
    InputStream getAtomContent(Atom atom);

    /**
     * Get the manifest that matches a given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws ManifestNotFoundException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     *
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException;

    /**
     *
     * @return Identify for this instance of the sea of stuff.
     */
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
     * @param identity                      used to verify the manifest
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     * @throws ManifestVerificationFailedException
     */
    boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationFailedException;

    /**
     *
     * @param type
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findManifestByType(String type) throws ManifestNotFoundException;

    /**
     *
     * @param label
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findManifestByLabel(String label) throws ManifestNotFoundException;

    /**
     *
     * @param invariant
     * @return
     * @throws ManifestNotFoundException
     */
    Collection<IGUID> findVersions(IGUID invariant) throws ManifestNotFoundException;

}
