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

describe('trace createGraph', function() {

  it('create graph with only root', function() {
    var root = TESTUTIL.createDefaultNode(0);
    TRACE.appendPath(root);
    assert.equal(root["path"][root.id].distance, 0);
    _validateNodes(root);
  });

  it('create graph with only parent and child relationship', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child = TESTUTIL.createDefaultNode(1, root);
    TRACE.appendPath(root);

    _validateNodes(root);
    _validateInfiniteEdges(root);
  });

  it('create graph with only potential parent and child relationship', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var child2 = TESTUTIL.createDefaultNode(2, root);
    var parent1 = TESTUTIL.createDefaultNode(3, root);
    var parent2 = TESTUTIL.createDefaultNode(4, root);
    TESTUTIL.setPotentialChildRelationship(parent1, child1);
    TESTUTIL.setPotentialChildRelationship(parent2, child1);
    TESTUTIL.setPotentialChildRelationship(parent1, child2);
    TRACE.appendPath(root);

    _validateNodes(root);
    _validateInfiniteEdges(root);
  });

  it('create graph with predecessors relationship', function() {
    var node = TESTUTIL.createDefaultNode(0);
    var pred1 = TESTUTIL.createDefaultNode(1);
    var pred2 = TESTUTIL.createDefaultNode(2);
    TESTUTIL.setPredecessorRelationship(node, pred1);
    TESTUTIL.setPredecessorRelationship(node, pred2);
    TRACE.appendPath(node);

    _validateNodes(node);
    _validateEdges(node, pred1, pred2);
  });

  it('create graph with possible predecessors relationship', function() {
    var node = TESTUTIL.createDefaultNode(0);
    var pred1 = TESTUTIL.createDefaultNode(1);
    var pred2 = TESTUTIL.createDefaultNode(2);
    TESTUTIL.setPossiblePredecessorRelationship(node, pred1);
    TESTUTIL.setPossiblePredecessorRelationship(node, pred2);
    TRACE.appendPath(node);

    _validateNodes(node);
    _validateEdges(node, pred1, pred2);
  });

  it('create graph with successors relationship', function() {
    var node = TESTUTIL.createDefaultNode(0);
    var pred1 = TESTUTIL.createDefaultNode(1);
    var pred2 = TESTUTIL.createDefaultNode(2);
    node.possiblePredecessors = [pred1, pred2];
    pred1.possibleSuccessors = [node];
    pred2.possibleSuccessors = [node];
    TRACE.appendPath(node);

    _validateNodes(node);
    _validateEdges(node, pred1, pred2);
  });

  it('create complex graph with parent and predecessor relationship', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var child2 = TESTUTIL.createDefaultNode(2, root);
    var parent1 = TESTUTIL.createDefaultNode(3, root);
    var parent2 = TESTUTIL.createDefaultNode(4, root);
    var pred1 = TESTUTIL.createDefaultNode(5, root);
    var pred2 = TESTUTIL.createDefaultNode(6, root);
    TESTUTIL.setPotentialChildRelationship(parent1, child1);
    TESTUTIL.setPotentialChildRelationship(parent1, child2);
    TESTUTIL.setPotentialChildRelationship(parent2, child1);
    TESTUTIL.setPredecessorRelationship(pred1, pred2);
    TESTUTIL.setPredecessorRelationship(child1, pred1);
    TESTUTIL.setPossiblePredecessorRelationship(child2, pred2);
    TRACE.appendPath(root);

    _validateNodes(root);
    _validateDirectEdges(root);
    _validateInfiniteEdge(child1, child2);
    assert.equal(pred2["path"][child1.id].distance, 2);
    assert.equal(child1["path"][pred2.id].distance, Number.POSITIVE_INFINITY);
    assert.equal(child1["path"][pred1.id].distance, Number.POSITIVE_INFINITY);
    assert.equal(pred1["path"][pred2.id].distance, Number.POSITIVE_INFINITY);
  });

  //only when there are no predeccessor/successor relationship
  var _validateInfiniteEdges = function(node) {
    if (node.children) {
      node.children.forEach(function(d) {
        _validateInfiniteEdge(node, d);
        _validateInfiniteEdges(d);
      });
    }
    if (node.parent) {
      _validateInfiniteEdge(node, node.parent);
    }
    if (node.potentialChildren) {
      node.potentialChildren.forEach(function(d) {
        _validateInfiniteEdge(node, d);
        _validateInfiniteEdges(d);
      });
    }
    if (node.potentialParents) {
      node.potentialParents.forEach(function(d) {
        _validateInfiniteEdge(node, d);
      });
    }
  };

  var _validateInfiniteEdge = function(node, d) {
    assert.equal(node["path"][node.id].distance, 0);
    assert.equal(d["path"][d.id].distance, 0);
    assert.equal(node["path"][d.id].distance, Number.POSITIVE_INFINITY);
    assert.equal(d["path"][node.id].distance, Number.POSITIVE_INFINITY);
  };

  var _validateEdges = function (node, pred1, pred2) {
    _validateDirectEdges(node);
    _validateInfiniteEdge(pred1, pred2);
    assert.equal(node["path"][pred1.id].distance, Number.POSITIVE_INFINITY);
    assert.equal(node["path"][pred2.id].distance, Number.POSITIVE_INFINITY);
  };

  var _validateDirectEdges = function(node) {
    assert.equal(node["path"][node.id].distance, 0);

    _validateArrNodes(node, "predecessors", function(d) {
      assert.equal(d["path"][node.id].distance, 1);
      _validateDirectEdges(d);
    });

    _validateArrNodes(node, "possiblePredecessors", function(d) {
      assert.equal(d["path"][node.id].distance, 1);
      _validateDirectEdges(d);
    });

    _validateArrNodes(node, "possibleSuccessors", function(d) {
      assert.equal(node["path"][d.id].distance, 1);
    });

    _validateArrNodes(node, "successors", function(d) {
      assert.equal(node["path"][d.id].distance, 1);
    });
  };

  var _validateNodes = function(node) {
    assert.ok(node["path"] !== undefined);
    if (node.parent) {
        assert.ok(node.parent["path"] !== undefined);
    }
    _assertArrNodesContainsNode(node, "potentialParents");
    _assertArrNodesContainsNode(node, "possibleSuccessors");
    _assertArrNodesContainsNode(node, "successors");
    _validateArrNodes(node, "potentialChildren", _validateNodes);
    _validateArrNodes(node, "predecessors", _validateNodes);
    _validateArrNodes(node, "possiblePredecessors", _validateNodes);
    _validateArrNodes(node, "children", _validateNodes);
  };

  var _assertArrNodesContainsNode = function(node, field) {
    _validateArrNodes(node, field, function(d) {
      assert.ok(d["path"] !== undefined);
    });
  };

  var _validateArrNodes = function(node, field, validateFunc) {
    if (node[field] !== undefined) {
      node[field].forEach(function(d) {
        validateFunc(d);
      });
    }
  };
});

