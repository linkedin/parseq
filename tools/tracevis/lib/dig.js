/*
Copyright (c) 2012 Chris Pettitt

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
(function() {
  dig = {};
dig.version = "0.0.4";
function dig_util_forEach(array, func) {
  for (var i = 0; i < array.length; ++i) {
    func(array[i]);
  }
}

var dig_util_defineProperty = (function() {
  if (Object.defineProperty) {
    return function(obj, property, value) {
      Object.defineProperty(obj, property, {value: value});
    };
  } else {
    return function(obj, property, value) {
      obj[property] = value;
    };
  }
})();

function dig_util_objToArr(obj) {
  var arr = [];
  for (var k in obj) {
    arr.push(k);
  }
  return arr;
}

function dig_util_all(arr, func) {
  for (var i = 0; i < arr.length; ++i) {
    if (!func(arr[i])) {
      return false;
    }
  }

  return true;
}
dig.data = {};
var dig_data_PriorityQueue = dig.data.PriorityQueue = (function() {
  function PriorityQueue() {
    if (!(this instanceof PriorityQueue)) {
      throw new Error("Constructor called without using `new`");
    }

    dig_util_defineProperty(this, "_arr", []);
    dig_util_defineProperty(this, "_keyIndices", {});
  }

  PriorityQueue.prototype = {
    size: function() {
      return this._arr.length;
    },

    keys: function() {
      return dig_util_objToArr(this._keyIndices);
    },

    has: function(key) {
      return key in this._keyIndices;
    },

    priority: function(key) {
      var index = this._keyIndices[key];
      if (index !== undefined) {
        return this._arr[index].pri;
      }
    },

    add: function(key, pri) {
      if (!(key in this._keyIndices)) {
        var entry = {key: key, pri: pri};
        var index = this._arr.length;
        this._keyIndices[key] = index;
        this._arr.push(entry);
        _decrease(this, index);
        return true;
      }
      return false;
    },

    min: function() {
      if (this.size() > 0) {
        return this._arr[0].key;
      }
    },

    removeMin: function() {
      _swap(this, 0, this._arr.length - 1);
      var min = this._arr.pop();
      delete this._keyIndices[min.key];
      _heapify(this, 0);
      return min.key;
    },

    decrease: function(key, pri) {
      var index = this._keyIndices[key];
      if (pri > this._arr[index].pri) {
        throw new Error("New priority is greater than current priority. " +
            "Key: " + key + " Old: " + this._arr[index].pri + " New: " + pri);
      }
      this._arr[index].pri = pri;
      _decrease(this, index);
    }
  };

  function _heapify(self, i) {
    var arr = self._arr;
    var l = 2 * i,
        r = l + 1,
        largest = i;
    if (l < arr.length) {
      largest = arr[l].pri < arr[largest].pri ? l : largest;
      if (r < arr.length) {
        largest = arr[r].pri < arr[largest].pri ? r : largest;
      }
      if (largest !== i) {
        _swap(self, i, largest);
        _heapify(self, largest);
      }
    }
  }

  function _decrease(self, index) {
    var arr = self._arr;
    var pri = arr[index].pri;
    var parent;
    while (index > 0) {
      parent = index >> 1;
      if (arr[parent].pri < pri) {
        break;
      }
      _swap(self, index, parent);
      index = parent;
    }
  }

  function _swap(self, i, j) {
    var arr = self._arr;
    var keyIndices = self._keyIndices;
    var tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
    keyIndices[arr[i].key] = i;
    keyIndices[arr[j].key] = j;
  }

  return PriorityQueue;
})();
var dig_data_Queue = dig.data.Queue = (function() {
  function Queue(arr) {
    if (!(this instanceof Queue)) {
      throw new Error("Constructor called without using `new`");
    }

    dig_util_defineProperty(this, "_data", {
      size: 0,
      head: undefined,
      tail: undefined
    });

    if (arr) {
      for (var i = 0; i < arr.length; ++i) {
        this.enqueue(arr[i]);
      }
    }
  };

  Queue.prototype = {
    size: function() { return this._data.size; },

    enqueue: function(elem) {
      var data = this._data;

      if (data.size === 0) {
        data.head = data.tail = { value: elem };
      } else {
        data.tail = data.tail.next = { value: elem };
      }
      data.size++;
    },

    dequeue: function() {
      var data = this._data;

      if (data.size > 0) {
        var value = data.head.value;
        data.head = data.head.next;
        data.size--;
        return value;
      }
    }
  };

  return Queue;
})();
var dig_data_Stack = dig.data.Stack = (function() {
  function Stack() {
    if (!(this instanceof Stack)) {
      throw new Error("Constructor called without using `new`");
    }

    dig_util_defineProperty(this, "_data", {
      stack: [],
      onStack: {}
    });
  }

  Stack.prototype = {
    size: function() { return this._data.stack.length; },

    push: function(elem) {
      var onStack = this._data.onStack[elem] || 0;
      this._data.onStack[elem] = onStack + 1;
      this._data.stack.push(elem);
    },

    pop: function() {
      if (this.size() == 0) {
        throw new Error("stack underflow");
      }
      var top = this._data.stack.pop();

      var onStack = (this._data.onStack[top] -= 1);
      if (!onStack) {
        delete this._data.onStack[top];
      }

      return top;
    },

    has: function(elem) {
      return elem in this._data.onStack;
    }
  };

  return Stack;
})();
dig.Graph = (function() {
  function _copyObj(obj) {
    var copy = {};
    for (var k in obj) {
      if (obj.hasOwnProperty(k)) {
        copy[k] = obj[k];
      }
    }
    return copy;
  }

  function _safeGetNode(graph, node) {
    var nodes = graph._nodes;
    if (!(node in nodes)) {
      throw new Error("Node not in graph: " + node);
    }
    return nodes[node];
  }

  function Graph(graph) {
    var nodes = this._nodes = {};
    this._order = 0;
    if (graph) {
      var graphNodes = graph._nodes;
      for (i in graphNodes) {
        var node = graphNodes[i];
        nodes[i] = {
          predecessors: _copyObj(node.predecessors),
          successors: _copyObj(node.successors)
        };
        this._order++;
      };
    }
  }

  Graph.prototype = {
    order: function() {
      return this._order;
    },

    size: function() {
      return this.edges().length;
    },

    nodes: function() {
      return dig_util_objToArr(this._nodes);
    },

    edges: function() {
      var edges = [];
      for (var i in this._nodes) {
        for (var j in this._nodes[i].successors) {
          edges.push({from: i, to: j});
        };
      };
      return edges;
    },

    sources: function() {
      var sources = [];
      var self = this;
      dig_util_forEach(this.nodes(), function(i) { 
        if (self.indegree(i) == 0) {
          sources.push(i);
        }
      });
      return sources;
    },

    sinks: function() {
      var sinks = [];
      var self = this;
      dig_util_forEach(this.nodes(), function(i) {
        if (self.outdegree(i) === 0) {
          sinks.push(i);
        }
      });
      return sinks;
    },

    copy: function() {
      return new Graph(this);
    },

    hasNode: function(node) {
      return node in this._nodes;
    },

    addNode: function(node) {
      if (!this.hasNode(node)) {
        this._nodes[node] = {
          predecessors: {},
          successors: {}
        };
        this._order++;
        return true;
      }
      return false;
    },

    addNodes: function() {
      for (var i = 0; i < arguments.length; ++i) {
        this.addNode(arguments[i]);
      }
    },

    removeNode: function(node) {
      var self = this;
      if (this.hasNode(node)) {
        dig_util_forEach(this.predecessors(node), function(i) {
          self.removeEdge(i, node);
        });
        dig_util_forEach(this.successors(node), function(k) {
          self.removeEdge(node, k);
        });
        delete this._nodes[node];
        this._order--;
        return true;
      }
      return false;
    },

    hasEdge: function(from, to) {
      return this.hasNode(from) && to in this._nodes[from].successors;
    },

    addEdge: function(from, to) {
      var fromNode = _safeGetNode(this, from);
      var toNode = _safeGetNode(this, to);
      if (!this.hasEdge(from, to)) {
        fromNode.successors[to] = true;
        toNode.predecessors[from] = true;
        return true;
      }
      return false;
    },

    addPath: function() {
      var prev, curr;
      if (arguments.length > 1) {
        prev = arguments[0];
        for (var i = 1; i < arguments.length; ++i) {
          curr = arguments[i];
          this.addEdge(prev, curr);
          prev = curr;
        }
      }
    },

    removeEdge: function(from, to) {
      if (this.hasEdge(from, to)) {
        delete this._nodes[from].successors[to];
        delete this._nodes[to].predecessors[from];
        return true;
      }
      return false;
    },

    indegree: function(node) {
      return this.inEdges(node).length;
    },

    outdegree: function(node) {
      return this.outEdges(node).length;
    },

    degree: function(node) {
      return this.indegree(node) + this.outdegree(node);
    },

    inEdges: function(node) {
      var edges = [];
      var preds = this.predecessors(node);
      for (var i = 0; i < preds.length; i++) {
        edges.push({from: preds[i], to: node});
      }
      return edges;
    },

    outEdges: function(node) {
      var edges = [];
      var sucs = this.successors(node);
      for (var i = 0; i < sucs.length; i++) {
        edges.push({from: node, to: sucs[i]});
      };
      return edges;
    },

    predecessors: function(node) {
      return dig_util_objToArr(_safeGetNode(this, node).predecessors);
    },

    successors: function(node) {
      return dig_util_objToArr(_safeGetNode(this, node).successors);
    },

    neighbors: function(node) {
      return this.predecessors(node).concat(this.successors(node));
    },

    isAcyclic: function() {
      var components = dig_alg_tarjan(this);
      var self = this;
      return dig_util_all(components, function(component) {
        var v = component[0];
        return component.length === 1 && !self.hasEdge(v, v);
      });
    },

    equals: function(graph) {
      return this.order() === graph.order() &&
             dig_util_all(this.nodes(), function(v) { return graph.hasNode(v); }) &&
             dig_util_all(this.edges(), function(e) { return graph.hasEdge(e.from, e.to); });
    }
  };

  return Graph;
})();
dig.alg = {};
// Algorithm derived from: 
// http://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm
var dig_alg_tarjan = dig.alg.tarjan = function(graph) {
  var index = 0;
  var stack = new dig.data.Stack();
  var visited = {}; // node -> index + lowlink
  var results = [];

  function scc(v) {
    var vEntry;

    vEntry = visited[v] = {
      index: index,
      lowlink: index
    };
    index++;
    stack.push(v);

    dig_util_forEach(graph.successors(v), function(w) {
      if (!(w in visited)) {
        scc(w);
        vEntry.lowlink = Math.min(vEntry.lowlink, visited[w].lowlink);
      } else if (stack.has(w)) {
        vEntry.lowlink = Math.min(vEntry.lowlink, visited[w].index);
      }
    });

    var component;
    var w;
    if (vEntry.lowlink == vEntry.index) {
      component = [];
      do {
        w = stack.pop();
        component.push(w);
      } while (w !== v);
      results.push(component);
    }
  }

  dig_util_forEach(graph.nodes(), function(v) {
    if (!(v in visited)) {
      scc(v);
    }
  });

  return results;
};
var dig_alg_topsort = dig.alg.topsort = function(graph) {
  var visited = {};
  var stack = {};
  var results = [];

  function visit(node) {
    if (node in stack) {
      throw new Error("Graph has at least one cycle!");
    }

    if (!(node in visited)) {
      stack[node] = true;
      visited[node] = true;
      dig_util_forEach(graph.predecessors(node), function(pred) {
        visit(pred);
      });
      delete stack[node];
      results.push(node);
    }
  }

  var sinks = graph.sinks();
  if (graph.order() != 0 && sinks.length == 0) {
    throw new Error("Graph has at least one cycle!");
  }

  dig_util_forEach(graph.sinks(), function(sink) {
    visit(sink);
  });

  return results;
}
/*
 * Implementation derived from wikipedia: http://en.wikipedia.org/wiki/Dijkstra's_algorithm
 */
