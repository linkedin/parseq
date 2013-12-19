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

GRAPHVIZ = (function() {
  /**
   * Many graphviz dispatch function. This function determines if the given
   * trace has already been rendered. If it has, then it returns immediately.
   * Otherwise this function delegates to _graphvizComposite or _graphvizTask
   * as appropriate.
   */
  var _graphvizNode = function(trace, visited, level, acc) {
    if (visited[trace.id]) {
      return;
    }

    if (trace.children) {
      _graphvizComposite(trace, visited, level, acc);
    } else {
      _graphvizTask(trace, visited, level, acc);
    }
  };

  var _graphvizTask = function(trace, visited, level, acc) {
    visited[trace.id] = true;
    var color = _traceToColor(trace);
    _appendLine(level, acc, 'T' + trace.id +
        ' [label="' + _escapeLabel(trace.name) + "\\l" +
        '|{' + _startMillis(trace) + '\\r|' + _runMillis(trace) + '\\r|' + _totalMillis(trace) + '\\r}",' +
        'style=filled,shape=Mrecord,fillcolor="' + color + '\"]');
  };

  var _graphvizComposite = function(trace, visited, level, acc) {
    visited[trace.id] = true;
    var color = _traceToColor(trace);

    _appendLine(level, acc, 'subgraph "cluster_' + trace.id + '" {');
    level++;
    _appendLine(level, acc, 'label="' + _escapeLabel(trace.name) + ' (' +
        _startMillis(trace) + ', ' + _runMillis(trace) + ', ' + _totalMillis(trace) + ')"');
    _appendLine(level, acc, 'labeljust="l"');
    _appendLine(level, acc, 'color="#aaaaaa"');
    _appendLine(level, acc, 'style="dashed"');
    _appendLine(level, acc, _sourceId(trace) + ' [shape=circle,style=filled,label="&nbsp;"]');
    _appendLine(level, acc, _sinkId(trace) + ' [shape=doublecircle,style=filled,fillcolor="' + color + '",label="&nbsp;"]');

    _graphvizChildren(trace, visited, level, acc);

    level--;
    _appendLine(level, acc, "}");
  };

  var _graphvizChildren = function(trace, visited, level, acc) {
    if (trace.children) {
      trace.children.forEach(function(child) {
        _graphvizNode(child, visited, level, acc);
      });
    }
  };

  var _graphvizChildSourceSink = function(trace, child, acc) {
    var allChildren = trace.children;
    if (trace.potentialChildren) {
      allChildren = allChildren.concat(trace.potentialChildren);
    }

    // Set up source links
    if (!_graphvizContainsPathFromSet(allChildren, child)) {
      _graphvizDependency(_sourceId(trace), _sourceId(child), acc);
    }

    // Set up sink links
    if (!_graphvizContainsPathToSet(child, allChildren)) {
      _graphvizDependency(_sinkId(child), _sinkId(trace), acc);
    }
  };

  var _graphvizPotentialChildSourceSink = function(child, acc) {
    if (child.potentialParents) {
      child.potentialParents.forEach(function (pP) {
        var allChildren = pP.children.concat(pP.potentialChildren);

        // Set up source links for potential parent
        var node;
        if (!_graphvizContainsPathToSet(child, allChildren)) {
          node = _graphvizGetClosestNode(child, pP.children);
          if (node) {
            _graphvizInvisibleDependency(_sinkId(child), _sinkId(node), acc);
          }
          _graphvizPossibleDashedDependency(_sinkId(child), _sinkId(pP), acc);
        }

        // Set up sink links for potential parent
        if (!_graphvizContainsPathFromSet(allChildren, child)) {
          node = _graphvizGetClosestNode(child, pP.children);
          if (node) {
            _graphvizInvisibleDependency(_sourceId(pP), _sourceId(node), acc);
          }
          _graphvizPossibleDashedDependency(_sourceId(pP), _sourceId(child), acc);
        }
      });
    }
  };

  var _graphvizEdges = function(trace, visited, acc) {
    if (visited[trace.id]) {
      return;
    }
    visited[trace.id] = true;
    _graphvizPredecessors(trace, visited, acc);
    _graphvizPossiblePredecessors(trace, visited, acc);


    if (trace.children) {
      trace.children.forEach(function(child) {
        _graphvizChildSourceSink(trace, child, acc);
        _graphvizPotentialChildSourceSink(child, acc);
      });

      trace.children.forEach(function(child) {
        _graphvizEdges(child, visited, acc);
      });
    }
  };

  var _graphvizPredecessors = function(trace, visited, acc) {
    if (trace.predecessors) {
      trace.predecessors.forEach(function(d) {
        _graphvizDependency(_sinkId(d), _sourceId(trace), acc);
      });
    }
  };

  var _graphvizPossiblePredecessors = function(trace, visited, acc) {
    //create edges between nodes
    if (trace.possiblePredecessors) {
      trace.possiblePredecessors.forEach(function(d) {
        if (d.parent && d.parent.children) {
          var node = _graphvizGetClosestNode(trace, d.parent.children);
          if (!node && (!trace.parent || (trace.parent !== d.parent))) {
            node = d.parent;
          }
          if (node) {
            _graphvizInvisibleDependency(_sinkId(d), _sinkId(node), acc);
          }
        }
        _graphvizPossibleDashedDependency(_sinkId(d), _sourceId(trace), acc);
      });
    }
  };

  var _graphvizGetClosestNode = function(from, toSet) {
    var result;
    var min = Number.POSITIVE_INFINITY;
    if (toSet) {
      toSet.forEach(function(t) {
        if (from !== t) {
          var edge = from.path[t.id];
          if (edge.distance < min) {
            min = edge.distance;
            result = t;
          }
        }
      });
    }
    return result;
  };

  var _graphvizContainsPathFromSet = function(fromSet, to) {
    var result = false;
    if (fromSet) {
      for (var i = 0; i < fromSet.length; i++) {
        if (to !== fromSet[i]) {
          if (fromSet[i].path[to.id].distance !== Number.POSITIVE_INFINITY) {
            result = true;
            break;
          }
        }
      }
    }
    return result;
  };

  var _graphvizContainsPathToSet = function(from, toSet) {
    var result = false;
    if (toSet) {
      for (var i = 0 ; i < toSet.length; i++) {
        if (from !== toSet[i]) {
          if (from.path[toSet[i].id].distance !== Number.POSITIVE_INFINITY) {
            result = true;
            break;
          }
        }
      }
    }
    return result;
  };

  var _graphvizInvisibleDependency = function(sourceId, sinkId, acc) {
    _appendLine(1, acc, sourceId + " -> " + sinkId + " [style=invis]");
  };

  var _graphvizPossibleDashedDependency = function(sourceId, sinkId, acc) {
    //sink edges are not dashed
    if (sinkId.search("_sink$") > -1) {
      _graphvizDependency(sourceId, sinkId, acc);
    }
    else {
      _appendLine(1, acc, sourceId + " -> " + sinkId + " [style=dashed]");
    }
  };

  var _graphvizDependency = function(sourceId, sinkId, acc) {
    _appendLine(1, acc, sourceId + " -> " + sinkId);
  };

  var _matchSymbols = /([<>|"{} ])/g;
  var _matchNewLines = /\n/g;
  var _escapeLabel = function(label) {
    return label.replace(_matchSymbols, "\\$1")
        .replace(_matchNewLines, "\\l");
  };

  var _startMillis = function(trace) { return '@' + TRACE.alignMillis(trace.startMillis); };
  var _runMillis = function(trace) { return 'runMillis' in trace ? TRACE.alignMillis(trace.runMillis) : '?'; };
  var _totalMillis = function(trace) { return '+' + TRACE.alignMillis(trace.totalMillis); };

  var _sourceId = function(source) { return "T" + (source.children ? source.id + "_source" : source.id); };
  var _sinkId   = function(sink) { return "T" + (sink.children ? sink.id + "_sink" : sink.id); };

  var _appendLine = function(level, acc, str) {
    var indent = "";
    for (var i = 0; i < level; i++) {
      indent += "    ";
    }
    acc.push(indent);
    acc.push(str);
    acc.push('\n');
  };

  var _traceToColor = function(trace) {
    switch (trace.resultType) {
      case "success":      return "#e0ffe0";
      case "error":        return "#ffe0e0";
      case "early_finish": return "#fffacd";
      default:             return "#aaaaaa";
    }
  };

  /**
   * Renders the view and append it to a d3 selection.
   */
  var render = function(selection, root) {
    selection.classed("graphvizview", true);

    var pre = selection.append("textarea")
        .style("width", "100%")
        .style("height", "600px");

    var visited = {};

    var acc = [];
    _appendLine(0, acc, "digraph {");
    if (root.dummy) {
      root.children.forEach(function(child) {
        _graphvizNode(child, visited, 1, acc);
      });
    } else {
      _graphvizNode(root, visited, 1, acc);
    }

    visited = {};
    if (root.dummy) {
      root.children.forEach(function(child) {
        _graphvizEdges(child, visited, acc);
      });
    } else {
      _graphvizEdges(root, visited, acc);
    }
    _appendLine(0, acc, "}");
    pre.text(acc.join(''));
  };

  return {
    render: render
  };
})();
