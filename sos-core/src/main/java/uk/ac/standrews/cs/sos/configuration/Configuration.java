package uk.ac.standrews.cs.sos.configuration;

import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Configuration {

    String filename;
    Properties properties;

    /**
     * Create a configuration using the specified file (must be accessibly locally)
     * @param filename
     */
    public Configuration(String filename) {
        this.filename = filename;
        properties = new Properties();

        try (InputStream inputStream =
                     new FileInputStream(filename)) {

            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO - check what properties were loaded. Generate what it can be generated.
        // Throw exception is something is missing and cannot be generated
        // things that we can generate:
        // node id
        // default node port?

        String nodeGUID = getPropertyFromConfig(PropertyKeys.NODE_GUID);
        if (nodeGUID == null || nodeGUID.isEmpty()) {
            IGUID guid = GUIDFactory.generateRandomGUID();
            try {
                setProperty(PropertyKeys.NODE_GUID, guid.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param property Name of the properties used in the config.properties file
     * @return Value of the Property
     */
    public String getPropertyFromConfig(String property) {
        return properties.getProperty(property);
    }

    /**
     * Creates/Updates the given key property with the specified value
     * @param key
     * @param value
     * @throws IOException
     */
    public void setProperty(String key, String value) throws IOException {
        try (FileOutputStream out =
                     new FileOutputStream(filename)) {

            properties.setProperty(key, value);
            properties.store(out, null);
        }
    }
}
