// This is the code that makes the interactions with the SOS DAG as visualized in the sos web app
function evaluateGraph(id) {

    $.get("/graph/" + id, function (data) {
        buildAndRenderGraph(data);
    });

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

            // FIXME - perform actions below only based on the buttons in manifestTemplate
            if (id.startsWith('DATA-')) {
                return;
            }

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

            $.get("/graph/" + id, function (data) {
                buildAndRenderGraph(data);
            });

        });
    }
}