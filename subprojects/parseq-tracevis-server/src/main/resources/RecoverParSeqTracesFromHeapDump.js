function stringify(o) {
	if (o) {
		return o.toString()
	} else {
		return null;
	}
}

function shallowTraceToMap(trace) {
  var m = {
  	id: trace._id.value,
  	name: stringify(trace._name),
  	resultType: stringify(trace._resultType.name),
  	value: stringify(trace._value),
  	hidden: trace._hidden,
  	systemHidden: trace._systemHidden
  };

  if (trace._startNanos) {
  	m["startNanos"] = trace._startNanos.value
  }
  if (trace._pendingNanos) {
  	m["pendingNanos"] = trace._pendingNanos.value
  } else {
    m["pendingNanos"] = m["startNanos"]
  }
  if (trace._endNanos) {
  	m["endNanos"] = trace._endNanos.value
  } else {
    m["endNanos"] = m["pendingNanos"]
  }
  return m;
}

function addTracesFromChain(entry, traces) {
  var cur = entry;
  while (cur) {
    traces.push(shallowTraceToMap(cur.value._value));
    cur = cur.next;
  }
}

function shallowTracesToArray(sts) {
  var traces = [];
  for (var i in sts.table) {
    var entry = sts.table[i];
    if (entry) {
      addTracesFromChain(entry, traces);
    }
  }
  return traces;
}

function relationshipToMap(rel) {
  return {
  	relationship: stringify(rel._relationship.name),
  	from: rel._from.value,
  	to: rel._to.value
  };
}

function addRelationshipFromChain(entry, relationships) {
  var cur = entry;
  while (cur) {
    relationships.push(relationshipToMap(cur.key));
    cur = cur.next;
  }
}

function relationshipsToArray(rels) {
  var relationships = [];
  for (var i in rels.table) {
    var entry = rels.table[i];
    if (entry) {
      addRelationshipFromChain(entry, relationships);
    }
  }
  return relationships;
}

function traceToMap(t) {
  var r = {
    traces: shallowTracesToArray(t._traceBuilders),
    relationships: relationshipsToArray(t._relationships.map),
    planClass: t._planClass,
    planId: t._planId
  };
  if (t._planClass) {
    r["planClass"] = stringify(t._planClass)
  } else {
    r["planClass"] = 'unknown'
  }
  if (t._planId) {
    r["planId"] = t._planId.value
  } else {
    r["planId"] = 0
  }
  return r;
}

function tracify(t) {
  return JSON.stringify(traceToMap(t));
}

function getTraces() {
  return map(heap.objects('com.linkedin.parseq.trace.TraceBuilder'), tracify);
}

getTraces()
