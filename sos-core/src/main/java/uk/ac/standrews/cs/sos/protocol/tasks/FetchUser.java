package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.HTTPStatus;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchUser extends Task {

    private Node node;
    private IGUID userid;
    private User user;

    public FetchUser(Node node, IGUID userid) throws IOException {

        if (!node.isRMS()) {
            throw new IOException("Attempting to fetch user from non-RMS node");
        }

        this.node = node;
        this.userid = userid;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "User with GUID " + userid.toMultiHash() + " will be fetched from node " + node.getNodeGUID().toShortString());

        try {
            URL url = SOSURL.RMS_GET_USER(node, userid);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);
                    this.user = JSONHelper.JsonObjMapper().readValue(responseBody, User.class);

                    SOS_LOG.log(LEVEL.INFO, "User fetched successfully from node " + node.getNodeGUID());
                }

            } else {
                SOS_LOG.log(LEVEL.ERROR, "User was not fetched successfully from node " + node.getNodeGUID().toShortString());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch user");
        }
    }

    public User getUser() {
        return user;
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) throws IOException {
        return null;
    }
}
