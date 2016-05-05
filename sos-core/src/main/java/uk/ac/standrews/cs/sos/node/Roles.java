package uk.ac.standrews.cs.sos.node;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public enum Roles {
    VOID((byte) 0b0000),
    CLIENT((byte) 0b0000),
    STORAGE((byte) 0b0000),
    COORDINATOR((byte) 0b0000);

    private final byte mask;

    Roles(final byte mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return Byte.toString(mask);
    }
}
