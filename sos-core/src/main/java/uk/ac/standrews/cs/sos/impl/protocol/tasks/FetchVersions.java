package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchVersions extends Task {

    private Node node;
    private IGUID invariant;
    private Set<IGUID> versions;

    public FetchVersions(Node node, IGUID invariant) throws IOException {
        super();

        if (!node.isDDS()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch manifest from non-DDS node");
        }

        if (invariant == null || invariant.isInvalid()) {
            setState(TaskState.ERROR);
            throw new IOException("Attempting to fetch manifest, but you have given an invalid GUID");
        }

        this.node = node;
        this.invariant = invariant;
        this.versions = new LinkedHashSet<>();
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Versions for invariant " + invariant.toMultiHash() + " will be fetched from node " + node.guid().toShortString());

        try {
            URL url = SOSURL.DDS_GET_VERSIONS(node, invariant);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);
                    this.versions = readJSONArrayOfGUIDs(responseBody);
                    SOS_LOG.log(LEVEL.INFO, "Manifest fetched successfully from node " + node.guid());
                    setState(TaskState.SUCCESSFUL);
                }

            } else {
                setState(TaskState.UNSUCCESSFUL);
                SOS_LOG.log(LEVEL.ERROR, "Unable to fetch versions for invariant " + invariant.toMultiHash() + " successfully from node " + node.guid().toShortString());
                throw new IOException();
            }


        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch versions");
        }
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }

    public Set<IGUID> getVersions() {
        return versions;
    }

    private Set<IGUID> readJSONArrayOfGUIDs(String json) throws IOException {

        Set<IGUID> retval = new LinkedHashSet<>();

        JsonNode node = JSONHelper.JsonObjMapper().readTree(json);
        for(JsonNode child:node) {

            IGUID guid;
            try {
                guid = GUIDFactory.recreateGUID(child.asText());
            } catch (GUIDGenerationException e) {
                guid = new InvalidID();
            }

            retval.add(guid);
        }

        return retval;
    }
}
