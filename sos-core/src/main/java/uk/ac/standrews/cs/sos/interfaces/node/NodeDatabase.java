package uk.ac.standrews.cs.sos.interfaces.node;

import uk.ac.standrews.cs.sos.exceptions.db.DatabaseException;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface NodeDatabase {

    DBConnection getDBConnection() throws DatabaseException;

    void addNode(Node node);

    Collection<Node> getNodes();
}
