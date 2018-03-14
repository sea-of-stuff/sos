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
package uk.ac.standrews.cs.sos.impl.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.constants.JSONConstants;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotMadeException;
import uk.ac.standrews.cs.sos.impl.context.ContextManifest;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.NodesCollection;
import uk.ac.standrews.cs.sos.model.Role;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import static uk.ac.standrews.cs.sos.constants.JSONConstants.KEY_CONTEXT_TIMESTAMP;
import static uk.ac.standrews.cs.sos.impl.context.ContextManifest.PREDICATE_ALWAYS_TO_COMPUTE;
import static uk.ac.standrews.cs.sos.impl.json.CommonJson.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ContextDeserializer extends JsonDeserializer<Context> {

    @Override
    public Context deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        try {
            String name = node.get(JSONConstants.KEY_CONTEXT_NAME).asText();

            NodesCollection domain = getNodesCollection(node, JSONConstants.KEY_CONTEXT_DOMAIN);
            domain.addNodeRef(SOSLocalNode.settings.guid()); // Ensure that local node is part of the domain
            NodesCollection codomain = getNodesCollection(node, JSONConstants.KEY_CONTEXT_CODOMAIN);

            long maxage = node.has(JSONConstants.KEY_CONTEXT_MAX_AGE) ? node.get(JSONConstants.KEY_CONTEXT_MAX_AGE).asLong() : PREDICATE_ALWAYS_TO_COMPUTE;
            IGUID predicate = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_CONTEXT_PREDICATE).asText());
            IGUID content = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_CONTEXT_CONTENT).asText());

            Set<IGUID> policies = getPolicies(node);

            String signature = getSignature(node);
            IGUID signerRef = getSignerRef(node);
            Role signer = getSigner(signerRef);

            if (createImmutableContext(node)) {

                IGUID guid = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_GUID).asText());
                IGUID invariant = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_CONTEXT_INVARIANT).asText());
                IGUID previous = GUIDFactory.recreateGUID(node.get(JSONConstants.KEY_CONTEXT_PREVIOUS).asText());
                Instant timestamp = Instant.ofEpochSecond(node.get(KEY_CONTEXT_TIMESTAMP).asLong());

                if (signer == null) {
                    return new ContextManifest(timestamp, guid, name, domain, codomain, predicate, maxage, policies, signerRef, signature, content, invariant, previous);
                } else {
                    return new ContextManifest(timestamp, guid, name, domain, codomain, predicate, maxage, policies, signer, signature, content, invariant, previous);
                }
            } else { // see SOSContextService.addContext(ContextBuilder) method

                if (signer == null) {
                    return new ContextManifest(name, domain, codomain, predicate, maxage, policies, signerRef, content);
                } else {
                    return new ContextManifest(name, domain, codomain, predicate, maxage, policies, signer, content);
                }
            }

        } catch (GUIDGenerationException e) {
            throw new IOException("Unable to generate GUIDs for context");
        } catch (ManifestNotMadeException e) {
            throw new IOException("Unable to make context manifest");
        }

    }

    private Set<IGUID> getPolicies(JsonNode node) throws GUIDGenerationException {

        Set<IGUID> policies = new LinkedHashSet<>();
        JsonNode policies_n = node.get(JSONConstants.KEY_CONTEXT_POLICIES);
        for(JsonNode policy_n:policies_n) {

            IGUID policy = GUIDFactory.recreateGUID(policy_n.asText());
            policies.add(policy);
        }

        return policies;
    }

    private boolean createImmutableContext(JsonNode node) {

        return node.has(JSONConstants.KEY_CONTEXT_INVARIANT) &&
                node.has(JSONConstants.KEY_CONTEXT_PREVIOUS) &&
                node.has(KEY_CONTEXT_TIMESTAMP);
    }

}
