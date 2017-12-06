package uk.ac.standrews.cs.sos.impl.metadata;

import uk.ac.standrews.cs.guid.IGUID;

/**
 * Triplet (type, key, value) for holding metadata information
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class MetaProperty {

    private MetaType metaType;
    private String key;
    private String value_s;
    private long value_l;
    private IGUID value_g;
    private boolean encrypted = false;

    // Should be used only for encrypted meta properties!
    public MetaProperty(MetaType metaType, String key, String value) {
        this.metaType = metaType;
        this.key = key;
        this.value_s = value;
        this.encrypted = true;
    }

    public MetaProperty(String key, String value) {
        this.metaType = MetaType.STRING;
        this.key = key;
        this.value_s = value;
    }

    public MetaProperty(String key, long value) {
        this.metaType = MetaType.LONG;
        this.key = key;
        this.value_l = value;
    }

    public MetaProperty(String key, IGUID value) {
        this.metaType = MetaType.GUID;
        this.key = key;
        this.value_g = value;
    }

    public MetaType getMetaType() {
        return metaType;
    }

    public String getKey() {
        return key;
    }

    public String getValue_s() {
        return value_s;
    }

    public long getValue_l() {
        return value_l;
    }

    public IGUID getValue_g() {
        return value_g;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    @Override
    public String toString() {

        String retval = metaType.name();
        retval += "::";
        retval += key;
        retval += "=";

        if (encrypted) {
            retval += value_s;

        } else {

            switch (metaType) {
                case LONG:
                    retval += value_l;
                    break;
                case STRING:
                    retval += value_s;
                    break;
                case GUID:
                    retval += value_g.toMultiHash();
                    break;
            }
        }

        return retval;
    }
}
