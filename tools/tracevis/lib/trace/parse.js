/*
 * Copyright 2013 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the 'License'); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var CDigraph = require('graphlib').CDigraph;

module.exports = parse;

/*
 * Parses `json` and returns a directed graph representation.
 */
function parse(json) {
  var g = new CDigraph(),
      snapshotNanos = json.snapshotNanos;

  json.traces.forEach(function(trace) {
    trace = shallowCopy(trace);
    var id = trace.id;
    delete trace.id;

    if (trace.pendingNanos === undefined)
      trace.pendingNanos = snapshotNanos;

    if (trace.endNanos === undefined)
      trace.endNanos = snapshotNanos;

    g.addNode(id, trace);
  });
  if (json.relationships) {
    json.relationships.forEach(function(rel) {
      switch(rel.relationship) {
        case 'PARENT_OF':
          g.parent(rel.to, rel.from);
          break;
        case 'SUCCESSOR_OF':
          g.addEdge(null, rel.to, rel.from, { relationship: 'SUCCESSOR' });
          break;
        case 'POSSIBLE_SUCCESSOR_OF':
          // Note that the change of name from POSSIBLE_SUCCESSOR_OF to
          // POTENTIAL_SUCCESSOR is intentional. The trace format is
          // inconsistent but we'll at least keep the visualization code
          // consistent.
          g.addEdge(null, rel.to, rel.from, { relationship: 'POTENTIAL_SUCCESSOR' });
          break;
        case 'POTENTIAL_PARENT_OF':
          g.addEdge(null, rel.to, rel.from, { relationship: 'POTENTIAL_PARENT' });
          break;
      }
    });
  }

  precomputeSortOrder(g);

  return g;
}

function shallowCopy(obj) {
  var copy = {};
  Object.keys(obj).forEach(function(k) {
    if (obj.hasOwnProperty(k)) {
      copy[k] = obj[k];
    }
  });
  return copy;
}

function precomputeSortOrder(g) {
  var counter = 0;
  function dfs(u) {
    var children = g.children(u);
    children.sort(function(v, w) { return g.node(v).startNanos - g.node(w).startNanos; });
    children.forEach(function(v) {
      g.node(v).order = counter++;
      dfs(v);
    });
  }
  dfs(null);
}
