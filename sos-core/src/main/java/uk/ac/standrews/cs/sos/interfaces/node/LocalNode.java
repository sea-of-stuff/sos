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
     */
    void kill();
}
