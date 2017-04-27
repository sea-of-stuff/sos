package uk.ac.standrews.cs.sos.experiments;

import uk.ac.standrews.cs.sos.model.Node;

/**
 * Methods for managing versions using different tools.
 * These methods will be useful when designing the experiments
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Versioning {

    /**
     * Version the content at the given source.
     * Use the versioning tool specified by the type.
     * The versions are to be distributed to the specified nodes.
     * If shard is true (SOS only), then the repository is to be split across multiple nodes
     *
     * @param source
     * @param type
     * @param nodes
     * @param shard SOS Only
     * @param granularity SOS Only
     * @return
     */
    public RepoStats version(Object source, String type, Node[] nodes, boolean shard, int granularity) {


        // Could you JGit for interact with git?

        return null;
    }

}
