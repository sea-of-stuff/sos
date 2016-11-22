package uk.ac.standrews.cs.sos.model.manifests.atom;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.model.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImpl implements LocationsIndex, Serializable {

    private transient HashMap<IGUID, HashSet<LocationBundle>> index;

    public LocationsIndexImpl() {
        index = new HashMap<>();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {

        if (index.containsKey(guid)) {
            index.get(guid).add(locationBundle);
        } else {
            HashSet<LocationBundle> bundles = new HashSet<>();
            bundles.add(locationBundle);
            index.put(guid, bundles);
        }
    }

    @Override
    public Iterator<LocationBundle> findLocations(IGUID guid) {
        return new LocationsIterator(guid);
    }

    private class LocationsIterator implements Iterator<LocationBundle> {

        Iterator<LocationBundle> it;

        LocationsIterator(IGUID guid) {
            if (index.containsKey(guid)) {
                it = index.get(guid).iterator();
            } else {
                it = Collections.emptyIterator();
            }
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public LocationBundle next() {
            return it.next();
        }

    }

    @Override
    public void persist(File file) throws IOException {
        FileOutputStream ostream = new FileOutputStream(file.toFile());
        ObjectOutputStream p = new ObjectOutputStream(ostream);

        p.writeObject(this);
        p.flush();
        ostream.close();
    }

    public static LocationsIndex load(File file) throws IOException, ClassNotFoundException {

        // Check that file is not empty
        BufferedReader br = new BufferedReader(new FileReader(file.getPathname()));
        if (br.readLine() == null) {
            return null;
        }

        FileInputStream istream = new FileInputStream(file.toFile());
        ObjectInputStream q = new ObjectInputStream(istream);

        LocationsIndex persistedLocationsIndex = (LocationsIndex)q.readObject();

        return persistedLocationsIndex;
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(index.size());
        for (IGUID key : index.keySet()) {

            out.writeUTF(key.toString());

            int numberOfLocations = index.get(key).size();
            out.writeInt(numberOfLocations);

            Iterator<LocationBundle> it = findLocations(key);
            while (it.hasNext()) {
                out.writeUTF(it.next().toString());
            }
        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        int indexSize = in.readInt();
        index = new HashMap<>();
        for(int i = 0; i < indexSize; i++) {
            try {
                IGUID key = GUIDFactory.recreateGUID(in.readUTF());
                int numberOfLocations = in.readInt();

                for (int j = 0; j < numberOfLocations; j++) {
                    LocationBundle bundle = JSONHelper.JsonObjMapper().readValue(in.readUTF(), LocationBundle.class);
                    addLocation(key, bundle);
                }

            } catch (GUIDGenerationException e) {
                e.printStackTrace();
            }

        }
    }
}
