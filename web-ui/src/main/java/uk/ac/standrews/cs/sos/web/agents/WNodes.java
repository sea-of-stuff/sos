package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.logger.LEVEL;
import uk.ac.standrews.cs.sos.exceptions.node.NodeNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.NodeStats;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.utils.SOS_LOG;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WNodes {

    public static String Render(SOSLocalNode sos) throws JsonProcessingException {
        Map<String, Object> model = new HashMap<>();

        model.put("thisNode", sos.getNDS().getThisNode());
        model.put("nodes", sos.getNDS().getNodes());

        return VelocityUtils.RenderTemplate("velocity/nodes.vm", model);
    }

    public static String GetInfo(Request request, SOSLocalNode sos) throws GUIDGenerationException {

        IGUID nodeid = GUIDFactory.recreateGUID(request.params("nodeid"));
        try {
            return sos.getNDS().infoNode(nodeid);
        } catch (NodeNotFoundException e) {
            return "Info for Node " + nodeid.toMultiHash() + " N/A";
        }
    }

    public static String Find(Request request, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException {

        String q = request.queryParams("nodeid");
        IGUID nodeid = GUIDFactory.recreateGUID(q);
        try {
            sos.getNDS().getNode(nodeid); // force the nds to look for the node and register it?
        } catch (NodeNotFoundException e) {
            SOS_LOG.log(LEVEL.WARN, "WebApp - Unable to find node with GUID: " + nodeid.toMultiHash());
        }

        return Render(sos);
    }

    public static String Stats(Request request, SOSLocalNode sos) throws GUIDGenerationException {

        IGUID nodeid = GUIDFactory.recreateGUID(request.params("nodeid"));
        NodeStats nodeStats = sos.getNDS().getNodeStats(nodeid);

        ObjectNode node = JSONHelper.jsonObjMapper().createObjectNode();

        // Columns
        ArrayNode cols = node.putArray("cols");
        cols.add(
                JSONHelper.jsonObjMapper().createObjectNode()
                        .put("id", "time")
                        .put("label", "time")
                        .put("type", "date")
        );
        cols.add(
                JSONHelper.jsonObjMapper().createObjectNode()
                        .put("id", "active")
                        .put("label", "avg availability")
                        .put("type", "number")
        );

        // Rows
        ArrayNode rows = node.putArray("rows");

        for(Pair<Long, NodeStats.DataPoint> stat:nodeStats.getMeasurements()) {
            ObjectNode cells = JSONHelper.jsonObjMapper().createObjectNode();
            ArrayNode values = cells.putArray("c");
            values.add(JSONHelper.jsonObjMapper().createObjectNode()
                            .put("v", "Date(" + stat.X() + ")") // Adding half a second, otherwise data cannot be visualised
                    )
                    .add(JSONHelper.jsonObjMapper().createObjectNode()
                            .put("v", stat.Y().getProgressiveAvgAvailability() * 100)
                    );

            rows.add(cells);
        }

        return node.toString();
    }

}
