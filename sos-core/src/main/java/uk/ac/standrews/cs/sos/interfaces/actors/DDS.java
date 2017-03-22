package uk.ac.standrews.cs.sos.interfaces.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.Version;

import java.util.List;

/**
 * Data Discovery Service
 *
 * The DDS takes care of:
 * - managing the manifests in the SOS
 * - track where the data is and help nodes to find the data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DDS extends SeaOfStuff {

    /**
     * Add a manifest to the sea of stuff.
     * If {@code recursive} is true, then manifests referenced from the specified one will also be added,
     * assuming that such manifests are available and reachable.
     * This operation will be performed recursively.
     *
     * @param manifest to add to the sea of stuff
     * @param recursive if true adds the references manifests and data recursively.
     * @return Manifest - the returned manifests might differ from the one passed to the sea of stuff {@code manifest}
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest, boolean recursive) throws ManifestPersistException;

    /**
     * Map the GUID of a manifest with the GUID of a dds node.
     * This mapping will be used when trying to get the manifest via #getManifest(guid)
     * @param manifest
     * @param ddsNode
     */
    void addManifestDDSMapping(IGUID manifest, IGUID ddsNode);

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

    List<Version> getAllAssets();

    /**
     * Flushes any in-memory information into disk
     */
    void flush();

}
