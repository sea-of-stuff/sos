package uk.ac.standrews.cs.sos.web.home;

import org.apache.xmlbeans.impl.util.Base64;
import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static uk.ac.standrews.cs.sos.web.WebApp.DATA_LIMIT;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WHome {

    public static String Render(SOSLocalNode sos) {

        // TODO - show node configuration and stats?

        Map<String, Object> model = new HashMap<>();

        Set<Map<String, Object> > assets = new LinkedHashSet<>();

        Set<IGUID> assetInvariants = sos.getDDS().getAllAssets();
        for(IGUID guid : assetInvariants) {

            Map<String, Object> versionModel = new HashMap<>();
            try {
                Version version = (Version) sos.getDDS().getManifest(sos.getDDS().getHead(guid));

                Manifest manifest = sos.getDDS().getManifest(version.getContentGUID());
                if (manifest.getType().equals(ManifestType.ATOM)) {
                    Data data = sos.getStorage().getAtomContent(manifest.guid());

                    String type = "Raw";
                    try {
                        if (version.getMetadata() != null && !version.getMetadata().isInvalid()) {
                            Metadata metadata = sos.getMMS().getMetadata(version.getMetadata());
                            type = metadata.getPropertyAsString("Content-Type");
                        }
                    } catch (MetadataNotFoundException ignored) { }

                    String outputData = "Cannot render";
                    switch (type) {
                        case "Raw":
                        case "application/octet-stream":
                        case "text/plain":
                        case "text/plain; charset=ISO-8859-1":
                            outputData = (data.toString().length() > DATA_LIMIT ? data.toString().substring(0, DATA_LIMIT) + ".... OTHER DATA FOLLOWING" : data.toString());
                            break;
                        case "image/png":
                        case "image/jpeg":
                        case "image/jpg":
                        case "image/gif":
                            byte b[] = data.getState();
                            byte[] encodeBase64 = Base64.encode(b);
                            String encodedData = new String(encodeBase64, StandardCharsets.UTF_8);
                            outputData = "<img style=\"max-width:500px;\" src=\"data:" + type + ";base64," + encodedData + "\">";
                            break;
                    }

                    versionModel.put("data", outputData);
                }

                versionModel.put("invariant", version.getInvariantGUID());
                versionModel.put("version", version.getVersionGUID());
                versionModel.put("content", version.getContentGUID());

            } catch (ManifestNotFoundException | HEADNotFoundException | AtomNotFoundException e) {
                e.printStackTrace();
            }

            assets.add(versionModel);
        }

        model.put("assets", assets);

        return VelocityUtils.RenderTemplate("velocity/index.vm", model);
    }

    public static String UploadAtom(Request request, Response response, SOSLocalNode sos) {

        String newLineDelimiter ="\r\n";

        String body = request.body();
        int delimPos = body.indexOf(newLineDelimiter);
        String delimiter = body.substring(0, delimPos);
        body = body.substring(delimPos);

        int bodyIndex = body.indexOf(newLineDelimiter + newLineDelimiter); // empty line
        int finalDelimiter = body.indexOf(newLineDelimiter + delimiter + "--");
        String atomContent = body.substring(bodyIndex + 2*newLineDelimiter.length(), finalDelimiter);

        AtomBuilder atomBuilder = new AtomBuilder().setData(new StringData(atomContent));
        VersionBuilder versionBuilder = new VersionBuilder().setAtomBuilder(atomBuilder);

        sos.getAgent().addData(versionBuilder);

        return "";
    }

}
