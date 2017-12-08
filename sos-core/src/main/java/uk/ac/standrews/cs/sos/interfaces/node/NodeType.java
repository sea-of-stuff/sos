package uk.ac.standrews.cs.sos.interfaces.node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum NodeType {

    AGENT("agent"),
    CMS("cms"), // TODO - find another acronym
    DDS("dds"), // TODO - DDS -> MDS
    MMS("mms"),
    NDS("nds"),
    RMS("rms"),
    STORAGE("storage"),
    UNKNOWN("unknown");

    private final String text;

    /**
     * Construct a new NodeType
     * This method is not visible outside of this class
     */
    NodeType(final String text) {
        this.text = text;
    }

    /**
     * Get the string representation of the enum NodeType
     * @return string for the NodeType
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Parse a string to the corresponding NodeType enum
     * @param value string version of the enum
     * @return the enum NodeType
     */
    public static NodeType get(String value) {
        for(NodeType v : values())
            if(v.toString().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}
