package uk.ac.standrews.cs.sos.model.manifests.atom.store;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.protocol.SOSEP;
import uk.ac.standrews.cs.sos.interfaces.node.Node;
import uk.ac.standrews.cs.sos.model.locations.SOSLocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.model.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.network.Method;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.Response;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.exceptions.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RemoteStore implements Store {

    private Node node;
    private InputStream inputStream;
    private IGUID dataGUID;

    public RemoteStore(Node node, InputStream inputStream) {
        this.node = node;
        this.inputStream = inputStream;
    }

    @Override
    public IGUID store() throws StorageException {

        URL url;
        try {
            url = SOSEP.STORAGE_POST_DATA(node);
            SyncRequest request = new SyncRequest(Method.POST, url);
            request.setBody(inputStream);

            Response response = RequestsManager.getInstance().playSyncRequest(request);

            JsonNode node = JSONHelper.JsonObjMapper().readTree(response.getBody());
            String guid = node.get("ContentGUID").textValue();

            // TODO - get locations back!
            dataGUID = GUIDFactory.recreateGUID(guid);
        } catch (IOException | GUIDGenerationException e) {
            e.printStackTrace();
        }

        return dataGUID;
    }

    @Override
    public LocationBundle getLocationBundle() {
        try {
            return new PersistLocationBundle(new SOSLocation(node.getNodeGUID(), dataGUID));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
