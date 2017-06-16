package uk.ac.standrews.cs.sos.web.usro;

import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserRolePersistException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.roles.UserImpl;
import uk.ac.standrews.cs.sos.model.User;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WUsersRoles {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();
        model.put("users", sos.getRMS().getUsers());
        model.put("roles", sos.getRMS().getRoles());

        return VelocityUtils.RenderTemplate("velocity/usro.vm", model);
    }

    public static String CreateUser(Request request, Response response, SOSLocalNode sos){

        try {
            String username = request.queryParams("username");

            User user = new UserImpl(username);
            sos.getRMS().addUser(user);

            response.status(201);
            return VelocityUtils.RenderTemplate("velocity/usro.vm");

        } catch (SignatureException | UserRolePersistException e) {
            response.status(500);
            return VelocityUtils.RenderTemplate("velocity/usro.vm");
        }
    }
}
