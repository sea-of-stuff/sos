package uk.ac.standrews.cs.sos.model.metadata.basic;

import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.model.metadata.AbstractMetadata;
import uk.ac.standrews.cs.storage.interfaces.File;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class BasicMetadata extends AbstractMetadata implements SOSMetadata {

    private HashMap<String, String> metadata;

    public BasicMetadata(String[] ignoreMetadata) {
        super(ignoreMetadata);

        metadata = new HashMap<>();
    }

    public BasicMetadata(File file, String[] ignoreMetadata) {
        super(ignoreMetadata);
        metadata = new HashMap<>();

        try (Stream<String> stream = Files.lines(file.toFile().toPath())) {
            stream.forEach((s) -> {
                String[] pair = s.split("::");
                addProperty(pair[0], pair[1]);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addProperty(String property, String value) {
        metadata.put(property, value);
    }

    @Override
    public String getProperty(String propertyName) {
        return metadata.get(propertyName);
    }

    @Override
    public String[] getAllPropertyNames() {
        Set<String> keySet = metadata.keySet();

        return keySet.toArray(new String[keySet.size()]);
    }

}