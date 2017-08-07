package uk.ac.standrews.cs.sos.web.graph;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.impl.util.Base64;
import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.web.Utils;
import uk.ac.standrews.cs.utilities.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WData {

    private final static String DEFAULT_TYPE = "Raw";

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, IOException, MetadataNotFoundException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);
        Manifest manifest = sos.getAgent().getManifest(guid);

        Pair<String, String> data = getData(sos, manifest, DEFAULT_TYPE);

        if (data.Y().isEmpty()) {
            return " ";
        }

        switch(data.X()) {
            case "Raw":
            case "application/octet-stream":
            case "text/plain":
            case "text/plain; charset=ISO-8859-1":
                return "<pre style=\"white-space: pre-wrap; word-wrap: break-word;\">" +
                        (data.Y().length() > 140 ? data.Y().substring(0, 140) + ".... OTHER DATA FOLLOWING" : data.Y()) +
                        "</pre>";
            case "image/png":
            case "image/jpeg":
            case "image/jpg":
                return "<img style=\"max-width:500px;\" src=\"data:" + data.X() + ";base64," + data.Y() + "\">";
        }

        return "Unable to render data";
    }

    private static Pair<String, String> getData(SOSLocalNode sos, Manifest manifest, String type) throws IOException, ManifestNotFoundException, MetadataNotFoundException {

        if (manifest.getType() == ManifestType.VERSION) {
            Version version = (Version) manifest;
            Manifest contentManifest = sos.getAgent().getManifest(version.getContentGUID());

            if (version.getMetadata() != null) {
                Metadata metadata = sos.getAgent().getMetadata(version.getMetadata());
                type = metadata.getPropertyAsString("Content-Type");
            }
            return getData(sos, contentManifest, type);
        }

        if (manifest.getType() == ManifestType.ATOM) {
            Atom atom = (Atom) manifest;

            InputStream atomContent;
            try {
                atomContent = sos.getAgent().getAtomContent(atom);
            } catch (AtomNotFoundException e) {
                return new Pair<>(type, "ATOM NOT FOUND");
            }

            String retval;
            switch(type) {
                case "image/png":
                case "image/jpeg":
                case "image/jpg":
                    byte b[] = IOUtils.toByteArray(atomContent);
                    byte[] encodeBase64 = Base64.encode(b);
                    retval = new String(encodeBase64 , StandardCharsets.UTF_8);
                    break;
                default:
                    retval = Utils.InputStreamToString(atomContent);
            }

            return new Pair<>(type, retval);
        }

        return new Pair<>(type, "N/A");
    }
}
