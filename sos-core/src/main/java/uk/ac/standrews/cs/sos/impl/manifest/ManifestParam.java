package uk.ac.standrews.cs.sos.impl.manifest;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ManifestParam {

    private String type;
    private String value;

    public ManifestParam(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
