package uk.ac.standrews.cs.sos.web.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Atom;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.web.VelocityUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WGraph {

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

    public static String RenderPartial(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        // We assume that the manifest is of type version
        Version version = (Version) sos.getClient().getManifest(guid);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graph = mapper.createObjectNode();

        ArrayNode nodes = VersionNodeGraph(sos.getClient(), version);
        ArrayNode edges = VersionEdgesGraph(sos.getClient(), version);

        graph.put("nodes", nodes);
        graph.put("edges", edges);

        return graph.toString();
    }

    // Graph of version and related
    private static ArrayNode VersionNodeGraph(Client client, Version version) throws ManifestNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        ObjectNode versionNode = VersionNode(version);
        arrayNode.add(versionNode);

        Manifest contentManifest = client.getManifest(version.getContentGUID());
        if (contentManifest.getManifestType() == ManifestType.ATOM) {
            Atom atom = (Atom) contentManifest;
            ObjectNode atomNode = AtomNode(atom);
            arrayNode.add(atomNode);
        } else if (contentManifest.getManifestType() == ManifestType.COMPOUND) {
            Compound compound = (Compound) contentManifest;
            ObjectNode compoundNode = CompoundNode(compound);
            arrayNode.add(compoundNode);
        }

        return arrayNode;
    }

    private static ArrayNode VersionEdgesGraph(Client client, Version version) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("from", version.getVersionGUID().toString());
        objectNode.put("to", version.getContentGUID().toString());
        objectNode.put("arrows", "to");
        objectNode.put("physics", "false");

        arrayNode.add(objectNode);

        return arrayNode;
    }

    private static ObjectNode VersionNode(Version version) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", version.getVersionGUID().toString());
        objectNode.put("label", "Type: " + version.getManifestType() + "\nGUID: " + version.guid().toString().substring(0, 5));
        objectNode.put("group", version.getInvariantGUID().toString());
        objectNode.put("shape", "box");

        return objectNode;
    }

    private static ObjectNode CompoundNode(Compound compound) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", compound.getContentGUID().toString());
        objectNode.put("label", "Type: " + compound.getManifestType() + "\nGUID: " + compound.guid().toString().substring(0, 5));
        objectNode.put("group", compound.getManifestType().toString());
        objectNode.put("shape", "box");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode AtomNode(Atom atom) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", atom.getContentGUID().toString());
        objectNode.put("label", "Type: " + atom.getManifestType() + "\nGUID: " + atom.guid().toString().substring(0, 5));
        objectNode.put("group", atom.getManifestType().toString());
        objectNode.put("shape", "box");

        return objectNode;
    }

    private static String nodes(Object[] versions, Object[] compounds, Object[] atoms) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        for(Object v:versions) {
            ObjectNode version = VersionNode((Version)v);
            arrayNode.add(version);
        }

        for(Object c:compounds) {
            ObjectNode compound = CompoundNode((Compound) c);
            arrayNode.add(compound);
        }

        for(Object a:atoms) {
            ObjectNode atom = AtomNode((Atom)a);
            arrayNode.add(atom);
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
            }

            ObjectNode objectNode2 = mapper.createObjectNode();
            objectNode2.put("from", version.getVersionGUID().toString());
            objectNode2.put("to", version.getContentGUID().toString());
            objectNode2.put("arrows", "to");
            objectNode2.put("physics", "false");

            arrayNode.add(objectNode2);
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
    }

}
