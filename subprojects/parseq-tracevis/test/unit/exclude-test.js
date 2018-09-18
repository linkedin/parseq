var assert = require('./assert'),
    CDigraph = require('graphlib').CDigraph,
    trace = require('../..').trace,
    excludeParentTasks = trace.excludeParentTasks;

var hiddenType = {
  excludeUserTasks: 'hidden',
  excludeSystemTasks: 'systemHidden'
};

Object.keys(hiddenType).forEach(function(fnName) {
  var prop = hiddenType[fnName],
      fn = trace[fnName];

  describe(fnName, function() {
    var g, a, b, c;

    beforeEach(function() {
      g = new CDigraph();
      a = g.addNode('a', {});
      b = g.addNode('b', {});
      c = g.addNode('c', {});
      addSuccessorEdge(g, 'a', 'b');
      addSuccessorEdge(g, 'b', 'c');
    });

    it('does not change a task without the ' + prop + ' property', function() {
      fn(g);
      assert.sameMembers(g.successors(a), [b]);
      assert.sameMembers(g.successors(b), [c]);
      assert.sameMembers(g.successors(c), []);
    });

    it('removes hidden nodes', function() {
      g.node(b)[prop] = true;
      fn(g);
      assert.isFalse(g.hasNode(b));
    });

    it('points in-edges to the removed node\'s successor', function() {
      g.node(b)[prop] = true;
      fn(g);
      assert.sameMembers(g.successors(a), [c]);
      assert.sameMembers(g.successors(c), []);
    });

    it('does not change the edges in a removed composite task', function() {
      g.node(b)[prop] = true;
      g.parent(g.addNode('d', {}), b);
      g.parent(g.addNode('e', {}), b);
      addSuccessorEdge(g, 'd', 'e');
      fn(g);
      assert.sameMembers(g.successors('d'), ['e']);
    });

    it('points in-edges to a removed composite node\'s source nodes', function() {
      g.node(b)[prop] = true;
      g.parent(g.addNode('d', {}), b);
      g.parent(g.addNode('e', {}), b);
      addSuccessorEdge(g, 'd', 'e');
      fn(g);
      assert.sameMembers(g.successors(a), ['d']);
    });

    it('points the sinks of removed composite node to it\'s successors', function() {
      g.node(b)[prop] = true;
      g.parent(g.addNode('d', {}), b);
      g.parent(g.addNode('e', {}), b);
      addSuccessorEdge(g, 'd', 'e');
      fn(g);
      assert.sameMembers(g.successors('e'), [c]);
    });
  });
});

describe('excludeParentTasks', function() {
  var g, a, b, c;

  beforeEach(function() {
    g = new CDigraph();
    a = g.addNode('a', {});
    b = g.addNode('b', {});
    c = g.addNode('c', {});
    addSuccessorEdge(g, 'a', 'b');
    addSuccessorEdge(g, 'b', 'c');
  });

  it('does not change a non-composite task', function() {
    excludeParentTasks(g);
    assert.sameMembers(g.successors(a), [b]);
    assert.sameMembers(g.successors(b), [c]);
    assert.sameMembers(g.successors(c), []);
  });

  it('removes composite nodes', function() {
    g.parent(g.addNode('d', {}), b);
    g.parent(g.addNode('e', {}), b);
    addSuccessorEdge(g, 'd', 'e');
    excludeParentTasks(g);
    assert.notInclude(g.nodes(), b);
  });

  it('properly links child nodes with nodes adjacent to the parent', function() {
    g.parent(g.addNode('d', {}), b);
    g.parent(g.addNode('e', {}), b);
    addSuccessorEdge(g, 'd', 'e');
    excludeParentTasks(g);
    assert.sameMembers(g.successors(a), ['d']);
    assert.sameMembers(g.successors('d'), ['e']);
    assert.sameMembers(g.successors('e'), [c]);
  });
});

function addSuccessorEdge(g, u, v) {
  g.addEdge(null, u, v, { relationship: 'SUCCESSOR' });
}
