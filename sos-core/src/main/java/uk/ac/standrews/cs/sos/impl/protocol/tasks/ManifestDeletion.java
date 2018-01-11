package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.IOException;
import java.net.URL;

/**
 * TODO - extend task to all manifest-based services (mms, cms, usro, etc)
 * TODO - use this task in MDS
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestDeletion extends Task {

    private Node node;
    private Manifest manifest;

    public ManifestDeletion(Node node, Manifest manifest) {
        this.node = node;
        this.manifest = manifest;
    }

    @Override
    protected void performAction() {

        try {
            URL url = getManifestURL(node, manifest.getType(), manifest.guid());
            SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url, ResponseType.JSON);
            Response response = RequestsManager.getInstance().playSyncRequest(request);

            if (!(response instanceof ErrorResponseImpl)) {

                response.consumeResponse();
                setState(TaskState.SUCCESSFUL);
            } else {
                SOS_LOG.log(LEVEL.DEBUG, "ManifestDeletion -- ERROR RESPONSE");
                setState(TaskState.ERROR);
            }

        } catch (SOSURLException | IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to delete manifest with GUID " + manifest.guid().toMultiHash() + " in node " + node.guid().toMultiHash());
        }
    }

    private URL getManifestURL(Node node, ManifestType type, IGUID guid) throws SOSURLException {

        switch(type) {

            case ATOM: case ATOM_PROTECTED:
            case COMPOUND: case COMPOUND_PROTECTED:
            case VERSION:

                if (node.isDDS()) {
                    return SOSURL.DDS_DELETE_MANIFEST(node, guid);
                }

            case CONTEXT:

                if (node.isCMS()) {
                    return SOSURL.CMS_DELETE_CONTEXT_VERSIONS(node, guid);
                }

            case ROLE:
            case USER:
            case METADATA: case METADATA_PROTECTED:
            case NODE:
                throw new SOSURLException("Type: " + type + " not supported yet");

            default:
                throw new SOSURLException("Unable to return manifest URL for node " + node.toString());
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
}
