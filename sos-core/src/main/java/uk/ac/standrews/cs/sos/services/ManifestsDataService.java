package uk.ac.standrews.cs.sos.services;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestPersistException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifest.ManifestParam;
import uk.ac.standrews.cs.sos.interfaces.node.NodeType;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.List;
import java.util.Set;

/**
 * Manifest Data Service
 *
 * The MDS takes care of:
 * - managing the first class entities of the SOS
 * - track where the data is and help nodes to find the data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface ManifestsDataService extends Service {

    /**
     * Add a manifest to the local sea of stuff.
     *
     * @param manifest to add to the sea of stuff
     * @throws ManifestPersistException if the manifest could not be added correctly
     */
    void addManifest(Manifest manifest) throws ManifestPersistException;

    /**
     * Adds a manifest to the specified nodes using the replication factor as an AT_LEAST restriction
     *
     * @param manifest to be added
     * @param nodes where to add the manifest
     * @param replication suggested replication factor for the manifest
     * @throws ManifestPersistException if the manifest could not be added correctly
     */
    void addManifest(Manifest manifest, NodesCollection nodes, int replication) throws ManifestPersistException;

    /**
     * Get the manifest that matches a given GUID.
     *
     * @param guid                  of the manifest.
     * @return Manifest             the manifest associated with the GUID.
     * @throws ManifestNotFoundException if the GUID is not known within the currently
     *                              explorable Sea of Stuff.
     */
    Manifest getManifest(IGUID guid) throws ManifestNotFoundException;

    /**
     * Get the manifest that matches the given given GUID.
     * This method contacts any node in the SOS.
     *
     * @param guid of the manifest
     * @param nodeTypeFilter type of node to contact
     * @return the manifest
     * @throws ManifestNotFoundException if the manifest could not be found
     */
    Manifest getManifest(IGUID guid, NodeType nodeTypeFilter) throws ManifestNotFoundException;

    /**
     * Challenge manifest matching the GUID with the given string challenge
     *
     * @param guid of the manifest to be challenged
     * @param challenge for the manifest
     * @return GUID of the challenge
     */
    IGUID challenge(IGUID guid, String challenge);

    /**
     * Get the manifest matching the given GUID from the nodes collection
     *
     * @param nodes where to get the manifest from
     * @param nodeTypeFilter type of node to contact
     * @param guid of the manifest to get
     * @return the manifest
     * @throws ManifestNotFoundException if the manifest could not be found
     */
    Manifest getManifest(NodesCollection nodes, NodeType nodeTypeFilter, IGUID guid) throws ManifestNotFoundException;

    /**
     * Map the GUID of a manifest with the GUID of a DDS node.
     * This mapping will be used when trying to get the manifest via #getManifest(guid)
     *
     *
     * @param manifest for which to add a DDSnode ref
     * @param ddsNode the DDS node ref
     */
    void addManifestNodeMapping(IGUID manifest, IGUID ddsNode);

    /**
     * Delete manifest from local node.
     *
     * @param guid of manifest
     * @throws ManifestNotFoundException if manifest is not found
     */
    void delete(IGUID guid) throws ManifestNotFoundException;

    /**
     * For ATOMS only!
     *
     * @param guid of atom
     * @throws ManifestNotFoundException if atom is not found
     */
    void deleteLocalLocation(IGUID guid) throws ManifestNotFoundException;

    /**
     * Get all the tips for the given invariant.
     * The tips are the leaves in the DAG of the asset.
     *
     * @param invariant for which to get the tips
     * @return the tips of the asset identified by the invariant
     */
    Set<IGUID> getTips(IGUID invariant) throws TIPNotFoundException;

    /**
     * Get the HEAD version for an asset.
     * The HEAD does not need to match one of the TIPS.
     *
     * The HEAD is the version of the asset that is currently active for this node.
     *
     * @param invariant for which to get the head
     * @return the reference of the HEAD version
     */
    IGUID getHead(IGUID invariant) throws HEADNotFoundException;

    /**
     * Set the specified version as the HEAD for its asset
     *
     * @param version the version to set as HEAD for its asset
     */
    void setHead(Version version);

    /**
     * Get all the known version for an invariant.
     *
     * @param invariant of the asset
     * @return set of versions references
     */
    Set<IGUID> getVersions(IGUID invariant);

    /**
     * Get all versions for the given invariant from the specified node collection
     *
     * @param nodesCollection where to get the versions from
     * @param invariant of the asset
     * @return set of refs to versions of the asset
     */
    Set<IGUID> getVersions(NodesCollection nodesCollection, IGUID invariant);

    /**
     * Get manifests of given type
     *
     * @param type of manifests to get
     * @return set of refs to manifests
     */
    Set<IGUID> getManifests(ManifestType type);

    /**
     * Search manifests of given type for the given params
     *
     * @param type of manifest to search
     * @param params search parameters
     * @return set of refs to manifest that match the search criteria
     */
    Set<IGUID> searchVersionableManifests(ManifestType type, List<ManifestParam> params);

}