describe('trace flatten', function() {

  it('flatten with only root', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var flatten = TRACE.flatten(root);

    _validateFlatten(flatten, root);
  });

  it('flatten with only parent child relationship', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child = TESTUTIL.createDefaultNode(1, root);
    var flatten = TRACE.flatten(root);

    _validateFlatten(flatten, root);
  });

  it('flatten parent with multiple children', function() {
    var root = TESTUTIL.createDefaultNode(0);
    var child1 = TESTUTIL.createDefaultNode(1, root);
    var child2 = TESTUTIL.createDefaultNode(2, root);
    var subChild1 = TESTUTIL.createDefaultNode(3, child1);
    var subChild2 = TESTUTIL.createDefaultNode(4, child2);
    var subChild3 = TESTUTIL.createDefaultNode(5, child2);
    var flatten = TRACE.flatten(root);

    _validateFlatten(flatten, root);
  });

  var _validateFlatten = function(flatten, root) {
    var queue = [[root, 0]];
    var length = 0;
    while(queue.length > 0) {
      var element = queue.shift();
      var node = element[0];
      var depth = element[1];
      assert.equal(node.depth, depth);
      length++;
      if (node.children) {
        node.children.forEach(function(child) {
          queue.push([child, depth + 1]);
        });
      }
    }
    assert.equal(flatten.length, length);
  };
});