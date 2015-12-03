package model.implementations.components.manifests;


import model.implementations.utils.Location;
import model.interfaces.identity.Identity;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest {

    @Test
    public void testManifestType() throws IOException {
        Collection<Location> mySet = (Collection<Location>) mock(Collection.class);
        Identity identityMocked = mock(Identity.class);

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(mySet, identityMocked);

        assertEquals(atomManifest.getManifestType(), "Atom");
    }

}