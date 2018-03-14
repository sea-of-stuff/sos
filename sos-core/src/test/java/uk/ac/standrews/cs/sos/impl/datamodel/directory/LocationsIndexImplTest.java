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
package uk.ac.standrews.cs.sos.impl.datamodel.directory;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.CastoreBuilder;
import uk.ac.standrews.cs.castore.CastoreFactory;
import uk.ac.standrews.cs.castore.CastoreType;
import uk.ac.standrews.cs.castore.exceptions.StorageException;
import uk.ac.standrews.cs.castore.interfaces.IDirectory;
import uk.ac.standrews.cs.castore.interfaces.IFile;
import uk.ac.standrews.cs.castore.interfaces.IStorage;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.SettingsConfiguration;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.exceptions.IgnoreException;
import uk.ac.standrews.cs.sos.exceptions.protocol.SOSProtocolException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.SOSLocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.ExternalLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.LocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.bundles.PersistLocationBundle;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.sos.SOSURLProtocol;
import uk.ac.standrews.cs.sos.impl.node.LocalStorage;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.interfaces.manifests.LocationsIndex;
import uk.ac.standrews.cs.sos.utils.Persistence;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Iterator;

import static org.testng.Assert.*;
import static uk.ac.standrews.cs.sos.constants.Internals.GUID_ALGORITHM;
import static uk.ac.standrews.cs.sos.constants.Internals.LOCATIONS_INDEX_FILE;
import static uk.ac.standrews.cs.sos.constants.Paths.TEST_RESOURCES_PATH;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class LocationsIndexImplTest {

    @Test
    public void addGetLocationsTest() throws URISyntaxException {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));

        locationsIndex.addLocation(guid, locationBundle);

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
    }

    @Test
    public void noLocationsTest() {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void persistLocationsIndexTest() throws StorageException, DataStorageException, URISyntaxException, IOException, ClassNotFoundException, IgnoreException {

        String root = System.getProperty("user.home") + "/sos/";

        CastoreBuilder castoreBuilder = new CastoreBuilder()
                .setType(CastoreType.LOCAL)
                .setRoot(root);
        IStorage stor = CastoreFactory.createStorage(castoreBuilder);
        LocalStorage localStorage = new LocalStorage(stor);

        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));

        locationsIndex.addLocation(guid, locationBundle);

        IDirectory cachesDir = localStorage.getNodeDirectory();
        IFile file = localStorage.createFile(cachesDir, LOCATIONS_INDEX_FILE);
        locationsIndex.persist(file);


        LocationsIndex locationsIndexPersisted = (LocationsIndex) Persistence.load(file);
        assertNotNull(locationsIndexPersisted);

        Iterator<LocationBundle> it = locationsIndexPersisted.findLocations(guid).iterator();
        assertTrue(it.hasNext());
        assertEquals(locationBundle, it.next());
    }

    @Test
    public void iteratorOrderingTest() throws URISyntaxException {
        LocationsIndex locationsIndex = new LocationsIndexImpl();

        IGUID guid = GUIDFactory.generateRandomGUID(GUID_ALGORITHM);
        LocationBundle locationBundle = new CacheLocationBundle(new URILocation("http://example.org/resource"));
        LocationBundle locationBundlePersist = new PersistLocationBundle(new URILocation("http://example.org/persist"));

        locationsIndex.addLocation(guid, locationBundlePersist);
        locationsIndex.addLocation(guid, locationBundle);

        Iterator<LocationBundle> it = locationsIndex.findLocations(guid).iterator();
        assertTrue(it.hasNext());

        LocationBundle indexedLocationBundle = it.next();
        assertEquals(indexedLocationBundle, locationBundle);

        LocationBundle indexedLocationBundle2 = it.next();
        assertEquals(indexedLocationBundle2, locationBundlePersist);
    }

    @Test
    public void comparatorTest() throws URISyntaxException, MalformedURLException, SOSProtocolException, ConfigurationException {
        SettingsConfiguration.Settings settings = new SettingsConfiguration(new File(TEST_RESOURCES_PATH + "configurations/config_test.json")).getSettingsObj();
        SOSLocalNode.settings = settings;
        SOSLocalNode.settings.setGuid(GUIDFactory.generateRandomGUID(GUID_ALGORITHM).toMultiHash());
        SOSURLProtocol.getInstance().register(null, null); // Local storage is not needed for this set of tests

        Comparator<LocationBundle> comparator = LocationsIndexImpl.comparator(); // The comparator returns 0 if and only if the locations are the same.

        assertEquals(comparator.compare(new ExternalLocationBundle(new URILocation("http://example.org/resource")), new ExternalLocationBundle(new URILocation("http://example.org/resource"))), 0);
        assertEquals(comparator.compare(new ExternalLocationBundle(new URILocation("http://example.org/resource")), new ExternalLocationBundle(new URILocation("http://example.org/other"))), -1);
        assertEquals(comparator.compare(new ExternalLocationBundle(new URILocation("http://example.org/other")), new ExternalLocationBundle(new URILocation("http://example.org/resource"))), -1);

        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/other")), new ExternalLocationBundle(new URILocation("http://example.org/resource"))), -1);
        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/other")), new CacheLocationBundle(new URILocation("http://example.org/resource"))), -1);
        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/resource")), new CacheLocationBundle(new URILocation("http://example.org/resource"))), 0);
        assertEquals(comparator.compare(new ExternalLocationBundle(new URILocation("http://example.org/resource")), new CacheLocationBundle(new URILocation("http://example.org/resource"))), 0); // PREFER LOCATION OVER TYPE
        assertEquals(comparator.compare(new PersistLocationBundle(new URILocation("http://example.org/other")), new ExternalLocationBundle(new URILocation("http://example.org/resource"))), -1);
        assertEquals(comparator.compare(new PersistLocationBundle(new URILocation("http://example.org/other")), new PersistLocationBundle(new URILocation("http://example.org/resource"))), -1);
        assertEquals(comparator.compare(new PersistLocationBundle(new URILocation("http://example.org/resource")), new PersistLocationBundle(new URILocation("http://example.org/resource"))), 0);
        assertEquals(comparator.compare(new ExternalLocationBundle(new URILocation("http://example.org/resource")), new PersistLocationBundle(new URILocation("http://example.org/resource"))), 0); // PREFER LOCATION OVER TYPE
        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/other")), new PersistLocationBundle(new URILocation("http://example.org/resource"))), -1);

        assertEquals(comparator.compare(new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);
        assertEquals(comparator.compare(new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);
        assertEquals(comparator.compare(new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), 1);
        assertEquals(comparator.compare(new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);

        assertEquals(comparator.compare(new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new ExternalLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);
        assertEquals(comparator.compare(new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new ExternalLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);
        assertEquals(comparator.compare(new ExternalLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new PersistLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), 1);
        assertEquals(comparator.compare(new ExternalLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), 1);

        // Ignore URILocation vs SOSLocation as the SOSLocation is not this node
        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/other")),
                new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), -1);
        assertEquals(comparator.compare(new CacheLocationBundle(new SOSLocation(GUIDFactory.generateRandomGUID(GUID_ALGORITHM), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new CacheLocationBundle(new URILocation("http://example.org/other"))), -1);

        assertEquals(comparator.compare(new CacheLocationBundle(new URILocation("http://example.org/other")),
                new CacheLocationBundle(new SOSLocation(settings.guid(), GUIDFactory.generateRandomGUID(GUID_ALGORITHM)))), 1);
        assertEquals(comparator.compare(new CacheLocationBundle(new SOSLocation(settings.guid(), GUIDFactory.generateRandomGUID(GUID_ALGORITHM))),
                new CacheLocationBundle(new URILocation("http://example.org/other"))), -1);
    }

}
