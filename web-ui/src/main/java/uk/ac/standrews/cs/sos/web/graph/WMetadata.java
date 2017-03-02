package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WMetadata {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);


        Agent agent = sos.getAgent();

        Manifest manifest = agent.getManifest(guid);
        if (manifest.getType() == ManifestType.ASSET) {

            Asset asset = (Asset) manifest;
            IGUID metadataGUID = asset.getMetadata();
            if (metadataGUID == null || metadataGUID.isInvalid()) {
                return "N/A";
            }

            SOSMetadata metadata = agent.getMetadata(metadataGUID);
            return metadata.toString();
        } else {
            return "N/A";
        }

    }
}
