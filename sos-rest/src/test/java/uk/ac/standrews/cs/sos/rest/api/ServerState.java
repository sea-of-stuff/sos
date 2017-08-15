package uk.ac.standrews.cs.sos.rest.api;

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

    public SOSLocalNode init(File file) {
        try {
            SettingsConfiguration settingsConfiguration = new SettingsConfiguration(file);
            return startSOS(settingsConfiguration.getSettingsObj());

        } catch (SOSException | ConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void kill() throws DataStorageException {
        sos.kill();

        localStorage.destroy();
    }

    private SOSLocalNode startSOS(SettingsConfiguration.Settings settings) throws SOSException {

        try {
            CastoreBuilder builder = settings.getStore().getCastoreBuilder();
            IStorage storage = CastoreFactory.createStorage(builder);
            localStorage = new LocalStorage(storage);
        } catch (StorageException | DataStorageException | ConfigurationException e) {
            throw new SOSException(e);
        }

        List<SettingsConfiguration.Settings.NodeSettings> bootstrapNodes = settings.getBootstrapNodes();

        SOSLocalNode.Builder builder = new SOSLocalNode.Builder();
        sos = builder.settings(settings)
                .internalStorage(localStorage)
                .bootstrapNodes(bootstrapNodes)
                .build();

        return sos;
    }

}
