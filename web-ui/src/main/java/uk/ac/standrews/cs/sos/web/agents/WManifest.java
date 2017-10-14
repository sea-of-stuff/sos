package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;

import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WManifest {

    public static String Render(Request req, SOSLocalNode sos) throws GUIDGenerationException, IOException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        try {
            Manifest manifest = sos.getAgent().getManifest(guid);
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(manifest.toString(), Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

        } catch (ServiceException e) {

            return "Manifest Not Found";
        }
    }
}
