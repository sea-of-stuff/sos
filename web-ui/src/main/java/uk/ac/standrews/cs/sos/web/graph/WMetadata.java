package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WMetadata {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);


        Client client = sos.getClient();

        Manifest manifest = client.getManifest(guid);
        if (manifest.getManifestType() == ManifestType.VERSION) {

            Version version = (Version) manifest;
            Collection<IGUID> metadataArrayList = version.getMetadata();
            if (metadataArrayList == null || metadataArrayList.isEmpty()) {
                return "N/A";
            }

            IGUID metadataGUID = (IGUID) metadataArrayList.toArray()[0];

            SOSMetadata metadata = client.getMetadata(metadataGUID);
            return metadata.tabularFormat();
        } else {
            return "N/A";
        }

    }
}
