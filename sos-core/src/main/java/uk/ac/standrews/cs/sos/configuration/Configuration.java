package uk.ac.standrews.cs.sos.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.sos.exceptions.ConfigurationException;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.File;
import java.io.IOException;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Configuration {

    private File file;
    private JsonNode config;

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     * @param file
     */
    public Configuration(File file) throws ConfigurationException {
        this.file = file;

        try {
            config = JSONHelper.JsonObjMapper().readTree(file);
        } catch (IOException e) {
            throw new ConfigurationException("Unable to read configuration");
        }
    }

    protected String getString(String key) {

        JsonNode retval = getNode(key);
        return retval.textValue();
    }

    protected String getString(JsonNode node, String key) {

        JsonNode retval = getNode(node, key);
        return retval.textValue();
    }

    protected int getInt(String key) {

        JsonNode retval = getNode(key);
        return retval.intValue();
    }

    protected int getInt(JsonNode node, String key) {

        JsonNode retval = getNode(node, key);
        return retval.intValue();
    }

    protected boolean getBoolean(String key) {

        JsonNode retval = getNode(key);
        return retval.booleanValue();
    }

    protected boolean getBoolean(JsonNode node, String key) {

        JsonNode retval = getNode(node, key);
        return retval.booleanValue();
    }

    protected JsonNode getNode(String key) {

        String[] keys = key.split("\\.");

        JsonNode tmp = config;
        for(String k:keys) {
            tmp = tmp.get(k);
        }

        return tmp;
    }

    protected JsonNode getNode(JsonNode node, String key) {

        String[] keys = key.split("\\.");

        JsonNode tmp = node;
        for(String k:keys) {
            tmp = tmp.get(k);
        }

        return tmp;
    }

    protected boolean hasProperty(String key) {

        String[] keys = key.split("\\.");

        JsonNode tmp = config;
        for(String k:keys) {

            if (!tmp.has(k)) return false;

            tmp = tmp.get(k);
        }

        return true;
    }

    protected void setProperty(String key, String value) throws IOException {

        if (config.has(key)) {
            ((ObjectNode) config).put(key, value);
        }

        JSONHelper.JsonObjMapper().writeValue(file, config);
    }

    protected void checkKey(String key) throws ConfigurationException {
        if (!hasProperty(key)) {
            throw new ConfigurationException("No value for key: " + key);
        }
    }
}
