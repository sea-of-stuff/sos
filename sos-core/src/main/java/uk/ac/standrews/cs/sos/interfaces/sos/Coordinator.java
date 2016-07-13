package uk.ac.standrews.cs.sos.interfaces.sos;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.node.Node;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Coordinator extends SeaOfStuff {

    /**
     * Add a manifest to the sea of stuff.
     * If {@code recursive} is true, then manifests referenced from the specified one will also be added,
     * assuming that such manifests are available and reachable.
     * This operation will be performed recursively.
     *
     * @param manifest to add to the NodeManager
     * @param recursive if true adds the references manifests and data recursively.
     * @return Manifest - the returned manifests might differ from the one passed to the sea of stuff {@code manifest}
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException;

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

    /**
     * Get a known node to this Sea Of Stuff.
     * Client and IStorage will not support this call.
     *
     * @param guid
     * @return
     */
    Node getNode(IGUID guid);
}
