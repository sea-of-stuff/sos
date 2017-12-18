package uk.ac.standrews.cs.sos.impl.context;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.impl.manifest.BasicManifest;
import uk.ac.standrews.cs.sos.model.ManifestType;
import uk.ac.standrews.cs.sos.model.Policy;
import uk.ac.standrews.cs.sos.utils.IO;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.io.InputStream;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public abstract class BasePolicy extends BasicManifest implements Policy {

    private IGUID guid;
    private JsonNode policyManifest;

    protected BasePolicy(JsonNode policyManifest) {
        super(ManifestType.POLICY);

        this.policyManifest = policyManifest;
        this.guid = makeGUID();
    }

    @Override
    public IGUID guid() {
        return guid;
    }

    @Override
    public InputStream contentToHash() {
        return IO.StringToInputStream(policyManifest.toString());
    }

    @Override
    public JsonNode dependencies() {
        return policyManifest.get(JSONConstants.KEY_COMPUTATIONAL_DEPENDENCIES);
    }

    @Override
    public JsonNode apply() {
        return policyManifest.get(JSONConstants.KEY_POLICY_APPLY);
    }

    @Override
    public JsonNode satisfied() {
        return policyManifest.get(JSONConstants.KEY_POLICY_SATISFIED);
    }

    @Override
    public JsonNode fields() {
        if (policyManifest.has(JSONConstants.KEY_POLICY_FIELDS)) {
            return policyManifest.get(JSONConstants.KEY_POLICY_FIELDS);
        } else {
            return JSONHelper.jsonObjMapper().createArrayNode();
        }
    }
}
