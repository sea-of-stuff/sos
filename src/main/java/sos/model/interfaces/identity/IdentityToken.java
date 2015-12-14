package sos.model.interfaces.identity;

/**
 * TODO
 *
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public interface IdentityToken {

    long getTokey();

    IdentityToken next();
}
