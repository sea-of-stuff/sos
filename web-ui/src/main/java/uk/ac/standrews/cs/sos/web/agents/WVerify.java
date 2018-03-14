/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module web-ui.
 *
 * web-ui is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * web-ui is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with web-ui. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.web.agents;

import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.Manifest;
import uk.ac.standrews.cs.sos.model.Role;
import uk.ac.standrews.cs.sos.services.Agent;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WVerify {

    public static String VerifySignature(Request request, SOSLocalNode sos) throws GUIDGenerationException, ServiceException, RoleNotFoundException {

        Agent agent = sos.getAgent();

        IGUID manifestId = GUIDFactory.recreateGUID(request.params("id"));
        Manifest manifest = agent.getManifest(manifestId);

        IGUID roleid = GUIDFactory.recreateGUID(request.params("roleid"));
        Role role = sos.getUSRO().getRole(roleid);

        try {
            if (agent.verifyManifestSignature(role, manifest)) {
                return "VERIFIED";
            } else {
                return "NOT VERIFIED";
            }
        } catch (ServiceException e) {
            return "Signature Exception";
        }
    }

    public static String VerifyIntegrity(Request request, SOSLocalNode sos) throws GUIDGenerationException, ServiceException {

        Agent agent = sos.getAgent();

        IGUID manifestId = GUIDFactory.recreateGUID(request.params("id"));
        Manifest manifest = agent.getManifest(manifestId);

        if (agent.verifyManifestIntegrity(manifest)) {
            return "VERIFIED";
        } else {
            return "NOT VERIFIED";
        }
    }
}
