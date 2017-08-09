package uk.ac.standrews.cs.sos.model;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum ManifestType {

    ATOM("Atom"), ATOM_PROTECTED("AtomP"),
    COMPOUND("Compound"), COMPOUND_PROTECTED("CompoundP"),
    VERSION("Version"),
    METADATA("Metadata");

    private final String text;

    /**
     * Construct a new manifest type
     * This method is not visible outside of this class
     */
    ManifestType(final String text) {
        this.text = text;
    }

    /**
     * Get the string representation of the enum ManifestType
     * @return
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding ManifestType enum
     * @param value
     * @return
     */
    public static ManifestType get(String value) {
        for(ManifestType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
