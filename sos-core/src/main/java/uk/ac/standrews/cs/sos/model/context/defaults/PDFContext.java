package uk.ac.standrews.cs.sos.model.context.defaults;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.interfaces.model.Version;
import uk.ac.standrews.cs.sos.model.context.ContextImpl;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PDFContext extends ContextImpl {

    public PDFContext(SOSAgent agent, String name) {
        super(agent, name);
    }

    @Override
    public boolean test(Version version) {

        try {
            Metadata metadata = agent.getMetadata(version.getMetadata());
            String contentType = metadata.getPropertyAsString("Content-Type");
            return contentType.toLowerCase().equals("application/pdf");
        } catch (MetadataNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
