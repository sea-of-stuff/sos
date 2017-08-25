package uk.ac.standrews.cs.sos.archive;

import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.SOSException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;

/**
 * The following creates a node instance of the SOS.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
class ServerState {

    private static SOSLocalNode sos;

    static SOSLocalNode init(SettingsConfiguration.Settings settings) {
        try {
            return ServerState.startSOS(settings);
        } catch (SOSException e) {
            e.printStackTrace();
        }

        return null;
    }

    static void kill() {
        sos.kill();
    }

    private static SOSLocalNode startSOS(SettingsConfiguration.Settings settings) throws SOSException {

        LocalStorage localStorage;
        try {
            CastoreBuilder builder = settings.getStore().getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException | ConfigurationException e) {
            throw new SOSException(e);
        }

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.settings(settings)
                .internalStorage(localStorage)
                .build();

        return sos;
    }

}
