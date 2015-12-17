package sos.managers;

import redis.clients.jedis.Jedis;
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

    /**
     * Singleton pattern.
     *
     * @return
     */
    public static MemCache getInstance() {
        if(instance == null) {
            instance = new RedisCache();
        }
        return instance;
    }

    /**
     * The Cache must be killed to avoid memory leaks.
     */
    @Override
    public void killInstance() {
        redis.quit();
        instance = null;
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
        addContentGUID(manifest);

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
    public String getManifestType(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_TYPE));
    }

    @Override
    public Collection<String> getLocations(GUID manifestGUID) {
        return redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_LOCATION));
    }

    // can be used for incarnation guid, or for content guid
    @Override
    public Set<String> getManifests(GUID guid) {
        return redis.smembers(getGUIDRedisKey(guid, RedisKeys.HANDLE_MANIFEST));
    }

    @Override
    public String getSignature(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_SIGNATURE));
    }

    public String getContent(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_CONTENT));
    }

    @Override
    public Set<String> getContents(GUID contentGUID) {
        return redis.smembers(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENTS));
    }

    @Override
    public String getIncarnation(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_INCARNATION));
    }

    @Override
    public Set<String> getPrevs(GUID manifestGUID) {
        return redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_PREVS));
    }

    @Override
    public String getMetadata(GUID manifestGUID) {
        return redis.get(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_METADATA));
    }

    @Override
    public Set<String> getMetaValueMatches(String value) {
        return redis.smembers(getMetaRedisKey(value, RedisKeys.HANDLE_META_VAL));
    }

    @Override
    public Set<String> getMetaTypeMatches(String type) {
        return redis.smembers(getMetaRedisKey(type, RedisKeys.HANDLE_META_TYPE));
    }

    // manifestGUID --> manifestType
    private void addType(Manifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        String type = manifest.getManifestType();
        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_TYPE), type);
    }

    // bi-directional mapping
    // contentGUID --> [manifestGUID] - there can be multiple manifests for same content
    // manifestGUID --> contentGUID
    private void addContentGUID(Manifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_MANIFEST), manifestGUID.toString());
        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_CONTENT), contentGUID.toString());
    }

    // manifestGUID --> [locations]
    private void addAtomManifest(AtomManifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        Collection<Location> locations = manifest.getLocations();

        for(Location location:locations) {
            redis.sadd(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_LOCATION), location.toString());
        }
    }

    // manifestGUID --> signature
    // manifestGUID --> [contentGUIDs]
    private void addCompoundManifest(CompoundManifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        String signature = manifest.getSignature();
        Collection<Content> contents = manifest.getContents();

        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_SIGNATURE), signature);
        for(Content content:contents) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENTS), content.toString());
            addMetaContent(content);
        }
    }

    // bi-directional mapping for incarnation
    // manifestGUID -->incarnation
    // incarnation --> [manifestGUID] - there can be multiple manifests for this incarnation
    // manifestGUID --> signature
    // manifestGUID --> [prevs]
    // manifestGUID --> metadata
    private void addAssetManifest(AssetManifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID incarnation = manifest.getAssetGUID();
        Collection<GUID> prevs = manifest.getPreviousManifests();
        GUID metadata = manifest.getMetadataGUID();
        String signature = manifest.getSignature();

        redis.sadd(getGUIDRedisKey(incarnation, RedisKeys.HANDLE_MANIFEST), manifestGUID.toString());
        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_INCARNATION), incarnation.toString());

        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_SIGNATURE), signature);
        redis.set(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_METADATA), metadata.toString());
        for(GUID prev:prevs) {
            redis.sadd(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_PREVS), prev.toString());
        }

        addMetaContent(manifest.getContent());
    }

    private void addMetaContent(Content content) {
        if (content.typeAndValueExist()) {
            redis.sadd(getMetaRedisKey(content.getType(), RedisKeys.HANDLE_META_TYPE), content.getGUID().toString());
            redis.sadd(getMetaRedisKey(content.getValue(), RedisKeys.HANDLE_META_VAL), content.getGUID().toString());
        }
    }

    private String getGUIDRedisKey(GUID guid, String key) {
        return RedisKeys.MAIN_HANDLE_GUID+":"+guid.toString()+":"+key;
    }

    private String getMetaRedisKey(String value, String key) {
        return RedisKeys.MAIN_HANDLE_META+":"+value+":"+key;
    }
}
