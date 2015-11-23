package interfaces;

import exceptions.UnknownGUIDException;
import exceptions.UnknownIdentityException;
import interfaces.components.*;
import interfaces.components.identity.Identity;
import interfaces.components.identity.IdentityToken;
import interfaces.components.identity.Signature;
import interfaces.components.manifests.AssetManifest;
import interfaces.components.manifests.AtomManifest;
import interfaces.components.manifests.CompoundManifest;
import interfaces.components.manifests.Manifest;
import interfaces.entities.Asset;
import interfaces.entities.Atom;
import interfaces.entities.Compound;

/**
 * This interface describes the set of allowed operations in the Sea of Stuff.
 *
 * The sea of stuff is a large collection of assets, compounds and atoms stored
 * across a collection of storage repositories.
 * The sea of stuff supports locatable persistent data via asset, compound and
 * atom GUIDs.
 * <br>
 * The Sea of Stuff is divided into two logical sub-spaces: data and manifest space.
 * TODO - tell a bit more about these spaces?
 * <p>
 * Notes:
 * - I am not sure if timestamp is absolutely necessary in the manifest.
 * </p>
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface SeaOfStuff {

    /**
     * Register this identity for the current session.
     * Any operations following the register operation will be under the
     * registered identity.UnknownIdentityException
     *
     * TODO - define session
     *
     * @param identity
     * @see #unregister(IdentityToken)
     * Notes: change method name to subscribe?
     */
    IdentityToken register(Identity identity);

    /**
     * Unregister this identity from the current session.
     * TODO
     *
     * @param identityToken
     *
     * @see #unregister(IdentityToken)
     */
    void unregister(IdentityToken identityToken) throws UnknownIdentityException;

    // TODO - not sure if this call is needed at all
    // FIXME
    void addContext(Context context);

    /**
     * Adds an {@link Atom} to the Sea of Stuff. The content of the atom and it
     * location are used to generate a {@link Manifest}.
     *
     * If an identity is registered, then the manifest will be signed.
     * @param atom  to be added to the Sea of Stuff
     * @return Manifest of the form:
     * <p>
     * {@link Manifest} {@link GUID} <br>
     * {@link ManifestType} {@link ManifestType#ATOM} <br>
     * Timestamp - ? <br>
     * {@link Signature} signature of the manifest <br>
     * {@link Location} list of locations
     * </p>
     *
     * @see #register(Identity)
     */
    Manifest addAtom(Atom atom);

    /**
     * Adds a Compound to the Sea of Stuff. The content of the compound
     * and its location are used to generate a Manifest
     *
     * @param compound to be added to the Sea of Stuff
     * @return Manifest of the form:
     * <p>
     * {@link Manifest} {@link GUID} <br>
     * {@link ManifestType} {@link ManifestType#COMPOUND}<br>
     * Timestamp - ? <br>
     * {@link Location} list of locations
     * </p>
     *
     * @see Compound
     * @see Manifest
     */
    Manifest addCompound(Compound compound);

    /**
     * Adds an asset to the Sea of Stuff
     *
     * @param asset
     * @return {@link Manifest} of the form:
     * <p>
     * {@link Manifest} {@link GUID} <br>
     * {@link Asset} {@link GUID} <br>
     * {@link ManifestType} {@link ManifestType#ASSET} <br>
     * Timestamp - ? <br>
     * {@link Signature} - ? <br>
     * Previous Asset {@link GUID} <br>
     * Content {@link GUID} <br>
     * {@link Metadata} {@link GUID}
     * </p>
     */
    Manifest addAsset(Asset asset);

    /**
     * Get the {@link Manifest} that matches a given {@link GUID}.
     *
     * @param guid                  of the manifest
     * @return Manifest             the manifest associated with the GUID.
     * @throws UnknownGUIDException if the GUID is not known within the currently
     *                              explorable Sea of Stuff
     */
    Manifest getManifest(GUID guid) throws UnknownGUIDException;

    // TODO - javadocs
    // not that sure about these methods
    Asset getAssetContent(AssetManifest assetManifest);
    Atom getAtomContent(AtomManifest atomManifest);
    Compound getCompoundContent(CompoundManifest compoundManifest);

    /**
     * FIXME - define what validity is and split this into verify manifest and verify data in given locations
     * Verify the validity of a {@link Manifest} by comparing the GUID of
     * this manifest with the content of the manifest as well as the content
     * referred by the manifest.
     *
     * @param manifest                      to be verified
     * @return <code>true</code>            if the GUID of the manifest matches
     *                                      the content referred by the manifest.
     */
    boolean verifyManifest(Manifest manifest);

    /**
     *
     * @param guid
     * @param location
     * @return
     */
    boolean verifyDataInLocation(GUID guid, Location location);

    // TODO - additional calls into the sea of stuff for searching and setting up policies

    // TODO - search methods
    /*

    public ManifestStream findManifests(Metadata metadata);

    public ManifestStream getCompoundEntities(Manifest manifest);
    */
}
