package model.interfaces;

import model.exceptions.UnknownGUIDException;
import model.exceptions.UnknownIdentityException;
import model.implementations.components.identity.Session;
import model.implementations.components.manifests.AssetManifest;
import model.implementations.components.manifests.AtomManifest;
import model.implementations.components.manifests.CompoundManifest;
import model.implementations.utils.GUID;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.entities.Manifest;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;

/**
 * This interface describes the set of allowed operations in the Sea of Stuff.
 * <br>
 * Client applications interact with the Sea of Stuff through this interface.
 * This interface abstracts the complexity of the data management in the Sea of Stuff,
 * such as data synching, data retrieval from remote, de-duplication or data
 * redundancy.
 * <br>
 * The sea of stuff is a large collection of assets, compounds and atoms stored
 * across a collection of storage repositories.
 * The sea of stuff supports locatable persistent data via asset, compound and
 * atom GUIDs.
 * <br>
 * The Sea of Stuff is divided into two logical sub-spaces: data and manifest space.
 * All manifests reside in the manifest space, while all other data is stored in the data space.
 * In reality, however, all data is stored in the data space.
 *
 * <p>
 * Entities:
 * </p>
 * <p>
 * Operations:
 * </p>
 * <p>
 * Session:
 * </p>
 *
 * TODO - model
 * TODO - metadata
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaOfStuff {

    /**
     * Register this identity for the current session.
     * Any operations following the register operation will be under the
     * registered identity.
     *
     * @param identity
     * @return
     *
     * @see #unregister(IdentityToken)
     * @see Session
     * Notes: change method name to subscribe?
     */
    IdentityToken register(Identity identity);

    /**
     * Unregister this identity from the current session.
     * TODO
     *
     * @param identityToken
     * @throws UnknownIdentityException if the identityToken is unknown
     *
     * @see #register(Identity)
     */
    void unregister(IdentityToken identityToken) throws UnknownIdentityException;

    /**
     * Adds an atom to the Sea of Stuff. The content of the atom and it
     * location are used to generate a manifest.
     *
     * If an identity is registered, then the manifest will be signed.
     * @param atom to be added to the Sea of Stuff
     * @return AtomManifest for the added atom
     *
     * @see Atom
     * @see Manifest
     */
    AtomManifest addAtom(Atom atom);

    /**
     * Get an atom given an AtomManifest.
     * @param atomManifest
     * @return
     *
     * @see // TODO - policy
     */
    Atom getAtomContent(AtomManifest atomManifest);

    /**
     * Adds a CompoundManifest to the Sea of Stuff.
     * FIXME - The content of the compound
     * and its location are used to generate a Manifest
     *
     * @param compoundManifest to be added to the Sea of Stuff
     *
     * @see Manifest
     */
    void addCompound(CompoundManifest compoundManifest);

    /**
     * Adds an asset to the Sea of Stuff.
     * Note that an asset exists only in the manifest space.
     *
     * @param assetManifest
     */
    void addAsset(AssetManifest assetManifest);

    /**
     * Get the manifest that matches a given GUID.
     *
     * @param guid                  of the manifest
     * @return Manifest             the manifest associated with the GUID.
     * @throws UnknownGUIDException if the GUID is not known within the currently
     *                              explorable Sea of Stuff
     *
     * @see Manifest
     * @see GUID
     */
    Manifest getManifest(GUID guid) throws UnknownGUIDException;

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
     *
     * @see Manifest
     * @see GUID
     */
    boolean verifyManifest(Manifest manifest);

    // decide on how data is replicated, etc.
    void setPolicy(/* TODO - policy */);

    // TODO - how to add data to an existing asset?
}

// TODO - additional calls into the sea of stuff for searching and setting up policies

// TODO - search methods

// TODO - describe this workflow
// XXX 1- get manifest from sea of stuff
// XXX 2 - copy data
// XXX 3 - create new manifest, which has similarities with other manifest.

/*

public ManifestStream findManifests(Metadata metadata);

public ManifestStream getCompoundEntities(Manifest manifest);
*/
