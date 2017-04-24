package uk.ac.standrews.cs.sos.model;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.storage.data.Data;

import java.util.Set;

/**
 * Methods accessible to the policy
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface PolicyLanguage {

    void postData(Data data, Set<IGUID> nodes, int replicationFactor);

    void deleteData(IGUID guid, Set<IGUID> nodes);

    boolean hasData(IGUID guid, IGUID node);

    int numberOfReplicas(IGUID guid);

    Data getData(IGUID guid);

    Manifest getManifest(IGUID guid);

    void protect(Data data, Role role);

    void unprotect(Manifest manifest, Role role);

    void compress(Data data);

    void decompress(Manifest manifest);

    Node getNode(IGUID guid);

    Set<Node> getNodes(int type);

    Role getRole(IGUID guid);

}
