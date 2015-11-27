package model.implementations.components.manifests;

import IO.sources.DataSource;
import IO.utils.StreamsUtils;
import model.interfaces.components.entities.Atom;
import model.interfaces.components.identity.Identity;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class AtomManifestTest {

    @PrepareForTest(ManifestFactory.class)
    @Test
    public void testManifestType() throws IOException {
        DataSource dataSourceMocked = mock(DataSource.class);

        Atom atomMocked = mock(Atom.class);
        when(atomMocked.getSource()).thenReturn(dataSourceMocked);
        when(dataSourceMocked.getInputStream()).thenReturn(StreamsUtils.StringToInputStream("TEST"));

        Identity identityMocked = mock(Identity.class);

        AtomManifest atomManifest = ManifestFactory.createAtomManifest(atomMocked, identityMocked);

        assertEquals(atomManifest.getManifestType(), "Atom");
    }

}