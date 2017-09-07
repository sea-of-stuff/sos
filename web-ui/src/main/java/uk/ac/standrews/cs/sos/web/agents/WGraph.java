package uk.ac.standrews.cs.sos.web.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import spark.Request;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.IGUID;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.exceptions.manifest.HEADNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.ManifestNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.manifest.TIPNotFoundException;
import uk.ac.standrews.cs.sos.exceptions.metadata.MetadataNotFoundException;
import uk.ac.standrews.cs.sos.impl.node.SOSLocalNode;
import uk.ac.standrews.cs.sos.model.*;
import uk.ac.standrews.cs.sos.services.Agent;
import uk.ac.standrews.cs.sos.services.DataDiscoveryService;
import uk.ac.standrews.cs.sos.utils.JSONHelper;

import java.util.Set;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class WGraph {

    public static String RenderPartial(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException {

        String guidParam = req.params("id");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        // We assume that the manifest is of type version
        Manifest selectedManifest = sos.getAgent().getManifest(guid);
        ObjectNode graph = JSONHelper.JsonObjMapper().createObjectNode();
        MakeVersionGraph(graph, sos, selectedManifest);

        return graph.toString();
    }

    public static String RenderAsset(Request req, SOSLocalNode sos) throws GUIDGenerationException, ManifestNotFoundException, MetadataNotFoundException {

        String guidParam = req.params("versionid");
        IGUID guid = GUIDFactory.recreateGUID(guidParam);

        // We assume that the manifest is of type version
        Manifest manifest = sos.getAgent().getManifest(guid);
        if (!manifest.getType().equals(ManifestType.VERSION)) return "Version manifest should be selected";

        Version version = (Version) manifest;
        ObjectNode graph = JSONHelper.JsonObjMapper().createObjectNode();
        MakeAssetGraph(graph, sos, version.getInvariantGUID());

        return graph.toString();
    }

    private static void MakeVersionGraph(ObjectNode graph, SOSLocalNode sos, Manifest selectedManifest) throws ManifestNotFoundException {

        ArrayNode nodes = ManifestNodeGraph(sos.getAgent(), sos.getDDS(), selectedManifest);
        ArrayNode edges = ManifestEdgesGraph(selectedManifest);

        graph.put("nodes", nodes);
        graph.put("edges", edges);
    }

    private static void MakeAssetGraph(ObjectNode graph, SOSLocalNode sos, IGUID invariant) throws ManifestNotFoundException {

        ArrayNode nodes = AllVersionsNodesGraph(sos.getDDS(), sos.getDDS(), invariant);
        ArrayNode edges = AllVersionsEdgesGraph(sos.getDDS(), sos.getDDS(), invariant);

        graph.put("nodes", nodes);
        graph.put("edges", edges);
    }

    private static ArrayNode AllVersionsNodesGraph(DataDiscoveryService dataDiscoveryService, DataDiscoveryService dds, IGUID invariant) throws ManifestNotFoundException {

        ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();

        Set<IGUID> versionRefs = dataDiscoveryService.getVersions(invariant);
        for(IGUID versionRef:versionRefs) {

            Version version = (Version) dataDiscoveryService.getManifest(versionRef);

            boolean isHEAD = false;
            try {
                IGUID head = dataDiscoveryService.getHead(version.getInvariantGUID());
                isHEAD = version.guid().equals(head);
            } catch (HEADNotFoundException ignored) { }

            boolean isTIP = false;
            try {
                Set<IGUID> tips = dataDiscoveryService.getTips(version.getInvariantGUID());
                isTIP = tips.contains(version.guid());
            } catch (TIPNotFoundException ignored) { }


            ObjectNode node = VersionManifestNode(version, version.getInvariantGUID().toMultiHash(), isHEAD, isTIP);
            arrayNode.add(node);
        }

        return arrayNode;
    }

    private static ArrayNode AllVersionsEdgesGraph(DataDiscoveryService dataDiscoveryService, DataDiscoveryService dds, IGUID invariant) throws ManifestNotFoundException {

        ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();

        Set<IGUID> versionRefs = dataDiscoveryService.getVersions(invariant);
        for(IGUID versionRef:versionRefs) {

            Version version = (Version) dataDiscoveryService.getManifest(versionRef);

            Set<IGUID> prevs = version.getPreviousVersions();
            if (prevs != null && !prevs.isEmpty()) {
                for (IGUID prev : prevs) {
                    ObjectNode prevNode = MakeEdge(version.guid(), prev, "", "Previous");
                    arrayNode.add(prevNode);
                }
            }
        }

        return arrayNode;
    }

    // TODO - must refactor
    private static ArrayNode ManifestNodeGraph(Agent agent, DataDiscoveryService dataDiscoveryService, Manifest manifest) throws ManifestNotFoundException {

        ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();

        if (manifest.getType() == ManifestType.VERSION) {
            Version version = (Version) manifest;

            boolean isHEAD = false;
            try {
                IGUID head = dataDiscoveryService.getHead(version.getInvariantGUID());
                isHEAD = version.guid().equals(head);
            } catch (HEADNotFoundException ignored) { }

            boolean isTIP = false;
            try {
                Set<IGUID> tips = dataDiscoveryService.getTips(version.getInvariantGUID());
                isTIP = tips.contains(version.guid());
            } catch (TIPNotFoundException ignored) { }


            ObjectNode node = VersionManifestNode(version, version.getInvariantGUID().toMultiHash(), isHEAD, isTIP);
            arrayNode.add(node);

            // Content
            Manifest contentManifest = agent.getManifest(version.getContentGUID());
            ObjectNode contentNode = ManifestNode(contentManifest);
            arrayNode.add(contentNode);

            // Previous
            Set<IGUID> prevs = version.getPreviousVersions();

            if (prevs != null && !prevs.isEmpty()) {
                for (IGUID prev : prevs) {
                    Manifest previousManifest = agent.getManifest(prev);
                    ObjectNode prevNode = ManifestNode(previousManifest, version.getInvariantGUID().toMultiHash());
                    arrayNode.add(prevNode);
                }
            }

            // Metadata
            IGUID metaGUID = version.getMetadata();
            if (metaGUID != null && !metaGUID.isInvalid()) {
                try {
                    Metadata metadata = agent.getMetadata(metaGUID);
                    ObjectNode metadataNode = ManifestNode(metadata);
                    arrayNode.add(metadataNode);
                } catch (MetadataNotFoundException e) {
                    System.err.println(e.getMessage());
                }
            }

        } else if (manifest.getType() == ManifestType.COMPOUND) {
            ObjectNode node = ManifestNode(manifest);
            arrayNode.add(node);

            Compound compound = (Compound) manifest;
            Set<Content> contents = compound.getContents();
            for(Content content:contents) {
                Manifest contentManifest = agent.getManifest(content.getGUID());
                ObjectNode contentNode = ManifestNode(contentManifest);
                arrayNode.add(contentNode);
            }
        } else { // ATOM
            ObjectNode node = ManifestNode(manifest);
            arrayNode.add(node);

            ObjectNode dataNode = DataNode(manifest.guid());
            arrayNode.add(dataNode);
        }


        return arrayNode;
    }

    private static ArrayNode ManifestEdgesGraph(Manifest manifest) {

        ArrayNode arrayNode = JSONHelper.JsonObjMapper().createArrayNode();

        if (manifest.getType() == ManifestType.VERSION) {
            Version version = (Version) manifest;

            ObjectNode objectNode = MakeEdge(version.guid(), version.getContentGUID());
            arrayNode.add(objectNode);

            // Previous
            Set<IGUID> prevs = version.getPreviousVersions();
            if (prevs != null && !prevs.isEmpty()) {
                for (IGUID prev : prevs) {
                    ObjectNode prevNode = MakeEdge(version.guid(), prev, "", "Previous");
                    arrayNode.add(prevNode);
                }
            }

            // Metadata
            IGUID metaGUID = version.getMetadata();
            if (metaGUID != null && !metaGUID.isInvalid()) {
                ObjectNode metadataNode = MakeEdge(version.guid(), metaGUID);
                arrayNode.add(metadataNode);
            }

        } else if (manifest.getType() == ManifestType.COMPOUND) {
            Compound compound = (Compound) manifest;
            Set<Content> contents = compound.getContents();
            for(Content content:contents) {
                ObjectNode objectNode = MakeEdge(manifest.guid(), content.getGUID());
                arrayNode.add(objectNode);
            }
        } else { // ATOM
            ObjectNode objectNode = MakeEdge(manifest.guid(), manifest.guid(), "DATA-");
            arrayNode.add(objectNode);
        }

        return arrayNode;
    }

    private static ObjectNode ManifestNode(Manifest manifest) {
        ObjectMapper mapper = JSONHelper.JsonObjMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", manifest.guid().toMultiHash());
        objectNode.put("label", "Type: " + manifest.getType() + "\nGUID: " + manifest.guid().toMultiHash().substring(0, 15));
        objectNode.put("group", manifest.getType().toString());
        objectNode.put("shape", "box");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode ManifestNode(Manifest manifest, String group) {
        ObjectMapper mapper =JSONHelper.JsonObjMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", manifest.guid().toMultiHash());
        objectNode.put("label", "Type: " + manifest.getType() + "\nGUID: " + manifest.guid().toMultiHash().substring(0, 15));
        objectNode.put("group", group);
        objectNode.put("shape", "box");
        objectNode.put("font", mapper.createObjectNode().put("face", "monospace").put("align", "left"));

        return objectNode;
    }

    private static ObjectNode VersionManifestNode(Version version, String group, boolean isHEAD, boolean isTIP) {
        ObjectMapper mapper = JSONHelper.JsonObjMapper();

        String head = isHEAD ? "<b>" + isHEAD + "</b>" : isHEAD + "";
        String tip = isTIP ? "<b>" + isTIP + "</b>" : isTIP + "";

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", version.guid().toMultiHash());
        objectNode.put("label", "Type: " + version.getType() + "\nGUID: " + version.guid().toMultiHash().substring(0, 15) + "\nHEAD: " + head + "\nTIP: " + tip);
        objectNode.put("group", group);
        objectNode.put("shape", "box");
        objectNode.put("font",
                mapper.createObjectNode()
                        .put("face", "monospace")
                        .put("align", "left")
                        .put("multi", "html"));

        return objectNode;
    }

    private static ObjectNode DataNode(IGUID guid) {
        ObjectMapper mapper = JSONHelper.JsonObjMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("id", "DATA-" + guid.toString());
        objectNode.put("label", "Type: DATA\nGUID: " + guid.toMultiHash());
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
        ObjectMapper mapper = JSONHelper.JsonObjMapper();

        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("from", from.toMultiHash());
        objectNode.put("to", toPrefix + to.toMultiHash());
        objectNode.put("label", label);
        objectNode.put("arrows", "to");
        // objectNode.put("physics", "false");

        return objectNode;
    }

}
