package uk.ac.standrews.cs.sos.experiments.protocol;

import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.*;

import java.io.IOException;
import java.net.URL;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DeleteAllAtoms extends Task {

    private Node node;

    public DeleteAllAtoms(Node node) {
        this.node = node;
    }

    @Override
    protected void performAction() {

        try {
            URL url = ExperimentURL.DELETE_ALL_ATOMS(node);

            SyncRequest request = new SyncRequest(HTTPMethod.DELETE, url, ResponseType.TEXT);
            Response response = RequestsManager.getInstance().playSyncRequest(request);
            if (response instanceof ErrorResponseImpl) {
                System.out.println("ERROR (1) - Unable to delete all atoms from node " + node.guid().toMultiHash());
                setState(TaskState.ERROR);
                throw new IOException();
            }

            if (response.getCode() == HTTPStatus.OK) {
                System.out.println("Deleted all atoms from node " + node.guid().toMultiHash());
                setState(TaskState.SUCCESSFUL);
            } else {
                System.out.println("UNSUCCESSFUL - Unable to delete all atoms from node " + node.guid().toMultiHash());
                setState(TaskState.UNSUCCESSFUL);
            }

        } catch (IOException | SOSURLException e) {
            e.printStackTrace();
            System.out.println("ERROR (2) - Unable to delete all atoms from node " + node.guid().toMultiHash());
            setState(TaskState.ERROR);
        }
    }

    @Override
    public String serialize() {
        return null;
    }

    @Override
    public Task deserialize(String json) {
        return null;
    }
}
