package com.linkedin.parseq.trace;

import java.util.Set;

/**
 * @author Ang Xu (axu@linkedin.com)
 */
public class ComparableTrace extends TraceImpl
{
  public ComparableTrace(ShallowTrace trace, Set<Related<Trace>> related)
  {
    super(trace, related);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ComparableTrace that = (ComparableTrace) o;

    if (!this.getShallowTrace().equals(that.getShallowTrace())) return false;
    if (!this.getRelated().equals(that.getRelated())) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = getShallowTrace().hashCode();
    result = 31 * result + getRelated().hashCode();
    return result;
  }
}
