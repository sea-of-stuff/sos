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
        redis = new Jedis("localhost", 6379); // TODO - have a pool if we want to have multiple instances access this.
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

    public String getSignature(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "signature"));
    }

    public String getContentGUID(GUID manifestGUID) {
        return redis.get(getRedisKey(manifestGUID, "content"));
    }

    public String getManifestGUID(GUID contentGUID) {
        return redis.get(getRedisKey(contentGUID, "manifest"));
    }

    public Set<String> getContents(GUID contentGUID) {
        return redis.smembers(getRedisKey(contentGUID, "contents"));
    }

    // manifestGUID --> manifestType
    private void addType(Manifest manifest) {
        GUID guid = manifest.getManifestGUID();
        String type = manifest.getManifestType();
        redis.set(getRedisKey(guid, "type"), type);
    }

    // bi-directional mapping
    // contentGUID --> manifestGUID
    // manifestGUID --> contentGUID
    private void addContentGUID(Manifest manifest) {
        GUID manifestGUID = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        redis.set(getRedisKey(contentGUID, "manifest"), manifestGUID.toString());
        redis.set(getRedisKey(manifestGUID, "content"), contentGUID.toString());
    }

    // manifestGUID --> [locations]
    private void addAtomManifest(AtomManifest manifest) {
        GUID guid = manifest.getManifestGUID();
        Collection<Location> locations = manifest.getLocations();

        for(Location location:locations) {
            redis.sadd(getRedisKey(guid, "location"), location.toString());
        }
    }

    // manifestGUID --> signature
    // manifestGUID --> [contentGUIDs]
    private void addCompoundManifest(CompoundManifest manifest) {
        GUID guid = manifest.getManifestGUID();
        GUID contentGUID = manifest.getContentGUID();
        String signature = manifest.getSignature();
        Collection<Content> contents = manifest.getContents();

        redis.set(getRedisKey(guid, "signature"), signature);
        for(Content content:contents) {
            redis.sadd(getRedisKey(contentGUID, "contents"), content.toString());
        }
    }

    // manifestGUID --> signature
    // manifest
    private void addAssetManifest(AssetManifest manifest) {

    }

    private String getRedisKey(GUID guid, String key) {
        return "guid:"+guid.toString()+":"+key;
    }
}
