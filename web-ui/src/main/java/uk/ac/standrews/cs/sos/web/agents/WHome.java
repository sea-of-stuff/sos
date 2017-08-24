package uk.ac.standrews.cs.sos.web.agents;

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.AtomNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static uk.ac.standrews.cs.sos.web.agents.WData.GetData;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WHome {

    public static String Render(SOSLocalNode sos) {

        Map<String, Object> model = new HashMap<>();

        Set<Map<String, Object> > assets = new LinkedHashSet<>();

        Set<IGUID> assetInvariants = sos.getDDS().getAllAssets();
        for(IGUID guid : assetInvariants) {

            Map<String, Object> versionModel = new HashMap<>();
            try {
                Version version = (Version) sos.getDDS().getManifest(sos.getDDS().getHead(guid));

                Manifest manifest = sos.getDDS().getManifest(version.getContentGUID());
                if (manifest.getType().equals(ManifestType.ATOM)) {
                    String outputData = GetData(sos, version);
                    versionModel.put("data", outputData);
                }

                versionModel.put("invariant", version.getInvariantGUID());
                versionModel.put("version", version.getVersionGUID());
                versionModel.put("content", version.getContentGUID());
                versionModel.put("contentType", manifest.getType());

            } catch (ManifestNotFoundException | HEADNotFoundException | AtomNotFoundException e) {
                e.printStackTrace();
            }

            assets.add(versionModel);
        }

        model.put("assets", assets);
        model.put("roles", sos.getRMS().getRoles());

        return VelocityUtils.RenderTemplate("velocity/index.vm", model);
    }

}
