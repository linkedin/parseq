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

exports.userTasks = function(g) {
  excludeTasks(g, function(u) { return g.node(u).hidden; });
};

exports.systemTasks = function(g) {
  excludeTasks(g, function(u) { return g.node(u).systemHidden; });
};

exports.parentTasks = function(g) {
  excludeTasks(g, function(u) { return g.children(u).length; });
};

function excludeTasks(g, predicate) {
  g.eachNode(function(u) {
    if (predicate(u)) {
      if (g.children(u).length) {
        // Nodes in a deleted subgraph are automatically promoted to the parent,
        // but we need to take care to point edges to and from the node's sources
        // and sinks respectively.
        var sourceNodes = sources(g, u),
            sinkNodes = sinks(g, u);
        filterSuccessorEdges(g, g.inEdges(u)).forEach(function(e) {
          sourceNodes.forEach(function(v) {
            g.addEdge(null, g.source(e), v, { relationship: 'SUCCESSOR' });
          });
        });

        filterSuccessorEdges(g, g.outEdges(u)).forEach(function(e) {
          sinkNodes.forEach(function(v) {
            g.addEdge(null, v, g.target(e), { relationship: 'SUCCESSOR' });
          });
        });
      } else {
        filterSuccessorEdges(g, g.inEdges(u)).forEach(function(e) {
          filterSuccessorEdges(g, g.outEdges(u)).forEach(function(e2) {
            g.addEdge(null, g.source(e), g.target(e2), { relationship: 'SUCCESSOR' });
          });
        });
      }

      g.delNode(u);
    }
  });
}

function sources(g, parent) {
  return g.children(parent).filter(function(u) {
    return !filterOutPotentialParents(g, g.inEdges(u)).length;
  });
}

function sinks(g, parent) {
  return g.children(parent).filter(function(u) {
    return !filterOutPotentialParents(g, g.outEdges(u)).length;
  });
}

function filterOutPotentialParents(g, es) {
  return es.filter(function(e) { return g.edge(e).relationship !== 'POTENTIAL_PARENT'; });
}

function filterSuccessorEdges(g, es) {
  return es.filter(function(e) { return g.edge(e).relationship === 'SUCCESSOR'; });
}
