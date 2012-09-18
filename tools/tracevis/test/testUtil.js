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

TESTUTIL = (function() {
  var createDefaultNode = function(id, parent) {
    var name = "" + id;
    return createNode(id, name, false, false, 1, 1, 1000, 1, "success", "value", parent);
  };

  var createNode = function(id, name, hidden, systemHidden, startNanos, start, elapsedNanos, elapsed, resultType, value, parent) {
    var node = {
      id: id,
      name: name,
      hidden: hidden,
      systemHidden: systemHidden,
      startNanos: startNanos,
      start: startNanos,
      elapsedNanos: elapsedNanos,
      elapsed: elapsed,
      resultType : resultType,
      value: value
    };
    node.parent = parent;
    if (parent) {
      if (parent.children) {
        parent.children.push(node);
      }
      else {
        parent.children = [node];
      }
    }
    return node;
  };

  var setPotentialChildRelationship = function(from, to) {
    _setRelationship(from, to, "potentialChildren", "potentialParents");
  };

  var setPossiblePredecessorRelationship = function(from, to) {
    _setRelationship(from, to, "possiblePredecessors", "possibleSuccessors");
  };

  var setPredecessorRelationship = function(from, to) {
    _setRelationship(from, to, "predecessors", "successors");
  };

  var _setRelationship = function(from, to, fromField, toField) {
    _pushElement(from, to, fromField);
    _pushElement(to, from, toField);
  }

  var _pushElement = function(target, element, field) {
    if (target[field] !== undefined) {
      target[field].push(element);
    }
    else {
      target[field] = [element];
    }
  };

  return {
    createDefaultNode: createDefaultNode,
    createNode: createNode,
    setPotentialChildRelationship: setPotentialChildRelationship,
    setPossiblePredecessorRelationship: setPossiblePredecessorRelationship,
    setPredecessorRelationship: setPredecessorRelationship
  };
})();
