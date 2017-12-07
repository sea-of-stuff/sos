package uk.ac.standrews.cs.sos.impl.protocol.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.node.NodesCollectionException;
import uk.ac.standrews.cs.sos.impl.node.NodesCollectionImpl;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.NodesCollectionType;
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
    private String guid;

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
    }

    public void setData(String data) {
        this.data = data;
    }

    private String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @JsonIgnore
    public IGUID getGUIDObj() {

        try {
            return GUIDFactory.recreateGUID(getGuid());
        } catch (GUIDGenerationException e) {
            return new InvalidID();
        }
    }

    public static class Metadata {

        private int replicationFactor;
        private ReplicationNodes replicationNodes;
        private boolean protectedData;

        public Metadata() {}

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

        public boolean isProtectedData() {
            return protectedData;
        }

        public void setProtectedData(boolean protectedData) {
            this.protectedData = protectedData;
        }

        public static class ReplicationNodes {

            private NodesCollectionType type;
            private String[] refs;

            public ReplicationNodes() {}

            public NodesCollectionType getType() {
                return type;
            }

            public void setType(NodesCollectionType type) {
                this.type = type;
            }

            public String[] getRefs() {
                return refs;
            }

            public void setRefs(String[] refs) {
                this.refs = refs;
            }

            public NodesCollection getNodesCollection() throws NodesCollectionException {

                if (type.equals(NodesCollectionType.ANY)) {

                    return new NodesCollectionImpl(type);
                } else {

                    Set<IGUID> nodeRefs = Arrays.stream(refs).map(r -> {
                        try {
                            return GUIDFactory.recreateGUID(r);
                        } catch (GUIDGenerationException e) {
                            return new InvalidID();
                        }
                    }).collect(Collectors.toSet());

                    return new NodesCollectionImpl(nodeRefs);
                }
            }
        }
    }
}
