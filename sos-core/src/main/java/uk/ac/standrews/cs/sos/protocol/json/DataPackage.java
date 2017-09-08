package uk.ac.standrews.cs.sos.protocol.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.utils.IO;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataPackage {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Metadata metadata;
    private String data;

    public DataPackage() {}

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getData() {
        return data;
    }

    @JsonIgnore
    public Data getDataObj() {
        return new InputStreamData(IO.Base64StringToInputStream(data));
        // return new StringData(data);
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class Metadata {

        private String guid;
        private int replicationFactor;
        private ReplicationNodes replicationNodes;

        public Metadata() {}

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public int getReplicationFactor() {
            return replicationFactor;
        }

        public void setReplicationFactor(int replicationFactor) {
            this.replicationFactor = replicationFactor;
        }

        public ReplicationNodes getReplicationNodes() {
            return replicationNodes;
        }

        public void setReplicationNodes(ReplicationNodes replicationNodes) {
            this.replicationNodes = replicationNodes;
        }

        public static class ReplicationNodes {

            private NodesCollection.TYPE type;
            private String[] refs;

            public ReplicationNodes() {}

            public NodesCollection.TYPE getType() {
                return type;
            }

            public void setType(NodesCollection.TYPE type) {
                this.type = type;
            }

            public String[] getRefs() {
                return refs;
            }

            public void setRefs(String[] refs) {
                this.refs = refs;
            }

            public NodesCollection getNodesCollection() throws NodesCollectionException {

                if (type.equals(NodesCollection.TYPE.ANY)) {

                    return new NodesCollectionImpl(type);
                } else {

                    Set<IGUID> nodeRefs = Arrays.asList(refs).stream().map(r -> {
                        try {
                            return GUIDFactory.recreateGUID(r);
                        } catch (GUIDGenerationException e) {
                            return new InvalidID();
                        }
                    }).collect(Collectors.toSet());

                    return new NodesCollectionImpl(type, nodeRefs);
                }
            }
        }
    }
}
