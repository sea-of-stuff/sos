package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import spark.Response;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.IKey;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.impl.context.utils.ContextClassBuilder;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.utils.JSONHelper;
import uk.ac.standrews.cs.sos.web.VelocityUtils;
import uk.ac.standrews.cs.utilities.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WContexts {

    public static String Render(SOSLocalNode sos){
        Map<String, Object> model = new HashMap<>();
        model.put("contexts", sos.getCMS().getContexts());
        model.put("roles", sos.getRMS().getRoles());

        return VelocityUtils.RenderTemplate("velocity/contexts.vm", model);
    }

    public static String GetContents(Request req, SOSLocalNode sos) throws GUIDGenerationException, JsonProcessingException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        Map<String, Object> model = new HashMap<>();

        model.put("contents", sos.getCMS().getContents(guid).stream()
                .map(IKey::toMultiHash)
                .collect(Collectors.toSet()));

        return JSONHelper.JsonObjMapper().writeValueAsString(model);
    }

    public static String CreateContext(Request request, Response response, SOSLocalNode sos) {

        try {
            String contextJSON = request.queryParams("contextJSON");
            sos.getCMS().addContext(contextJSON);

            response.redirect("/contexts");
            return "";

        } catch (Exception e) {

            response.redirect("/contexts");
            return "ERROR";
        }

    }

    public static String PreviewClassContext(Request request, Response response) {

        try {
            String contextJSON = request.body();
            JsonNode jsonNode = JSONHelper.JsonObjMapper().readTree(contextJSON);
            String clazz = ContextClassBuilder.ConstructClass(jsonNode);

            response.status(200);
            return clazz;

        } catch (Exception e) {

            response.status(400);
            return "Unable to preview class";
        }

    }

    public static String Threads(Request req, SOSLocalNode sos) {

        ObjectNode node = JSONHelper.JsonObjMapper().createObjectNode();

        // Columns
        ArrayNode cols = node.putArray("cols");
        cols.add(
                JSONHelper.JsonObjMapper().createObjectNode()
                        .put("id", "active")
                        .put("label", "Active")
                        .put("type", "date")
        );

        cols.add(
                JSONHelper.JsonObjMapper().createObjectNode()
                        .put("id", "predicate")
                        .put("label", "Predicate")
                        .put("type", "number")
        );

        // Rows
        ArrayNode rows = node.putArray("rows");

        Iterator<Pair<Long, Long>> threadStats = sos.getCMS().getPredicateThreadSessionStatistics().iterator();

        System.out.println("THREAD STATS: " + sos.getCMS().getPredicateThreadSessionStatistics().size());
        while(threadStats.hasNext()) {
            Pair<Long, Long> ts = threadStats.next();

            ObjectNode cells = JSONHelper.JsonObjMapper().createObjectNode();
            cells.putArray("c")
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.X() + ")")
                    )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "0")
                    );
            rows.add(cells);

            cells = JSONHelper.JsonObjMapper().createObjectNode();
            cells.putArray("c")
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.X() + ")")
                    )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "1")
                    );
            rows.add(cells);

            cells = JSONHelper.JsonObjMapper().createObjectNode();
            cells.putArray("c")
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.Y() + ")")
                    )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "1")
                    );
            rows.add(cells);

            cells = JSONHelper.JsonObjMapper().createObjectNode();
            cells.putArray("c")
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "Date(" + ts.Y() + ")")
                    )
                    .add(JSONHelper.JsonObjMapper().createObjectNode()
                            .put("v", "0")
                    );
            rows.add(cells);
//
//                    .add(JSONHelper.JsonObjMapper().createObjectNode()
//                            .put("v", "Date(" + (ts.X() + ts.Y() + 1000000) + ")")
//                    );

        }

        System.out.println(node.toString());
        return node.toString();
    }

    //            rows.add(
//                    JSONHelper.JsonObjMapper().createObjectNode()
//                            .putArray("c")
//                            .add(JSONHelper.JsonObjMapper().createObjectNode()
//                                    .put("v", "Predicate")
//                            )
//                            .add(JSONHelper.JsonObjMapper().createObjectNode()
//                                    .put("v", "new Date(" + ts.X() / 1000 + ")")
//                            )
//                            .add(JSONHelper.JsonObjMapper().createObjectNode()
//                                    .put("v", "new Date(" + (ts.X() + ts.Y()) / 1000 + ")")
//                            )
//            );

    //node.put("" + ts.Y() / 1000, r.getInt("experiments"));
//        {
//            "cols": [
//            {"id":"","label":"Topping","pattern":"","type":"string"},
//            {"id":"","label":"Slices","pattern":"","type":"number"}
//      ],
//            "rows": [
//            {"c":[{"v":"Mushrooms","f":null},{"v":3,"f":null}]},
//            {"c":[{"v":"Onions","f":null},{"v":1,"f":null}]},
//            {"c":[{"v":"Olives","f":null},{"v":1,"f":null}]},
//            {"c":[{"v":"Zucchini","f":null},{"v":1,"f":null}]},
//            {"c":[{"v":"Pepperoni","f":null},{"v":2,"f":null}]}
//      ]
//        }


//        dataTable.addColumn({ type: 'string', id: 'Thread' });
//        dataTable.addColumn({ type: 'date', id: 'Start' });
//        dataTable.addColumn({ type: 'date', id: 'End' });
//        dataTable.addRows([
//          [ 'Predicate Thread', new Date(1789, 3, 30), new Date(1797, 2, 4) ],
//          [ 'Policy Thread (1)',      new Date(1797, 2, 4),  new Date(1801, 2, 4) ],
//          [ 'Policy Thread (2)',  new Date(1801, 2, 4),  new Date(1809, 2, 4) ]]);

}
