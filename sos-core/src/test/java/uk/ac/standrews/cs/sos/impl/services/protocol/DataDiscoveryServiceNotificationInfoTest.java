package uk.ac.standrews.cs.sos.impl.services.protocol;

import org.testng.annotations.Test;
import uk.ac.standrews.cs.sos.model.Node;
import uk.ac.standrews.cs.sos.protocol.DDSNotificationInfo;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class DataDiscoveryServiceNotificationInfoTest {

    @Test
    public void constructorTest() {
        DDSNotificationInfo info = new DDSNotificationInfo();

        assertFalse(info.useDefaultDDSNodes());
        assertFalse(info.useSuggestedDDSNodes());
        assertEquals(info.getSuggestedDDSNodes().size(), 0);
        assertFalse(info.notifyDDSNodes());
    }

    @Test
    public void gettersSettersTest() {
        DDSNotificationInfo info = new DDSNotificationInfo();

        info.setNotifyDDSNodes(true);
        assertTrue(info.notifyDDSNodes());
        info.setNotifyDDSNodes(false);
        assertFalse(info.notifyDDSNodes());

        info.setUseDefaultDDSNodes(true);
        assertTrue(info.useDefaultDDSNodes());
        info.setUseDefaultDDSNodes(false);
        assertFalse(info.useDefaultDDSNodes());

        info.setUseSuggestedDDSNodes(true);
        assertTrue(info.useSuggestedDDSNodes());
        info.setUseSuggestedDDSNodes(false);
        assertFalse(info.useSuggestedDDSNodes());

        info.setSuggestedDDSNodes(new HashSet<>());
        assertEquals(info.getSuggestedDDSNodes().size(), 0);

        Set<Node> nodes = new HashSet<>();
        nodes.add(mock(Node.class));
        info.setSuggestedDDSNodes(nodes);
        assertEquals(info.getSuggestedDDSNodes().size(), 1);

        nodes.add(mock(Node.class));
        info.setSuggestedDDSNodes(nodes);
        assertEquals(info.getSuggestedDDSNodes().size(), 2);

        info.setSuggestedDDSNodes(new HashSet<>());
        assertEquals(info.getSuggestedDDSNodes().size(), 0);
    }
}