package uk.ac.standrews.cs.sos.web.agents;

import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.services.Agent;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WVerify {

    public static String Render(Request request, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, RoleNotFoundException {

        Agent agent = sos.getAgent();

        IGUID manifestId = GUIDFactory.recreateGUID(request.params("id"));
        Manifest manifest = agent.getManifest(manifestId);

        IGUID roleid = GUIDFactory.recreateGUID(request.params("roleid"));
        Role role = sos.getRMS().getRole(roleid);

        try {
            if (agent.verifyManifestSignature(role, manifest)) {
                return "VERIFIED";
            } else {
                return "NOT VERIFIED";
            }
        } catch (SignatureException e) {
            return "ManifestVerificationException";
        }
    }
}
