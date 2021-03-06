/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    public String getGuid() {
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

        private int replicationFactor = 0;
        private ReplicationNodes replicationNodes = new ReplicationNodes();
        private boolean protectedData = false;

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

            private NodesCollectionType type = NodesCollectionType.ANY;
            private String[] refs = new String[]{};

            public ReplicationNodes() {}

            public NodesCollectionType getType() {
                return type;
            }

            public void setType(NodesCollectionType type) {
                this.type = type;
            }

            public String[] getRefs() {
                return refs.clone();
            }

            public void setRefs(String[] refs) {
                this.refs = refs.clone();
            }

            @JsonIgnore
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
