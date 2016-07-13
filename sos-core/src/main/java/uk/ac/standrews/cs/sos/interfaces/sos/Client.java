package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.locations.Location;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.CompoundType;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.InputStream;
import java.util.Collection;

/**
 * The Client is one of the three node roles within the Sea of Stuff.
 * <br>
 * The Client supports the following operations:
 * - pushing data/manifests to the SOS
 * - get data/manifests from the SOS
 * - find data in the SOS
 *
 * The behaviour of these operations depends on the policy used by this SOS instance.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Client extends SeaOfStuff {

    /**
     * Adds an atom to the Sea of Stuff.
     * The atom is cached locally and replicated according to the policy used by this instance.
     *
     * @param location of the data for the atom.
     * @return the added atom.
     * @throws StorageException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Atom addAtom(Location location)
            throws StorageException, ManifestPersistException;

    /**
     * Adds a stream of data to the Sea of Stuff as an atom.
     * The atom is cached locally and replicated according to the policy used by this instance.
     *
     * @param inputStream for this atom
     * @return the added atom
     * @throws StorageException
     * @throws ManifestPersistException
     */
    Atom addAtom(InputStream inputStream)
            throws StorageException, ManifestPersistException;

    /**
     * Adds a Compound to the Sea of Stuff.
     *
     * @param contents of this compound.
     * @return the added compound.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     * @see Manifest
     */
    Compound addCompound(CompoundType type, Collection<Content> contents)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Adds a version of an asset to the Sea of Stuff.
     *
     * @param content of this version.
     * @param invariant guid of this asset
     * @param prevs version of this asset.
     * @param metadata of this version.
     * @return the added version.
     * @throws ManifestNotMadeException
     * @throws ManifestPersistException
     *
     */
    Version addVersion(IGUID content, IGUID invariant,
                       Collection<IGUID> prevs, Collection<IGUID> metadata)
            throws ManifestNotMadeException, ManifestPersistException;

    /**
     * Add a manifest to the sea of stuff.
     * If {@code recursive} is true, then manifests referenced from the one specified will also be added,
     * assuming that such manifests are available and reachable.
     *
     * @param manifest to add to the NodeManager
     * @param recursive if true adds the references manifests and data recursively.
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException;

    /**
     * Get the data of an Atom.
     *
     * @param atom describing the atom to retrieve.
     * @return InputStream
     */
    InputStream getAtomContent(Atom atom);

    /**
     * Get the manifest matching the given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws ManifestNotFoundException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     *
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException;

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
     * @throws ManifestVerificationException
     */
    boolean verifyManifest(Identity identity, Manifest manifest) throws ManifestVerificationException;

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
