package uk.ac.standrews.cs.sos.impl.context.defaults;

import uk.ac.standrews.cs.sos.impl.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.model.Version;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.impl.context.CommonContext;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PDFContext extends CommonContext {

    public PDFContext(SOSAgent agent, String name) {
        super(agent, name);
    }

    @Override
    public boolean test(Version version) {

        try {
            Metadata metadata = agent.getMetadata(version.getMetadata());
            String contentType = metadata.getProperty("Content-Type");
            return contentType.toLowerCase().equals("application/pdf");
        } catch (MetadataNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
