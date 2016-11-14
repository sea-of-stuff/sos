package uk.ac.standrews.cs.sos.web.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import uk.ac.standrews.cs.GUIDFactory;
import uk.ac.standrews.cs.IGUID;
import uk.ac.standrews.cs.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.interfaces.manifests.Compound;
import uk.ac.standrews.cs.sos.interfaces.manifests.Manifest;
import uk.ac.standrews.cs.sos.interfaces.manifests.Version;
import uk.ac.standrews.cs.sos.interfaces.sos.Client;
import uk.ac.standrews.cs.sos.model.manifests.Content;
import uk.ac.standrews.cs.sos.model.manifests.ManifestType;
import uk.ac.standrews.cs.sos.node.SOSLocalNode;

import java.util.Collection;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WGraph {

    public static String RenderPartial(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException {
        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        // We assume that the manifest is of type version
        Manifest selectedManifest = sos.getClient().getManifest(guid);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode graph = mapper.createObjectNode();

        ArrayNode nodes = ManifestNodeGraph(sos.getClient(), selectedManifest);
        ArrayNode edges = ManifestEdgesGraph(selectedManifest);

        graph.put("nodes", nodes);
        graph.put("edges", edges);

        return graph.toString();
    }

    // TODO - must refactor
    private static ArrayNode ManifestNodeGraph(Client client, Manifest manifest) throws ManifestNotFoundException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (manifest.getManifestType() == ManifestType.VERSION) {
            Version version = (Version) manifest;

            ObjectNode node = ManifestNode(version, version.getInvariantGUID().toString());
            arrayNode.add(node);

            // Content
            Manifest contentManifest = client.getManifest(version.getContentGUID());
            ObjectNode contentNode = ManifestNode(contentManifest);
            arrayNode.add(contentNode);

            // Previous
            Collection<IGUID> prevs = version.getPreviousVersions();
            for(IGUID prev:prevs) {
                Manifest previousManifest = client.getManifest(prev);
                ObjectNode prevNode = ManifestNode(previousManifest, version.getInvariantGUID().toString());
                arrayNode.add(prevNode);
            }

        } else if (manifest.getManifestType() == ManifestType.COMPOUND) {
            ObjectNode node = ManifestNode(manifest);
            arrayNode.add(node);

            Compound compound = (Compound) manifest;
            Collection<Content> contents = compound.getContents();
            for(Content content:contents) {
                Manifest contentManifest = client.getManifest(content.getGUID());
                ObjectNode contentNode = ManifestNode(contentManifest);
                arrayNode.add(contentNode);
            }
        } else { // ATOM
            ObjectNode node = ManifestNode(manifest);
            arrayNode.add(node);

            ObjectNode dataNode = DataNode(manifest.getContentGUID());
            arrayNode.add(dataNode);
        }


        return arrayNode;
    }

    private static ArrayNode ManifestEdgesGraph(Manifest manifest) {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        if (manifest.getManifestType() == ManifestType.VERSION) {
            Version version = (Version) manifest;

            ObjectNode objectNode = MakeEdge(version.guid(), version.getContentGUID());
            arrayNode.add(objectNode);

            // Previous
            Collection<IGUID> prevs = version.getPreviousVersions();
            for(IGUID prev:prevs) {
                ObjectNode prevNode = MakeEdge(version.guid(), prev, "", "Previous");
                arrayNode.add(prevNode);
            }

        } else if (manifest.getManifestType() == ManifestType.COMPOUND) {
            Compound compound = (Compound) manifest;
            Collection<Content> contents = compound.getContents();
            for(Content content:contents) {
                ObjectNode objectNode = MakeEdge(manifest.guid(), content.getGUID());
                arrayNode.add(objectNode);
            }
        } else { // ATOM
            ObjectNode objectNode = MakeEdge(manifest.guid(), manifest.getContentGUID(), "DATA-");
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }

    private static ObjectNode ManifestNode(Manifest manifest) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", manifest.guid().toString());
        objectNode.put("label", "Type: " + manifest.getManifestType() + "\nGUID: " + manifest.guid().toString().substring(0, 5));
        objectNode.put("group", manifest.getManifestType().toString());
        objectNode.put("shape", "box");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode ManifestNode(Manifest manifest, String group) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", manifest.guid().toString());
        objectNode.put("label", "Type: " + manifest.getManifestType() + "\nGUID: " + manifest.guid().toString().substring(0, 5));
        objectNode.put("group", group);
        objectNode.put("shape", "box");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode DataNode(IGUID guid) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", "DATA-" + guid.toString());
        objectNode.put("label", "Type: DATA\nGUID: " + guid.toString().substring(0, 5));
        objectNode.put("shape", "diamond");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode MakeEdge(IGUID from, IGUID to) {
        return MakeEdge(from, to, "");
    }

    private static ObjectNode MakeEdge(IGUID from, IGUID to, String toPrefix) {
        return MakeEdge(from, to, toPrefix, "");
    }

    private static ObjectNode MakeEdge(IGUID from, IGUID to, String toPrefix, String label) {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("from", from.toString());
        objectNode.put("to", toPrefix + to.toString());
        objectNode.put("label", label);
        objectNode.put("arrows", "to");
        objectNode.put("physics", "false");

        return objectNode;
    }

}