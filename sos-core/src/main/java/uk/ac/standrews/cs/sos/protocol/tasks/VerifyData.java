package uk.ac.standrews.cs.sos.protocol.tasks;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
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
import java.util.List;

/**
 * TODO - get result method
 *
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
    private Node challengedNode;

    private boolean challengePassed;

    public VerifyData(IGUID entity, Data challengedData, Node challengedNode) throws GUIDGenerationException, IOException {
        this.entity = entity;
        this.challengedNode = challengedNode;

        // Calculate the result of the challenge in advance
        this.challenge = new BigInteger(130, random).toString(32);
        List<InputStream> streams = Arrays.asList(challengedData.getInputStream(), new ByteArrayInputStream(challenge.getBytes()));
        InputStream stream = new SequenceInputStream(Collections.enumeration(streams));
        challengedEntity = GUIDFactory.generateGUID(ALGORITHM.SHA256, stream);
    }

    @Override
    public void performAction() {

        try {
            challengePassed = challenge(challengedNode);

            // TODO - update data structures in this node?
            if (challengePassed) {
                SOS_LOG.log(LEVEL.INFO, "Data with GUID " + entity + " was verified against node " + challengedNode);
            } else {
                SOS_LOG.log(LEVEL.WARN, "Data with GUID " + entity + " failed to be verified against node " + challengedNode);
            }
        } catch (SOSURLException |IOException e) {
            SOS_LOG.log(LEVEL.ERROR, "Unable to verify data with GUID " + entity + " against node " + challengedNode);
        }
    }

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

    public boolean isChallengePassed() {
        return challengePassed;
    }
}
