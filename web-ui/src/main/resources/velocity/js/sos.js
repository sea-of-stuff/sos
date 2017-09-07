// This is the code that makes the interactions with the SOS DAG as visualized in the sos web app

function buildAndRenderGraph(data) {
    data = JSON.parse(data);
    var nodes = new vis.DataSet(data['nodes']);
    var edges = new vis.DataSet(data['edges']);

    // create a network
    var container = $('#sos-graph')[0];
    var graphData = {
        nodes: nodes,
        edges: edges
    };
    var options = {
        height: '500px',
        layout: {
            improvedLayout: true
        },
        edges: {
            smooth: {
                forceDirection: "none"
            }
        },
        physics: {
            minVelocity: 0.75,
            solver: "repulsion"
        }
    };

    var network = new vis.Network(container, graphData, options);

    // Actions
    network.on("click", function (params) {
        id = params['nodes'][0];

        if (!id) return;

        console.log("network id " + id);
        $('#dag_selected_node').html(id.substring(0, 15));
        $('#dag_selected_node_full').html(id);
    });
}

function evaluateGraph(id) {

    $.get("/graph/" + id, function (data) {
        buildAndRenderGraph(data);
    });
}


$("#dag_show").click(function() {
    id = $('#dag_selected_node_full').html();

    // This is dead-end
    if (id.startsWith('DATA-')) {
        return;
    }

    // Get manifest, metadata, data whenever possible

    $.get("/manifest/" + id, function (data) {
        $('#manifest').html(data);
        hljs.highlightBlock($('#manifest')[0]);
    });

    $.get("/metadata/" + id, function (data) {
        $('#metadata').html(data);
        hljs.highlightBlock($('#metadata')[0]);
    });

    $.get("/data/" + id, function (data) {
        $('#data').html(data);
    });
});

$("#dag_explore").click(function() {
    id = $('#dag_selected_node_full').html();

    $.get("/graph/" + id, function (data) {
        buildAndRenderGraph(data);
    });
});

$("#dag_reset").click(function() {
    id = $('#manifestid_full').html();

    $.get("/graph/" + id, function (data) {
        buildAndRenderGraph(data);
    });
});

$("#dag_seeAllVersions").click(function() {
    id = $('#manifestid_full').html();

    $.get("/graph/asset/" + id, function (data) {
        buildAndRenderGraph(data);
    })
});

$("#dag_setHead").click(function() {
    id = $('#dag_selected_node_full').html();

    $.post("version/" + id + "/sethead");

    // refresh page
    location = location;
});
