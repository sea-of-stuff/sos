package uk.ac.standrews.cs.sos.rest.api;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.rest.HTTP.HTTPStatus;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RESTExperimentTest extends CommonRESTTest {

    @Test
    public void basicTest() throws ContextException {

        String contextJSON = "{\n" +
                "\t\"context\": {\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"domain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"codomain\": {\n" +
                "\t\t\t\"type\": \"LOCAL\",\n" +
                "\t\t\t\"nodes\": []\n" +
                "\t\t},\n" +
                "\t\t\"max_age\": 0\n" +
                "\t},\n" +
                "\t\"predicate\": {\n" +
                "\t\t\"type\": \"Predicate\",\n" +
                "\t\t\"predicate\": \"true;\"\n" +
                "\t},\n" +
                "\t\"policies\": []\n" +
                "}";

        IGUID guidContext = state.sos.getCMS().addContext(contextJSON);

        Response response = target("/sos/experiment/cms/guid/" + guidContext.toMultiHash() + "/predicate").request().get();
        assertEquals(response.getStatus(), HTTPStatus.OK);
    }

}
