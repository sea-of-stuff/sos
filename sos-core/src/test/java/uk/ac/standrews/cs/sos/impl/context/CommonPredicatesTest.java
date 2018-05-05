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

import org.testng.annotations.Test;
import uk.ac.standrews.cs.castore.data.StringData;
import uk.ac.standrews.cs.sos.SetUpTest;
import uk.ac.standrews.cs.sos.exceptions.ServiceException;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.AtomBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.builders.VersionBuilder;
import uk.ac.standrews.cs.sos.impl.datamodel.locations.URILocation;
import uk.ac.standrews.cs.sos.impl.metadata.MetadataBuilder;
import uk.ac.standrews.cs.sos.model.Version;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class CommonPredicatesTest extends SetUpTest {

    @Test
    public void searchTextFails() throws ServiceException {

        String stringData = "TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), "FAIL");
        assertFalse(result);
    }


    @Test
    public void searchTextPass() throws ServiceException {

        String stringData = "TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), "TEST");
        assertTrue(result);
    }

    @Test
    public void searchTextPass2() throws ServiceException {

        String stringData = "preword TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), "TEST");
        assertTrue(result);
    }

    @Test
    public void searchTextPass3() throws ServiceException {

        String stringData = "preword anotherword thatisaword word word word numbers 1 2 3 4 56789 string TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), "TEST");
        assertTrue(result);
    }

    @Test
    public void searchTextPass4() throws ServiceException {

        String stringData = "preword anotherTESTword";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), "TEST");
        assertTrue(result);
    }

    @Test
    public void searchTextPass5() throws ServiceException {

        String stringData = "preword anotherTESTword";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchText(version.guid(), " TEST "); // spaces before and after word
        assertFalse(result);
    }

    @Test
    public void searchTextIgnoreCase() throws ServiceException {

        String stringData = "TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchTextIgnoreCase(version.guid(), "test");
        assertTrue(result);
    }

    @Test
    public void searchTextPassIgnoreCase2() throws ServiceException {

        String stringData = "preword TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchTextIgnoreCase(version.guid(), "test");
        assertTrue(result);
    }

    @Test
    public void searchTextPassIgnoreCase3() throws ServiceException {

        String stringData = "preword anotherword thatisaword word word word numbers 1 2 3 4 56789 string TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.SearchTextIgnoreCase(version.guid(), "test");
        assertTrue(result);
    }

    @Test
    public void textOccurrences() throws ServiceException {

        String stringData = "TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        int result = CommonPredicates.TextOccurrences(version.guid(), "TEST");
        assertEquals(result, 1);
    }

    @Test
    public void textOccurrences2() throws ServiceException {

        String stringData = "preword TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        int result = CommonPredicates.TextOccurrences(version.guid(), "TEST");
        assertEquals(result, 1);
    }

    @Test
    public void textOccurrences3() throws ServiceException {

        String stringData = "preword TEST TEST TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        int result = CommonPredicates.TextOccurrences(version.guid(), "TEST");
        assertEquals(result, 3);
    }

    @Test
    public void textOccurrences4() throws ServiceException {

        String stringData = "preword TEST test TEST test TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        int result = CommonPredicates.TextOccurrences(version.guid(), "TEST");
        assertEquals(result, 3);
    }

    @Test
    public void textOccurrences5() throws ServiceException {

        String stringData = "preword TEST test TEST test TEST";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));
        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        int result = CommonPredicates.TextOccurrencesIgnoreCase(version.guid(), "TEST");
        assertEquals(result, 5);
    }

    @Test
    public void contentType() throws ServiceException {

        String stringData = "This is some text";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.ContentTypePredicate(version.guid(),
                Arrays.asList("text/plain; charset=UTF-8",
                        "text/plain; charset=windows-1252",
                        "text/plain; charset=ISO-8859-1"));
        assertTrue(result);
    }

    @Test
    public void contentTypeFails() throws ServiceException {

        String stringData = "This is some text";
        AtomBuilder atomBuilder = new AtomBuilder()
                .setData(new StringData(stringData));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.ContentTypePredicate(version.guid(),
                Collections.singletonList("FAIL"));
        assertFalse(result);
    }

    @Test
    public void isMostlyRedSuccess() throws URISyntaxException, ServiceException {

        AtomBuilder atomBuilder = new AtomBuilder()
                .setLocation(new URILocation(getImageFilePath("red.jpg")));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.IsMostly(version.guid(), 0);
        assertTrue(result);
    }

    @Test
    public void isMostlyGreenSuccess() throws URISyntaxException, ServiceException {

        AtomBuilder atomBuilder = new AtomBuilder()
                .setLocation(new URILocation(getImageFilePath("green.jpg")));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.IsMostly(version.guid(), 1);
        assertTrue(result);
    }

    @Test
    public void isMostlyBlueSuccess() throws URISyntaxException, ServiceException {

        AtomBuilder atomBuilder = new AtomBuilder()
                .setLocation(new URILocation(getImageFilePath("blue.jpg")));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.IsMostly(version.guid(), 2);
        assertTrue(result);
    }

    @Test
    public void isMostlyBlueFail() throws URISyntaxException, ServiceException {

        AtomBuilder atomBuilder = new AtomBuilder()
                .setLocation(new URILocation(getImageFilePath("red.jpg")));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.IsMostly(version.guid(), 2);
        assertFalse(result);
    }

    @Test
    public void isMostlyFailOnNonImage() throws URISyntaxException, ServiceException {

        AtomBuilder atomBuilder = new AtomBuilder()
                .setLocation(new URILocation(getImageFilePath("non_image")));

        MetadataBuilder metadataBuilder = new MetadataBuilder()
                .setData(atomBuilder.getData());

        VersionBuilder versionBuilder = new VersionBuilder()
                .setAtomBuilder(atomBuilder)
                .setMetadataBuilder(metadataBuilder);

        Version version = localSOSNode.getAgent().addData(versionBuilder);

        boolean result = CommonPredicates.IsMostly(version.guid(), 0);
        assertFalse(result);

        result = CommonPredicates.IsMostly(version.guid(), 1);
        assertFalse(result);

        result = CommonPredicates.IsMostly(version.guid(), 2);
        assertFalse(result);
    }

    private String getImageFilePath(String imageName) throws URISyntaxException {
        return getClass().getResource("/images/" + imageName).toURI().getPath();
    }
}