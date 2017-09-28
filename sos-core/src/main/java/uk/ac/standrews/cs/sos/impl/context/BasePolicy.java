package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.crypto.SignatureException;
import uk.ac.standrews.cs.sos.impl.manifests.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.utils.IO;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasePolicy extends BasicManifest implements Policy {

    private IGUID guid;
    private String code;

    protected BasePolicy() {
        super(ManifestType.POLICY);

        this.guid = makeGUID();
    }

    protected BasePolicy(String code) {
        this();

        this.code = code;
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    public boolean verifySignature(Role role) throws SignatureException {
        return false;
    }

    public InputStream contentToHash() throws IOException {
        return IO.StringToInputStream(code);
    }

    @Override
    public JsonNode dependencies() {
        return null;
    }

    @Override
    public JsonNode code() {
        return null;
    }
}
