package sos.model.implementations;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sos.exceptions.UnknownIdentityException;
import sos.model.interfaces.SeaOfStuff;
import sos.model.interfaces.identity.IdentityToken;

import static org.mockito.Mockito.mock;

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
//        Identity mockedIdentity = mock(Identity.class);
//
//        IdentityToken tokey = sos.model.registerIdentity((mockedIdentity));
//        Session session = sos.model.getSession();
//        Map<IdentityToken, Identity> tokeys = session.getAllRegisteredIdentities();
//
//        assertEquals(tokeys.isEmpty(), false);
//        assertEquals(tokeys.size(), 1);
//        assertEquals(tokeys.get(tokey), mockedIdentity);
    }

    @Test
    public void testUnregister() throws Exception, UnknownIdentityException {
//        Identity mockedIdentity = mock(Identity.class);
//
//        IdentityToken tokey = sos.model.registerIdentity((mockedIdentity));
//        sos.model.unregisterIdentity(tokey);
//        Session session = sos.model.getSession();
//        Map<IdentityToken, Identity> tokeys = session.getAllRegisteredIdentities();
//
//        assertEquals(tokeys.isEmpty(), true);
    }

    @Test (expectedExceptions = UnknownIdentityException.class)
    public void testUnregisterWithException() throws Exception, UnknownIdentityException {
        IdentityToken mockedTokey = mock(IdentityToken.class);
        model.unregisterIdentity(mockedTokey);
    }

}
