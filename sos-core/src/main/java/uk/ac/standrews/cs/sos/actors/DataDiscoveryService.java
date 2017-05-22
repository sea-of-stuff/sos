package uk.ac.standrews.cs.sos.actors;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

/**
 * Data Discovery Service
 *
 * The DDS takes care of:
 * - managing the manifests in the SOS
 * - track where the data is and help nodes to find the data
 *
 * TODO - pass param to methods so that it is possible to restrict the scope:
 * e.g. get manifest from this node, vs from all the nodes in the world
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface DataDiscoveryService extends SeaOfStuff {

    /**
     * Add a manifest to the sea of stuff.
     *
     * @param manifest to add to the sea of stuff
     * @return Manifest - the returned manifests might differ from the one passed to the sea of stuff {@code manifest}
     * @throws ManifestPersistException
     */
    void addManifest(Manifest manifest) throws ManifestPersistException;

    /**
     * Adds a manifest to the specified nodes using the replication factor as an AT_LEAST restriction
     *
     * @param manifest
     * @param nodes
     * @param replication
     */
    void addManifest(Manifest manifest, NodesCollection nodes, int replication) throws ManifestPersistException;

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
     * Map the GUID of a manifest with the GUID of a DDS node.
     * This mapping will be used when trying to get the manifest via #getManifest(guid)
     *
     * @param manifest
     * @param ddsNode
     */
    void addManifestDDSMapping(IGUID manifest, IGUID ddsNode);

    /**
     * Get all known versions to this DDS node
     *
     * @return list of DDS versions
     *
     * TODO - remove this method
     */
    Set<Version> getAllVersions();

    /**
     * Get all the assets stored at this node
     *
     * @return invariants
     */
    Set<IGUID> getAssets();

    /**
     * Get all the HEADS for the given invariant
     *
     * @param invariant
     * @return
     */
    Set<Version> getHeads(IGUID invariant);

    /**
     * Get the CURRENT version for the role.
     * The CURRENT does not need to match one of the HEADS
     *
     * Different roles might have different currents.
     *
     * @param role
     * @param invariant
     * @return
     */
    // TODO - what if multiple currents at different nodes?
    Version getCurrent(Role role, IGUID invariant);

    /**
     * Set the specified version as the CURRENT for its asset and for the given role
     *
     * @param role
     * @param version
     */
    void setCurrent(Role role, Version version);

    /**
     * Flushes any in-memory information into disk
     */
    void flush();

}
