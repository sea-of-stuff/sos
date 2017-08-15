package uk.ac.standrews.cs.sos.web.agents;

import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
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

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        Agent agent = sos.getAgent();

        Manifest manifest = agent.getManifest(guid);
        if (manifest.getType() == ManifestType.VERSION) {

            Version version = (Version) manifest;
            IGUID metadataGUID = version.getMetadata();
            if (metadataGUID == null || metadataGUID.isInvalid()) {
                return "N/A";
            }

            Metadata metadata = agent.getMetadata(metadataGUID);
            return metadata.toString();
        } else {
            return "N/A";
        }

    }
}