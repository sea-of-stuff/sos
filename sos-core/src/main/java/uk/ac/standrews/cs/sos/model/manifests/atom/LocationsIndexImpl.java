package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImpl implements LocationsIndex {

    private transient HashMap<IGUID, LocationBundle> index;

    public LocationsIndexImpl() {
        index = new HashMap<>();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {
        index.put(guid, locationBundle);
    }

    @Override
    public Iterator<LocationBundle> findLocations(IGUID guid) {
        return new LocationsIterator();
    }

    private class LocationsIterator implements Iterator<LocationBundle> {

        Iterator<IGUID> iterator;

        LocationsIterator() {
            iterator = index.keySet().iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public LocationBundle next() {
            IGUID key = iterator.next();

            return index.get(key);
        }

    }
}
