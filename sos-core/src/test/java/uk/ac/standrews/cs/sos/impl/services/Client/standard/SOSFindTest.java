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
package uk.ac.standrews.cs.sos.impl.services.Client.standard;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SOSFindTest extends AgentTest {

//    @Test
//    public void testFindAtoms() throws Exception {
//        Location location = HelperTest.createDummyDataFile(internalStorage);
//        AtomBuilder builder = new AtomBuilder().setLocation(location);
//        Atom manifest = client.addAtom(builder);
//
//        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
//        HelperTest.appendToFile(otherLocation, "another random line");
//        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
//        Atom manifestOther = client.addAtom(otherBuilder);
//
//        Set<IGUID> manifests = client.findManifestByType("Atom");
//        assertEquals(manifests.size(), 2);
//        assertTrue(manifests.contains(manifest.content()));
//        assertTrue(manifests.contains(manifestOther.content()));
//    }
//
//    @Test
//    public void testFindAtomsButNotCompounds() throws Exception {
//        Location location = HelperTest.createDummyDataFile(internalStorage);
//        AtomBuilder builder = new AtomBuilder().setLocation(location);
//        Atom manifest = client.addAtom(builder);
//
//        Location otherLocation = HelperTest.createDummyDataFile(internalStorage, "another-file");
//        HelperTest.appendToFile(otherLocation, "another random line");
//        AtomBuilder otherBuilder = new AtomBuilder().setLocation(otherLocation);
//        Atom manifestOther = client.addAtom(otherBuilder);
//
//        Content cat = new Content("cat", manifest.content());
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//
//        client.addCompound(CompoundType.DATA, contents);
//
//        Set<IGUID> manifests = client.findManifestByType("Atom");
//        assertEquals(manifests.size(), 2);
//        assertTrue(manifests.contains(manifest.content()));
//        assertTrue(manifests.contains(manifestOther.content()));
//    }
//
//    @Test
//    public void testFindContentsByLabel() throws Exception {
//        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//        client.addCompound(CompoundType.DATA, contents);
//
//        Content dog = new Content("dog", GUIDFactory.recreateGUID("343"));
//        Set<Content> otherContents = new ArrayList<>();
//        otherContents.add(dog);
//        client.addCompound(CompoundType.DATA, otherContents);
//
//        Set<IGUID> cats = client.findManifestByLabel("cat");
//        assertEquals(cats.size(), 1);
//        assertTrue(cats.contains(GUIDFactory.recreateGUID("123")));
//
//        Set<IGUID> dogs = client.findManifestByLabel("dog");
//        assertEquals(dogs.size(), 1);
//        assertTrue(dogs.contains(GUIDFactory.recreateGUID("343")));
//    }
//
//    @Test
//    public void testFindVersions() throws Exception {
//        Content cat = new Content("cat", GUIDFactory.recreateGUID("123"));
//        Set<Content> contents = new ArrayList<>();
//        contents.add(cat);
//
//        Compound compound = client.addCompound(CompoundType.DATA, contents);
//
//        VersionBuilder builder = new VersionBuilder(compound.content());
//        Version manifest = client.addVersion(builder);
//
//        Content feline = new Content("feline", GUIDFactory.recreateGUID("456"));
//        Set<Content> newContents = new ArrayList<>();
//        newContents.add(feline);
//
//        Compound newCompound = client.addCompound(CompoundType.DATA, newContents);
//        Set<IGUID> prevs = new ArrayList<>();
//        prevs.add(manifest.version());
//
//        VersionBuilder newBuilder = new VersionBuilder(newCompound.content())
//                .setInvariant(manifest.invariant())
//                .setPrevious(prevs);
//        Version newManifest = client.addVersion(newBuilder);
//
//        Set<IGUID> versions = client.findVersions(manifest.invariant());
//        assertEquals(versions.size(), 2);
//        assertTrue(versions.contains(manifest.version()));
//        assertTrue(versions.contains(newManifest.version()));
//    }

}
