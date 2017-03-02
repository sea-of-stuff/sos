package uk.ac.standrews.cs.sos.web.graph;

import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.model.Atom;
import uk.ac.standrews.cs.sos.interfaces.model.Manifest;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WData {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, IOException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getAgent().getManifest(guid);

        String data = getData(sos, manifest);

        if (data.isEmpty()) {
            data = " ";
        }

        return data.length() > 140 ? data.substring(0, 140) + ".... OTHER DATA FOLLOWING" : data;
    }

    private static String getData(SOSLocalNode sos, Manifest manifest) throws IOException, ManifestNotFoundException {

        if (manifest.getType() == ManifestType.ASSET) {
            Manifest contentManifest = sos.getAgent().getManifest(manifest.guid());
            return getData(sos, contentManifest);
        }

        if (manifest.getType() == ManifestType.ATOM) {
            Atom atom = (Atom) manifest;

            InputStream atomContent = null;
            try {
                atomContent = sos.getAgent().getAtomContent(atom);
            } catch (AtomNotFoundException e) {
                return "ATOM NOT FOUND";
            }
            String retval = Utils.InputStreamToString(atomContent);
            return retval;
        }

        return "N/A";
    }
}
