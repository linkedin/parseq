/*
 * Copyright 2012 LinkedIn, Inc
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

var assert = require('assert');
require('../index');
require('./testUtil');

describe('graphviz createView', function() {
  var _viewStub;
  beforeEach(function() {
    var _output = "";
    _viewStub = {
      classed: function() {
        return _viewStub;
      },

      style: function() {
        return _viewStub;
      },

      append: function() {
        return _viewStub;
      },

      text: function(output) {
        _output = output;
        return _viewStub;
      },

      result: function() {
        return _output;
      }
    };
  });

  afterEach(function() {
    delete _viewStub;
  });

  it('create single node test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    _renderGraphviz(root);

    var arr = [];
    _buildExpected(arr, root);
    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('create parent-child test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child = TESTUTIL.createDefaultNode(1, root);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), child);
    _buildEdge(edges, child, _sinkId(root));

    _buildExpected(arr, root, edges);
    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('Node with predecessors test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var child2 = TESTUTIL.createDefaultNode(2, root);
    var child3 = TESTUTIL.createDefaultNode(3, root);
    TESTUTIL.setPredecessorRelationship(child3, child2);
    TESTUTIL.setPredecessorRelationship(child2, child1);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), child1);
    _buildEdge(edges, child3, _sinkId(root));
    _buildEdge(edges, child1, child2);
    _buildEdge(edges, child2, child3);
    _buildExpected(arr, root, edges);

    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('Node with sub-graph test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var subChild1 = TESTUTIL.createDefaultNode(2, child1);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), _sourceId(child1));
    _buildEdge(edges, _sinkId(child1), _sinkId(root));
    _buildEdge(edges, _sourceId(child1), subChild1);
    _buildEdge(edges, subChild1, _sinkId(child1));
    _buildExpected(arr, root, edges);
    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('Node with possible predeccessors test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var child2 = TESTUTIL.createDefaultNode(2, root);
    var child3 = TESTUTIL.createDefaultNode(3, root);
    TESTUTIL.setPredecessorRelationship(child3, child2);
    TESTUTIL.setPredecessorRelationship(child2, child1);
    TESTUTIL.setPossiblePredecessorRelationship(child3, child1);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), child1);
    _buildEdge(edges, child3, _sinkId(root));
    _buildEdge(edges, child1, child2);
    _buildEdge(edges, child2, child3);
    _buildDashedEdge(edges, child1, child3);

    _buildExpected(arr, root, edges);
    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('Node with potential parents test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var parent1 = TESTUTIL.createDefaultNode(1, root);
    var parent2 = TESTUTIL.createDefaultNode(2, root);
    var child1 = TESTUTIL.createDefaultNode(3, parent1);
    var child2 = TESTUTIL.createDefaultNode(4, parent1);
    var child3 = TESTUTIL.createDefaultNode(5, parent2);
    var child4 = TESTUTIL.createDefaultNode(6, parent2);

    TESTUTIL.setPredecessorRelationship(child2, child1);
    TESTUTIL.setPredecessorRelationship(child4, child3);
    TESTUTIL.setPotentialChildRelationship(parent1, child4);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), _sourceId(parent1));
    _buildEdge(edges, _sinkId(parent1), _sinkId(root));
    _buildEdge(edges, _sourceId(root), _sourceId(parent2));
    _buildEdge(edges, _sinkId(parent2), _sinkId(root));
    _buildEdge(edges, _sourceId(parent1), child1);
    _buildEdge(edges, child2, _sinkId(parent1));
    _buildEdge(edges, child1, child2);
    _buildEdge(edges, _sourceId(parent2), child3);
    _buildEdge(edges, child4, _sinkId(parent2));
    _buildEdge(edges, child4, _sinkId(parent1));
    _buildDashedEdge(edges, _sourceId(parent1), child4);
    _buildEdge(edges, child3, child4);
    _buildExpected(arr, root, edges);

    assert.equal(_viewStub.result(), arr.join(''));
  });

  it('Potential parent with possible predecessor test', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var parent1 = TESTUTIL.createDefaultNode(1, root);
    var parent2 = TESTUTIL.createDefaultNode(2, root);
    var child1 = TESTUTIL.createDefaultNode(3, parent1);
    var child2 = TESTUTIL.createDefaultNode(4, parent1);
    var child3 = TESTUTIL.createDefaultNode(5, parent2);
    var child4 = TESTUTIL.createDefaultNode(6, parent2);

    TESTUTIL.setPredecessorRelationship(child2, child1);
    TESTUTIL.setPredecessorRelationship(child4, child3);
    TESTUTIL.setPossiblePredecessorRelationship(child4, child2);
    TESTUTIL.setPotentialChildRelationship(parent1, child4);
    _renderGraphviz(root);

    var arr = [];
    var edges = [];
    _buildEdge(edges, _sourceId(root), _sourceId(parent1));
    _buildEdge(edges, _sinkId(parent1), _sinkId(root));
    _buildEdge(edges, _sourceId(root), _sourceId(parent2));
    _buildEdge(edges, _sinkId(parent2), _sinkId(root));
    _buildEdge(edges, _sourceId(parent1), child1);
    _buildEdge(edges, child1, child2);
    _buildEdge(edges, _sourceId(parent2), child3);
    _buildEdge(edges, child4, _sinkId(parent2));
    _buildEdge(edges, child4, _sinkId(parent1));
    _buildEdge(edges, child3, child4);
    _buildInvisEdge(edges, child2, _sinkId(parent1));
    _buildDashedEdge(edges, child2, child4);
    _buildExpected(arr, root, edges);
    assert.equal(_viewStub.result(), arr.join(''));
  });

  var _renderGraphviz = function(root) {
    var path = TRACE.appendPath(root);
    GRAPHVIZ.render(_viewStub, root, path);
  }

  var _buildInvisEdge = function(arr, from, to) {
    _buildEdge(arr, from, to, "invis");
  };

  var _buildDashedEdge = function (arr, from, to) {
    _buildEdge(arr, from, to, "dashed");
  };

  var _buildEdge = function(arr, from, to, styleOption) {
    var fromStr = from;
    var toStr = to;

    if (from.id) {
      fromStr = "T" + from.id;
    }
    if (to.id) {
      toStr = "T" + to.id;
    }
    var edge = fromStr + " -> " + toStr;
    if (styleOption) {
      edge = edge + " [style=" + styleOption + "]";
    }
    _appendLine(1, arr, edge);
  };

  var _buildExpected = function(arr, node, edges) {

    _appendLine(0, arr, "digraph {");
    if (node.children) {
      _buildSubGraph(1, arr, node);
    }
    else {
    _buildNode(1, arr, node);
    }

    if (edges) {
      _appendLine(0, arr, edges.join('') + "}");
    }
    else {
      _appendLine(0, arr, "}");
    }
  };

  var _buildSubGraph = function(level, arr, node) {
    _appendLine(level, arr, 'subgraph "cluster_' + node.id + '" {');
    level++;
    _appendLine(level, arr, 'label="' + node.name + ' (@1, +1)"');
    _appendLine(level, arr, 'labeljust="l"');
    _appendLine(level, arr, 'color="#aaaaaa"');
    _appendLine(level, arr, 'style="dashed"');
    _appendLine(level, arr, _sourceId(node) + ' [shape=circle,style=filled,label="&nbsp;"]');
    _appendLine(level, arr, _sinkId(node) + ' [shape=doublecircle,style=filled,fillcolor="#e0ffe0",label="&nbsp;"]');

    node.children.forEach(function(child) {
      if (child.children)
      {
        _buildSubGraph(level, arr, child);
      }
      else {
        _buildNode(level, arr, child);
      }
    });
    level--;
    _appendLine(level, arr, "}");
  };

  var _buildNode = function(level, arr, node) {
    _appendLine(level, arr, 'T' + node.id + ' [label="' + node.name + "\\l" + '|{@1|+1}",style=filled,shape=Mrecord,fillcolor="#e0ffe0\"]');
  };

  var _sourceId = function(source) { return "T" + (source.children ? source.id + "_source" : source.id); };
  var _sinkId   = function(sink) { return "T" + (sink.children ? sink.id + "_sink" : sink.id); };
  var _appendLine = function (level, arr, str) {
    var indent = "";
    for (var i = 0; i < level; i++) {
      indent += "    ";
    }
    arr.push(indent);
    arr.push(str);
    arr.push('\n');
  };
});