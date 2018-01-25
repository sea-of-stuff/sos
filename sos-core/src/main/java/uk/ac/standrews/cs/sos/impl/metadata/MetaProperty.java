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
    private double value_d;
    private boolean value_b;
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

    public MetaProperty(String key, double value) {
        this.metaType = MetaType.DOUBLE;
        this.key = key;
        this.value_d = value;
    }

    public MetaProperty(String key, boolean value) {
        this.metaType = MetaType.BOOLEAN;
        this.key = key;
        this.value_b = value;
    }

    public MetaProperty(String key, IGUID value) {
        this.metaType = MetaType.GUID;
        this.key = key;
        this.value_g = value;
    }

    public MetaProperty(String key) {
        this.metaType = MetaType.ANY;
        this.key = key;
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

    public double getValue_d() {
        return value_d;
    }

    public boolean getValue_b() {
        return value_b;
    }

    public IGUID getValue_g() {
        return value_g;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    @Override
    public String toString() {

        String retval = metaType.toString();
        retval += "_";
        retval += key;
        retval += "_";

        if (encrypted) {
            retval += value_s;

        } else {

            switch (metaType) {
                case LONG:
                    retval += value_l;
                    break;
                case DOUBLE:
                    retval += value_d;
                    break;
                case BOOLEAN:
                    retval += value_b;
                    break;
                case STRING:
                    retval += value_s;
                    break;
                case GUID:
                    retval += value_g.toMultiHash();
                    break;
                case ANY:
                    break;
            }
        }

        return retval;
    }
}
