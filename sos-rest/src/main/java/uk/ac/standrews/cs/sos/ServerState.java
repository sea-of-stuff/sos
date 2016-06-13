package uk.ac.standrews.cs.sos;

import com.google.gson.Gson;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.LocalSOSNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static LocalSOSNode sos;
    public static Gson gson;

    public static void startSOS() {
        try {
            Configuration configuration = Configuration.getInstance();
            Index index = LuceneIndex.getInstance();

            LocalSOSNode.setIndex(index);
            LocalSOSNode.create(configuration);

            ServerState.sos = LocalSOSNode.getInstance();
        } catch (SOSException | IndexException | ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
