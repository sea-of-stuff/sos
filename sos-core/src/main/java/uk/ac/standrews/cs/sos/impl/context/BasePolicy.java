package uk.ac.standrews.cs.sos.impl.context;

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
    private String policyManifest;

    // REMOVEME
    protected BasePolicy() {
        super(ManifestType.POLICY);
    }

    protected BasePolicy(String policyManifest) {
        super(ManifestType.POLICY);

        this.policyManifest = policyManifest;
        this.guid = makeGUID();
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    public boolean verifySignature(Role role) throws SignatureException {
        return false;
    }

    public InputStream contentToHash() throws IOException {
        return IO.StringToInputStream(policyManifest);
    }

}
