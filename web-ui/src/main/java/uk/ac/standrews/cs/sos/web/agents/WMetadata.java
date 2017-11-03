package uk.ac.standrews.cs.sos.web.agents;

import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.services.Agent;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WMetadata {

    public static String Render(Request req, SOSLocalNode sos) {

        try {
            String guidParam = req.params("id");
            IGUID guid = GUIDFactory.recreateGUID(guidParam);

            Agent agent = sos.getAgent();

            Manifest manifest = agent.getManifest(guid);
            if (manifest.getType() == ManifestType.VERSION) {

                Version version = (Version) manifest;
                Metadata metadata = agent.getMetadata(version);
                return metadata.toString();
            } else {
                return "N/A";
            }

        } catch (GUIDGenerationException e) {
            return "Metadata GUID is invalid";
        } catch (ServiceException e) {
            return "Unable to find metadata";
        }
    }
}
