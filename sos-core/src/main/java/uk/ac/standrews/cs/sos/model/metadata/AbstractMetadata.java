package uk.ac.standrews.cs.sos.model.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.interfaces.metadata.SOSMetadata;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractMetadata implements SOSMetadata {

    protected String[] ignoreMetadata;

    public AbstractMetadata(String[] ignoreMetadata) {
        this.ignoreMetadata = ignoreMetadata;
    }

    @Override
    public abstract String getProperty(String propertyName);

    @Override
    public abstract String[] getAllPropertyNames();

    public String[] getAllFilteredPropertyNames() {

        List<String> filteredNames = new ArrayList<>();
        String[] names = getAllPropertyNames();
        for(String meta:names) {
            boolean ignore = Arrays.asList(ignoreMetadata).contains(meta);
            if (!ignore) {
                filteredNames.add(meta);
            }
        }

        return filteredNames.toArray(new String[filteredNames.size()]);
    }

    @Override
    public IGUID guid() throws GUIDGenerationException {
        String metadata = tabularFormat();
        IGUID guid = GUIDFactory.generateGUID(metadata);

        return guid;
    }

    // NOTE - Order matters, order alphabetically
    // rename method
    @Override
    public String tabularFormat() {

        String retval = "";
        for(String meta: getAllFilteredPropertyNames()) {
            retval += meta + ":: " + getProperty(meta) + "\n";
        }

        return retval;
    }

    @Override
    public String toString() {
        try {
            return JSONHelper.JsonObjMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
