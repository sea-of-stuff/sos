package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.actors.Agent;
import uk.ac.standrews.cs.sos.interfaces.manifests.Asset;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WMetadata {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);


        Agent agent = sos.getAgent();

        Manifest manifest = agent.getManifest(guid);
        if (manifest.getManifestType() == ManifestType.ASSET) {

            Asset asset = (Asset) manifest;
            Set<IGUID> metadataArrayList = asset.getMetadata();
            if (metadataArrayList == null || metadataArrayList.isEmpty()) {
                return "N/A";
            }

            IGUID metadataGUID = (IGUID) metadataArrayList.toArray()[0];

            SOSMetadata metadata = agent.getMetadata(metadataGUID);
            return metadata.tabularFormat();
        } else {
            return "N/A";
        }

    }
}
