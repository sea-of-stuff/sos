package uk.ac.standrews.cs.sos.impl.protocol.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSURLException;
import uk.ac.standrews.cs.sos.impl.protocol.SOSURL;
import uk.ac.standrews.cs.sos.impl.protocol.Task;
import uk.ac.standrews.cs.sos.impl.protocol.TaskState;
import uk.ac.standrews.cs.sos.interfaces.network.Response;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.network.ErrorResponseImpl;
import uk.ac.standrews.cs.sos.network.HTTPMethod;
import uk.ac.standrews.cs.sos.network.RequestsManager;
import uk.ac.standrews.cs.sos.network.SyncRequest;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.impl.protocol.json.TaskJSONFields.*;

/**
 * Other node should return hash(data + random string)
 * Other node can return the right hash only if it really has the data
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class EntityChallenge extends Task {

    private static final int CHALLENGE_SUFFIX_LENGTH_BITS = 1024;
    private static final int CHALLENGE_SUFFIX_BASE = 32;

    private final String challenge;
    private final IGUID entity;
    private final Node challengedNode;
    private final boolean isData;
    private final IGUID challengedEntity;

    private boolean challengePassed;

    public EntityChallenge(IGUID entity, Data challengedData, Node challengedNode, boolean isData) throws GUIDGenerationException, IOException {
        super();

        this.entity = entity;
        this.challengedNode = challengedNode;
        this.isData = isData;

        // Calculate the result of the challenge in advance
        // How to generate a random alpha-numeric string?
        // http://stackoverflow.com/a/41156/2467938
        SecureRandom random = new SecureRandom();
        this.challenge = new BigInteger(CHALLENGE_SUFFIX_LENGTH_BITS, random).toString(CHALLENGE_SUFFIX_BASE);

        List<InputStream> streams = new LinkedList<>();
        try (InputStream data = challengedData.getInputStream();
            InputStream challengeStream = new ByteArrayInputStream(challenge.getBytes())) {

            streams.add(data);
            streams.add(challengeStream);

            try (InputStream stream = new SequenceInputStream(Collections.enumeration(streams))) {

                this.challengedEntity = GUIDFactory.generateGUID(GUID_ALGORITHM, stream);
            }
        }

    }

    @Override
    public void performAction() {

        try {
            challengePassed = challenge(challengedNode);

            // TODO - data structures of local node should be updated
            if (challengePassed) {
                setState(TaskState.SUCCESSFUL);
                SOS_LOG.log(LEVEL.INFO, "Entity with GUID " + entity + " was verified against node " + challengedNode.guid().toShortString());
            } else {
                setState(TaskState.UNSUCCESSFUL);
                SOS_LOG.log(LEVEL.WARN, "Entity with GUID " + entity + " failed to be verified against node " + challengedNode.guid().toShortString());
            }
        } catch (SOSURLException |IOException e) {
            setState(TaskState.ERROR);
            SOS_LOG.log(LEVEL.ERROR, "Unable to verify entity with GUID " + entity + " against node " + challengedNode.guid().toShortString());
        }
    }

    @Override
    public String serialize() {

        ObjectNode node = JSONHelper.jsonObjMapper().createObjectNode();
        node.put(TASK_TYPE, "EntityChallenge");
        node.put(TASK_ENTITY, entity.toMultiHash());
        // TODO - the challenged data will be retrieved again from the node
        node.put(TASK_CHALLENGED_NODE, challengedNode.toString());

        return node.toString();
    }

    @Override
    public Task deserialize(String json) throws IOException {

        JsonNode node = JSONHelper.jsonObjMapper().readTree(json);

        return null;
    }

    private boolean challenge(Node node) throws SOSURLException, IOException {

        URL url = isData ? SOSURL.STORAGE_ATOM_CHALLENGE(node, entity, challenge) : SOSURL.MDS_MANIFEST_CHALLENGE(node, entity, challenge);
        SyncRequest request = new SyncRequest(node.getSignatureCertificate(), HTTPMethod.GET, url);
        Response response = RequestsManager.getInstance().playSyncRequest(request);
        if (response instanceof ErrorResponseImpl) {
            throw new IOException();
        }

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
        return "EntityChallenge. GUID " + entity.toMultiHash() + " - Challenge " + challenge;
    }

    public boolean isChallengePassed() {
        return challengePassed;
    }
}
