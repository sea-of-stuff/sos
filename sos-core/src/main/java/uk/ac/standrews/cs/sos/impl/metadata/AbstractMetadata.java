package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.guid.impl.keys.InvalidID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
    public abstract Object getProperty(String propertyName);

    public String getPropertyAsString(String propertyName) {
        return (String) getProperty(propertyName);
    }

    public Integer getPropertyAsInteger(String propertyName) {
        return (Integer) getProperty(propertyName);
    }

    public IGUID getPropertyAsGUID(String propertyName) {

        try {
            return GUIDFactory.recreateGUID(getPropertyAsString(propertyName));
        } catch (GUIDGenerationException e) {
            return new InvalidID();
        }
    }

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
    public InputStream contentToHash() throws UnsupportedEncodingException {
        return IO.StringToInputStream(metadata());
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
    public boolean verifySignature(Role role) throws SignatureException {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    // http://stackoverflow.com/a/5439547/2467938
    protected static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
