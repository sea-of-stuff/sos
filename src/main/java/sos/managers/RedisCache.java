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

import java.util.Collection;
import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCache extends MemCache {

    private static RedisCache instance = null;
    private Jedis redis;

    public static RedisCache getInstance() {
        if(instance == null) {
            instance = new RedisCache();
        }
        return instance;
    }

    public void killInstance() {
        redis.quit(); // TODO - maybe use close, or both
        instance = null;
    }

    private RedisCache() {
        redis = new Jedis("localhost", 6380); // TODO - have a pool if we want to have multiple instances access this.
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

    @Override
    public GUID getGUIDReference(String match) {
        return null;
    }

    public String getManifestType(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "type"));
    }

    // TODO - convert string-locations to string objects?
    public Set<String> getLocations(GUID manifestGUID) {
        return redis.smembers(getRedisKey(manifestGUID, "location"));
    }

    // can be used for incarnation guid, or for content guid
    public Set<String> getManifests(GUID guid) {
        return redis.smembers(getRedisKey(guid, "manifest"));
    }

    public String getSignature(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "signature"));
    }

    public String getContentGUID(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "content"));
    }

    public Set<String> getContents(GUID contentGUID) {
        return redis.smembers(getRedisKey(contentGUID, "contents"));
    }

    public String getIncarnation(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "incarnation"));
    }

    public Set<String> getPrevs(GUID manifestGUID) {
        return redis.smembers(getRedisKey(manifestGUID, "prevs"));
    }

    public String getMetadata(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "metadata"));
    }

    // manifestGUID --> manifestType
    private void addType(Manifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        String type = manifest.getManifestType();
        redis.set(getRedisKey(manifestGUID, "type"), type);
    }

    // bi-directional mapping
    // contentGUID --> [manifestGUID] - there can be multiple manifests for same content
    // manifestGUID --> contentGUID
    private void addContentGUID(Manifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        redis.sadd(getRedisKey(contentGUID, "manifest"), manifestGUID.toString());
        redis.set(getRedisKey(manifestGUID, "content"), contentGUID.toString());
    }

    // manifestGUID --> [locations]
    private void addAtomManifest(AtomManifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        Collection<Location> locations = manifest.getLocations();

        for(Location location:locations) {
            redis.sadd(getRedisKey(manifestGUID, "location"), location.toString());
        }
    }

    // manifestGUID --> signature
    // manifestGUID --> [contentGUIDs]
    private void addCompoundManifest(CompoundManifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        String signature = manifest.getSignature();
        Collection<Content> contents = manifest.getContents();

        redis.set(getRedisKey(manifestGUID, "signature"), signature);
        for(Content content:contents) {
            redis.sadd(getRedisKey(contentGUID, "contents"), content.toString());
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

        redis.sadd(getRedisKey(incarnation, "manifest"), manifestGUID.toString());
        redis.set(getRedisKey(manifestGUID, "incarnation"), incarnation.toString());

        redis.set(getRedisKey(manifestGUID, "signature"), signature);
        redis.set(getRedisKey(manifestGUID, "metadata"), metadata.toString());
        for(GUID prev:prevs) {
            redis.sadd(getRedisKey(manifestGUID, "prevs"), prev.toString());
        }
    }

    private String getRedisKey(GUID guid, String key) {
        return "guid:"+guid.toString()+":"+key;
    }
}
