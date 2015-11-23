package model.implementations.identity;

import model.interfaces.components.identity.IdentityToken;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IdentityTokenImplementation implements IdentityToken {

    private long tokey;

    public IdentityTokenImplementation(long tokey) {
        this.tokey = tokey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final IdentityToken other = (IdentityToken) obj;

        if (this.tokey != other.getTokey())
            return false;

        return true;
    }

    public long getTokey() {
        return tokey;
    }
}



