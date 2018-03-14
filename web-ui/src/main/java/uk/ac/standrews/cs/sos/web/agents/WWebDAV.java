/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module web-ui.
 *
 * web-ui is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * web-ui is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with web-ui. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.web.agents;

import uk.ac.standrews.cs.fs.interfaces.IDirectory;
import uk.ac.standrews.cs.fs.interfaces.IFile;
import uk.ac.standrews.cs.fs.interfaces.IFileSystem;
import uk.ac.standrews.cs.fs.persistence.impl.NameAttributedPersistentObjectBinding;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WWebDAV {

    public static String RenderNoWebDav() {
        return VelocityUtils.RenderTemplate("velocity/invalid.vm");
    }

    public static String Render(SOSLocalNode sos, IFileSystem fileSystem){
        Map<String, Object> model = new HashMap<>();

        model.put("node_id", sos.guid().toMultiHash());

        String data = getTreeInJson(fileSystem);
        model.put("tree", data);

        UsersRolesService usersRolesService = sos.getUSRO();

        Set<Pair<User, Role>> usro = new LinkedHashSet<>();
        for(IGUID roleRef:usersRolesService.getRoles()) {
            try {
                Role role = usersRolesService.getRole(roleRef);
                User user = usersRolesService.getUser(role.getUser());
                usro.add(new Pair<>(user, role));
            } catch (RoleNotFoundException | UserNotFoundException e) { /* do nothing */ }
        }
        model.put("usro", usro);

        return VelocityUtils.RenderTemplate("velocity/webdav.vm", model);
    }

    private static String getTreeInJson(IFileSystem fileSystem) {

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

    private static String getChildren(IDirectory parent, IGUID parentGUID) {
        StringBuilder retval = new StringBuilder();

        Iterator<NameAttributedPersistentObjectBinding> it = parent.iterator();
        while(it.hasNext()) {
            NameAttributedPersistentObjectBinding child = it.next();
            retval.append(getChild(parentGUID, child));

            // Recursively look for children in subdirectories
            if (child.getObject() instanceof IDirectory) {
                retval.append(getChildren((IDirectory) child.getObject(), child.getObject().getGUID()));
            }
        }

        return retval.toString();
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
