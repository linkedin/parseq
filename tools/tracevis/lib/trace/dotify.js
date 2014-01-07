/*
 * Copyright 2013 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/* jshint -W079 */
var Set = require('cp-data').Set;
/* jshint +W079 */

var util = require('./util');
var dijkstraAll = require('graphlib').alg.dijkstraAll;

module.exports = dotify;

function dotify(graph) {
  graph = graph.copy();

  var pp = extractPotentialParents(graph),
      parents = createParentSet(graph, pp),
      copy = graph.copy();

  copy.graph({});
  rewriteNodes(copy, parents);
  rewriteEdges(copy);
  expandClusters(copy, parents);
  addSourceSinkEdges(copy, pp, parents, graph);
  renameClusters(copy);

  return copy;
}

/*
 * Removes 'POTENTIAL_PARENT' edges and saves them in a map of child ->
 * [potential parent ids].
 */
function extractPotentialParents(graph) {
  var pp = {};
  graph.eachEdge(function(e, child, parent, value) {
    if (value && value.relationship === 'POTENTIAL_PARENT') {
      if (!(child in pp)) {
        pp[child] = [];
      }
      pp[child].push(parent);
      graph.delEdge(e);
    }
  });
  return pp;
}

function rewriteNodes(g, parents) {
  g.eachNode(function(u, value) { if (value === undefined) { g.node(u, {}); } });

  var minStartNanos = Math.min.apply(Math, g.nodes().map(function(u) { return g.node(u).startNanos; }));
  g.eachNode(function(u, value) {
    var start = '@' + util.alignMillis((value.startNanos - minStartNanos) / (1000 * 1000)),
        run = '?',
        total = '+' + util.alignMillis((value.endNanos - value.startNanos) / (1000 * 1000));

    if ('pendingNanos' in value) {
      run = util.alignMillis((value.pendingNanos - value.startNanos) / (1000 * 1000));
    }

    if (parents.has(u)) {
      g.node(u, {
        label: value.name + ' (' + start + ', ' + run + ', ' + total + ')',
        labeljust: 'l',
        style:     'dashed',
        color:     '#aaaaaa',
        fillcolor: fillcolor(value.resultType),
        resultType: value.resultType
      });
    } else {
      g.node(u, {
        label: escapeLabel(value.name) + '\\l|{' + start + '\\r|' + run + '\\r|' + total + '\\r}',
        shape: 'Mrecord',
        style: 'filled',
        fillcolor: fillcolor(value.resultType),
        resultType: value.resultType
      });
    }
  });
}

function rewriteEdges(g) {
  g.eachEdge(function(e, u, v, value) {
    value = value || {};

    var newValue = {};
    if (value.relationship === 'POTENTIAL_SUCCESSOR') {
      newValue.style = 'dashed';
    } else {
      newValue.style = 'solid';
    }
    g.edge(e, newValue);
  });
}

function expandClusters(g, parents) {
  function dfs(u) {
    if (!parents.has(u)) return u;

    var value = g.node(u);
    var source = value.source = 'source_' + u;
    var sink = value.sink = 'sink_' + u;

    g.addNode(source, {
      shape: 'circle',
      style: 'filled',
      label: '&nbsp;'
    });
    g.parent(source, u);

    g.addNode(sink, {
      shape: 'doublecircle',
      style: 'filled',
      fillcolor: g.node(u).fillcolor,
      label: '&nbsp;'
    });
    g.parent(sink, u);

    // Anything pointing at u should point to u's source node
    g.inEdges(u).forEach(function(e) {
      addSolidEdge(g, g.source(e), source);
      g.delEdge(e);
    });

    // Anything that u points at should be pointed at by u's sink node
    g.outEdges(u).forEach(function(e) {
      if (wasFinished(value)) {
        addSolidEdge(g, sink, g.target(e));
      } else {
        addDashedEdge(g, sink, g.target(e));
      }
      g.delEdge(e);
    });

    g.children(u).forEach(function(v) { dfs(v); });
  }
  g.children(null).forEach(function(u) { dfs(u); });
}

