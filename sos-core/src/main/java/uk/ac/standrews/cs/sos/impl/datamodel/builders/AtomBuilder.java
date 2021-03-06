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

    private boolean doNotStoreDataLocally = false;
    private boolean doNotStoreManifestLocally = false;
    private int replicationFactor = 0; // TODO - check if the value 0 is correct -- A replication factor of 1, means that data is stored in this node only
    private NodesCollection replicationNodes;
    private boolean delegateReplication = false;
    private boolean setLocationAndProvenance = true;

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

    public boolean isDoNotStoreDataLocally() {
        return doNotStoreDataLocally;
    }

    public AtomBuilder setDoNotStoreDataLocally(boolean doNotStoreDataLocally) {
        this.doNotStoreDataLocally = doNotStoreDataLocally;

        return this;
    }

    public boolean isDoNotStoreManifestLocally() {
        return doNotStoreManifestLocally;
    }

    public AtomBuilder setDoNotStoreManifestLocally(boolean doNotStoreManifestLocally) {
        this.doNotStoreManifestLocally = doNotStoreManifestLocally;

        return this;
    }

    public boolean isSetLocationAndProvenance() {
        return setLocationAndProvenance;
    }

    public AtomBuilder setSetLocationAndProvenance(boolean setLocationAndProvenance) {
        this.setLocationAndProvenance = setLocationAndProvenance;

        return this;
    }
}
