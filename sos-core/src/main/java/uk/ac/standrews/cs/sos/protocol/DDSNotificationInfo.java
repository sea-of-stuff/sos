package uk.ac.standrews.cs.sos.protocol;

import uk.ac.standrews.cs.sos.model.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Global settings about DDS
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DDSNotificationInfo {

    private static final int MAX_DEFAULT_DDS_NODES = 3;

    private boolean notifyDDSNodes;
    private boolean useDefaultDDSNodes;
    private boolean useSuggestedDDSNodes;
    private Set<Node> suggestedDDSNodes;

    public DDSNotificationInfo() {
        this.suggestedDDSNodes = new HashSet<>();
    }

    public boolean notifyDDSNodes() {
        return notifyDDSNodes;
    }

    public DDSNotificationInfo setNotifyDDSNodes(boolean notifyDDSNodes) {
        this.notifyDDSNodes = notifyDDSNodes;

        return this;
    }

    public boolean useDefaultDDSNodes() {
        return useDefaultDDSNodes;
    }

    public DDSNotificationInfo setUseDefaultDDSNodes(boolean useDefaultDDSNodes) {
        this.useDefaultDDSNodes = useDefaultDDSNodes;

        return this;
    }

    public int getMaxDefaultDDSNodes() {
        return MAX_DEFAULT_DDS_NODES;
    }

    public boolean useSuggestedDDSNodes() {
        return useSuggestedDDSNodes;
    }

    public DDSNotificationInfo setUseSuggestedDDSNodes(boolean useSuggestedDDSNodes) {
        this.useSuggestedDDSNodes = useSuggestedDDSNodes;

        return this;
    }

    public Set<Node> getSuggestedDDSNodes() {
        return suggestedDDSNodes;
    }

    public DDSNotificationInfo setSuggestedDDSNodes(Set<Node> suggestedDDSNodes) {
        this.suggestedDDSNodes = suggestedDDSNodes;

        return this;
    }
}