function addSourceSinkEdges(g, potentialParents, parents, originalG) {
  var paths = dijkstraAll(originalG),
      allKids = allChildren(originalG, potentialParents);
  originalG.eachNode(function(u, uValue) {
    var parent = g.parent(u);

    if (parent !== null) {
      var parentValue = g.node(parent),
          parentSource = parentValue.source,
          parentSink = parentValue.sink,
          sibs = originalG.children(parent);

      if (!hasPathFromSet(allKids[parent], u, paths)) {
        addSolidEdge(g, parentSource, u);
      } else if (!hasPathFromSet(sibs, u, paths)) {
        addInvisEdge(g, parentSource, u);
      }

      if (!hasPathToSet(u, allKids[parent], paths)) {
        if (wasFinished(uValue) && wasFinished(parentValue)) {
          addSolidEdge(g, u, parentSink);
        } else {
          addDashedEdge(g, u, parentSink);
        }
      } else if (!hasPathToSet(u, sibs, paths)) {
        addInvisEdge(g, u, parentSink);
      }

      var closestPred = closestPredecessorInSet(u, sibs, paths);
      if (closestPred) {
        // If the closest predecessor is not directedly connected
        if (!g.outEdges(closestPred, u).length) {
          addInvisEdge(g, closestPred, u);
        }
      }

      if (u in potentialParents) {
        potentialParents[u].forEach(function(potentialParent) {
          var ppValue = g.node(potentialParent),
              ppSource = ppValue.source,
              ppSink = ppValue.sink,
              ppChildren = originalG.children(potentialParent);

          if (!hasPathToSet(u, ppChildren, paths)) {
            if (wasFinished(uValue) && wasFinished(ppValue)) {
              addSolidEdge(g, u, ppSink);
            } else {
              addDashedEdge(g, u, ppSink);
            }
          }
          if (!hasPathFromSet(ppChildren, u, paths)) {
            addDashedEdge(g, ppSource, u);
          }
        });
      }
    }
  });

  // One more pass through parents to create an invisible edge from source to
  // sink only if there are no other nodes in the cluster. This can happen for
  // potential parents.
  parents.keys().forEach(function(parent) {
    if (g.children(parent).length === 2) {
      var parentValue = g.node(parent);
      addInvisEdge(g, parentValue.source, parentValue.sink);
    }
  });
}

/*
 * Renames compound nodes so that they start with the prefix 'cluster_'. This
 * tells the graphviz engine to treat the subgraph as a cluster.
 */
function renameClusters(g) {
  g.eachNode(function(u, value) {
    if (g.children(u).length) {
      var cluster = g.addNode('cluster_' + u, value);
      g.parent(cluster, g.parent(u));
      g.children(u).forEach(function(v) {
        g.parent(v, cluster);
      });
      g.delNode(u);
    }
  });
}

function hasPathToSet(u, set, paths) {
  for (var i = 0, il = set.length; i < il; ++i) {
    var v = set[i];
    if (u !== v) {
      if (paths[u][v].distance !== Number.POSITIVE_INFINITY) {
        return true;
      }
    }
  }
  return false;
}

function hasPathFromSet(set, v, paths) {
  for (var i = 0, il = set.length; i < il; ++i) {
    var u = set[i];
    if (u !== v) {
      if (paths[u][v].distance !== Number.POSITIVE_INFINITY) {
        return true;
      }
    }
  }
  return false;
}

function closestPredecessorInSet(u, set, paths) {
  var minNode,
      minDistance = Number.POSITIVE_INFINITY;
  set.forEach(function(v) {
    if (u !== v) {
      var entry = paths[v][u];
      if (entry.distance < minDistance) {
        minNode = v;
        minDistance = entry.distance;
      }
    }
  });
  return minNode;
}

/*
 * Given a graph and a map of tasks to an array of potential parents for the
 * task, this function returns a new mapping of parent tasks to all children
 * reachable via a parent or potential parent relationship.
 */
function allChildren(g, pp) {
  var children = {};

  g.eachNode(function(u) {
    children[u] = g.children(u);
  });

  Object.keys(pp).forEach(function(u) {
    pp[u].forEach(function(parent) {
      children[parent].push(u);
    });
  });

  return children;
}

function addSolidEdge(g, u, v) {
  if (g.node(u).sink !== undefined) { u = g.node(u).sink; }
  if (g.node(v).source !== undefined) { v = g.node(v).source; }
  g.addEdge(null, u, v, { style: 'solid' });
}

function addDashedEdge(g, u, v) {
  if (g.node(u).sink !== undefined) { u = g.node(u).sink; }
  if (g.node(v).source !== undefined) { v = g.node(v).source; }
  g.addEdge(null, u, v, { style: 'dashed' });
}

function addInvisEdge(g, u, v) {
  if (g.node(u).sink !== undefined) { u = g.node(u).sink; }
  if (g.node(v).source !== undefined) { v = g.node(v).source; }

  // Only add an invisible edge if it would not produce a multi-edge
  if (!g.outEdges(u, v).length) {
    g.addEdge(null, u, v, { style: 'invis' });
  }
}

function createParentSet(g, pp) {
  var parents = new Set(g.nodes().filter(function(u) { return g.children(u).length; }));
  Object.keys(pp).forEach(function(child) {
    pp[child].forEach(function(parent) {
      parents.add(parent);
    });
  });
  return parents;
}

var ESCAPE_CHARS = /([<>|{}])/g;
function escapeLabel(label) {
  return label ? label.replace(ESCAPE_CHARS, '\\$1') : label;
}

function fillcolor(resultType) {
  switch (resultType) {
    case 'SUCCESS':      return '#e0ffe0';
    case 'ERROR':        return '#ffe0e0';
    case 'EARLY_FINISH': return '#fffacd';
    default:             return '#aaaaaa';
  }
}

/*
 * Helper that returns true only if the task was completed without an early
 * finish state.
 */
function wasFinished(taskValue) {
  if (!taskValue) return true;

  var resultType = taskValue.resultType;
  return !resultType || resultType === 'SUCCESS' || resultType === 'ERROR';
}
