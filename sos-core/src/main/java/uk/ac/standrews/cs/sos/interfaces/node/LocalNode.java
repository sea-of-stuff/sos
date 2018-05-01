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
package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.services.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocalNode extends Node {

    /**
     * Agent for this node
     *
     * @return the agent of this node
     */
    Agent getAgent();

    /**
     * Storage actor for this node
     *
     * @return storage service
     */
    StorageService getStorageService();

    /**
     * Data discovery service for this node
     *
     * @return manifest-data service
     */
    ManifestsDataService getMDS();

    /**
     * Node discovery service for this node
     *
     * @return node discovery service
     */
    NodeDiscoveryService getNDS();

    /**
     * Metadata service for this node
     *
     * @return metadata service
     */
    MetadataService getMMS();

    /**
     * Context service for this node
     *
     * @return context servie
     */
    ContextService getCMS();

    /**
     * User and Role service for this node
     *
     * @return user role service
     */
    UsersRolesService getUSRO();

    /**
     * Kill all the resources for this node
     *
     * @param flush flush the resource to disk
     */
    void kill(boolean flush);
}
