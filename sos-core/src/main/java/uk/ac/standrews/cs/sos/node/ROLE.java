package uk.ac.standrews.cs.sos.node;

public enum ROLE {
    CLIENT((byte) 0b0000),
    STORAGE((byte) 0b0000),
    COORDINATOR((byte) 0b0000);

    public final byte mask;

    ROLE(final byte mask) {
        this.mask = mask;
    }

    @Override
    public String toString() {
        return Byte.toString(mask);
    }

}
