package uk.ac.standrews.cs.sos.impl.metadata;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Metadata;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class AbstractMetadata extends BasicManifest implements Metadata {

    private int size = -1;

    public AbstractMetadata(ManifestType manifestType) {
        super(manifestType);
    }

    @Override
    public abstract MetaProperty getProperty(String propertyName);

    public String getPropertyAsString(String propertyName) {
        return getProperty(propertyName).getValue_s();
    }

    public Long getPropertyAsLong(String propertyName) {
        return getProperty(propertyName).getValue_l();
    }

    public IGUID getPropertyAsGUID(String propertyName) {

        return getProperty(propertyName).getValue_g();
    }

    @Override
    public abstract String[] getAllPropertyNames();

    @Override
    public IGUID guid() {
        return guid;
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
        return manifestType;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public InputStream contentToHash() {

        String toHash = getType() + "WIP"; // FIXME

        return IO.StringToInputStream(toHash);
    }

    public void generateAndSetGUID() {
        if (guid == null) {
            this.guid = makeGUID();
        }
    }

    @Override
    public int size() {

        if (size == -1) {
            size = this.toString().length();
        }

        return size;
    }

}
