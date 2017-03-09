package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

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
        } catch (ManifestVerificationException e) {
            return "ManifestVerificationException";
        }
    }
}
