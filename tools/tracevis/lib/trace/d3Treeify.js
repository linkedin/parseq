module.exports = d3Treeify;

/*
 * Creates a representation of the graph that can be used for hierarchical
 * layouts with d3. There should be no direct dependency on D3 in this module.
 */
function d3Treeify(g) {
  var minStart = Math.min.apply(Math,
                                g.nodes().map(function(u) {
                                  return g.node(u).startNanos;
                                }));

  if (g.children(null).length === 0) {
    throw new Error('Input graph must have at least one node!');
  }

  function dfs(u) {
    var uValue = g.node(u),
        children = [];

    g.children(u).forEach(function(v) {
      children.push(dfs(v));
    });

    // Sort children by start time. This is used implicitly by
    // d3.layout.hierarchy layer.
    sortByOrder(children);

    var result = {
      id: u,
      name: uValue.name,
      startMillis: (uValue.startNanos - minStart) / (1000 * 1000),
      totalMillis: (uValue.endNanos - uValue.startNanos) / (1000 * 1000),
      value: uValue.value,
      resultType: uValue.resultType,
      order: uValue.order
    };

    if ('pendingNanos' in uValue) {
      result.runMillis = (uValue.pendingNanos - uValue.startNanos) / (1000 * 1000);
    }

    if (children.length) {
      result.children = children;
    }

    return result;
  }

  return {
    id: null,
    name: '',
    startMillis: 0,
    runMillis: 0,
    totalMillis: 0,
    value: '',
    resultType: '',
    order: -1,
    children: sortByOrder(g.children(null).map(dfs))
  };
}

function sortByOrder(tasks) {
  return tasks.sort(function(a, b) { return a.order - b.order; });
}
