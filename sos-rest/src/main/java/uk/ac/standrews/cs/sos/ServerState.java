package uk.ac.standrews.cs.sos;

import com.google.gson.Gson;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.NodeManagerException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;
import uk.ac.standrews.cs.sos.node.SOSNodeManager;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static SOSNodeManager sos;
    public static Gson gson;

    public static void startSOS() {
        try {
            SeaConfiguration configuration = SeaConfiguration.getInstance();
            Index index = LuceneIndex.getInstance(configuration);

            SOSNodeManager.setConfiguration(configuration);
            SOSNodeManager.setIndex(index);

            ServerState.sos = SOSNodeManager.getInstance();
        } catch (NodeManagerException | IndexException | SeaConfigurationException e) {
            e.printStackTrace();
        }
    }
}
