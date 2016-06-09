package uk.ac.standrews.cs.sos;

import com.google.gson.Gson;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.Configuration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.NodeManager;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static NodeManager sos;
    public static Gson gson;

    public static void startSOS() {
        try {
            Configuration configuration = Configuration.getInstance();
            Index index = LuceneIndex.getInstance(configuration);

            NodeManager.setConfiguration(configuration);
            NodeManager.setIndex(index);

            ServerState.sos = NodeManager.getInstance();
        } catch (NodeManagerException | IndexException | ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
