package uk.ac.standrews.cs.sos.web.home;

import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.impl.manifests.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.manifests.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

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
        // model.put("assets",sos.getDDS().getAllAssets());

        Set<Map<String, Object> > assets = new LinkedHashSet<>();

        Set<IGUID> assetInvariants = sos.getDDS().getAllAssets();
        for(IGUID guid : assetInvariants) {

            Map<String, Object> versionModel = new HashMap<>();
            try {
                Version version = (Version) sos.getDDS().getManifest(sos.getDDS().getHead(guid));

                Manifest manifest = sos.getDDS().getManifest(version.getContentGUID());
                if (manifest.getType().equals(ManifestType.ATOM)) {
                    Data data = sos.getStorage().getAtomContent(manifest.guid());

                    String outputData = (data.toString().length() > DATA_LIMIT ? data.toString().substring(0, DATA_LIMIT) + ".... OTHER DATA FOLLOWING" : data.toString());
                    versionModel.put("data", outputData);
                }

                versionModel.put("invariant", version.getInvariantGUID());
                versionModel.put("version", version.getVersionGUID());
                versionModel.put("content", version.getContentGUID());

            } catch (ManifestNotFoundException | HEADNotFoundException e) {
                e.printStackTrace();
            } catch (AtomNotFoundException e) {
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
