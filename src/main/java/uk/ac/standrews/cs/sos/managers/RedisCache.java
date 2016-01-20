package uk.ac.standrews.cs.sos.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.ac.standrews.cs.sos.deserializers.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.ContentDeserializer;
import uk.ac.standrews.cs.sos.exceptions.UnknownManifestTypeException;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.ManifestConstants;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;
import uk.ac.standrews.cs.sos.model.implementations.utils.Location;
import uk.ac.standrews.cs.sos.model.interfaces.components.Manifest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Basic implementation of in-memory cache using Redis.
 * Redis, mainly, stores data as strings (in C strings are sequences of bytes).
 * Thus when storing manifests in Redis, we convert information to Java strings.
 * When querying Redis, we get the information back in strings and not in objects.
 * In this implementation, the information is not parsed back to objects (naive).
 *
 * Dev Notes
 * convert redis stored data from strings to proper objects.
 * Consider storing data as JSON and then use a deserialiser for the object.
 * Consider using hashes to store structured data
 * Consider marshalling the java objects
 * @see: http://stackoverflow.com/questions/28935229/best-way-to-store-a-list-of-java-objects-in-redis
 * @see: http://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet
 *
 * look also at serialise/deserialise methods - http://commons.apache.org/proper/commons-lang/javadocs/api-release/index.html
 *
 * Reasons for not using hashes:
 * -> cannot store sets inside hashes
 * -> lose flexibility over storing individual keys on their own
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCache extends MemCache {

    public static final int REDIS_PORT = 6380;
    private static RedisCache instance = null;
    private Jedis redis;
    private static RedisServer server;
    private static Gson gson;

    /**
     * Singleton pattern.
     *
     * Start a Redis server and return a client instance back.
     *
     * @return
     */
    public static MemCache getInstance() throws IOException {
        if(instance == null) {
            server = new RedisServer(REDIS_PORT);
            server.start();
            instance = new RedisCache();

            configureGson();
        }
        return instance;
    }

    /**
     * The Cache must be killed manually to avoid memory leaks.
     *
     * The server instance is also shutdown.
     */
    @Override
    public void killInstance() {
        redis.quit();
        instance = null;
        server.stop();
    }

    @Override
    public void flushDB() {
        redis.flushDB();
    }

    private RedisCache() {
        // XXX - maybe have a pool if we want to have multiple instances access this.
        redis = new Jedis("localhost", REDIS_PORT);
    }

    private static void configureGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        registerGSonTypeAdapters(gsonBuilder);
        gson = gsonBuilder.create();
    }

    private static void registerGSonTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(Content.class, new ContentDeserializer());
        builder.registerTypeAdapter(AtomManifest.class, new AtomManifestDeserializer());
    }

    @Override
    public void addManifest(Manifest manifest) throws UnknownManifestTypeException {
        String type = manifest.getManifestType();
        switch(type) {
            case ManifestConstants.ATOM:
                addAtomManifest((AtomManifest) manifest);
                break;
            case ManifestConstants.COMPOUND:
                addCompoundManifest((CompoundManifest) manifest);
                break;
            case ManifestConstants.ASSET:
                addAssetManifest((AssetManifest) manifest);
                break;
            default:
                throw new UnknownManifestTypeException();
        }
    }

    public void conjunction(String... keys) {
        redis.sinter(keys);
        throw new NotImplementedException();
    }

    public void disjunction(String... keys) {
        redis.sunion(keys);
        throw new NotImplementedException();
    }

    @Override
    public String getManifestType(GUID guid) {
        return redis.get(getGUIDRedisKey(guid, RedisKeys.HANDLE_TYPE));
    }

    @Override
    public Collection<Location> getLocations(GUID manifestGUID) throws MalformedURLException {
        Collection<String> cachedLocations =  redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_LOCATION));

        Collection<Location> locations = new ArrayList<Location>();
        for(String cachedLocation:cachedLocations) {
            locations.add(new Location(cachedLocation));
        }

        return locations;
    }

    // can be used for invariant guid, or for content guid
    @Override
    public Collection<Manifest> getManifests(GUID guid) {
        //return redis.smembers(getGUIDRedisKey(guid, RedisKeys.HANDLE_VERSION)); -- TODO - this should be renamed to get versions
        return null;
    }

    @Override
    public String getSignature(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_SIGNATURE));
    }

    @Override
    public Collection<Content> getContents(GUID contentGUID) {
        Collection<String> cachedContents = redis.smembers(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENT));

        Collection<Content> contents = new ArrayList<Content>();
        for(String cachedContent:cachedContents) {
            Content content = gson.fromJson(cachedContent, Content.class);
            contents.add(content);
        }

        return contents;
    }

    @Override
    public GUID getInvariant(GUID manifestGUID) {
        String sGUID = redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_INVARIANT));
        return new GUIDsha1(sGUID);
    }

    @Override
    public Collection<GUID> getPrevs(GUID manifestGUID) {
        Collection<String> cachedPrevious = redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_PREVS));

        Collection<GUID> previous = new ArrayList<GUID>();
        for(String cachedPrev:cachedPrevious) {
            GUID guid = new GUIDsha1(cachedPrev);
            previous.add(guid);
        }

        return previous;
    }

    @Override
    public Collection<GUID> getMetadata(GUID manifestGUID) {
        Collection<String> cachedMetadata = redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_METADATA));

        Collection<GUID> metadata = new ArrayList<GUID>();
        for(String meta:cachedMetadata) {
            GUID guid = new GUIDsha1(meta);
            metadata.add(guid);
        }

        return metadata;
    }

    @Override
    public Set<String> getMetaLabelMatches(String value) {
        return redis.smembers(getMetaRedisKey(value, RedisKeys.HANDLE_META_LABEL));
    }

    // contentGUID --> [locations]
    private void addAtomManifest(AtomManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String type = manifest.getManifestType();
        redis.set(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_TYPE), type);

        Collection<Location> locations = manifest.getLocations();
        for(Location location:locations) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_LOCATION), location.toString());
        }
    }

    // contentGUID --> signature
    // contentGUID --> [contentGUIDs]
    private void addCompoundManifest(CompoundManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String type = manifest.getManifestType();
        redis.set(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_TYPE), type);

        String signature = manifest.getSignature();
        redis.set(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_SIGNATURE), signature);

        Collection<Content> contents = manifest.getContents();
        for(Content content:contents) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENT), content.toString());
            addMetaContent(content);
        }
    }

    // TODO - refactor
    // bi-directional mapping for invariant
    //  versionGUID -->invariant
    //  invariant --> [manifestGUID] - there can be multiple manifests for this invariant
    // versionGUID --> contentGUID
    // versionGUID --> signature
    // versionGUID --> [prevs]
    // versionGUID --> metadata
    private void addAssetManifest(AssetManifest manifest) {
        GUID invariant = manifest.getInvariantGUID();
        GUID version = manifest.getVersionGUID();
        Collection<GUID> prevs = manifest.getPreviousManifests();
        Collection<GUID> metadata = manifest.getMetadata();
        String signature = manifest.getSignature();
        Content content = manifest.getContent();

        String type = manifest.getManifestType();
        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_TYPE), type);

        redis.sadd(getGUIDRedisKey(invariant, RedisKeys.HANDLE_VERSION), version.toString());
        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_INVARIANT), invariant.toString());

        redis.sadd(getGUIDRedisKey(version, RedisKeys.HANDLE_CONTENT), content.toString());
        addMetaContent(content);


        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_SIGNATURE), signature);

        if (prevs != null && !prevs.isEmpty()) {
            for (GUID prev : prevs) {
                redis.sadd(getGUIDRedisKey(version, RedisKeys.HANDLE_PREVS), prev.toString());
            }
        }

        if (metadata != null && !metadata.isEmpty()) {
            for (GUID meta : metadata) {
                redis.sadd(getGUIDRedisKey(version, RedisKeys.HANDLE_METADATA), meta.toString());
            }
        }
    }

    private void addMetaContent(Content content) {
        String label = content.getLabel();
        if (label != null && !label.isEmpty()) {
            redis.sadd(getMetaRedisKey(content.getLabel(), RedisKeys.HANDLE_META_LABEL), content.getGUID().toString());
        }
    }

    private String getGUIDRedisKey(GUID guid, String key) {
        return RedisKeys.MAIN_HANDLE_GUID+":"+guid.toString()+":"+key;
    }

    private String getMetaRedisKey(String value, String key) {
        return RedisKeys.MAIN_HANDLE_META+":"+value+":"+key;
    }
}
