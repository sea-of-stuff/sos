package model.implementations;

import model.exceptions.UnknownIdentityException;
import model.interfaces.SeaOfStuff;
import model.interfaces.components.identity.Identity;
import model.interfaces.components.identity.IdentityToken;
import model.interfaces.components.identity.Session;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class SeaOfStuffSessionTest {

    private SeaOfStuff model;

    @BeforeMethod
    public void setUp() {
        model = new SeaOfStuffImpl();
    }

    @AfterMethod
    public void tearDown() {
        model = null;
    }

    @Test
    public void testRegister() throws Exception {
        Identity mockedIdentity = mock(Identity.class);

        IdentityToken tokey = model.register((mockedIdentity));
        Session session = model.getSession();
        Map<IdentityToken, Identity> tokeys = session.getAllRegisteredIdentities();

        assertEquals(tokeys.isEmpty(), false);
        assertEquals(tokeys.size(), 1);
        assertEquals(tokeys.get(tokey), mockedIdentity);
    }

    @Test
    public void testUnregister() throws Exception, UnknownIdentityException {
        Identity mockedIdentity = mock(Identity.class);

        IdentityToken tokey = model.register((mockedIdentity));
        model.unregister(tokey);
        Session session = model.getSession();
        Map<IdentityToken, Identity> tokeys = session.getAllRegisteredIdentities();

        assertEquals(tokeys.isEmpty(), true);
    }

    @Test
    public void testGetSession() throws Exception {
        // TODO
    }
}
