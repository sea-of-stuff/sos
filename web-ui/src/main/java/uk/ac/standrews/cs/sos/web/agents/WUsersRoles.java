package uk.ac.standrews.cs.sos.web.agents;

import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.crypto.ProtectionException;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.usro.RoleImpl;
import uk.ac.standrews.cs.sos.impl.usro.UserImpl;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.services.ManifestsDataService;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WUsersRoles {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();

        ManifestsDataService manifestsDataService = sos.getMDS();
        UsersRolesService usersRolesService = sos.getUSRO();

        model.put("users", get(manifestsDataService, usersRolesService.getUsers()));
        model.put("roles", get(manifestsDataService, usersRolesService.getRoles()));

        return VelocityUtils.RenderTemplate("velocity/usro.vm", model);
    }

    private static Set<Manifest> get(ManifestsDataService manifestsDataService, Set<IGUID> refs) {

        Set<Manifest> manifests = new LinkedHashSet<>();
        for(IGUID ref:refs) {

            Manifest manifest = null;
            try {
                manifest = manifestsDataService.getManifest(ref);
            } catch (ManifestNotFoundException e) {
                /* IGNORE */
            }
            manifests.add(manifest);
        }

        return manifests;
    }

    public static String CreateUser(Request request, Response response, SOSLocalNode sos) {

        try {
            String username = request.queryParams("username");

            User user = new UserImpl(username);
            sos.getUSRO().addUser(user);

            response.redirect("/usro");
            return "";

        } catch (SignatureException | UserRolePersistException e) {
            response.redirect("/usro");
            return "";
        }
    }

    public static String CreateRole(Request request, Response response, SOSLocalNode sos) {

        try {
            IGUID userGUID = GUIDFactory.recreateGUID(request.queryParams("userGUID"));
            String rolename = request.queryParams("rolename");

            User user = sos.getUSRO().getUser(userGUID);

            Role role = new RoleImpl(user, rolename);
            sos.getUSRO().addRole(role);

            response.redirect("/usro");
            return "";

        } catch (SignatureException | UserRolePersistException | UserNotFoundException | GUIDGenerationException | ProtectionException e) {
            e.printStackTrace();
            response.redirect("/usro");
            return "";
        }
    }

    public static String Delete(Request request, Response response, SOSLocalNode sos) {

        try {
            IGUID guid = GUIDFactory.recreateGUID(request.params("id"));
            sos.getUSRO().delete(guid);

            response.redirect("/usro");
            return "";

        } catch (GUIDGenerationException | UserNotFoundException | RoleNotFoundException e) {

            e.printStackTrace();
            response.redirect("/usro");
            return "";
        }
    }

}
