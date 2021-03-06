/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
 * Manifest Data Service (MDS)
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
     * @param storeLocally if true the manifest is stored locally
     * @param nodes where to add the manifest
     * @param replication suggested replication factor for the manifest
     * @param limitReplication if true check replication factor against node settings
     * @throws ManifestPersistException if the manifest could not be added correctly
     */
    void addManifest(Manifest manifest, boolean storeLocally, NodesCollection nodes, int replication, boolean limitReplication) throws ManifestPersistException;

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
     * Get manifests of given type
     *
     * @param type of manifests to get
     * @return set of refs to manifests
     */
    Set<IGUID> getManifests(ManifestType type);

    /**
     * Resolve the path to a manifest
     * @param path of the form guid/guid/guid or guid/label/label or a mix
     * @return manifest
     * @throws ManifestNotFoundException if manifest could not be found
     */
    Manifest resolvePath(String path) throws ManifestNotFoundException;

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
     * Map the GUID of a manifest with the GUID of a MDS node.
     * This mapping will be used when trying to get the manifest via #getManifest(guid)
     *
     *
     * @param manifest for which to add a MDSnode ref
     * @param mdsNode the MDS node ref
     */
    void addManifestNodeMapping(IGUID manifest, IGUID mdsNode);

    /**
     * Delete manifest from local node.
     *
     * @param guid of manifest
     * @throws ManifestNotFoundException if manifest is not found
     */
    void delete(IGUID guid) throws ManifestNotFoundException;

    /**
     * Delete the manifest with the matching guid from the nodes specified
     * @param guid of the manifest to be deleted
     * @param nodesCollection nodes where to delete manifest. Type must be SPECIFIED
     * @param localyCopy if true, delete manifest in this node too
     * @throws ManifestNotFoundException if manifest could not be deleted
     */
    void delete(IGUID guid, NodesCollection nodesCollection, boolean localyCopy) throws ManifestNotFoundException;

    /**
     * For ATOMS only!
     *
     * @param guid of atom
     */
    void deleteLocalLocation(IGUID guid);

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
     * Search manifests of given type for the given params
     *
     * @param type of manifest to search
     * @param params search parameters
     * @return set of refs to manifest that match the search criteria
     */
    Set<IGUID> searchVersionableManifests(ManifestType type, List<ManifestParam> params);

}
