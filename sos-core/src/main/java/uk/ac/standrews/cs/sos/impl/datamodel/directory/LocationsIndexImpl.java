package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import uk.ac.standrews.cs.castore.exceptions.PersistenceException;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.BundleTypes;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.impl.utils.LRU_GUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImpl implements LocationsIndex {

    private transient HashMap<IGUID, PriorityQueue<LocationBundle>> index;
    private transient LRU_GUID lru;

    private static final long serialVersionUID = 1L;
    public LocationsIndexImpl() {
        index = new HashMap<>();
        lru = new LRU_GUID();
    }

    @Override
    public void addLocation(IGUID guid, LocationBundle locationBundle) {

        IGUID guidToRemove = lru.applyLRU(guid);
        if (guidToRemove != null && !guidToRemove.isInvalid()) {
            index.remove(guidToRemove);
        }

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
    public Queue<LocationBundle> findLocations(IGUID guid) {

        lru.applyReadLRU(guid);

        if (index.containsKey(guid)) {
            return index.get(guid);
        }

        return new PriorityQueue<>();
    }

    /**
     * Order priority:
     * local node data first (no matter if cache, persistent or external)
     * cache
     * persistent
     * external
     *
     * @return comparator value
     */
    public static Comparator<LocationBundle> comparator() {
        return (o1, o2) -> {

            if (o1.getLocation().equals(o2.getLocation())) return 0;

            if (o1.getLocation() instanceof SOSLocation && o2.getLocation() instanceof SOSLocation) {

                SOSLocation s1 = (SOSLocation) o1.getLocation();
                SOSLocation s2 = (SOSLocation) o2.getLocation();

                if (isLocalNode(s1.getMachineID()) && isLocalNode(s2.getMachineID())) return -1; // Must still be able to distinguish the two locations
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
                return -1; // Must still be able to distinguish the two locations

            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.PERSISTENT)
                return -1; // Must still be able to distinguish the two locations

            if (o1.getType() == BundleTypes.EXTERNAL && o2.getType() == BundleTypes.EXTERNAL)
                return -1; // Must still be able to distinguish the two locations


            if (o1.getType() == BundleTypes.CACHE && o2.getType() == BundleTypes.PERSISTENT)
                return -1;
            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.CACHE)
                return 1;

            if (o1.getType() == BundleTypes.CACHE && o2.getType() == BundleTypes.EXTERNAL)
                return -1;
            if (o1.getType() == BundleTypes.EXTERNAL && o2.getType() == BundleTypes.CACHE)
                return 1;

            if (o1.getType() == BundleTypes.PERSISTENT && o2.getType() == BundleTypes.EXTERNAL)
                return -1;
            if (o1.getType() == BundleTypes.EXTERNAL && o2.getType() == BundleTypes.PERSISTENT)
                return 1;

            return 0;
        };
    }

    private static boolean isLocalNode(IGUID nodeGUID) {

        IGUID localNodeGUID = SOSLocalNode.settings.guid();
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

        try (FileOutputStream ostream = new FileOutputStream(file.toFile());
             ObjectOutputStream p = new ObjectOutputStream(ostream)) {

            p.writeObject(this);
            p.flush();
        }
    }

    @Override
    public void deleteLocation(IGUID node, IGUID guid) {

        if (index.containsKey(guid)) {
            PriorityQueue<LocationBundle> locationBundles = index.get(guid);

            Iterator<LocationBundle> iterator = locationBundles.iterator();
            while(iterator.hasNext()) {
                LocationBundle bundle = iterator.next();

                if (bundle.getLocation() instanceof SOSLocation) {
                    SOSLocation location = (SOSLocation) bundle.getLocation();

                    if (location.getMachineID().equals(node) && location.getEntityID().equals(guid)) {
                        iterator.remove();
                        break;
                    }
                }

            }
        }
    }

    @Override
    public void clear() {

        index.clear();
        lru.clear();
    }

    // This method defines how the cache is serialised
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        int index_size = index.size();
        if (index_size > 0) {
            out.writeInt(index_size);

            // Store entries as ordered in the LRU
            ConcurrentLinkedQueue<IGUID> lruQueue = new ConcurrentLinkedQueue<>(lru.getQueue());
            IGUID key;
            while ((key = lruQueue.poll()) != null) {

                PriorityQueue<LocationBundle> values = index.get(key);
                if (values != null) {
                    int numberOfLocations = values.size();

                    out.writeInt(numberOfLocations);
                    if (numberOfLocations > 0) {
                        out.writeUTF(key.toMultiHash());

                        for (LocationBundle bundle : findLocations(key)) {
                            out.writeUTF(bundle.toString());
                        }
                    }
                }
            }

        }
    }

    // This method defines how the cache is de-serialised
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        lru = new LRU_GUID();

        int indexSize = in.readInt();
        index = new HashMap<>();
        for(int i = 0; i < indexSize; i++) {
            try {
                int numberOfLocations = in.readInt();
                if (numberOfLocations == 0) continue;

                IGUID key = GUIDFactory.recreateGUID(in.readUTF());

                for (int j = 0; j < numberOfLocations; j++) {
                    LocationBundle bundle = JSONHelper.jsonObjMapper().readValue(in.readUTF(), LocationBundle.class);
                    addLocation(key, bundle);
                }

            } catch (GUIDGenerationException e) {
                throw new IOException();
            }

        }
    }
}
