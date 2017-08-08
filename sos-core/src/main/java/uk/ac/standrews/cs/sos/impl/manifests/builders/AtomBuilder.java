package uk.ac.standrews.cs.sos.impl.manifests.builders;

import uk.ac.standrews.cs.castore.data.Data;
import uk.ac.standrews.cs.castore.data.EmptyData;
import uk.ac.standrews.cs.castore.data.InputStreamData;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleType;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.model.Location;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;

/**
 * TODO - rename to AtomSourceBuilder
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomBuilder {

    private Location location;
    private Data data;

    private boolean buildIsSet = false;
    private boolean isLocation = false;
    private boolean isData = false;

    private Role role = null;
    private BundleType bundleType = BundleTypes.CACHE;

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

    public AtomBuilder setRole(Role role) {
        this.role = role;

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
                return new InputStreamData(location.getSource());
            } else {
                return new EmptyData();
            }

        } catch (IOException e) {
            return new EmptyData();
        }

    };

    public boolean isData() {
        return isData;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public BundleType getBundleType() {
        return bundleType;
    }

    public Role getRole() {
        return role;
    }
}
