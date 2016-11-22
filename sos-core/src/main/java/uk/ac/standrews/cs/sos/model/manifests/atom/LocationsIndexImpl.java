package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImpl implements LocationsIndex, Serializable {

    private transient HashMap<IGUID, ArrayList<LocationBundle>> index;

    public LocationsIndexImpl() {
        index = new HashMap<>();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {

        if (index.containsKey(guid)) {
            ArrayList<LocationBundle> bundles = new ArrayList<>();
            bundles.add(locationBundle);
            index.put(guid, bundles);
        } else {
            index.get(guid).add(locationBundle);
        }
    }

    @Override
    public Iterator<LocationBundle> findLocations(IGUID guid) {
        return new LocationsIterator(guid);
    }

    private class LocationsIterator implements Iterator<LocationBundle> {

        Iterator<LocationBundle> it;

        LocationsIterator(IGUID guid) {
            it = index.get(guid).iterator();
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public LocationBundle next() {
            return it.next();
        }

    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(index.size());
        for (IGUID key : index.keySet()) {
            Iterator<LocationBundle> it = findLocations(key);

            out.writeUTF(key.toString());

            while (it.hasNext()) {
                out.writeUTF(it.next().toString());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        // TODO - recreate index
    }
}
