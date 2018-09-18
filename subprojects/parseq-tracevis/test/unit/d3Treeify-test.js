var assert = require('./assert'),
    CDigraph = require('graphlib').CDigraph,
    d3Treeify = require('../../lib/trace/d3Treeify');

describe('d3Treeify', function() {
  it('can treeify a single node', function() {
    var g = new CDigraph();

    var n1 = createNode();
    n1.name = 'foo';
    n1.value = 'bar';
    g.addNode(1, n1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.deepPropertyVal(root, 'children[0].name', n1.name);
    assert.deepPropertyVal(root, 'children[0].value', n1.value);
  });

  it('adds a totalMillis property derived from startNanos and endNanos', function() {
    var g = new CDigraph();

    var n1 = createNode();
    n1.startNanos = 50 * 1000 * 1000;
    n1.endNanos  = 125 * 1000 * 1000;
    g.addNode(1, n1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.deepPropertyVal(root, 'children[0].totalMillis', 75);
  });

  it('adds a runMillis property derived from startNanos and pendingNanos', function() {
    var g = new CDigraph();

    var n1 = createNode();
    n1.startNanos = 50 * 1000 * 1000;
    n1.pendingNanos  = 125 * 1000 * 1000;
    g.addNode(1, n1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.deepPropertyVal(root, 'children[0].runMillis', 75);
  });

  it('does not add a runMillis property if pendingNanos is missing', function() {
    var g = new CDigraph();

    var n1 = createNode();
    n1.startNanos = 50 * 1000 * 1000;
    g.addNode(1, n1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.notDeepProperty(root, 'children[0].runMillis');
  });


  it('can handle a shallow tree of nodes', function() {
    var g = new CDigraph();

    var n1 = createNode();
    var n2 = createNode();
    n2.name = 'some other node';
    n2.value = 'some other value';
    g.addNode(1, n1);
    g.addNode(2, n2);
    g.parent(2, 1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.deepPropertyVal(root, 'children[0].children[0].id', 2);
    assert.deepPropertyVal(root, 'children[0].children[0].name', n2.name);
    assert.deepPropertyVal(root, 'children[0].children[0].value', n2.value);
  });

  it('can handle deeply nested trees', function() {
    var g = new CDigraph();
    g.addNode(1, createNode());
    g.addNode(2, createNode());
    g.parent(2, 1);
    g.addNode(3, createNode());
    g.parent(3, 1);
    g.addNode(4, createNode());
    g.parent(4, 3);
    g.addNode(5, createNode());
    g.parent(5, 3);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].id', 1);
    assert.deepPropertyVal(root, 'children[0].children[0].id', 2);
    assert.deepPropertyVal(root, 'children[0].children[1].id', 3);
    assert.deepPropertyVal(root, 'children[0].children[1].children[0].id', 4);
    assert.deepPropertyVal(root, 'children[0].children[1].children[1].id', 5);
  });

  it('sets start time relative to smallest start time in the graph', function() {
    var g = new CDigraph();
    var n1 = createNode();
    n1.startNanos =  50 * 1000 * 1000;
    n1.endNanos =   500 * 1000 * 1000;
    var n2 = createNode();
    n2.startNanos = 100 * 1000 * 1000;
    n2.endNanos   = 300 * 1000 * 1000;

    g.addNode(1, n1);
    g.addNode(2, n2);
    g.parent(2, 1);

    var root = d3Treeify(g);
    assert.deepPropertyVal(root, 'children[0].startMillis', 0);
    assert.deepPropertyVal(root, 'children[0].totalMillis', 450);
    assert.deepPropertyVal(root, 'children[0].children[0].startMillis', 50);
    assert.deepPropertyVal(root, 'children[0].children[0].totalMillis', 200);
  });

  it('works for two root nodes', function() {
    var g = new CDigraph();
    g.addNode(1, createNode());
    g.addNode(2, createNode());

    var root = d3Treeify(g);
    assert.lengthOf(root.children, 2);
    assert.sameMembers(root.children.map(function(x) { return x.id; }), [1, 2]);
  });

  it('fails for an empty graph', function() {
    assert.throws(function() { d3Treeify(new CDigraph()); });
  });
});

function createNode() {
  return {
    hidden: false,
    systemHidden: false,
    name: 'unset name',
    value: 'unset value',
    resultType: 'SUCCESS',
    startNanos: 0,
    endNanos: 0
  };
}
