/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
