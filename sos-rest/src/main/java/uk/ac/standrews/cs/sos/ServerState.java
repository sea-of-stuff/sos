package uk.ac.standrews.cs.sos;

import com.google.gson.Gson;
import uk.ac.standrews.cs.sos.exceptions.IndexException;
import uk.ac.standrews.cs.sos.exceptions.SeaConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SeaOfStuffException;
import uk.ac.standrews.cs.sos.interfaces.SeaOfStuff;
import uk.ac.standrews.cs.sos.interfaces.index.Index;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.SeaOfStuffImpl;
import uk.ac.standrews.cs.sos.model.index.LuceneIndex;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public static SeaOfStuff sos;
    public static Gson gson;

    public static void startSOS() {
        try {
            SeaConfiguration configuration = SeaConfiguration.getInstance();
            Index index = LuceneIndex.getInstance(configuration);
            ServerState.sos = new SeaOfStuffImpl(configuration, index);
        } catch (SeaOfStuffException | IndexException | SeaConfigurationException e) {
            e.printStackTrace();
        }
    }
}
