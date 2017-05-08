package uk.ac.standrews.cs.sos.archive;

import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.configuration.SOSConfiguration;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Node;

import java.util.List;

/**
 * The following creates a node instance of the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class ServerState {

    private static SOSLocalNode sos;

    static SOSLocalNode init(SOSConfiguration configuration) {
        try {
            return ServerState.startSOS(configuration);
        } catch (SOSException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return null;
    }

    static void kill() {
        sos.kill();
    }

    private static SOSLocalNode startSOS(SOSConfiguration configuration) throws SOSException, GUIDGenerationException {

        LocalStorage localStorage;
        try {
            CastoreBuilder builder = configuration.getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException e) {
            throw new SOSException(e);
        }

        List<Node> bootstrapNodes = configuration.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.configuration(configuration)
                .internalStorage(localStorage)
                .bootstrapNodes(bootstrapNodes)
                .build();

        return sos;
    }

}
