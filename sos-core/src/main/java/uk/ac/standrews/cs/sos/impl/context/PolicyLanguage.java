package uk.ac.standrews.cs.sos.impl.context;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.sos.actors.NDS;
import uk.ac.standrews.cs.sos.actors.RMS;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.model.Scope;

import java.util.Set;

/**
 * Methods accessible to the policy
 *
 * What it is needed:
 * - calls to appropriate tasks
 * - indices for this node updated
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class PolicyLanguage {

    private static NDS nds;
    private static RMS rms;

    private PolicyLanguage(NDS nds, RMS rms) {
        this.nds = nds;
        this.rms = rms;
    }

    private static PolicyLanguage instance;
    public static PolicyLanguage instance(NDS nds, RMS rms) {

        if (instance == null) {
            instance = new PolicyLanguage(nds, rms);
        }

        return instance;
    }

    public static PolicyLanguage instance() {

        if (instance != null) {
            return instance;
        }

        return null; // TODO - throw exception
    }

    public void postData(Data data, Set<IGUID> nodes, int replicationFactor) {

    }

    public void deleteData(IGUID guid, Set<IGUID> nodes) {

    }

    public boolean hasData(IGUID guid, IGUID node) {

        return false;
    }

    public int numberOfReplicas(Scope scope, IGUID guid) {

        return -1;
    }

    public Data getData(Scope scope, IGUID guid) {

        return null;
    }

    public Manifest getManifest(Scope scope, IGUID guid) {

        return null;
    }

    public void protect(Data data, Role role) {

    }

    public void unprotect(Manifest manifest, Role role) {

    }

    public void compress(Data data) {

    }

    public void decompress(Manifest manifest) {

    }

    public Node getNode(Scope scope, IGUID guid) throws NodeNotFoundException {

        return nds.getNode(guid); // TODO - restrict
    }

    public Set<Node> getNodes(Scope scope, int type) {


        return null;
    }

    public Role getRole(IGUID guid) {

        return rms.getRole(guid);
    }


}
