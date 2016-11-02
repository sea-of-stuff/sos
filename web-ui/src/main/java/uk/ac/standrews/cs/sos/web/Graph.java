package uk.ac.standrews.cs.sos.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Graph {

    public static Object Render(SOSLocalNode sos) {

        Map<String, Object> model = new HashMap<>();

        Object[] versions = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType() == ManifestType.VERSION).<Version>toArray();

        Object[] compounds = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType() == ManifestType.COMPOUND).<Compound>toArray();

        Object[] atoms = sos.getClient().getAllManifests().
                filter(m -> m.getManifestType() == ManifestType.ATOM).<Atom>toArray();

        model.put("nodes", nodes(versions, compounds, atoms));
        model.put("edges", edges(versions, compounds));

        return VelocityUtils.RenderTemplate("velocity/graph.vm", model);
    }

    private static String nodes(Object[] versions, Object[] compounds, Object[] atoms) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for(Object v:versions) {
            Version version = (Version)v;

            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", version.getVersionGUID().toString());
            objectNode1.put("label", version.getVersionGUID().toString().substring(0, 5));
            objectNode1.put("group", version.getInvariantGUID().toString());
            objectNode1.put("shape", "circle");

            arrayNode.add(objectNode1);
        }

        for(Object c:compounds) {
            Compound compound = (Compound)c;
            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", compound.getContentGUID().toString());
            objectNode1.put("label", compound.getContentGUID().toString().substring(0, 5));
            objectNode1.put("group", compound.getManifestType().toString());
            objectNode1.put("shape", "box");

            arrayNode.add(objectNode1);
        }

        for(Object a:atoms) {
            Atom atom = (Atom)a;
            ObjectNode objectNode1 = mapper.createObjectNode();
            objectNode1.put("id", atom.getContentGUID().toString());
            objectNode1.put("label", atom.getContentGUID().toString().substring(0, 5));
            objectNode1.put("group", atom.getManifestType().toString());
            objectNode1.put("shape", "triangle");

            arrayNode.add(objectNode1);
        }

        return arrayNode.toString();
    }

    private static String edges(Object[] versions, Object[] compounds) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for(Object v:versions) {

            Version version = (Version)v;
            if (version.getPreviousVersions() != null && version.getPreviousVersions().size() > 0) {
                ObjectNode objectNode1 = mapper.createObjectNode();

                objectNode1.put("from", version.getVersionGUID().toString());
                objectNode1.put("to", version.getPreviousVersions().toArray()[0].toString());
                objectNode1.put("arrows", "to");

                arrayNode.add(objectNode1);

                ObjectNode objectNode2 = mapper.createObjectNode();
                objectNode2.put("from", version.getVersionGUID().toString());
                objectNode2.put("to", version.getContentGUID().toString());
                objectNode1.put("arrows", "to");
                objectNode2.put("physics", "false");

                arrayNode.add(objectNode2);
            }
        }

        for(Object c:compounds) {
            Compound compound = (Compound)c;

            IGUID compoundGUID = compound.getContentGUID();

            if (compound.getContents() != null && compound.getContents().size() > 0) {
                Iterator<Content> contents = compound.getContents().iterator();

                while(contents.hasNext()) {
                    Content content = contents.next();

                    ObjectNode objectNode1 = mapper.createObjectNode();

                    objectNode1.put("from", compoundGUID.toString());
                    objectNode1.put("to", content.getGUID().toString());
                    objectNode1.put("label", content.getLabel());
                    objectNode1.put("arrows", "to");

                    arrayNode.add(objectNode1);
                }

            }


        }

        return arrayNode.toString();

        /**
         * To make the JSON String pretty use the below code
         */
        // System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayNode));
    }


}
