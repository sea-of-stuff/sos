package uk.ac.standrews.cs.sos.model.cache;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.SourceLocationException;
import uk.ac.standrews.cs.sos.exceptions.storage.DataStorageException;
import uk.ac.standrews.cs.sos.interfaces.storage.SOSFile;
import uk.ac.standrews.cs.sos.model.SeaConfiguration;
import uk.ac.standrews.cs.sos.model.locations.URILocation;
import uk.ac.standrews.cs.sos.model.locations.bundles.CacheLocationBundle;
import uk.ac.standrews.cs.utils.FileHelper;

import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StreamCache extends CommonCache implements Cache {

    private InputStream inputStream;
    private CacheLocationBundle cacheLocationBundle;

    public StreamCache(SeaConfiguration configuration, InputStream inputStream) {
        super(configuration);
        this.inputStream = inputStream;
    }

    @Override
    public IGUID cache() throws DataStorageException {
            IGUID guid;
            if (inputStream == null) {
                throw new DataStorageException();
            }

            try {
                IGUID tmpGUID = GUIDFactory.generateRandomGUID();
                storeData(inputStream, tmpGUID);

                SOSFile tmpCachedLocation = getAtomCachedLocation(tmpGUID);
                guid = generateGUID(new URILocation(tmpCachedLocation.getPathname()));

                SOSFile cachedLocation = getAtomCachedLocation(guid);
                FileHelper.renameFile(tmpCachedLocation.getPathname(), cachedLocation.getPathname());
                cacheLocationBundle = getCacheBundle(guid);

            } catch (GUIDGenerationException | SourceLocationException | URISyntaxException e) {
                throw new DataStorageException();
            }

            return guid;
        }

    @Override
    public CacheLocationBundle getCacheLocationBundle() {
        return cacheLocationBundle;
    }
}
