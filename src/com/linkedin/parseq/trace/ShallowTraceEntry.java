package com.linkedin.parseq.trace;

import com.linkedin.parseq.internal.ArgumentUtil;

public class ShallowTraceEntry
{
  private final int _id;
  private final ShallowTrace _shallowTrace;

  public ShallowTraceEntry(int id, ShallowTrace shallowTrace)
  {
    ArgumentUtil.notNull(shallowTrace, "shallowTrace");
    _id = id;
    _shallowTrace = shallowTrace;
  }

  public int getId()
  {
    return _id;
  }

  public ShallowTrace getShallowTrace()
  {
    return _shallowTrace;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ShallowTraceEntry that = (ShallowTraceEntry) o;

    if (_id != that._id) return false;
    if (!_shallowTrace.equals(that._shallowTrace)) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = _id;
    result = 31 * result + _shallowTrace.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return "TraceEntry{" +
        "_id=" + _id +
        ", _shallowTrace=" + _shallowTrace +
        '}';
  }
}
