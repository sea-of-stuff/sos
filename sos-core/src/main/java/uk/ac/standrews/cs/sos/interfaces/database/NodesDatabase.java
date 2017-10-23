package uk.ac.standrews.cs.sos.interfaces.database;

import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.impl.node.SOSNode;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NodesDatabase extends Database {

    /**
     * Add a given node to the database
     *
     * @param node to be added
     * @throws DatabaseConnectionException if the node could not be added
     */
    void addNode(Node node) throws DatabaseConnectionException;

    /**
     * Get all nodes from the database
     *
     * @return nodes to get
     * @throws DatabaseConnectionException if the nodes could not be retrieved
     */
    Set<SOSNode> getNodes() throws DatabaseConnectionException;
}
