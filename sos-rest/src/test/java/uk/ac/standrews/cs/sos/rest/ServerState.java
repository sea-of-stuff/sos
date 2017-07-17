package uk.ac.standrews.cs.sos.rest;

import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Node;

import java.io.File;
import java.util.List;

/**
 * The following creates a node instance of the SOS.
 * THIS CLASS SHOULD BE USED FOR TESTING PURPOSES ONLY
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServerState {

    public SOSLocalNode sos;
    private LocalStorage localStorage;

    public SOSLocalNode init(String propertyFilePath) {
        try {
            return startSOS(propertyFilePath);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void kill() throws DataStorageException {
        sos.kill();

        localStorage.destroy();
    }

    private SOSLocalNode startSOS(String properties) throws SOSException, GUIDGenerationException {

        try {
            File configFile = new File(properties);
            SOSConfiguration configuration = new SOSConfiguration(configFile);

            CastoreBuilder castoreBuilder = configuration.getCastoreBuilder();
            IStorage stor = CastoreFactory.createStorage(castoreBuilder);
            localStorage = new LocalStorage(stor);

            List<Node> bootstrapNodes = configuration.getBootstrapNodes();

            SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
            sos = builder.configuration(configuration)
                    .internalStorage(localStorage)
                    .bootstrapNodes(bootstrapNodes)
                    .build();

            return sos;

        } catch (StorageException | DataStorageException | ConfigurationException e) {
            throw new SOSException(e);
        }
    }

}
