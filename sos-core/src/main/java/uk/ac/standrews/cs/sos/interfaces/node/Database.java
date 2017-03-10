package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.exceptions.db.DatabaseConnectionException;
import uk.ac.standrews.cs.sos.node.SOSNode;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface Database {

    /**
     * Add a given node to the database
     *
     * @param node
     * @throws DatabaseConnectionException
     */
    void addNode(Node node) throws DatabaseConnectionException;

    /**
     * Get all nodes from the database
     *
     * @return
     * @throws DatabaseConnectionException
     */
    Set<SOSNode> getNodes() throws DatabaseConnectionException;
}
