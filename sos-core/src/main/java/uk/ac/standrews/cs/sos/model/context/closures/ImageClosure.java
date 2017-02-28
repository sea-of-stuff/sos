package uk.ac.standrews.cs.sos.model.context.closures;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.model.Asset;
import uk.ac.standrews.cs.sos.interfaces.model.SOSMetadata;

import java.util.function.Predicate;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ImageClosure extends BaseClosure {

    private ImageClosure(SOSAgent agent, Predicate<Asset> predicate) {
        super(agent, predicate);
    }

    public boolean test(Asset asset) {

        try {
            SOSMetadata metadata = agent.getMetadata(asset.getMetadata());
            String contentType = metadata.getProperty("Content-Type");
            return contentType.toLowerCase().startsWith("image/");
        } catch (MetadataNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }
}
