/*
 * Copyright 2016 LinkedIn, Inc
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

function ExecutionUnit(id, start, end) {
  this._id = id;
  this._start = start;
  this._end = end;
  this._parent = null;
  this._children = {};
  this._predecessors = {};
  this._successors = {};
}

function CriticalPathComputationException(msg) {
  this.message = msg;
}

function generateExecutionUnits(g) {
  var executionUnits = new Object();

  // Create execution units from all traces that have start and end timestamps
  g.eachNode(function(key, value) {
    if (value.hasOwnProperty("startNanos") && value.hasOwnProperty("endNanos")) {
      executionUnits[key.toString()] = new ExecutionUnit(key.toString(), value.startNanos, value.endNanos);
    }
  });

  // Add successor edges between execution units
  g.eachEdge(function(key, to, from, value) {
    if (value.relationship == "SUCCESSOR" && executionUnits.hasOwnProperty(from.toString())
        && executionUnits.hasOwnProperty(to.toString())) {
      executionUnits[to.toString()]._successors[from.toString()] = true;
      executionUnits[from.toString()]._predecessors[to.toString()] = true;
    }
  });

  // Add parent edges between execution units
  g.eachNode(function(key, value) {
    if (value.hasOwnProperty("startNanos") && value.hasOwnProperty("endNanos") && g.parent(key) != null
        && g.node(g.parent(key)).hasOwnProperty("startNanos") && g.node(g.parent(key)).hasOwnProperty("endNanos")) {
      executionUnits[key.toString()]._parent = g.parent(key).toString();
      executionUnits[g.parent(key).toString()]._children[key.toString()] = true;
    }
  });

  // Potential parents and possible successors can be ignored
  // because they describe what could have happened and not what actually happened
  // therefore they're irrelevant for critical path

  return executionUnits;
}

// Removes the original parent trace
// and adds two new traces for its start and end
// translating parent-child relations to predecessor-successor relations
function splitParent(key, executionUnits) {
  var parent = executionUnits[key];

  // Computation of first start and last end of children
  var minStartTime = Number.MAX_SAFE_INTEGER;
  var maxEndTime = Number.MIN_SAFE_INTEGER;

  for (var x in parent._children) {
    if (executionUnits[x]._start < minStartTime) {
      minStartTime = executionUnits[x]._start;
    }
    if (executionUnits[x]._end > maxEndTime) {
      maxEndTime = executionUnits[x]._end;
    }
  }

  // Start time
  if (minStartTime < parent._start) {
    throw new CriticalPathComputationException("Children cannot start before their parent!");
  }

  // Amendment of end timestamp - sometimes a child can terminate after the parent
  var finalTime = parent._end;
  if (maxEndTime > finalTime) {
    finalTime = maxEndTime;
  }

  var keyFront = key + ":front";
  var keyBack = key + ":back";

  // Initialization of new units
  var front = new ExecutionUnit(keyFront, parent._start, minStartTime);
  var back = new ExecutionUnit(keyBack, maxEndTime, finalTime);

  // Correction of predecessor pointers
  front._predecessors = parent._predecessors;
  for (var x in front._predecessors) {
    delete executionUnits[x]._successors[key];
    executionUnits[x]._successors[keyFront] = true;
  }

  // Correction of successor pointers
  back._successors = parent._successors;
  for (var x in back._successors) {
    delete executionUnits[x]._predecessors[key];
    executionUnits[x]._predecessors[keyBack] = true;
  }

  // Corrections of eventual parent pointer
  if (parent._parent != null) {
    front._parent = parent._parent;
    back._parent = parent._parent;
    delete executionUnits[parent._parent]._children[key];
    executionUnits[parent._parent]._children[keyFront] = true;
    executionUnits[parent._parent]._children[keyBack] = true;
  }

  // Translation of children
  for (var x in parent._children) {
    executionUnits[x]._parent = null;
    if (Object.keys(executionUnits[x]._predecessors).length == 0) {
      executionUnits[x]._predecessors[keyFront] = true;
      front._successors[x] = true;
    }

    if (Object.keys(executionUnits[x]._successors).length == 0) {
      executionUnits[x]._successors[keyBack] = true;
      back._predecessors[x] = true;
    }
  }

  // Inserting new execution units and deleting the old one
  executionUnits[keyFront] = front;
  executionUnits[keyBack] = back;
  delete executionUnits[key];
}

// Breaks each parent trace to two new traces
// translates all parent-child relations to predecessor-successor relations
function parentFission(executionUnits) {
  var keys = Object.keys(executionUnits);

  for (var key in keys) {
    if (Object.keys(executionUnits[keys[key]]._children).length > 0) {
      splitParent(keys[key], executionUnits);
    }
  }
}

// https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
function topologicalSorting(executionUnits) {
  var copy = new Object();
  for (var x in executionUnits) {
    copy[x] = executionUnits[x];
  }
  var list = [];
  var visited = new Object();
  while (Object.keys(copy).length > 0) {
    var unit = copy[Object.keys(copy)[0]];
    visit(copy, visited, list, unit);
  }

  // List must be reverted - see visit function
  var result = [];
  for (var x = list.length - 1; x >= 0; --x) {
    result.push(list[x]);
  }
  return result;
}

function visit(unmarked, temporarilyMarked, list, unit) {
  if (temporarilyMarked.hasOwnProperty(unit._id)) {
    throw new CriticalPathComputationException("Topological sorting doesn't exist, graph is not DAG!");
  }

  temporarilyMarked[unit._id] = true;
  for (var successor in unit._successors) {
    if (unmarked.hasOwnProperty(successor)) {
      visit(unmarked, temporarilyMarked, list, unmarked[successor]);
    }
  }
  delete unmarked[unit._id];
  delete temporarilyMarked[unit._id];

  // Adding items on the back of the list - it will be reverted finally
  // Original wikipedia algorithm version adds them to the front
  list.push(unit._id);
}

// Trivial algorithm - just linearly follows the topological order
// and assigns the maximal endOffset of it's predecessors as start offset
function computeOffsets(executionUnits, order) {
  for (var x in order) {
    var unit = executionUnits[order[x]];
    var parent = null;
    var startOffset = 0;
    for (var i in unit._predecessors) {
      if (executionUnits[i]._endOffset > startOffset) {
        startOffset = executionUnits[i]._endOffset;
        parent = i;
      }
    }
    unit._endOffset = unit._end - unit._start + startOffset;
    unit._parent = parent;
    executionUnits[order[x]] = unit;
  }
}


// Actually creates map
// IDs of tasks which are on the critical path are mapped to true
function extractCriticalPath(executionUnits) {

  // First it finds the task with highest endOffset - that is end of the critical path
  var maxEndOffset = 0;
  var tail = null;
  for (var x in executionUnits) {
    if (executionUnits[x]._endOffset > maxEndOffset) {
      maxEndOffset = executionUnits[x]._endOffset;
      tail = x;
    }
  }

  // Adding tail to critical path
  var items = [];
  if (tail == null) {
    return result;
  } else {
    items.push(tail);
  }

  // Follows parent links until execution unit with no parent is found - it's the start of the critical path
  while (executionUnits[tail]._parent != null) {
    tail = executionUnits[tail]._parent;
    items.push(tail);
  }

  // Converts task identifiers back to numbers
  var result = new Object();
  for (var x = 0; x < items.length; ++x) {
    var id;
    if (items[x].indexOf(':') != -1) {
      id = items[x].substring(0, items[x].indexOf(':'));
    } else {
      id = items[x];
    }
    result[id] = true;
  }
  return result;
}

exports.computeCriticalPath = function(g) {
  var executionUnits = generateExecutionUnits(g);
  parentFission(executionUnits);
  var order = topologicalSorting(executionUnits);
  computeOffsets(executionUnits, order);
  return extractCriticalPath(executionUnits);
}
