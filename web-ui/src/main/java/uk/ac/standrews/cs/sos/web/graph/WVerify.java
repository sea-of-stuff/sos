package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.actors.Agent;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WVerify {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        Agent agent = sos.getAgent();
        Manifest manifest = agent.getManifest(guid);

        try {
            if (agent.verifyManifest(null, manifest)) {
                return "verified";
            } else {
                return "not verified";
            }
        } catch (SignatureException e) {
            return "ManifestVerificationException";
        }
    }
}
