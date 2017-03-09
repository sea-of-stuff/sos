package uk.ac.standrews.cs.sos.model.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestVerificationException;
import uk.ac.standrews.cs.sos.interfaces.identity.Identity;
import uk.ac.standrews.cs.sos.interfaces.model.Metadata;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractMetadata implements Metadata {

    protected String[] ignoreMetadata;
    protected IGUID guid;

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
        filteredNames.sort(String::compareTo);

        return filteredNames.toArray(new String[filteredNames.size()]);
    }

    public IGUID generateGUID() throws GUIDGenerationException {
        String metadata = metadata();
        return GUIDFactory.generateGUID(metadata);
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public String metadata() {
        String retval = "";
        for(String meta: getAllFilteredPropertyNames()) {
            retval += meta + "::" + getProperty(meta);
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

    @Override
    public ManifestType getType() {
        return ManifestType.METADATA;
    }

    @Override
    public boolean verify(Identity identity) throws ManifestVerificationException {
        return false;
    }

    @Override
    public boolean check(String challenge) {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
