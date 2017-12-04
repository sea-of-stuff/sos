package uk.ac.standrews.cs.sos.impl.metadata;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum MetaType {

    LONG("LONG"), STRING("STRING"), GUID("GUID");

    private String type;
    MetaType(String type) {
        this.type = type;
    }
}
