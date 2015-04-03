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

var assert = require('./assert'),
    dotify = require('../../lib/trace/dotify'),
    CDigraph = require('graphlib').CDigraph;

describe('dotify', function() {
  describe('escapes', function() {
    ['<', '>', '|', '{', '}'].forEach(function(specialChar) {
      it('escapes `' + specialChar + '` for the Mrecord shape', function() {
        var g = new CDigraph();
        g.addNode(1, {
          name: 'before' + specialChar + 'after',
          startNanos: 0,
          endNanos: 0
        });
        assert.include(dotify(g).node(1).label, 'before\\' + specialChar + 'after');
      });
    });
  });

  describe('style', function() {
    it('styles nodes', function() {
      var g = new CDigraph();
      g.addNode(1, {
        name: 'foo task',
        startNanos: 0,
        pendingNanos: 250 * 1000 * 1000,
        endNanos: 500 * 1000 * 1000
      });

      var d = dotify(g);
      assert.equal(d.node(1).label, 'foo task\\l|{@0.000\\r|250.000\\r|+500.000\\r}');
      assert.equal(d.node(1).shape, 'Mrecord');
      assert.equal(d.node(1).style, 'filled');
    });

    it('styles composed tasks', function() {
      var g = new CDigraph();
      g.addNode('parent', {
        name: 'parent task',
        startNanos: 0,
        pendingNanos: 250 * 1000 * 1000,
        endNanos: 500 * 1000 * 1000
      });
      g.addNode('child', {
        startNanos: 300 * 1000 * 1000,
        pendingNanos: 400 * 1000 * 1000,
        endNanos:  450 * 1000 * 1000
      });
      g.parent('child', 'parent');

      var d = dotify(g);
      assert.propertyVal(d.node('cluster_parent'), 'label', 'parent task (@0.000, 250.000, +500.000)');
      assert.propertyVal(d.node('cluster_parent'), 'labeljust', 'l');
      assert.propertyVal(d.node('cluster_parent'), 'style', 'dashed');
      assert.propertyVal(d.node('cluster_parent'), 'color', '#cccccc');
    });

    it('styles source nodes', function() {
      var g = new CDigraph();
      g.addNode('parent');
      g.addNode('child');
      g.parent('child', 'parent');

      var d = dotify(g);
      assert.propertyVal(d.node('source_parent'), 'shape', 'circle');
      assert.propertyVal(d.node('source_parent'), 'style', 'filled');
      assert.propertyVal(d.node('source_parent'), 'label', '&nbsp;');
    });

    it('styles sink nodes', function() {
      var g = new CDigraph();
      g.addNode('parent');
      g.addNode('child');
      g.parent('child', 'parent');

      var d = dotify(g);
      assert.propertyVal(d.node('sink_parent'), 'shape', 'doublecircle');
      assert.propertyVal(d.node('sink_parent'), 'style', 'filled');
      assert.propertyVal(d.node('sink_parent'), 'label', '&nbsp;');
    });

    describe('colors', function() {
      var COLOR_MAP = {
        SUCCESS: '#e0ffe0',
        ERROR: '#ffe0e0',
        EARLY_FINISH: '#fffacd',
        ALL_OTHERS: '#cccccc'
      };

      Object.keys(COLOR_MAP).forEach(function(type) {
        it('colors a ' + type + ' task ' + COLOR_MAP[type], function() {
          var g = new CDigraph();
          g.addNode(1, {
            resultType: type
          });

          assert.equal(dotify(g).node(1).fillcolor, COLOR_MAP[type]);
        });

        it('colors a ' + type + ' sink node ' + COLOR_MAP[type], function() {
          var g = new CDigraph();
          g.addNode('parent', { resultType: type });
          g.addNode('child');
          g.parent('child', 'parent');

          assert.equal(dotify(g).node('sink_parent').fillcolor, COLOR_MAP[type]);
        });
      });
    });
  });

  describe('handles edges', function() {
    var g,
        root = 'root',
        rootCluster = 'cluster_' + root,
        rootSource = 'source_' + root,
        rootSink = 'sink_' + root;

    beforeEach(function() {
      g = new CDigraph();
      g.addNode(root, { startNanos: 0, pendingNanos: 0, endNanos: 0 });
    });

    it('adds source and sink nodes to composed tasks', function() {
      g.addNode('t1');
      g.parent('t1', root);
      assert.sameMembers(dotify(g).children(rootCluster),
                         [rootSource, 't1', rootSink]);
    });

    it('adds a solid edge from the source node to source tasks', function() {
      g.addNode('t1');
      g.parent('t1', root);
      assertEdgesFrom(dotify(g), rootSource, { t1: 'solid' });
    });

    it('does not add any edges to the source node', function() {
      g.addNode('t1');
      g.parent('t1', root);
      assert.lengthOf(dotify(g).predecessors(rootSource), 0);
    });

    it('adds a solid edge from sink tasks to the sink node', function() {
      g.addNode('t1');
      g.parent('t1', root);
      assertEdgesFrom(dotify(g), 't1', { 'sink_root': 'solid' });
    });

    it('adds a dashed edge from EARLY_FINISH sink tasks to the sink node', function() {
      g.addNode('t1', { resultType: 'EARLY_FINISH' });
      g.parent('t1', root);
      assertEdgesFrom(dotify(g), 't1', { 'sink_root': 'dashed' });
    });

    it('adds a dashed edge from EARLY_FINISH sink nodes to successor tasks', function() {
      g.parent(g.addNode('parent', { resultType: 'EARLY_FINISH' }), root);
      g.parent(g.addNode('t1'), 'parent');
      g.parent(g.addNode('t2'), root);
      g.addEdge(null, 'parent', 't2', { relationship: 'SUCCESSOR' });
      assertEdgesFrom(dotify(g), 'sink_parent', { 't2': 'dashed' });
    });

    it('does not add any edges from the sink node', function() {
      g.addNode('t1');
      g.parent('t1', root);
      assert.lengthOf(dotify(g).successors(rootSink), 0);
    });

    it('adds a solid edge between nodes with a successor relationship', function() {
      g.parent(g.addNode('t1'), root);
      g.parent(g.addNode('t2'), root);
      g.addEdge(null, 't1', 't2');
      assertEdgesFrom(dotify(g), 't1', { t2: 'solid' });
    });

    it('does not add an edge to the sink node from a non-sink target', function() {
      g.addNode('t1');
      g.parent('t1', root);
      g.addNode('t2');
      g.parent('t2', root);
      g.addEdge(null, 't1', 't2');
      assert.notInclude(dotify(g).predecessors(rootSink), ['t1']);
    });

    it('does not add an edge from the source node to a non-source target', function() {
      g.addNode('t1');
      g.parent('t1', root);
      g.addNode('t2');
      g.parent('t2', root);
      g.addEdge(null, 't1', 't2');
      assert.notInclude(dotify(g).successors(rootSource), ['t2']);
    });

    it('points edges that end in a compound task at the task\'s source', function() {
      g.addNode('t1');
      g.addNode('t2');
      g.parent('t2', 'root');
      g.addEdge(null, 't1', 'root');
      assertEdgesFrom(dotify(g), 't1', { source_root: 'solid' });
    });

    it('starts edges that start at a compound task from the task\'s sink', function() {
      g.addNode('t1');
      g.addNode('t2');
      g.parent('t2', 'root');
      g.addEdge(null, 'root', 't1');
      assertEdgesFrom(dotify(g), rootSink, { t1: 'solid' });
    });

    it('creates a dashed line for potential successor edges', function() {
      g.addNode('t1');
      g.addNode('t2');
      g.addEdge(null, 't1', 't2', { relationship: 'POTENTIAL_SUCCESSOR' });
      assertEdgesFrom(dotify(g), 't1', { t2: 'dashed' });
    });

    it('creates a dashed edge from a potential parent\'s source if there is no path from it\'s children to a task', function() {
      g.parent(g.addNode('t1'), root);
      g.addNode('pp');
      g.parent(g.addNode('t2'), 'pp');
      g.addEdge(null, 't1', 'pp', { relationship: 'POTENTIAL_PARENT' });
      assertEdge(dotify(g), 'source_pp', 't1', 'dashed');
    });

    it('creates a solid edge to a potential parent\'s sink if there is no path to it\'s children from a task', function() {
      g.parent(g.addNode('t1'), root);
      g.addNode('pp');
      g.parent(g.addNode('t2'), 'pp');
      g.addEdge(null, 't1', 'pp', { relationship: 'POTENTIAL_PARENT' });
      assertEdge(dotify(g), 't1', 'sink_pp', 'solid');
    });

    it('creates a dashed edge to a potential parent\'s sink if the task\'s result was EARLY_FINISH', function() {
      g.parent(g.addNode('t1', { resultType: 'EARLY_FINISH' }), root);
      g.addNode('pp');
      g.parent(g.addNode('t2'), 'pp');
      g.addEdge(null, 't1', 'pp', { relationship: 'POTENTIAL_PARENT' });
      assertEdge(dotify(g), 't1', 'sink_pp', 'dashed');
    });

    it('creates an invisible edge if the path to a task\'s closest sibling involves a non-sibling node', function() {
      g.parent(g.addNode('t1'), root);
      g.parent(g.addNode('t2'), root);
      g.addNode('p1');
      g.parent(g.addNode('t3'), 'p1');
      g.addEdge(null, 't1', 't3');
      g.addEdge(null, 't3', 't2');

      var d = dotify(g);
      assert.includeMembers(d.successors('t1'), ['t2']);
      assertEdge(d, 't1', 't2', 'invis');
    });

    it('creates edges with 2 level nested task', function() {
      g.parent(g.addNode('t1'), root);
      g.parent(g.addNode('t3'), root);
      g.parent(g.addNode('parent'), root);
      g.parent(g.addNode('t2'), 'parent');
      g.addEdge(null, 't1', 'parent');
      g.addEdge(null, 'parent', 't3');

      var d = dotify(g);
      assertEdgesFrom(d, rootSource,      { t1: 'solid' });
      assertEdgesFrom(d, 't1',            { source_parent: 'solid' });
      assertEdgesFrom(d, 'source_parent', { t2: 'solid' });
      assertEdgesFrom(d, 't2',            { 'sink_parent': 'solid' });
      assertEdgesFrom(d, 'sink_parent',   { 't3': 'solid' });
      assertEdgesFrom(d, 't3',            { sink_root: 'solid' });
    });

    it('creates an edge to the source of an implicitly sequenced nested task', function() {
      g.parent(g.addNode('parent'), root);
      g.parent(g.addNode('t1'), 'parent');
      assertEdgesFrom(dotify(g), rootSource, { source_parent: 'solid' });
    });

    it('creates an edge from the sink of an implicitly sequenced nested task', function() {
      g.parent(g.addNode('parent'), root);
      g.parent(g.addNode('t1'), 'parent');
      assertEdgesFrom(dotify(g), 'sink_parent', { sink_root: 'solid' });
    });

    it('creates an invisible edge from a source to child with a shortest path through another parent', function() {
      g.parent(g.addNode('p1'), root);
      g.parent(g.addNode('t1'), 'p1');
      g.parent(g.addNode('p2'), root);
      g.parent(g.addNode('t2'), 'p2');
      g.addEdge(null, 't2', 't1');
      g.addEdge(null, 't2', 'p1', { relationship: 'POTENTIAL_PARENT' });
      assertEdgesFrom(dotify(g), 'source_p1', { t1: 'invis', t2: 'dashed' });
    });

    it('creates an invisible edge to a sink from a child with a shortest path through another parent', function() {
      g.parent(g.addNode('p1'), root);
      g.parent(g.addNode('t1'), 'p1');
      g.parent(g.addNode('p2'), root);
      g.parent(g.addNode('t2'), 'p2');
      g.addEdge(null, 't1', 't2');
      g.addEdge(null, 't2', 'p1', { relationship: 'POTENTIAL_PARENT' });
      assertEdgesFrom(dotify(g), 't1', { sink_p1: 'invis', t2: 'solid' });
    });

    it('handles cases where a potential parent has no actual children', function() {
      // In this case the potential parent should still get a source and a sink node.
      g.parent(g.addNode('p1'), root);
      g.parent(g.addNode('t1'), root);
      g.addEdge(null, 't1', 'p1', { relationship: 'POTENTIAL_PARENT' });

      var d = dotify(g);
      assert.isTrue(d.hasNode('source_p1'));
      assert.isTrue(d.hasNode('sink_p1'));
      assertEdgesFrom(d, 'source_p1', { t1: 'dashed', sink_p1: 'invis' });
      assertEdge(d, 't1', 'sink_p1', 'solid');
    });

    it('passes old graphviz test for possible predecessors', function() {
      g.parent(g.addNode('t1'), root);
      g.parent(g.addNode('t2'), root);
      g.parent(g.addNode('t3'), root);
      g.addEdge(null, 't1', 't2');
      g.addEdge(null, 't2', 't3');
      g.addEdge(null, 't1', 't3', { relationship: 'POTENTIAL_SUCCESSOR' });

      var d = dotify(g);
      assertEdgesFrom(d, rootSource, { t1: 'solid' });
      assertEdgesFrom(d, 't1', { t2: 'solid', t3: 'dashed' });
      assertEdgesFrom(d, 't2', { t3: 'solid' });
      assertEdgesFrom(d, 't3', { sink_root: 'solid' });
    });

    it('passes old graphviz test for potential parents', function() {
      g.parent(g.addNode('p1'), root);
      g.parent(g.addNode('t1'), 'p1');
      g.parent(g.addNode('t2'), 'p1');
      g.parent(g.addNode('p2'), root);
      g.parent(g.addNode('t3'), 'p2');
      g.parent(g.addNode('t4'), 'p2');
      g.addEdge(null, 't1', 't2');
      g.addEdge(null, 't3', 't4');
      g.addEdge(null, 't4', 'p1', { relationship: 'POTENTIAL_PARENT' });

      var d = dotify(g);
      assertEdgesFrom(d, rootSource, { source_p1: 'solid', source_p2: 'solid' });

      assertEdgesFrom(d, 'source_p1', { t1: 'solid', t4: 'dashed' });
      assertEdgesFrom(d, 't1', { t2: 'solid' });
      assertEdgesFrom(d, 't2', { sink_p1: 'solid' });
      assertEdgesFrom(d, 'sink_p1', { sink_root: 'solid' });

      assertEdgesFrom(d, 'source_p2', { t3: 'solid' });
      assertEdgesFrom(d, 't3', { t4: 'solid' });
      assertEdgesFrom(d, 't4', { sink_p1: 'solid', sink_p2: 'solid' });
      assertEdgesFrom(d, 'sink_p2', { sink_root: 'solid' });
    });

    it('passes old graphviz test for potential parent with possible predecessor', function() {
      g.parent(g.addNode('p1'), root);
      g.parent(g.addNode('t1'), 'p1');
      g.parent(g.addNode('t2'), 'p1');
      g.parent(g.addNode('p2'), root);
      g.parent(g.addNode('t3'), 'p2');
      g.parent(g.addNode('t4'), 'p2');
      g.addEdge(null, 't1', 't2');
      g.addEdge(null, 't3', 't4');
      g.addEdge(null, 't2', 't4', { relationship: 'POTENTIAL_SUCCESSOR' });
      g.addEdge(null, 't4', 'p1', { relationship: 'POTENTIAL_PARENT' });

      var d = dotify(g);
      assertEdgesFrom(d, rootSource, { source_p1: 'solid', source_p2: 'solid' });

      assertEdgesFrom(d, 'source_p1', { t1: 'solid' });
      assertEdgesFrom(d, 't1', { t2: 'solid' });
      assertEdgesFrom(d, 't2', { sink_p1: 'invis', t4: 'dashed' });
      assertEdgesFrom(d, 'sink_p1', { sink_root: 'solid' });

      assertEdgesFrom(d, 'source_p2', { t3: 'solid' });
      assertEdgesFrom(d, 't3', { t4: 'solid' });
      assertEdgesFrom(d, 't4', { sink_p1: 'solid', sink_p2: 'solid' });
      assertEdgesFrom(d, 'sink_p2', { sink_root: 'solid' });
    });
  });
});

/*
 * Asserts that the only edges from the given node are included in the expected
 * map. The expected map provides a mapping between expected target and
 * the expected style.
 */
function assertEdgesFrom(g, u, expected) {
  assert.sameMembers(g.successors(u), Object.keys(expected));
  Object.keys(expected).forEach(function(v) {
    assertEdge(g, u, v, expected[v]);
  });
}

/*
 * Asserts that there is an edge from u to v and that it has the given style.
 */
function assertEdge(g, u, v, style) {
  assert.lengthOf(g.outEdges(u, v), 1);
  var e = g.outEdges(u, v)[0];
  assert.propertyVal(g.edge(e), 'style', style, 'Edge ' + u + ' -> ' + v);
}
