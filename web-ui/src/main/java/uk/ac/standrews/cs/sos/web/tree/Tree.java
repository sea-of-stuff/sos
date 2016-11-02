package uk.ac.standrews.cs.sos.web.tree;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Tree {

    // TODO - pass FileSystem from webdav instead!
    public static String Render(SOSLocalNode sos, IGUID root){
        Map<String, Object> model = new HashMap<>();

        try {
            String data = getTreeInJson(sos, root);
            System.out.println(data);

            model.put("tree", data);
        } catch (HEADNotFoundException e) {
            e.printStackTrace();
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return VelocityUtils.RenderTemplate("velocity/tree.vm", model);
    }

    private static String getTreeInJson(SOSLocalNode sos, IGUID root) throws HEADNotFoundException, ManifestNotFoundException {
        Manifest manifest = sos.getClient().getHEAD(root);

        String children = getChildren(sos, (Version) manifest);

        String data = "'data' : [{" +
                "id: '" + manifest.guid().toString() + "', " +
                "text: " + "'/', " +
                "parent: '#'" +
                "}, " +
                children
                + " ]";

        return data;
    }

    private static String getChildren(SOSLocalNode sos, Version version) throws ManifestNotFoundException {
        String retval = "";
        Manifest manifest = sos.getClient().getManifest(version.getContentGUID());

        if (manifest.getManifestType() == ManifestType.COMPOUND) {
            Compound compound = (Compound) manifest;
            for(Content content:compound.getContents()) {
                System.out.println(content.getLabel());
                retval += "{ id: '" + content.getGUID() + "', parent: '" + version.getVersionGUID() + "', text: '" + content.getLabel() + "'}, ";
            }
        }

        return retval;
    }
}
