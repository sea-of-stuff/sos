package uk.ac.standrews.cs.sos.impl.manifests.atom;

import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImpl implements LocationsIndex {

    private transient HashMap<IGUID, PriorityQueue<LocationBundle>> index;

    public LocationsIndexImpl() {
        index = new HashMap<>();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {

        if (index.containsKey(guid)) {
            if (!index.get(guid).contains(locationBundle)) {
                index.get(guid).add(locationBundle);
            }
        } else {
            PriorityQueue<LocationBundle> bundles = new PriorityQueue<>(comparator());
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

    /**
     * Order priority:
     * local node data first (no matter if cache, persistent or provenance)
     * cache
     * persistent
     * provenance
     *
     * @return comparator value
     */
    private Comparator<LocationBundle> comparator() {
        return (o1, o2) -> {

            if (o1.getLocation() instanceof SOSLocation && o2.getLocation() instanceof SOSLocation) {

                SOSLocation s1 = (SOSLocation) o1.getLocation();
                SOSLocation s2 = (SOSLocation) o2.getLocation();

                if (isLocalNode(s1.getMachineID()) && isLocalNode(s2.getMachineID())) return 0;
                if (isLocalNode(s1.getMachineID()) && !isLocalNode(s2.getMachineID())) return -1;
                if (!isLocalNode(s1.getMachineID()) && isLocalNode(s2.getMachineID())) return 1;
            }

            if (o1.getLocation() instanceof SOSLocation && !(o2.getLocation() instanceof SOSLocation)) {

                SOSLocation s1 = (SOSLocation) o1.getLocation();

                if (isLocalNode(s1.getMachineID())) return -1;
            }

            if (!(o1.getLocation() instanceof SOSLocation) && o2.getLocation() instanceof SOSLocation) {

                SOSLocation s2 = (SOSLocation) o2.getLocation();

                if (isLocalNode(s2.getMachineID())) return 1;
            }


            if (o1.getType() == BundleTypes.CACHE && o2.getType() == BundleTypes.CACHE)
                return 0;

            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.PERSISTENT)
                return 0;

            if (o1.getType() == BundleTypes.PROVENANCE && o2.getType() == BundleTypes.PROVENANCE)
                return 0;


            if (o1.getType() == BundleTypes.CACHE && o2.getType() == BundleTypes.PERSISTENT)
                return -1;
            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.CACHE)
                return 1;

            if (o1.getType() == BundleTypes.CACHE && o2.getType() == BundleTypes.PROVENANCE)
                return -1;
            if (o1.getType() == BundleTypes.PROVENANCE && o2.getType() == BundleTypes.CACHE)
                return 1;

            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.PROVENANCE)
                return -1;
            if (o1.getType() == BundleTypes.PROVENANCE && o2.getType() == BundleTypes.PERSISTENT)
                return 1;

            return 0;
        };
    }

    private boolean isLocalNode(IGUID nodeGUID) {

        IGUID localNodeGUID = SOSLocalNode.settings.getNodeGUID();
        return localNodeGUID.equals(nodeGUID);
    }

    @Override
    public void persist(IFile file) throws IOException {
        if (!file.exists()) {
            try {
                file.persist();
            } catch (PersistenceException e) {
                throw new IOException(e);
            }
        }

        FileOutputStream ostream = new FileOutputStream(file.toFile());
        ObjectOutputStream p = new ObjectOutputStream(ostream);

        p.writeObject(this);
        p.flush();
        ostream.close();
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(index.size());
        for (IGUID key : index.keySet()) {

            out.writeUTF(key.toMultiHash());

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
