package uk.ac.standrews.cs.sos.web.tree;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WTree {

    public static String Render(SOSLocalNode sos, IFileSystem fileSystem){
        Map<String, Object> model = new HashMap<>();

        try {
            String data = getTreeInJson(sos, fileSystem);
            // System.out.println(data);

            model.put("node_id", sos.getNodeGUID().toString());
            model.put("tree", data);
        } catch (HEADNotFoundException | ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return VelocityUtils.RenderTemplate("velocity/tree.vm", model);
    }

    private static String getTreeInJson(SOSLocalNode sos, IFileSystem fileSystem) throws HEADNotFoundException, ManifestNotFoundException {

        IGUID rootGUID = fileSystem.getRootId(); // Version ID for the root
        String children = getChildren(sos, fileSystem.getRootDirectory(), rootGUID);

        String data = "'data' : [{" +
                "id: '" + rootGUID.toString() + "', " +
                "text: " + "'/', " +
                "parent: '#'" +
                "}, " +
                children
                + " ]";

        return data;
    }

    private static String getChildren(SOSLocalNode sos, IDirectory parent, IGUID parentGUID) throws ManifestNotFoundException {
        String retval = "";

        Iterator<NameAttributedPersistentObjectBinding> it = parent.iterator();
        while(it.hasNext()) {
            NameAttributedPersistentObjectBinding child = it.next();
            retval += getChild(parentGUID, child);

            // Recursively look for children in subdirectories
            if (child.getObject() instanceof IDirectory) {
                retval += getChildren(sos, (IDirectory) child.getObject(), child.getObject().getGUID());
            }
        }

        return retval;
    }

    private static String getChild(IGUID parent, NameAttributedPersistentObjectBinding child) {
        if (child == null) {
            return "";
        }

        IGUID guid = child.getObject().getGUID();
        String name = child.getName();

        String icon = "";
        if (child.getObject() instanceof IFile) {
            icon = "icon: 'fa fa-file'";
        }
        return "{ id: '" + guid + "', parent: '" + parent + "', text: '" + name + "', " + icon + "}, ";
    }
}
