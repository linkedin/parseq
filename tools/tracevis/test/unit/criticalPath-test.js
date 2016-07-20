var assert = require('./assert'),
    CDigraph = require('graphlib').CDigraph,
    trace = require('../..').trace,
    excludeNonCriticalTasks = trace.excludeNonCriticalTasks;

describe('excludeNonCriticalTasks', function() {
  var g, a, b, c;

  beforeEach(function() {
    g = new CDigraph();
  });

  it('independent tasks - picks the longest', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 10 });
    b = g.addNode('b', { startNanos: 5, endNanos: 16 });
    c = g.addNode('c', { startNanos: 10, endNanos: 19 });
    excludeNonCriticalTasks(trace, g);
    assert.notInclude(g.nodes(), a);
    assert.notInclude(g.nodes(), c);
    assert.include(g.nodes(), b);
  });

  it('two tasks longer than one', function() {
    a = g.addNode('a', { startNanos: 5, endNanos: 15 });
    b = g.addNode('b', { startNanos: 0, endNanos: 6 });
    c = g.addNode('c', { startNanos: 7, endNanos: 13 });
    addSuccessorEdge(g, 'b', 'c');
    excludeNonCriticalTasks(trace, g);
    assert.notInclude(g.nodes(), a);
    assert.include(g.nodes(), b);
    assert.include(g.nodes(), c);
    assert.sameMembers(g.successors(b), [c]);
  });

  it('one tasks longer than two', function() {
    a = g.addNode('a', { startNanos: 5, endNanos: 15 });
    b = g.addNode('b', { startNanos: 0, endNanos: 6 });
    c = g.addNode('c', { startNanos: 7, endNanos: 10 });
    addSuccessorEdge(g, 'b', 'c');
    excludeNonCriticalTasks(trace, g);
    assert.notInclude(g.nodes(), b);
    assert.notInclude(g.nodes(), c);
    assert.include(g.nodes(), a);
  });

  it('three tasks in sequence', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 5 });
    b = g.addNode('b', { startNanos: 6, endNanos: 10 });
    c = g.addNode('c', { startNanos: 11, endNanos: 15 });
    addSuccessorEdge(g, 'a', 'b');
    addSuccessorEdge(g, 'b', 'c');
    excludeNonCriticalTasks(trace, g);
    assert.include(g.nodes(), a);
    assert.include(g.nodes(), b);
    assert.include(g.nodes(), c);
  });

  it('parent of serial children', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 10 });
    b = g.addNode('b', { startNanos: 1, endNanos: 5 });
    c = g.addNode('c', { startNanos: 6, endNanos: 9 });
    g.parent(b, a);
    g.parent(c, a);
    addSuccessorEdge(g, 'b', 'c');
    excludeNonCriticalTasks(trace, g);
    assert.include(g.nodes(), a);
    assert.include(g.nodes(), b);
    assert.include(g.nodes(), c);
    assert.sameMembers(g.successors(b), [c]);
    assert.sameMembers(g.children(a), [b, c]);
  });

  it('parent of parallel children', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 10 });
    b = g.addNode('b', { startNanos: 1, endNanos: 9 });
    c = g.addNode('c', { startNanos: 2, endNanos: 9 });
    g.parent(b, a);
    g.parent(c, a);
    excludeNonCriticalTasks(trace, g);
    assert.include(g.nodes(), a);
    assert.include(g.nodes(), b);
    assert.notInclude(g.nodes(), c);
    assert.sameMembers(g.children(a), [b]);
  });

  it('three tasks in tree', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 10 });
    b = g.addNode('b', { startNanos: 1, endNanos: 10 });
    c = g.addNode('c', { startNanos: 11, endNanos: 15 });
    addSuccessorEdge(g, 'a', 'c');
    addSuccessorEdge(g, 'b', 'c');
    excludeNonCriticalTasks(trace, g);
    assert.include(g.nodes(), a);
    assert.include(g.nodes(), c);
    assert.notInclude(g.nodes(), b);
    assert.sameMembers(g.successors(a), [c]);
  });

  it('three tasks in cycle', function() {
    a = g.addNode('a', { startNanos: 0, endNanos: 10 });
    b = g.addNode('b', { startNanos: 0, endNanos: 10 });
    c = g.addNode('c', { startNanos: 0, endNanos: 10 });
    addSuccessorEdge(g, 'a', 'b');
    addSuccessorEdge(g, 'b', 'c');
    addSuccessorEdge(g, 'c', 'a');
    try {
      excludeNonCriticalTasks(trace, g);
      assert.fail();
    } catch (expected) { }
  });
});

function addSuccessorEdge(g, u, v) {
  g.addEdge(null, u, v, { relationship: 'SUCCESSOR' });
}