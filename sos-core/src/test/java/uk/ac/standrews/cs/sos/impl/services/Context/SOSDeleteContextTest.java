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
package uk.ac.standrews.cs.sos.impl.services.Context;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextException;
import uk.ac.standrews.cs.sos.exceptions.context.ContextNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.model.Atom;
import uk.ac.standrews.cs.sos.model.Context;
import uk.ac.standrews.cs.sos.model.Version;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSDeleteContextTest extends ContextServiceTest {

    private static final String FAT_CONTEXT_1 = "{\n" +
            "\t\"context\": {\n" +
            "\t\t\"name\": \"All\",\n" +
            "\t\t\"domain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"codomain\": {\n" +
            "\t\t\t\"type\": \"LOCAL\",\n" +
            "\t\t\t\"nodes\": []\n" +
            "\t\t},\n" +
            "\t\t\"max_age\": 0\n" +
            "\t},\n" +
            "\t\"predicate\": {\n" +
            "\t\t\"type\": \"Predicate\",\n" +
            "\t\t\"predicate\": \"true;\"\n" +
            "\t},\n" +
            "\t\"policies\": []\n" +
            "}";

    @Test (expectedExceptions = ContextNotFoundException.class)
    public void addContextAndDeleteTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        contextService.deleteContextVersion(guid);
        contextService.getContext(guid);
    }

    @Test (expectedExceptions = ContextNotFoundException.class)
    public void deleteDeletedContextTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        contextService.deleteContextVersion(guid);
        contextService.deleteContextVersion(guid);
    }

    // The first part is the same as the test in SOSAddRetrieveContextTest.addContext_RunPredicate_GetOneContent_Test()
    @Test (expectedExceptions = ManifestNotFoundException.class)
    public void addContext_RunPredicate_GetOneContent_Delete_Test() throws ContextException, ServiceException, TIPNotFoundException, ManifestNotFoundException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Context context = contextService.getContext(guid);

        // START - ADD ATOM AND VERSION
        AtomBuilder atomBuilder = new AtomBuilder().setData(new StringData("TEST"));
        Atom atom = agent.addAtom(atomBuilder);

        VersionBuilder builder = new VersionBuilder(atom.guid());
        Version manifest = agent.addVersion(builder);
        // END - ADD ATOM AND VERSION

        int runs = contextService.runPredicates();
        assertEquals(runs, 1);
        Context contextTip = contextService.getContextTIP(context.invariant());
        Set<IGUID> contents = contextService.getContents(contextTip.guid());
        assertNotNull(contents);
        assertEquals(contents.size(), 1);

        // DELETE CONTEXT AND CHECK CONTENTS IS DELETED TO
        contextService.deleteContextVersion(contextTip.guid());
        localSOSNode.getMDS().getManifest(contextTip.content()); // should throw ManifestNotFoundException
    }

    @Test (expectedExceptions = ContextNotFoundException.class)
    public void addContextAndDeleteByInvariantTest() throws ContextException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Context context = contextService.getContext(guid);
        contextService.deleteContext(context.invariant());
        contextService.getContext(guid);
    }

    @Test (expectedExceptions = TIPNotFoundException.class)
    public void addContextAndDeleteByInvariantCheckTIPTest() throws ContextException, TIPNotFoundException {

        IGUID guid = contextService.addContext(FAT_CONTEXT_1);
        Context context = contextService.getContext(guid);
        contextService.deleteContext(context.invariant());
        contextService.getContextTIP(context.invariant());
    }

}
