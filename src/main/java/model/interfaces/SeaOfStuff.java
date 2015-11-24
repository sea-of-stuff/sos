package model.interfaces;

import model.exceptions.UnknownGUIDException;
import model.exceptions.UnknownIdentityException;
import model.implementations.utils.GUID;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.entities.Compound;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;
import model.interfaces.components.identity.Session;
import model.interfaces.components.manifests.AssetManifest;
import model.interfaces.components.manifests.AtomManifest;
import model.interfaces.components.manifests.CompoundManifest;
import model.interfaces.components.manifests.Manifest;

/**
 * This interface describes the set of allowed operations in the Sea of Stuff.
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
 * TODO - more on the operations and how these are used?
 * <p>
 * Session
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
     * registered identity
     *
     * TODO - define session
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
     * TODO
     * @return
     */
    Session getSession();

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
     * Adds a Compound to the Sea of Stuff. The content of the compound
     * and its location are used to generate a Manifest
     *
     * @param compound to be added to the Sea of Stuff
     * @return CompoundManifest for the added compound

     *
     * @see Compound
     * @see Manifest
     */
    CompoundManifest addCompound(Compound compound);

    /**
     * Adds an asset to the Sea of Stuff
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
     *
     * @param atomManifest
     * @return
     */
    Atom getAtomContent(AtomManifest atomManifest);

    /**
     *
     * @param compoundManifest
     * @return
     */
    Compound getCompoundContent(CompoundManifest compoundManifest);

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

}

// TODO - additional calls into the sea of stuff for searching and setting up policies

// TODO - search methods
/*

public ManifestStream findManifests(Metadata metadata);

public ManifestStream getCompoundEntities(Manifest manifest);
*/
