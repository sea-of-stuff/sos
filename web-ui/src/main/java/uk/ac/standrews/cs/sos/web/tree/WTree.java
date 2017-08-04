package uk.ac.standrews.cs.sos.web.tree;

import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
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
            String data = getTreeInJson(fileSystem);

            model.put("node_id", sos.getNodeGUID().toMultiHash());
            model.put("tree", data);
        } catch (TIPNotFoundException | ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return VelocityUtils.RenderTemplate("velocity/tree.vm", model);
    }

    private static String getTreeInJson(IFileSystem fileSystem) throws TIPNotFoundException, ManifestNotFoundException {

        IGUID rootGUID = fileSystem.getRootId(); // Version ID for the root
        String children = getChildren(fileSystem.getRootDirectory(), rootGUID);

        String data = "'data' : [{" +
                "id: '" + rootGUID.toMultiHash() + "', " +
                "text: " + "'/', " +
                "parent: '#'" +
                "}, " +
                children
                + " ]";

        return data;
    }

    private static String getChildren(IDirectory parent, IGUID parentGUID) throws ManifestNotFoundException {
        String retval = "";

        Iterator<NameAttributedPersistentObjectBinding> it = parent.iterator();
        while(it.hasNext()) {
            NameAttributedPersistentObjectBinding child = it.next();
            retval += getChild(parentGUID, child);

            // Recursively look for children in subdirectories
            if (child.getObject() instanceof IDirectory) {
                retval += getChildren((IDirectory) child.getObject(), child.getObject().getGUID());
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
        return "{ id: '" + guid.toMultiHash() + "', parent: '" + parent.toMultiHash() + "', text: '" + name + "', " + icon + "}, ";
    }
}
