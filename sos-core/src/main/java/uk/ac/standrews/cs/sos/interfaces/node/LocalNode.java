package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.actors.*;
import uk.ac.standrews.cs.sos.model.Node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface LocalNode extends Node {

    Agent getAgent();

    Storage getStorage();

    DataDiscoveryService getDDS();

    NodeDiscoveryService getNDS();

    MetadataService getMMS();

    ContextService getCMS();

    UsersRolesService getRMS();

    void kill();
}
