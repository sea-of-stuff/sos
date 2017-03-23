package uk.ac.standrews.cs.sos.interfaces.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum ManifestType {
    ATOM("Atom"),
    COMPOUND("Compound"),
    VERSION("Version"),
    CONTEXT("Context"),
    METADATA("Metadata");

    private final String text;

    ManifestType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ManifestType get(String value) {
        for(ManifestType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}