var dig_alg_dijkstra = dig.alg.dijkstra = function(graph, source) {
  var results = {};
  var q = new dig_data_PriorityQueue();
  var maxDist = Number.POSITIVE_INFINITY;
  var nodeU;
  var u, v;
  var altDist;

  dig_util_forEach(graph.nodes(), function(node) {
    var distance = node == source ? 0 : maxDist;
    results[node] = { distance: distance, predecessor: null };
    q.add(node, distance);
  });

  while (q.size() > 0) {
    nodeU = q.removeMin();
    u = results[nodeU];
    if (u.distance === maxDist) {
      break;
    } 

    dig_util_forEach(graph.successors(nodeU), function(nodeV) {
      v = results[nodeV];
      // TODO: support weighted edges
      altDist = u.distance + 1;
      if (altDist < v.distance) {
        v.distance = altDist;
        v.predecessor = nodeU;
        q.decrease(nodeV, v.distance);
      }
    });
  }

  return results;
};
/*
 * Floyd Warshall algorithm derived from Wikipedia:
 * http://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
 */
var dig_alg_floydWarshall = dig.alg.floydWarshall = function(graph) {
  var results = {};
  var nodes = graph.nodes();
  var altDistance;
  var rowI, rowK;
  var ik, kj, ij;
  var maxDist = Number.POSITIVE_INFINITY;

  dig_util_forEach(nodes, function(i) {
    rowI = results[i] = {};
    dig_util_forEach(nodes, function(j) {
      if (i == j) {
        rowI[j] = { distance: 0, predecessor: null };  
      } else if (graph.hasEdge(i, j)) {
        rowI[j] = { distance: 1, predecessor: i };
      } else {
        rowI[j] = { distance: maxDist, predecessor: null };
      }
    });
  });

  dig_util_forEach(nodes, function(k) {
    rowK = results[k];
    dig_util_forEach(nodes, function(i) {
      rowI = results[i];
      dig_util_forEach(nodes, function(j) {
        ik = rowI[k];
        kj = rowK[j];
        ij = rowI[j];
        altDistance = ik.distance + kj.distance;
        if (altDistance < ij.distance) {
          ij.distance = altDistance;
          ij.predecessor = kj.predecessor;
        }
      });
    });
  });

  return results;
}
dig.dot = {};

dig.dot.write = (function() {
  function id(obj) {
    return '"' + obj.toString().replace('"', '\\"') + '"';
  }

  return function(graph) {
    var str = "digraph {\n";

    dig_util_forEach(graph.nodes(), function(v) {
      str += "    " + id(v) + ";\n";
    });

    dig_util_forEach(graph.edges(), function(e) {
      str += "    " + id(e.from) + " -> " + id(e.to) + ";\n";
    });

    str += "}\n";
    return str;
  };
})();
})();
