package uk.ac.standrews.cs.sos.web.tree;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.fs.interfaces.IDirectory;
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
public class Tree {

    // TODO - pass FileSystem from webdav instead!
    public static String Render(SOSLocalNode sos, IFileSystem fileSystem){
        Map<String, Object> model = new HashMap<>();

        try {
            String data = getTreeInJson(sos, fileSystem);
            System.out.println(data);

            model.put("tree", data);
        } catch (HEADNotFoundException e) {
            e.printStackTrace();
        } catch (ManifestNotFoundException e) {
            e.printStackTrace();
        }

        return VelocityUtils.RenderTemplate("velocity/tree.vm", model);
    }

    private static String getTreeInJson(SOSLocalNode sos, IFileSystem fileSystem) throws HEADNotFoundException, ManifestNotFoundException {
        IGUID rootGUID = fileSystem.getRootDirectory().getGUID();

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

    private static String getChildren(SOSLocalNode sos, IDirectory directory, IGUID parent) throws ManifestNotFoundException {
        String retval = "";

        Iterator<NameAttributedPersistentObjectBinding> it = directory.iterator();
        while(it.hasNext()) {
            NameAttributedPersistentObjectBinding c = it.next();
            System.out.println(c.getName());
            IGUID guid = c.getObject().getGUID();

            String icon = "icon: 'file'";
//            if (c.getObject() instanceof IFile) {
//
//            }
            retval += "{ id: '" + guid + "', parent: '" + parent + "', text: '" + c.getName() + "', " + icon + "}, ";
        }

        return retval;
    }
}
