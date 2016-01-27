package uk.ac.standrews.cs.sos.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.lucene.queryparser.classic.ParseException;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;
import uk.ac.standrews.cs.sos.configurations.SeaConfiguration;
import uk.ac.standrews.cs.sos.deserializers.AtomManifestDeserializer;
import uk.ac.standrews.cs.sos.deserializers.ContentDeserializer;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AssetManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.AtomManifest;
import uk.ac.standrews.cs.sos.model.implementations.components.manifests.CompoundManifest;
import uk.ac.standrews.cs.sos.model.implementations.utils.Content;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUID;
import uk.ac.standrews.cs.sos.model.implementations.utils.GUIDsha1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Basic implementation of in-memory storage using Redis.
 * Redis stores data as strings (in C strings are sequences of bytes).
 * Thus when storing manifests in Redis, we convert information to Java strings.
 * When querying Redis, we get the information back in strings and not in objects.
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RedisCache extends CommonCache {

    public static final int REDIS_PORT = 6380;
    private static RedisCache instance = null;
    private Jedis redis;
    private static RedisServer server;
    private static Gson gson;
    private static SeaConfiguration instanceConfiguration;

    /**
     * Singleton pattern.
     *
     * Start a Redis server and return a client instance back.
     *
     * @return
     */
    public static MemCache getInstance(SeaConfiguration configuration) throws IOException {
        if(instance == null) {
            instanceConfiguration = configuration;
            new File(configuration.getIndexPath()).mkdirs();

            server = RedisServer.builder()
                .port(REDIS_PORT)
                .setting("dbfilename dump.rdb") // TODO - use config settings!
                .setting("dir " + configuration.getIndexPath())
                .build();
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
        redis.save();
        redis.quit();
        instance = null;
        server.stop();
    }

    @Override
    public void flushDB() {
        redis.flushDB();
    }

    private RedisCache() {
        // If needed, make a pool to have multiple instances access this.
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

    // can be used for invariant guid, or for content guid
    @Override
    public Collection<GUID> getVersions(GUID guid) {
        Collection<String> cachedVersions = redis.smembers(getGUIDRedisKey(guid, RedisKeys.HANDLE_VERSION));
        return convertToGUIDs(cachedVersions);
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
    public Collection<GUID> getMetadata(GUID manifestGUID) {
        Collection<String> cachedMetadata = redis.smembers(getGUIDRedisKey(manifestGUID, RedisKeys.HANDLE_METADATA));
        return convertToGUIDs(cachedMetadata);
    }

    @Override
    public Collection<GUID> getMetaLabelMatches(String label) {
        Collection<String> cachedGUIDs = redis.smembers(getMetaRedisKey(label, RedisKeys.HANDLE_META_LABEL));
        return convertToGUIDs(cachedGUIDs);
    }

    @Override
    public Collection<GUID> getManifestsOfType(String type) throws IOException, ParseException {
        Collection<String> cachedGUIDs = redis.smembers(getTypeRedisKey(type));

        Collection<GUID> guids = new ArrayList<GUID>();
        for(String cachedGUID:cachedGUIDs) {
            GUID guid = new GUIDsha1(cachedGUID);
            guids.add(guid);
        }

        return guids;
    }

    @Override
    public SeaConfiguration getConfiguration() {
        return instanceConfiguration;
    }

    @Override
    protected void addAtomManifest(AtomManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String type = manifest.getManifestType();
        redis.sadd(getTypeRedisKey(type), contentGUID.toString());
    }

    @Override
    protected void addCompoundManifest(CompoundManifest manifest) {
        GUID contentGUID = manifest.getContentGUID();
        String type = manifest.getManifestType();
        redis.sadd(getTypeRedisKey(type), contentGUID.toString());

        Collection<Content> contents = manifest.getContents();
        for(Content content:contents) {
            redis.sadd(getGUIDRedisKey(contentGUID, RedisKeys.HANDLE_CONTENT), content.toString());
            addMetaContent(content);
        }
    }

    @Override
    protected void addAssetManifest(AssetManifest manifest) {
        GUID invariant = manifest.getInvariantGUID();
        GUID version = manifest.getVersionGUID();
        Collection<GUID> metadata = manifest.getMetadata();
        String type = manifest.getManifestType();

        // Avoid to have two versions mapping to different assets.
        if (redis.exists(getGUIDRedisKey(version, RedisKeys.HANDLE_INVARIANT)))
            return;

        redis.sadd(getTypeRedisKey(type), invariant.toString());
        redis.sadd(getGUIDRedisKey(invariant, RedisKeys.HANDLE_VERSION), version.toString());
        redis.set(getGUIDRedisKey(version, RedisKeys.HANDLE_INVARIANT), invariant.toString());

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

    private String getTypeRedisKey(String key) {
        return RedisKeys.MAIN_HANDLE_GUID+":"+key;
    }

    private String getMetaRedisKey(String value, String key) {
        return RedisKeys.MAIN_HANDLE_META+":"+value+":"+key;
    }

    private Collection<GUID> convertToGUIDs(Collection<String> guidsInString) {
        Collection<GUID> guids = new ArrayList<GUID>();
        for(String guidString:guidsInString) {
            GUID guid = new GUIDsha1(guidString);
            guids.add(guid);
        }
        return guids;
    }
}
