var assert = require('./assert'),
    parseFunc = require('../..').trace.parse;

// Slightly modify the parse function to ensure required properties are always
// present.
function parse(json) {
  json.traces.forEach(function(trace) {
    if (trace.startNanos === undefined)
      trace.startNanos = 0;
  });
  return parseFunc(json);
}

describe('parse', function() {
  it('can parse a task\'s name', function() {
    var g = parse({ traces: [ { id: 1, name: 'foo' } ] });
    assert.propertyVal(g.node(1), 'name', 'foo');
  });

  it('can parse a task\'s value', function() {
    var g = parse({ traces: [ { id: 1, value: 'bar' } ] });
    assert.propertyVal(g.node(1), 'value', 'bar');
  });

  it('can parse a hidden task', function() {
    var g = parse({ traces: [ { id: 1, hidden: true } ] });
    assert.propertyVal(g.node(1), 'hidden', true);
  });

  it('can parse a system hidden task', function() {
    var g = parse({ traces: [ { id: 1, systemHidden: true } ] });
    assert.propertyVal(g.node(1), 'systemHidden', true);
  });

  it('can parse a task\'s startNanos', function() {
    var g = parse({ traces: [ { id: 1, startNanos: 50 } ] });
    assert.propertyVal(g.node(1), 'startNanos', 50);
  });

  it('sets a task\'s undefined pendingNanos to snapshotNanos, if available', function() {
    var g = parse({ snapshotNanos: 200, traces: [ { id: 1 } ] });
    assert.propertyVal(g.node(1), 'pendingNanos', 200);
  });

  it('sets a task\'s undefined pendingNanos to the greatest time in the trace, if snapshotNanos is undefined', function() {
    var g = parse({ traces: [ { id: 1 }, { id: 2, endNanos: 50 } ] });
    assert.propertyVal(g.node(1), 'pendingNanos', 50);
  });

  it('can parse a task\'s endNanos', function() {
    var g = parse({ traces: [ { id: 1, endNanos: 100 } ] });
    assert.propertyVal(g.node(1), 'endNanos', 100);
  });

  it('sets a task\'s undefined endNanos to snapshotNanos, if available', function() {
    var g = parse({ snapshotNanos: 200, traces: [ { id: 1 } ] });
    assert.propertyVal(g.node(1), 'endNanos', 200);
  });

  it('sets a task\'s undefined endNanos to the greatest time in the trace, if snapshotNanos is undefined #1', function() {
    var g = parse({ traces: [ { id: 1 }, { id: 2, endNanos: 50 } ] });
    assert.propertyVal(g.node(1), 'endNanos', 50);
  });

  it('sets a task\'s undefined endNanos to the greatest time in the trace, if snapshotNanos is undefined #2', function() {
    var g = parse({ traces: [ { id: 1 }, { id: 2, pendingNanos: 50 } ] });
    assert.propertyVal(g.node(1), 'endNanos', 50);
  });

  it('can parse a task\'s resultType', function() {
    var g = parse({ traces: [ { id: 1, resultType: 'SUCCESS' } ] });
    assert.propertyVal(g.node(1), 'resultType', 'SUCCESS');
  });

  it('can parse a composed task', function() {
    var g = parse({
      traces: [ createTaskJson(1), createTaskJson(2) ],
      relationships: [ { from: 1, to: 2, relationship: 'PARENT_OF' } ]
    });
    assertHasParent(g, 2, 1);
  });

  it('can parse a task sequence', function() {
    var g = parse({
      traces: [ createTaskJson(1), createTaskJson(2), createTaskJson(3) ],
      relationships: [
        { from: 1, to: 2, relationship: 'PARENT_OF' },
        { from: 1, to: 3, relationship: 'PARENT_OF' },
        { from: 3, to: 2, relationship: 'SUCCESSOR_OF' }
      ]
    });
    assertHasParent(g, 2, 1);
    assertHasParent(g, 3, 1);
    assertHasSuccessor(g, 2, 3);
  });

  it('can parse a potential predecessor relationship', function() {
    var g = parse({
      traces: [ createTaskJson(1), createTaskJson(2), createTaskJson(3) ],
      relationships: [
        { from: 1, to: 2, relationship: 'PARENT_OF' },
        { from: 1, to: 3, relationship: 'PARENT_OF' },
        { from: 3, to: 2, relationship: 'POSSIBLE_SUCCESSOR_OF' }
      ]
    });
    assertHasPotentialSuccessor(g, 2, 3);
  });

  it('can parse a potential parent relationship', function() {
    var g = parse({
      traces: [ createTaskJson(1), createTaskJson(2) ],
      relationships: [ { from: 1, to: 2, relationship: 'POTENTIAL_PARENT_OF' } ]
    });
    assertHasPotentialParent(g, 2, 1);
  });

  it('adds sort info by chronology', function() {
    var t1 = createTaskJson(1);
    t1.startNanos = 0;
    var t2 = createTaskJson(2);
    t2.startNanos = 20;
    var t3 = createTaskJson(3);
    t3.startNanos = 10;

    var g = parse({
      traces: [t1, t2, t3],
    });
    assert.propertyVal(g.node(1), 'order', 0);
    assert.propertyVal(g.node(3), 'order', 1);
    assert.propertyVal(g.node(2), 'order', 2);
  });

  it('adds sort info by hierarchy, then chronology', function() {
    var t1 = createTaskJson(1);
    t1.startNanos = 0;
    var t2 = createTaskJson(2);
    t2.startNanos = 20;
    var t3 = createTaskJson(3);
    t3.startNanos = 30;
    var t4 = createTaskJson(4);
    t4.startNanos = 5;
    var t5 = createTaskJson(5);
    t5.startNanos = 25;

    var g = parse({
      traces: [t1, t2, t3, t4, t5],
      relationships: [
        { from: 1, to: 2, relationship: 'PARENT_OF' },
        { from: 2, to: 3, relationship: 'PARENT_OF' },
        { from: 1, to: 4, relationship: 'PARENT_OF' },
        { from: 4, to: 5, relationship: 'PARENT_OF' }
      ]
    });
    assert.propertyVal(g.node(1), 'order', 0);
    assert.propertyVal(g.node(4), 'order', 1);
    assert.propertyVal(g.node(5), 'order', 2);
    assert.propertyVal(g.node(2), 'order', 3);
    assert.propertyVal(g.node(3), 'order', 4);
  });
});

function createTaskJson(id) {
  return { id: id };
}

function assertHasParent(g, child, parent) {
  assert.equal(g.parent(child), parent);
}

function assertHasEdgeOfType(g, u, v, type) {
  var edges = g.outEdges(u, v);
  assert.isTrue(edges.length > 0, 'Should have had at least one edge ' + u + ' -> ' + v);
  var matches = edges.map(function(e) { return g.edge(e).relationship; })
                     .filter(function(rel) { return rel === type; });
  assert.lengthOf(matches, 1);
}

function assertHasSuccessor(g, u, v) {
  assertHasEdgeOfType(g, u, v, 'SUCCESSOR');
}

function assertHasPotentialSuccessor(g, u, v) {
  assertHasEdgeOfType(g, u, v, 'POTENTIAL_SUCCESSOR');
}

function assertHasPotentialParent(g, child, parent) {
  assertHasEdgeOfType(g, child, parent, 'POTENTIAL_PARENT');
}
