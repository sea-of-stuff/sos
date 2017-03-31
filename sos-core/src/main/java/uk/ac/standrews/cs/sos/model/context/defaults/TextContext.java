package uk.ac.standrews.cs.sos.model.context.defaults;

import uk.ac.standrews.cs.sos.actors.SOSAgent;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.interfaces.model.Policy;
import uk.ac.standrews.cs.sos.interfaces.model.Version;
import uk.ac.standrews.cs.sos.model.context.ContextImpl;
import uk.ac.standrews.cs.sos.model.context.SOSPredicateImpl;
import uk.ac.standrews.cs.sos.model.context.policies.ReplicationPolicy;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class TextContext extends ContextImpl {

    public TextContext(SOSAgent agent, String name) {
        super(agent, name);

        predicate = new SOSPredicateImpl(p -> {
            try {
                Version version = (Version) agent.getManifest(p);
                Metadata metadata = agent.getMetadata(version.getMetadata());
                String contentType = metadata.getPropertyAsString("Content-Type");
                return isText(contentType);
            } catch (ManifestNotFoundException | MetadataNotFoundException e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    private boolean isText(String contentType) {
        switch(contentType.toLowerCase()) {
            case "text":
            case "text/plain":
                return true;
            default:
                return false;
        }
    }

    @Override
    public Policy[] getPolicies() {
        return new Policy[]{
                new ReplicationPolicy(2)
        };
    }
}
