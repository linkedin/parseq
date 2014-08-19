package com.linkedin.parseq.trace;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ang Xu (axu@linkedin.com)
 */
public class ComparableTraceBuilder
{
  private final ShallowTrace _shallowTrace;
  private final Set<Related<Trace>> _related;

  public ComparableTraceBuilder(Trace t)
  {
    _shallowTrace = buildShallowTrace(t);
    _related = buildRelated(t);
  }

  public ComparableTrace build()
  {
    return new ComparableTrace(_shallowTrace, _related);
  }

  private static ShallowTrace buildShallowTrace(Trace t)
  {
    ShallowTraceBuilder builder = new ShallowTraceBuilder(t.getResultType())
      .setName(t.getName())
      .setValue(t.getValue())
      .setStartNanos(t.getStartNanos())
      .setPendingNanos(t.getPendingNanos())
      .setEndNanos(t.getEndNanos())
      .setHidden(t.getHidden())
      .setSystemHidden(t.getSystemHidden());

    for (Map.Entry<String, String> attr : t.getAttributes().entrySet())
    {
      builder.addAttribute(attr.getKey(), attr.getValue());
    }

    return builder.build();
  }

  private static Set<Related<Trace>> buildRelated(Trace t)
  {
    final Set<Related<Trace>> newRelated = new HashSet<Related<Trace>>();
    for (Related<Trace> r : t.getRelated())
    {
      ComparableTrace ct;
      if (r.getRelated() instanceof ComparableTrace)
      {
        ct = (ComparableTrace) r.getRelated();
      }
      else
      {
        ct = new ComparableTraceBuilder(r.getRelated()).build();
      }
      newRelated.add(new Related<Trace>(r.getRelationship(), ct));
    }
    return newRelated;
  }
}
