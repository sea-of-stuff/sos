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
     * @return
     */
    Agent getAgent();

    /**
     * Storage actor for this node
     *
     * @return
     */
    Storage getStorage();

    /**
     * Data discovery service for this node
     *
     * @return
     */
    DataDiscoveryService getDDS();

    /**
     * Node discovery service for this node
     *
     * @return
     */
    NodeDiscoveryService getNDS();

    /**
     * Metadata service for this node
     *
     * @return
     */
    MetadataService getMMS();

    /**
     * Context service for this node
     *
     * @return
     */
    ContextService getCMS();

    /**
     * User and Role service for this node
     *
     * @return
     */
    UsersRolesService getRMS();

    /**
     * Kill all the resources for this node
     */
    void kill();
}
