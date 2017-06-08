package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.LEVEL;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.network.HTTPMethod;
import uk.ac.standrews.cs.sos.impl.network.RequestsManager;
import uk.ac.standrews.cs.sos.impl.network.SyncRequest;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.SOSURL;
import uk.ac.standrews.cs.sos.protocol.Task;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * challenge another node to verifySignature the data
 * GET /verifySignature?guid=GUID&challenge=Random string
 * Other node should return hash(data + random string)
 * Other node can return the right hash only if it really has the data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class VerifyData extends Task {

    // How to generate a random alpha-numeric string?
    // http://stackoverflow.com/a/41156/2467938
    private SecureRandom random = new SecureRandom();
    private String challenge;
    private IGUID challengedEntity;

    private IGUID entity;
    private Iterator<Node> nodes;


    public VerifyData(InputStream challengedStream, IGUID entity, Iterator<Node> nodes) {
        this.entity = entity;
        this.nodes = nodes;

        this.challenge = new BigInteger(130, random).toString(32);
        List<InputStream> streams = Arrays.asList(challengedStream, new ByteArrayInputStream(challenge.getBytes()));
        InputStream stream = new SequenceInputStream(Collections.enumeration(streams));
        try {
            challengedEntity = GUIDFactory.generateGUID(stream);
        } catch (GUIDGenerationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void performAction() {

        while(nodes.hasNext()) {

            Node node = nodes.next();
            try {
                boolean verified = challenge(node);

                if (verified) {
                    SOS_LOG.log(LEVEL.INFO, "Data with GUID " + entity + " was verified against node " + node);
                } else {
                    SOS_LOG.log(LEVEL.WARN, "Data with GUID " + entity + " failed to be verified against node " + node);
                }
            } catch (SOSURLException | IOException e) {
                SOS_LOG.log(LEVEL.ERROR, "Unable to verifySignature data with GUID " + entity + " against node " + node);
            }
        }
    }

    // TODO - we do not have this REST end-point yet
    private boolean challenge(Node node) throws SOSURLException, IOException {

        URL url = SOSURL.CHALLENGE(node, entity, challenge);
        SyncRequest request = new SyncRequest(HTTPMethod.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);

        try (InputStream inputStream = response.getBody()) {
            String responseBody = IO.InputStreamToString(inputStream);
            IGUID responseGUID = GUIDFactory.recreateGUID(responseBody);

            return responseGUID.equals(challengedEntity);
        } catch (GUIDGenerationException e) {
            return false;
        }

    }

    @Override
    public String toString() {
        return "VerifyData with guid" + entity + " with challenge " + challenge;
    }
}
