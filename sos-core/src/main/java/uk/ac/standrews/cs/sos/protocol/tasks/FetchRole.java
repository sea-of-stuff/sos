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
import uk.ac.standrews.cs.sos.model.Role;
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
public class FetchRole extends Task {

    private Node node;
    private IGUID roleid;
    private Role role;

    public FetchRole(Node node, IGUID roleid) throws IOException {

        if (!node.isRMS()) {
            throw new IOException("Attempting to fetch role from non-RMS node");
        }

        this.node = node;
        this.roleid = roleid;
    }

    @Override
    public void performAction() {

        SOS_LOG.log(LEVEL.INFO, "Role with GUID " + roleid.toMultiHash() + " will be fetched from node " + node.getNodeGUID().toShortString());

        try {
            URL url = SOSURL.RMS_GET_ROLE(node, roleid);
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (response.getCode() == HTTPStatus.OK) {

                try (InputStream inputStream = response.getBody()) {

                    String responseBody = IO.InputStreamToString(inputStream);
                    this.role = JSONHelper.JsonObjMapper().readValue(responseBody, Role.class);

                    SOS_LOG.log(LEVEL.INFO, "Role fetched successfully from node " + node.getNodeGUID());
                }

            } else {
                SOS_LOG.log(LEVEL.ERROR, "Role was not fetched successfully from node " + node.getNodeGUID().toShortString());
                throw new IOException();
            }
        } catch (SOSURLException | IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to fetch role");
        }
    }

    public Role getRole() {
        return role;
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
