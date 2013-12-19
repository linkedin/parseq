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

/**
 * The TRACE module contains various method that make it easier to work with
 * ParSeq traces.
 */
TRACE = (function() {
  var _CHRONOLOGICAL = function(d, e) {
    return e.startNanos - d.startNanos;
  };

  /**
   * Returns the first element in the array that satisfies the predicate or
   * undefined if no element satisfies the predicate.
   */
  var first = function(arr, pred) {
    for (var i = 0; i < arr.length; i++) {
      var obj = arr[i];
      if (pred(obj)) {
        return obj;
      }
    }
  };

  /**
   * Given an object, extract and return all values.
   */
  var values = function(map) {
    var arr = [];
    for (var k in map) {
      arr.push(map[k]);
    }
    return arr;
  };

  /**
   * Returns the array element that has the smallest value when evaluated
   * by the given function. Returns undefined if the array is empty.
   */
  var min = function(arr, f) {
    var m;
    arr.forEach(function(d) {
      m = m ? Math.min(m, f(d)) : f(d);
    });
    return m;
  };

  /**
   * Returns a new array with the given element d remove from the original
   * array and the values es (if defined) substituted in the position of the
   * replaced element. The returned list will be larger than the supplied list
   * when es.length > 1.
   */
  var _replace = function(arr, d, es) {
    var index = -1;
    for (var i = 0; i < arr.length; i++) {
      if (arr[i] === d) {
        index = i;
        break;
      }
    }

    if (index === -1) {
      throw "index not found!";
    }

    var result = arr.slice(0, index);
    if (es) {
      result = result.concat(es);
    }
    result = result.concat(arr.slice(index + 1));
    return result;
  };

  var _toMillis = function(d) {
    return Math.round(d / 1000) / 1000;
  };

  var _sources = function(trace) {
    return trace.children.filter(function(d) { return !d.predecessors; });
  };

  var _sinks = function(trace) {
    return trace.children.filter(function(d) { return !d.successors; });
  };
  var _possibleSources = function(trace) {
    return trace.potentialChildren.filter(function(d) { return !d.possiblePredecessors; });
  };

  var _possibleSinks = function(trace) {
    return trace.potentialChildren.filter(function(d) { return !d.possibleSuccessors; });
  };


  var _hiddenTrace = function(trace, excludeHiddenTraces, excludeSystemTraces, excludeAllCompositeTasks) {
    return (excludeHiddenTraces && trace.hidden) ||
           (excludeSystemTraces && trace.systemHidden) ||
           (excludeAllCompositeTasks && trace.children !== undefined);
  };

  var _initOrPush = function(obj, field, elem) {
    if (obj[field]) {
      obj[field].push(elem);
    } else {
      obj[field] = [elem];
    }
  };

  var _applyPotentialParentsHierarchy = function(traceMap, hierarchy) {
    hierarchy.forEach(function (d) {
      var parent = traceMap[d.from];
      var child = traceMap[d.to];

      _initOrPush(parent, "potentialChildren", child);
      _initOrPush(child, "potentialParents",parent);
    });
  };

  var _applyParentsHierarchy = function(traceMap, hierarchy) {
    hierarchy.forEach(function (d) {
      var parent = traceMap[d.from];
      var child = traceMap[d.to];

      _initOrPush(parent, "children", child);
      child.parent = parent;
    });

    values(traceMap).forEach(function(d) {
      if (d.children) {
        d.children.sort(_CHRONOLOGICAL);
      }
    });
  };

  var _applyOrdering = function(traceMap, order, successorsField, predecessorsField) {
    order.forEach(function(d) {
      var predecessor = traceMap[d.to];
      var successor = traceMap[d.from];

      _initOrPush(predecessor, successorsField, successor);
      _initOrPush(successor, predecessorsField, predecessor);
    });
  };

  //Traverse the trace[inputField] array and remove the trace from each trace[input]'s updateField array.
  //The function will also insert arr to the trace[input]'s updateField array.
  var _replaceAll = function(trace, arr, updateField, inputField) {
    if (trace[inputField]) {
      trace[inputField].forEach(function (d) {
        d[updateField] = _replace(d[updateField], trace, arr);
      });
    }
  };

  //For each element in arr replace the element[field] with trace[field] only
  //if trace[field] is defined.
  var _replaceTraceWithField = function(trace, arr, field) {
    if (trace[field] !== undefined) {
      arr.forEach(function (element) {
        element[field] = trace[field];
      });
    }
  }

  var _filterHiddenTraces = function(traceMap, excludeHiddenTraces, excludeSystemTraces, excludeAllCompositeTasks) {
    if (excludeHiddenTraces || excludeSystemTraces || excludeAllCompositeTasks) {
      var trace;
      for (var traceId in traceMap) {
        trace = traceMap[traceId];
        if (_hiddenTrace(trace, excludeHiddenTraces, excludeSystemTraces, excludeAllCompositeTasks)) {
          var preds;
          var sucs;
          var possiblePreds;
          var possibleSucs;
          if (trace.children) {
            var sources = sucs = _sources(trace);
            var sinks = preds = _sinks(trace);
            //reset the predecessors of the parent trace to all the child trace that
            //does not have any predecessors since the parent is being deleted
            _replaceTraceWithField(trace, sources, "predecessors");
            _replaceTraceWithField(trace, sinks, "successors");
            } else {
            preds = trace.predecessors;
            sucs = trace.successors;
          }

          if (trace.potentialChildren) {
            var sources = possibleSucs = _possibleSources(trace);
            var sinks = possiblePreds = _possibleSinks(trace);
            //reset the possible predecessors of the parent trace to all the potential child trace that
            //does not have any possible predecessors since the parent is being deleted
            _replaceTraceWithField(trace, sources, "possiblePredecessors");
            _replaceTraceWithField(trace, sinks, "possibleSuccessors");
          } else {
            possiblePreds = trace.possiblePredecessors;
            possibleSucs = trace.possibleSuccessors;
          }

          _replaceAll(trace, sucs, "successors", "predecessors");
          _replaceAll(trace, preds, "predecessors", "successors");
          _replaceAll(trace, possibleSucs, "possibleSuccessors", "possiblePredecessors");
          _replaceAll(trace, possiblePreds, "possiblePredecessors", "possibleSuccessors");
          _replaceAll(trace, trace.potentialChildren, "potentialChildren", "potentialParents");

          // Unlink children and relink with our parent
          var parent = trace.parent;
          if (parent) {
            parent.children = _replace(parent.children, trace, trace.children);
          }

          if (trace.children) {
            trace.children.forEach(function(child) { child.parent = trace.parent; });
          }

          if (trace.potentialParents && trace.potentialChildren) {
            trace.potentialChildren.forEach(function(child) { child.potentialParents = trace.potentialParents; });
          }

          delete traceMap[traceId];
        }
      }
    }
  };

  var _treeify = function(traceMap) {
    var roots = values(traceMap).filter(function(d) { return !d.parent; });

    // If we have a forrest then add a dummy root node to form a tree.
    roots.sort(_CHRONOLOGICAL);
    return roots.length === 1 ? roots[0] : {
      dummy: true,
      children: roots
    };
  }

  /**
   * Returns the root trace for the given JSON.
   */
  var parseJson = function(json, excludeHiddenTraces, excludeSystemTraces, excludeAllCompositeTasks) {
    var minStartNanos = min(json.traces, function(d) { return d.startNanos; });
    var traceMap = {};
    json.traces.forEach(function(d) {
      var startNanos   = d.startNanos - minStartNanos,
          totalNanos = d.endNanos - d.startNanos;

      var entry = traceMap[d.id] = {
        id: d.id,
        name: d.name,
        hidden: d.hidden,
        systemHidden: d.systemHidden,
        startNanos: startNanos,
        startMillis: _toMillis(startNanos),
        totalNanos: totalNanos,
        totalMillis: _toMillis(totalNanos),
        resultType : d.resultType.toLowerCase(),
        value: d.value
      };

      if ('pendingNanos' in d) {
        entry.runMillis = _toMillis(d.pendingNanos - d.startNanos);
      }
    });

    if (json.relationships) {
      var successors = json.relationships.filter(function(d) { return d.relationship == "SUCCESSOR_OF"; });
      var possibleSuccessors = json.relationships.filter(function(d) { return d.relationship == "POSSIBLE_SUCCESSOR_OF"; });
      var parents = json.relationships.filter(function(d) { return d.relationship == "PARENT_OF"; });
      var potentialParents = json.relationships.filter(function(d) { return d.relationship == "POTENTIAL_PARENT_OF"; });

      _applyOrdering(traceMap, successors, "successors", "predecessors");
      _applyOrdering(traceMap, possibleSuccessors, "possibleSuccessors", "possiblePredecessors");
      _applyParentsHierarchy(traceMap, parents);
      _applyPotentialParentsHierarchy(traceMap, potentialParents);
    }

    _filterHiddenTraces(traceMap, excludeHiddenTraces, excludeSystemTraces, excludeAllCompositeTasks);
    return _treeify(traceMap);
  };

  /**
   * Flattens the given trace tree into an array. Each trace element gets
   * a depth field that specifies how deep the trace is in the tree.
   */
  function flatten(root) {
    var stack;
    var flat = [];

    if (root.dummy) {
      stack = root.children.map(function(d) { return [d, 0]; });
    } else {
      stack = [[root, 0]];
    }

    var elem, trace, depth, children;
    while (elem = stack.pop()) {
      trace = elem[0];
      depth = elem[1];

      trace.depth = depth;
      flat.push(trace);
      depth += 1;

      if (trace.children) {
        children = trace.children.map(function(d) { return [d, depth]; });
        stack = stack.concat(children);
      }
    }

    return flat;
  }


  function appendPath(root) {
    //build graph
    var graph =  new dig.Graph();
    var queue = [];
    var nodes = [];
    if (root.dummy) {
      root.children.forEach(function(child) {
        queue.push(child);
      });
    }
    else {
      queue.push(root);
    }
    var visited = {};

    while(queue.length) {
      var trace = queue.pop();
      if (!visited[trace.id]) {
        visited[trace.id] = true;

        if (!graph.hasNode(trace.id)) {
          graph.addNode(trace.id);
          nodes.push(trace);
        }
        if (trace.children) {
          trace.children.forEach(function(child) {
            queue.push(child);
          });
        }
        if (trace.potentialChildren) {
          trace.potentialChildren.forEach(function(child) {
            queue.push(child);
          });
        }
        if(trace.predecessors) {
          trace.predecessors.forEach(function(d) {
            if (!graph.hasNode(d.id)) {
              graph.addNode(d.id);
              nodes.push(d);
            }
            graph.addEdge(d.id, trace.id);
          });
        }
        if(trace.possiblePredecessors) {
          trace.possiblePredecessors.forEach(function(d) {
            if (!graph.hasNode(d.id)) {
              graph.addNode(d.id);
              nodes.push(d);
            }
            graph.addEdge(d.id, trace.id);
          });
        }
      }
    }

    nodes.forEach(function(node) {
      node["path"] = dig.alg.dijkstra(graph, node.id);
    });
  }

  function alignMillis(millis) {
    var millisStr = String(millis);
    var indexOfPoint = millisStr.indexOf('.');
    if (indexOfPoint === -1) {
      return millisStr + ".000";
    } else {
      var delta = millisStr.length - indexOfPoint - 1;
      if (delta < 3) {
        millisStr += new Array(4 - delta).join("0");
      } else if (delta > 3) {
        millisStr = millisStr.substring(0, indexOfPoint + 4);
      }
    }
    return millisStr;
  }

  return {
    first: first,
    min: min,
    values: values,

    parseJson : parseJson,
    flatten: flatten,
    appendPath : appendPath,
    alignMillis: alignMillis
  };
})();
