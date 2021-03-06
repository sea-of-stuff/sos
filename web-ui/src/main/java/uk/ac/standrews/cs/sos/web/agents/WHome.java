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

import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.RoleNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.userrole.UserNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.UsersRolesService;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static uk.ac.standrews.cs.sos.web.WebApp.SHORT_DATA_LIMIT;
import static uk.ac.standrews.cs.sos.web.agents.WData.GetData;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WHome {

    public static String Render(SOSLocalNode sos) {

        Map<String, Object> model = new HashMap<>();

        Set<Map<String, Object> > assets = new LinkedHashSet<>();

        Set<IGUID> assetInvariants = sos.getMDS().getManifests(ManifestType.VERSION);
        for(IGUID invariant : assetInvariants) {

            Map<String, Object> versionModel = new HashMap<>();
            try {
                Version version = (Version) sos.getMDS().getManifest(sos.getMDS().getHead(invariant));

                Manifest manifest = sos.getMDS().getManifest(version.content());
                if (manifest.getType().equals(ManifestType.ATOM)) {
                    String outputData = GetData(sos, version, SHORT_DATA_LIMIT, true);
                    versionModel.put("data", outputData);
                }

                versionModel.put("invariant", version.invariant());
                versionModel.put("version", version.version());
                versionModel.put("content", version.content());
                versionModel.put("contentType", manifest.getType());

            } catch (ManifestNotFoundException | HEADNotFoundException e) {
                SOS_LOG.log(LEVEL.ERROR, "WebAPP - unable to process version for HOME PAGE");
            }

            assets.add(versionModel);
        }

        model.put("assets", assets);

        UsersRolesService usersRolesService = sos.getUSRO();

        Set<Pair<User, Role>> usro = new LinkedHashSet<>();
        for(IGUID roleRef:usersRolesService.getRoles()) {
            try {
                Role role = usersRolesService.getRole(roleRef);
                User user = usersRolesService.getUser(role.getUser());
                usro.add(new Pair<>(user, role));
            } catch (RoleNotFoundException | UserNotFoundException e) { /* do nothing */ }
        }
        model.put("usro", usro);

        return VelocityUtils.RenderTemplate("velocity/index.vm", model);
    }

}
