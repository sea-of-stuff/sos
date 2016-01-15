package sos.managers;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;
import sos.exceptions.UnknownManifestTypeException;
import sos.model.implementations.components.manifests.AssetManifest;
import sos.model.implementations.components.manifests.AtomManifest;
import sos.model.implementations.components.manifests.CompoundManifest;
import sos.model.implementations.components.manifests.ManifestConstants;
import sos.model.implementations.utils.Content;
import sos.model.implementations.utils.GUID;
import sos.model.implementations.utils.Location;
import sos.model.interfaces.components.Manifest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
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

    @Override
    public void addManifest(Manifest manifest) throws UnknownManifestTypeException {
        addType(manifest);

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
    public String getManifestType(GUID manifestGUID) { // TODO - rename manifestGUID to guid and do so for all other methods
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_TYPE));
    }

    @Override
    public Collection<String> getLocations(GUID manifestGUID) {
        return redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_LOCATION));
    }

    // can be used for invariant guid, or for content guid
    @Override
    public Set<String> getManifests(GUID guid) {
        return redis.smembers(getGUIDRedisKey(guid, RedisKeys.HANDLE_MANIFEST));
    }

    @Override
    public String getSignature(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_SIGNATURE));
    }

    @Override
    public Set<String> getContents(GUID contentGUID) {
        return redis.smembers(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENT));
    }

    @Override
    public String getInvariant(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_INVARIANT));
    }

    @Override
    public Set<String> getPrevs(GUID manifestGUID) {
        return redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_PREVS));
    }

    @Override
    public Set<String> getMetadata(GUID manifestGUID) {
        return redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_METADATA));
    }

    @Override
    public Set<String> getMetaLabelMatches(String value) {
        return redis.smembers(getMetaRedisKey(value, RedisKeys.HANDLE_META_LABEL));
    }

    // contentGUID --> manifestType
    private void addType(Manifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String type = manifest.getManifestType();
        redis.set(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_TYPE), type);
    }

    // contentGUID --> [locations]
    private void addAtomManifest(AtomManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        Collection<Location> locations = manifest.getLocations();

        for(Location location:locations) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_LOCATION), location.toString());
        }
    }

    // contentGUID --> signature
    // contentGUID --> [contentGUIDs]
    private void addCompoundManifest(CompoundManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String signature = manifest.getSignature();
        Collection<Content> contents = manifest.getContents();

        redis.set(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_SIGNATURE), signature);
        for(Content content:contents) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENT), content.toString());
            addMetaContent(content);
        }
    }

    // bi-directional mapping for invariant
    //  versionGUID -->invariant
    //  invariant --> [manifestGUID] - there can be multiple manifests for this invariant
    // versionGUID --> contentGUID
    // versionGUID --> signature
    // versionGUID --> [prevs]
    // versionGUID --> metadata
    private void addAssetManifest(AssetManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        GUID invariant = manifest.getInvariantGUID();
        GUID version = manifest.getVersionGUID();
        Collection<GUID> prevs = manifest.getPreviousManifests();
        Collection<GUID> metadata = manifest.getMetadataGUID();
        String signature = manifest.getSignature();

        redis.sadd(getGUIDRedisKey(invariant, RedisKeys.HANDLE_MANIFEST), version.toString());
        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_INVARIANT), invariant.toString());

        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_CONTENT), contentGUID.toString());
        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_SIGNATURE), signature);

        for(GUID prev:prevs) {
            redis.sadd(getGUIDRedisKey(version, RedisKeys.HANDLE_PREVS), prev.toString());
        }
        for(GUID meta:metadata) {
            redis.sadd(getGUIDRedisKey(version, RedisKeys.HANDLE_METADATA), meta.toString());
        }

        addMetaContent(manifest.getContent());
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
