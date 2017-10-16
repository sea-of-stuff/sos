package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.HTTPStatus;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * Fetch the roles of a given user
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class FetchRoles extends Task {

    private Node node;
    private IGUID userid;
    private Set<Role> roles;

    public FetchRoles(Node node, IGUID userid) throws IOException {

        if (!node.isRMS()) {
            throw new IOException("Attempting to fetch role from non-RMS node");
        }

        this.node = node;
        this.userid = userid;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Roles for user with GUID " + userid.toMultiHash() + " will be fetched from node " + node.getNodeGUID().toShortString());

        try {
            URL url = SOSURL.RMS_GET_ROLES(node, userid);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);

                    // TODO - array of roles...
                    // this.roles = JSONHelper.JsonObjMapper().readValue(responseBody, Role.class);

                    SOS_LOG.log(LEVEL.INFO, "Roles for given user fetched successfully from node " + node.getNodeGUID());
                }

            } else {
                SOS_LOG.log(LEVEL.ERROR, "Roles for given user were not fetched successfully from node " + node.getNodeGUID().toShortString());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch roles for given user");
        }
    }

    public Set<Role> getRoles() {
        return roles;
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
