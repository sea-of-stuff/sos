package uk.ac.standrews.cs.sos.impl.datamodel.builders;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleType;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.NodesCollection;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomBuilder extends ManifestBuilder {

    private Location location;
    private Data data;

    private boolean buildIsSet = false;
    private boolean isLocation = false;
    private boolean isData = false;

    private BundleType bundleType = BundleTypes.CACHE;

    private int replicationFactor = 1; // A replication factor of 1, means that data is stored in this node only
    private NodesCollection replicationNodes;
    private boolean delegateReplication = false;

    public boolean isBuildIsSet() {
        return buildIsSet;
    }

    public AtomBuilder setLocation(Location location) {
        if (!buildIsSet) {
            this.location = location;
            isLocation = true;
            buildIsSet = true;
        }

        return this;
    }

    public AtomBuilder setData(Data data) {
        if (!buildIsSet) {
            this.data = data;
            isData = true;
            buildIsSet = true;
        }

        return this;
    }

    public AtomBuilder setBundleType(BundleType bundleType) {
        this.bundleType = bundleType;

        return this;
    }

    public Location getLocation() {
        return location;
    }

    public Data getData() {

        try {
            if (isData) {
                return data;
            } else if (isLocation) {

                try (InputStream inputStream = location.getSource()) {
                    return new InputStreamData(inputStream);
                }

            } else {
                return new EmptyData();
            }

        } catch (IOException e) {
            return new EmptyData();
        }

    }

    public boolean isData() {
        return isData;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public BundleType getBundleType() {
        return bundleType;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public AtomBuilder setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;

        return this;
    }

    public NodesCollection getReplicationNodes() {
        return replicationNodes;
    }

    public AtomBuilder setReplicationNodes(NodesCollection replicationNodes) {
        this.replicationNodes = replicationNodes;

        return this;
    }

    public boolean isDelegateReplication() {
        return delegateReplication;
    }

    public AtomBuilder setDelegateReplication(boolean delegateReplication) {
        this.delegateReplication = delegateReplication;

        return this;
    }
}
